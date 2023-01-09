/**
 * GooglePlayBillingPlugin.kt - LICENSE Notice
 * *************************************************************************************************
 *
 * Copyright (c) Token Gamedev, Prakash Das.
 * Copyright (c) Contributors (cf. AUTHORS.md).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ********************************************************************************************** */

package gaml.google.play.billing

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.*
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


/**
 * Android plugin to incorporate Google play billing client in Godot Game for products and purchases
 * through notification play store in android devices
 */
class GooglePlayBillingPlugin(godot:Godot): GodotPlugin(godot),BillingClientStateListener,
    PurchasesResponseListener, PurchaseHistoryResponseListener, ProductDetailsResponseListener,
    PurchasesUpdatedListener {

    companion object{

        private val handler = Handler(Looper.getMainLooper())
        private const val TAG =  "GooglePlayBilling"

        private const val RECONNECT_TIMER_START_MILLISECONDS = 1L * 1000L // 1 Second
        private const val RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 10L // 10 minutes

        private const val PRODUCT_ID_NOT_IN_CACHE = 1
        private const val SERVICE_ERROR = 2
        private const val NO_ERROR = 0
    }

    private val context: Context =
        activity?.applicationContext ?: throw IllegalStateException()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()


    private var autoReconnect: Boolean = true

    // how long before the connection tries to reconnect to Google play
    private var reconnectTime = RECONNECT_TIMER_START_MILLISECONDS

    // Obfuscated account id to use for billing purpose
    private var obfuscatedAccountId = ""

    // Obfuscated profile id if the purchase will be by profile instead of account
    private var obfuscatedProfileId = ""

    // Cache to hold the product details
    private val productDetailsCache = mutableMapOf<String, ProductDetails>()

    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = "GooglePlayBilling"


    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(

            // Connection response signals
            SignalInfo("connected"),
            SignalInfo("connection_error", Any::class.java),
            SignalInfo("disconnected"),

            // Products Query completed (Success/Issues)
            SignalInfo("query_products_completed", Any::class.java,  Array<Any>::class.java),

            // Purchases Query Completed (Success/Errors)
            SignalInfo("query_purchases_completed", Any::class.java,  Array<Any>::class.java),

            // Purchase History Query Completed (Success/Errors)
            SignalInfo("query_purchase_history_completed", Any::class.java,  Array<Any>::class.java),

            // Purchase Flow completed (Success/Errors)
            SignalInfo("purchase_flow_completed", Any::class.java,  Array<Any>::class.java),

            // Purchase Acknowledgement completed (Success/Errors)
            SignalInfo("purchase_acknowledgement_completed",Any::class.java, String::class.java),

            // Purchase Consumption Completed (Success/Errors)
            SignalInfo("purchase_consumption_completed", Any::class.java, String::class.java)

            )
    }
    @UsedByGodot
    fun isReady() = billingClient.isReady

    /**
     * Gets the connection state of the client. Possible values are [BillingClient.ConnectionState]
     */
    @UsedByGodot
    fun getConnectionState() = billingClient.connectionState

    /**
     * Sets up the properties for the client. It should be called before startConnect() otherwise
     * it may have different behaviors.
     * @param autoReconnect[Boolean] if true will automatically reconnect if the connection
     * with the store is lost somehow (Default: true)
     */
    @UsedByGodot
    fun setupClient(autoReconnect:Boolean){
        this.autoReconnect = autoReconnect
    }

    /**
     * Starts a Connection to the store.
     */
    @UsedByGodot
    fun startConnection(){
        // Start the Connection
        billingClient.startConnection(this)
    }

    /**
     * It happens primarily if the Google Play Store self-upgrades or is force closed.
     * It is a rare scenario.
     */
    override fun onBillingServiceDisconnected() {
        if (autoReconnect)
            reconnectBillingService()
    }

    /**
     * It happens when the connection request is completed. It may be successful or not.
     */
    override fun onBillingSetupFinished(billingResult: BillingResult) {

        Log.d(TAG, "Received Connection Response...${billingResult.responseCode}/${billingResult.debugMessage}")
        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

            // Connection is Success
            reconnectTime = RECONNECT_TIMER_START_MILLISECONDS
            emitSignal("connected")
        }
        else {
            if (autoReconnect){
                reconnectBillingService()
            }
            else {
                emitSignal("connection_error",
                    BillingUtils.getErrorDictionary(billingResult))
            }
        }
    }

    /**
     * reconnect the service with exponentially increasing intervals till the Max value is reached.
     */
    private fun reconnectBillingService() {
        if (reconnectTime > RECONNECT_TIMER_MAX_TIME_MILLISECONDS){
            emitSignal("disconnected")
            return
        }
        else{
            handler.postDelayed(
                { billingClient.startConnection(this@GooglePlayBillingPlugin) },
                reconnectTime
            )
            reconnectTime *= 2
        }
    }

    /**
     * Terminates the connection to the billing server
     */
    @UsedByGodot
    fun endConnection(){
        billingClient.endConnection()
        emitSignal("disconnected")
    }

    // Query Products Section

    /**
     * Queries the list of products in the store with the given types
     * @param productIdList:List of productId of the products to be fetched
     * @param productType type of product from [BillingClient.ProductType]
     * @throws IllegalArgumentException: When both ProductIds and productTypes are not of same size
     */
    @UsedByGodot
    fun queryProducts(productType: String , productIdList: Array<String>){
        val productsList = mutableListOf<QueryProductDetailsParams.Product>()
        for (productId in productIdList){
            productsList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build()
            )
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(productsList)

        billingClient.queryProductDetailsAsync(params.build(), this)
    }

    /**
     * Callback happens in response to queryProduct requests to store
     * @param result the outcome status of the query (Ok/Error)
     * @param productDetails the list of products in the store.
     */
    override fun onProductDetailsResponse(result: BillingResult,
                                          productDetails: MutableList<ProductDetails>) {

        Log.i(TAG, "Product Response.$result >> $productDetails")

        if (result.responseCode == BillingClient.BillingResponseCode.OK){

            val productList = mutableListOf<Any>()
            for (productDetail in productDetails) {

                // Store in cache
                productDetailsCache[productDetail.productId] = productDetail

                // add to the list for sending back
                val dict = BillingUtils.getProductDetailsDictionary(productDetail)
                for (entry in dict.entries){
                    Log.i(TAG, "...${entry.key} = ${entry.value}")
                }
                Log.i(TAG,"")
                productList.add(dict)
            }

            // Send the list of products available.
            emitSignal("query_products_completed",
                BillingUtils.getErrorDictionary(result),
                productList.toTypedArray())
        }
        else {
            emitSignal("query_products_completed",
                BillingUtils.getErrorDictionary(result),
                emptyArray<Any>())
        }
    }

    /**
     * Queries the purchases for the given product type
     * @param productType type of product to search for (IN_APP or SUBS)
     */
    @UsedByGodot
    fun queryPurchases(productType: String){
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(productType)

        billingClient.queryPurchasesAsync(params.build(), this)
    }

    /**
     * Callback happens in response to queryPurchase requests to store
     * @param result the outcome status of the query (Ok/Error)
     * @param purchases the list of purchases the user has done in the store for the app
     */
    override fun onQueryPurchasesResponse(result: BillingResult, purchases: MutableList<Purchase>) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK){

            val purchaseList = mutableListOf<Any>()
            for (purchase in purchases)
                purchaseList.add(BillingUtils.getPurchaseDetailsDictionary(purchase))

            // Send the list of products available.
            emitSignal("query_purchases_completed",
                BillingUtils.getErrorDictionary(result),
                purchaseList.toTypedArray())
        }else{
            emitSignal("query_purchases_completed",
                BillingUtils.getErrorDictionary(result),
                emptyArray<Any>())
        }
    }

    /**
     * Queries the purchase History records for older purchases for the given product type
     * @param productType type of product to search for (IN_APP or SUBS)
     */
    @UsedByGodot
    fun queryPurchaseHistory(productType: String){
        val params = QueryPurchaseHistoryParams.newBuilder()
            .setProductType(productType)

        billingClient.queryPurchaseHistoryAsync(params.build(), this)
    }

    /**
     * Callback happens in response to queryPurchaseHistory requests to store
     * @param result the outcome status of the query (Ok/Error)
     * @param purchaseHistory the list of the most recent purchases made by the user for each
     * product, even if that purchase is expired, canceled, or consumed
     */
    override fun onPurchaseHistoryResponse(result: BillingResult,
                                           purchaseHistory: MutableList<PurchaseHistoryRecord>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK
            && purchaseHistory != null){

            val purchaseList = arrayOfNulls<Dictionary>(purchaseHistory.size)

            for (i in purchaseHistory.indices) {
                purchaseList[i] = BillingUtils.getPurchaseHistoryDictionary(purchaseHistory[i])
            }
            // Send the list of products available.
            emitSignal("query_purchase_history_completed",
                BillingUtils.getErrorDictionary(result),
                purchaseList)
        }
        else{
            emitSignal("query_purchases_history_completed",
                BillingUtils.getErrorDictionary(result),
                arrayOfNulls<Dictionary>(0))
        }
    }

    /**
     * launches the Purchase flow to purchase a product.
     * @param productId id of the product to be purchased
     * @return [Dictionary] containing error or success messages accordingly
     */
    @UsedByGodot
    fun launchPurchaseFlow(productId: String) : Dictionary {

        val productDetails = productDetailsCache[productId]
        if (productDetails == null){
            return BillingUtils.getCustomErrorDictionary(PRODUCT_ID_NOT_IN_CACHE)
        }
        else{
            // Here for SUBS(Subscriptions) offer token has to be provided.
            // Subs are not supported yet
            val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)

            if (obfuscatedAccountId.isNotEmpty()) {
                billingFlowParams.setObfuscatedAccountId(obfuscatedAccountId)
            }

            if (obfuscatedProfileId.isNotEmpty()) {
                billingFlowParams.setObfuscatedAccountId(obfuscatedProfileId)
            }

            // Launch the flow
            val result =  billingClient.launchBillingFlow(activity!!, billingFlowParams.build())

            // return success or error on launch
            return if (result.responseCode != BillingClient.BillingResponseCode.OK)
                BillingUtils.getCustomErrorDictionary(SERVICE_ERROR, result)
            else
                BillingUtils.getCustomErrorDictionary(NO_ERROR, result)
        }

    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {

        if (result.responseCode == BillingClient.BillingResponseCode.OK &&
            purchases != null){

            val purchaseList = mutableListOf<Any>()
            for (purchase in purchases)
                purchaseList.add(BillingUtils.getPurchaseDetailsDictionary(purchase))

            // Send the list of products available.
            emitSignal("purchase_flow_completed",
                BillingUtils.getErrorDictionary(result),
                purchaseList.toTypedArray())
        }else{
            emitSignal("purchase_flow_completed",
                BillingUtils.getErrorDictionary(result),
                emptyArray<Any>())
        }
    }

    /**
     * Acknowledges an In-app non-consumable.
     * On Completion "purchase_acknowledgement_completed" signal will be triggered
     * @param purchaseToken token of the purchase that will be consumed
     */
    @UsedByGodot
    fun acknowledgePurchase(purchaseToken: String){
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
            emitSignal(
                "purchase_acknowledgement_completed",
                BillingUtils.getErrorDictionary(it),
                purchaseToken)
        }
    }

    /**
     * Consumes an In-app consumable.
     * On Completion "purchase_consumption_completed" signal will be triggered
     * @param purchaseToken token of the purchase that will be consumed
     */
    @UsedByGodot
    fun consumePurchase(purchaseToken: String){
        val consumePurchaseParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.consumeAsync(consumePurchaseParams) { result, token ->
            emitSignal(
                "purchase_consumption_completed",
                BillingUtils.getErrorDictionary(result),
                token
            )
        }
    }
}


package gaml.google.play.billing

import com.android.billingclient.api.*
import org.godotengine.godot.Dictionary

/**
 * Utility class to convert Google play billing objects to dictionary object, which can be
 * sent to godot engine
 */
class BillingUtils {
    companion object {

        /**
         * Creates a dictionary out of ProductDetails object to be sent to Godot Engine.
         * @param product the product details object of type [ProductDetails] to be converted.
         * @return the converted godot dictionary of type [Dictionary]
         */
        fun getProductDetailsDictionary(product: ProductDetails): Dictionary {

            val dictionary = Dictionary()

            // Add all the common attributes of product details
            dictionary["product_id"] = product.productId
            dictionary["title"] = product.title
            dictionary["name"] = product.name
            dictionary["description"] = product.description
            dictionary["type"] = product.productType

            // Add specific attributes based on product Type
            if (product.productType == BillingClient.ProductType.INAPP) {

                val oneTimePurchaseOffer = product.oneTimePurchaseOfferDetails
                val productPrice = Dictionary()

                if (oneTimePurchaseOffer != null) {
                    productPrice["formatted_price"] = oneTimePurchaseOffer.formattedPrice
                    productPrice["price_amount_micros"] = oneTimePurchaseOffer.priceAmountMicros
                    productPrice["price_currency_code"] = oneTimePurchaseOffer.priceCurrencyCode
                }
                dictionary["product_price"] = productPrice
            } else {

                val subscriptionOfferDetails = product.subscriptionOfferDetails
                    ?: mutableListOf<ProductDetails.SubscriptionOfferDetails>()

                val subscriptionOffers = mutableListOf<Dictionary>()
                //val subscriptionOffers = arrayOfNulls<Dictionary>(subscriptionOfferDetails.size)
                for (offer in subscriptionOfferDetails) {
                    val offerDetail = Dictionary()
                    offerDetail["offer_tags"] = offer.offerTags.toTypedArray()
                    offerDetail["offer_token"] = offer.offerToken
                    offerDetail["offer_id"] = offer.offerId
                    offerDetail["base_plan_id"] = offer.basePlanId

                    val phases = mutableListOf<Dictionary>()
                    for (pricingPhase in offer.pricingPhases.pricingPhaseList) {
                        val phase = Dictionary()
                        phase["billing_cycle_count"] = pricingPhase.billingCycleCount
                        phase["billing_period"] = pricingPhase.billingPeriod
                        phase["formatted_price"] = pricingPhase.formattedPrice
                        phase["price_amount_micros"] = pricingPhase.priceAmountMicros
                        phase["price_currency_code"] = pricingPhase.priceCurrencyCode
                        phase["recurrence_mode"] = pricingPhase.recurrenceMode
                        phases.add(phase)

                    }
                    offerDetail["pricing_phases"] = phases.toTypedArray<Any>()

                    subscriptionOffers.add(offerDetail)
                }
                dictionary["subscription_offers"] = subscriptionOffers.toTypedArray<Any>()
            }
            return dictionary
        }

        /**
         * Creates a dictionary out of Purchase object to be sent to Godot Engine.
         * @param purchase the purchase object of type [Purchase] to be converted.
         * @return the converted godot dictionary of type [Dictionary]
         */
        fun getPurchaseDetailsDictionary(purchase: Purchase): Dictionary {

            val dictionary = Dictionary()

            // Add all the common attributes of product details
            dictionary["order_id"] = purchase.orderId
            dictionary["purchase_token"] = purchase.purchaseToken
            dictionary["original_json"] = purchase.originalJson
            dictionary["developer_payload"] = purchase.developerPayload
            dictionary["package_name"] = purchase.packageName
            dictionary["purchase_state"] = purchase.purchaseState
            dictionary["purchase_time"] = purchase.purchaseTime
            dictionary["quantity"] = purchase.quantity
            dictionary["is_acknowledged"] = purchase.isAcknowledged
            dictionary["is_auto_renewing"] = purchase.isAutoRenewing
            dictionary["signature"] = purchase.signature

            val accountIdentifiers = purchase.accountIdentifiers
            val accountInfo  = Dictionary()
            if (accountIdentifiers != null){
                accountInfo["obfuscated_account_id"] = accountIdentifiers.obfuscatedAccountId ?: ""
                accountInfo["profile_id"] = accountIdentifiers.obfuscatedProfileId ?: ""
            }
            dictionary["account_identifiers"] = accountInfo

            val products: Array<String> = purchase.products.toTypedArray()
            dictionary["products"] = products

            return dictionary
        }

        /**
         * Creates a dictionary out of PurchaseHistoryRecord object to be sent to Godot Engine.
         * @param purchaseHistory the purchase History record object of type [PurchaseHistoryRecord]
         * to be converted.
         * @return the converted godot dictionary of type [Dictionary]
         */
        fun getPurchaseHistoryDictionary(purchaseHistory: PurchaseHistoryRecord): Dictionary{
            val dictionary = Dictionary()

            // Add all the common attributes of product details
            dictionary["purchase_token"] = purchaseHistory.purchaseToken
            dictionary["purchase_time"] = purchaseHistory.purchaseTime

            dictionary["original_json"] = purchaseHistory.originalJson
            dictionary["signature"] = purchaseHistory.signature
            dictionary["quantity"] = purchaseHistory.quantity

            val products: Array<String> = purchaseHistory.products.toTypedArray()
            dictionary["products"] = products

            return dictionary
        }

        /**
         * Creates an error dictionary to return for billing flow
         * @param customErrorCode the error code for error happened in launching the flow
         * from the app
         * @param billingResult the error happened while launching the flow from store side
         * @return [Dictionary] dictionary containing all the values
         */
        fun getCustomErrorDictionary(customErrorCode: Int,
                                     billingResult: BillingResult? = null): Dictionary {
            val dict = Dictionary()
            dict["error_code"] = customErrorCode
            dict["int_error"] = billingResult?.let { getErrorDictionary(it) }
            return dict
        }

        /**
         * Creates an error dictionary out of billing flow for sending to Godot
         * @param billingResult the error object to represent
         * @return [Dictionary] dictionary containing the valuess
         */
        fun getErrorDictionary(billingResult: BillingResult): Dictionary{
            val dict = Dictionary()
            dict["response_code"] = billingResult.responseCode
            dict["debug_message"] = billingResult.debugMessage
            return dict
        }
    }
}
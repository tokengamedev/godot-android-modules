# Google Play Billing

Google Play Billing is an android plugin for Godot Game engine.
It allows the user to integrate google play billing library to View and Purchase in-app products and subscriptions in Google play store inside your Godot Game. These products are managed through Google play console.
For more details about Google play Billing library, you can go through [here](https://developer.android.com/google/play/billing)


### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("GooglePlayBilling"):

    # Get a reference to the singleton
    var billing = Engine.get_singleton("GooglePlayBilling")    
```

### Features:

There are basically three types of products you may have to work with:
1. One Time Products (Non-Consumable) - Purchase once and use it forever like "Remove Ads"
2. Consumable Products - Purchase multiple times and use it forever like "Buy Coin Purse"
3. Subscription Products - Purchase and use it for a specified amount of time like "VIP Access"

Note: For Subscription products various features are not fully supported yet
   - Product Listing : Not Tested
   - Purchase and Renewal: Not Tested
   - Price Change Flow: Not Implemented  

### APIs

#### Object Types:

- ##### Product:
  Reprsents a store product
  - **product_id [String]** - Id of the product
  - **title [String]** - Title of the Product
  - **name [String]** - name of the Product
  - **description [String]** - product description
  - **type [String]** - type of product as values in enum [BillingClient.ProductType](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.ProductType)
  - **product_price [OneTimeOffer]** - Price of one time purchase item
  - **subscription_offers [Array(SubscriptionOffer)]** - List of subscription offers

- ##### OneTimeOffer:
  Represents the price of one time product
  - **formatted_price [String]** - Textual representation of price with currency symbol
  - **price_amount_micros [Int]** - Price amount multiplied by 1 million
  - **price_currency_code [String]** - ISO 4217 currency code for the price

- ##### SubscriptionOffer:
  Represents a offer of the subscription
  - **offer_tags [Array(String)]** - List of offer tags
  - **offer_token [String]** - offer token (required to pass in launchBillingFlow to purchase)
  - **offer_id [String]** - Id of the offer
  - **base_plan_id [String]** - id of the base plan of the offer
  - **pricing_phases [Array(PricingPhase)]** - list of phases of subscription and its price
  
- ##### PricingPhase:
  Represents a Pricing Phase of the subscription:
  - **billing_cycle_count [Int]** - Number of cycles for which the billing period is applied.
  - **billing_period [String]** - Billing period for which the given price applies, specified in ISO 8601 format.
  - **formatted_price [String]** - Textual representation of price with currency symbol
  - **price_amount_micros [Int]** - Price amount multiplied by 1 million
  - **price_currency_code [String]** - ISO 4217 currency code for the price
  - **recurrence_mode [Int]** - Recurrence modes as in the values in the enum [ProductDetails.RecurrenceMode](https://developer.android.com/reference/com/android/billingclient/api/ProductDetails.RecurrenceMode)
  

- ##### Purchase:
  Represents a store purchase
  - **order_id [String]** - Returns a unique order identifier for the transaction
  - **purchase_token [String]** - Returns a token that uniquely identifies a purchase for a given item and user pair.
  - **original_json [String]** - Returns a String in JSON format that contains details about the purchase order
  - **developer_payload [String]** - Returns the payload specified when the purchase was acknowledged or consumed (backward Compatibility)
  - **package_name [String]** - Returns the application package from which the purchase originated.
  - **purchase_state [Int]** - State of the purchase as in the values in the enum [Purchase.PurchaseState](https://developer.android.com/reference/com/android/billingclient/api/Purchase.PurchaseState)
  - **purchase_time [Int]** - the time the product was purchased (unix).
  - **quantity [Int]** - quantity of purchase.
  - **is_acknowledged [Boolean]** - Indicates whether the purchase has been acknowledged.
  - **is_auto_renewing [Boolean]** - Indicates whether the subscription renews automatically.
  - **products [Array(String)]** - list of product_ids purchased
  - **account_identifiers [AccountIdentifiers]** - Custom or additional account Identification data
  - **signature [String]** - Signature of the purchase

- ##### AccountIdentifiers:
  Info to identify the purchasing user
  - **obfuscated_account_id [String]** - account Id.
  - **profile_id [String]** - profile Id.

- ##### PurchaseHistory:
  Info to identify the purchasing user
  - **purchase_token [String]** - Returns a token that uniquely identifies a purchase for a given item and user pair.
  - **purchase_time [Int]** - the time the product was purchased (unix).
  - **original_json [String]** - Returns a String in JSON format that contains details about the purchase order
  - **signature [String]** - Signature of the purchase
  - **quantity [Int]** - quantity of purchase.
  - **products [Array(String)]** - list of product_ids purchased

- ##### CustomError:
  Represents a custom error with custom error codes when purchase flow is launched
  - **error_code [String]** - custom error code. possible values are PRODUCT_ID_NOT_IN_CACHE(1), SERVICE_ERROR(2) or NO_ERROR(0)
  - **int_error [Result]** - Internal launch result from billing client if there is any.


- ##### Result:
  Represents the result from most api calls on billing client
  - **response_code [Int]** - Response code of the result.
  - **debug_message [String]** - Textual message of the result.



#### Methods

- ##### isReady()
  Checks if the billing client is ready or not
  
- ##### getConnectionState()
  Gets the connection state of the billing client
  **Returns:**
  - **[int]** - values from the enum [BillingClient.ConnectionState](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.ConnectionState)

- ##### setupClient(autoReconnect)
  Sets up the client with additional options for configuring before, connection is established.
  
  **Parameters**
  - **autoReconnect [Boolean]** (Default:True) If true, sets up the client to reconnect automatically if the client gets disconnected, else just fails.

- ##### startConnection()
  Initializes the Billing Client and starts the connection. [connected](#connected), [connection_error](#connection_errorresult) or [disconnected](#disconnected) signal will be generated after the connection process is completed.
  
- ##### endConnection()
  Ends the Billing Client connection to the server. [disconnected](#disconnected) signal will be generated after disconnection is completed. 

- ##### queryProducts(productType, productIdList)
  Queries the list of products in the store with the given type. [query_products_completed](#query_products_completedresult-products) signal will be generated after query is completed. 

  **Parameters**
  - **productType [String]** Type of products sent for query. Possible values from one of the values in enum [BillingClient.ProductType](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.ProductType)
  - **productIdList [Array(String)]** , List of ids of the products being queried.
  

- ##### queryPurchases(productType)
  Queries the list of purchases for the user in the store for the given product type. [query_purchases_completed](#query_purchases_completedresult-purchases) signal will be generated after query is completed. 

  **Parameters**
  - **productType [String]** Type of products sent for query. Possible values from one of the values in enum [BillingClient.ProductType](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.ProductType)


- ##### queryPurchaseHistory(productType)
  Queries the list of purchase history records for the user in the store for the given product type. [query_purchase_history_completed](#query_purchase_history_completedresult-purchases) signal will be generated after query is completed. 

  **Parameters**
  - **productType [String]** Type of products sent for query. Possible values from one of the values in enum [BillingClient.ProductType](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.ProductType)

- ##### launchPurchaseFlow(productId)
  Launches the purchase flow for purchasing the given product. [purchase_flow_completed](#purchase_flow_completedresult-purchases) signal will be generated after flow is completed. 

  **Parameters**
  - **productId [String]** The id of the product being purchased
  
  **Returns**
  - [CustomError](#customerror) - result if the flow is successfully launched or not
  

- ##### acknowledgePurchase(purchaseToken)
  Requests the store to acknowledge the purchase made by the user. [purchase_acknowledgement_completed](#purchase_acknowledgement_completedresult-purchase_token) signal will be generated after acknowledgement completed. 

  **Parameters**
  - **purchaseToken [String]** Unique token of the purchase made
  

- ##### consumePurchase(purchaseToken)
  Requests the store to consume the purchase made by the user. [purchase_consumption_completed](#purchase_consumption_completedresult-purchase_token) signal will be generated after consumption completed. 

  **Parameters**
  - **purchaseToken [String]** Unique token of the purchase made

  

#### Signals

- ##### connected()
  Raised when the billing client is connected to store

- ##### connection_error(result)
  Raised when the billing client is unable to connect to store

  **Parameters**
  - **result [[Result](#result)]** Error code and message for the error in connection


- ##### disconnected()
  Raised when the billing client is unable to connect after retries or client is manually disconnected 

- ##### query_products_completed(result, products)
  Raised when the products query is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **products [Array([Product](#product))]]** List of products if successful else empty


- ##### query_purchases_completed(result, purchases)
  Raised when the purchases query is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **purchases [Array([Purchase](#purchase))]]** List of purchases if successful else empty


- ##### query_purchase_history_completed(result, purchases)
  Raised when the purchases history query is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **purchases [Array([PurchaseHistory](#purchasehistory))]]** List of purchase history records if successful else empty

- ##### purchase_flow_completed(result, purchases)
  Raised when the purchase flow is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **purchases [Array([Purchase](#purchase))]]** List of purchase records if successful else empty


- ##### purchase_acknowledgement_completed(result, purchase_token)
  Raised when the purchase acknowledgement is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **purchase_token [String]** purchase token of the purchase sent for acknowledgement


- ##### purchase_consumption_completed(result, purchase_token)
  Raised when the purchase consumption is completed, success or failure.

  **Parameters**
  - **result [[Result](#result)]** Error code and message in case of any error in operation
  - **purchase_token [String]** purchase token of the purchase sent for consumption

### Developer Notes:

|             | Minimum  | Maximum |
|-------------|----------|---------|
| Android SDK | 23       | 32      |
| Java/JDK    | 11       |         |
| Kotlin      | 1.8.0    |         | 

- Library Dependencies:
    - com.android.billingclient:billing:5.1.0



package com.dream.friend.util

//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.PurchaseInfo

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.google.gson.Gson


/**
class SubscriptionUtil(
var context: Context
) {
lateinit var billingClient: BillingClient

//    var productId: String=""
var basePlanId: String = ""


//    fun subscribe(productId: String, inAppSubscriptionListener: InAppSubscriptionListener) {
//        checkSubscription(productId,object:CheckSubscriptionListener{
//            override fun alreadyHaveSubscription(productId: String) {
//                inAppSubscriptionListener.onItemAlreadyOwned(productId)
//            }
//
//            override fun noSubscription(productId: String) {
//                billingClient = BillingClient.newBuilder(context)
//                    .enablePendingPurchases()
//                    .setListener { billingResult, list ->
//                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
//                           val filteredList= list.filter { it.products.contains(productId) }
//                            if(filteredList.isNotEmpty()) {
//                                verifySubPurchase(productId, purchases = filteredList[0],
//                                    object : VerificationListener {
//                                        override fun onPurchaseVerified(purchases: Purchase) {
//                                            inAppSubscriptionListener.onSuccess(
//                                                productId,
//                                                basePlanId,
//                                                purchases
//                                            )
//                                        }
//
//                                        override fun onPurchaseFailed() {
//                                            inAppSubscriptionListener.onError("Something went wrong, please try again...")
//                                        }
//                                    })
//                            }else{
//                                inAppSubscriptionListener.onError("Something went wrong, please try again...")
//                            }
//                        }else if(billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
//                            inAppSubscriptionListener.onItemAlreadyOwned(productId)
//                        }else if(billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED){
//                            inAppSubscriptionListener.onUserCancels()
//                        }
//                    }.build()
//                establishConnection(productId)
//            }
//
//        })
//
//    }

fun establishConnection(productId: String) {
billingClient.startConnection(object : BillingClientStateListener {
override fun onBillingSetupFinished(billingResult: BillingResult) {
if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
showProducts(productId)
}
}

override fun onBillingServiceDisconnected() {
establishConnection(productId)
}
})
}

fun showProducts(productId: String) {
val productList = ImmutableList.of(
QueryProductDetailsParams.Product.newBuilder()
.setProductId(productId)
.setProductType(BillingClient.ProductType.SUBS)
.build(),  //Product 2
)
val params = QueryProductDetailsParams.newBuilder()
.setProductList(productList)
.build()
billingClient.queryProductDetailsAsync(
params
) { billingResult: BillingResult?, prodDetailsList: List<ProductDetails?>? ->
prodDetailsList?.get(0)?.let { launchPurchaseFlow(it) }
}

}
private fun launchPurchaseFlow(productDetails: ProductDetails) {
assert(productDetails.subscriptionOfferDetails != null)
val productDetailsParamsList = ImmutableList.of(
ProductDetailsParams.newBuilder()
.setProductDetails(productDetails)
.setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
.build()
)
val billingFlowParams = BillingFlowParams.newBuilder()
.setProductDetailsParamsList(productDetailsParamsList)
.build()
billingClient.launchBillingFlow(context as Activity, billingFlowParams)
}

fun checkSubscription(productId: String,checkSubscriptionListener: CheckSubscriptionListener){
billingClient = BillingClient.newBuilder(context)
.enablePendingPurchases()
.setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
.build()
val finalBillingClient = billingClient
billingClient.startConnection(object : BillingClientStateListener {
override fun onBillingServiceDisconnected() {}
override fun onBillingSetupFinished(billingResult: BillingResult) {
if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
finalBillingClient.queryPurchasesAsync(
QueryPurchasesParams.newBuilder()
.setProductType(BillingClient.ProductType.SUBS).build()
) { billingResult1: BillingResult, list: List<Purchase> ->
if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
if(list.any { it.purchaseState == BillingClient.BillingResponseCode.OK && it.products.contains(
productId
)
}){
checkSubscriptionListener.alreadyHaveSubscription(productId)
}else{
checkSubscriptionListener.noSubscription(productId)
}


}
}
}
}
})
}

fun verifySubPurchase(
productId: String,
purchases: Purchase,
verificationListener: VerificationListener
) {
val acknowledgePurchaseParams = AcknowledgePurchaseParams
.newBuilder()
.setPurchaseToken(purchases.purchaseToken)
.build()
billingClient.acknowledgePurchase(
acknowledgePurchaseParams
) { billingResult: BillingResult ->
if (billingResult.responseCode == BillingResponseCode.OK) {
val p=purchases
if(p.products.contains(productId) && p.purchaseState==PurchaseState.PURCHASED)
verificationListener.onPurchaseVerified(p)
else
verificationListener.onPurchaseFailed()
}else{
verificationListener.onPurchaseFailed()
}
}
}
interface  CheckSubscriptionListener{

fun alreadyHaveSubscription(productId: String)

fun noSubscription(productId: String)
}
interface VerificationListener{
fun onPurchaseVerified(purchases: Purchase)
fun onPurchaseFailed()
}

}
 **/

interface InAppSubscriptionListener {
    fun onSuccess(purchases: Purchase)
    fun onError(msg: String)

}


class SubscriptionUtil(var context: Context, var productId: String,planId: String,var listener: InAppSubscriptionListener) {
    interface VerificationListener {
        fun onPurchaseVerified(purchases: Purchase)
        fun onPurchaseFailed()
    }

    var billingClient: BillingClient? = null


    var planList: MutableList<ProductDetails>? = mutableListOf()


    var purchasesUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingResponseCode.OK) {
            purchases?.map {
                acknowledgeAndVerifyPurchase(it)
            }
        } else {
          listener.onError(getResponseDesc(billingResult.responseCode))
        }
    }
    var productDetailsResponseListener =
        ProductDetailsResponseListener { billingResult, productDetails ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                planList?.clear()
                planList?.addAll(productDetails)
                subscribe(planId)
            }
        }

    init {
        establishConnection()
    }

   private fun establishConnection() {
        billingClient = BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(purchasesUpdateListener)
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    fetchPlansDetails()
                }else{
                    listener.onError(getResponseDesc(billingResult.responseCode))
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection()
            }
        })
    }


    fun fetchPlansDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        billingClient?.queryProductDetailsAsync(params, productDetailsResponseListener)
    }

    fun subscribe(planId: String) {
        if (planList?.isNotEmpty() == true) {
            val productDetailsParamsList = mutableListOf<ProductDetailsParams>()
            val filteredList = mutableListOf<ProductDetails>()
            planList?.map { product ->
                if (product.subscriptionOfferDetails?.any {
                        it.basePlanId.equals(
                            planId,
                            true
                        )
                    } == true) {
                    filteredList.add(product)
                }
            }
            filteredList.map {
                val offerToken =
                    it.subscriptionOfferDetails?.filter { it.basePlanId.equals(planId, true) }
                        ?.get(0)?.offerToken ?: ""
                productDetailsParamsList.add(
                    ProductDetailsParams.newBuilder()
                        .setProductDetails(it)
                        .setOfferToken(offerToken)
                        .build()
                )
            }
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient?.launchBillingFlow(context as Activity, billingFlowParams)
        }else listener.onError("item not found.")
    }


    private fun acknowledgeAndVerifyPurchase(purchase: Purchase) {
        billingClient?.acknowledgePurchase(
            AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                 listener.onSuccess(purchase)
            } else {
                listener.onError(getResponseDesc(billingResult.responseCode))
            }
        }

    }

    fun getResponseDesc(code: Int): String {
        return when (code) {
            BillingResponseCode.BILLING_UNAVAILABLE -> "Billing API version is not supported for the type requested."
            BillingResponseCode.DEVELOPER_ERROR -> "Invalid arguments provided to the API."
            BillingResponseCode.ERROR -> "Fatal error during the API action."
            BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Requested feature is not supported by Play Store on the current device."
            BillingResponseCode.ITEM_ALREADY_OWNED -> "Failure to purchase since item is already owned."
            BillingResponseCode.ITEM_NOT_OWNED -> "Failure to consume since item is not owned."
            BillingResponseCode.ITEM_UNAVAILABLE -> "Requested product is not available for purchase."
            BillingResponseCode.OK -> "Success."
            BillingResponseCode.SERVICE_DISCONNECTED -> "Play Store service is not connected now-potentially transient state."
//            BillingResponseCode.SERVICE_TIMEOUT -> "The request has reached the maximum timeout before Google Play responds."
            BillingResponseCode.SERVICE_UNAVAILABLE -> "Network connection is down."
            BillingResponseCode.USER_CANCELED -> "User pressed back or canceled a dialog."
            else -> "Unknown error"
        }
    }

}







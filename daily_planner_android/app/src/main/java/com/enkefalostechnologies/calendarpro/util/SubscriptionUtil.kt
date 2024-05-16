package com.enkefalostechnologies.calendarpro.util


import android.app.Activity
import android.content.Context
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import java.util.Calendar
import java.util.Date



interface InAppSubscriptionListener {
    fun onConnected(){}
    fun onDisconnected(){}
    fun onSuccess(purchases: Purchase){}
    fun onError(msg: String){}
    fun onProductDetailsFetched(productId: String, productDetails: MutableList<ProductDetails>){}


}


class SubscriptionUtil(var context: Context) {

    var billingClient: BillingClient? = null

  private  fun establishConnection(listener: InAppSubscriptionListener){
        billingClient = BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(PurchasesUpdatedListener { billingResult, purchases ->
               when(billingResult.responseCode){
                   BillingResponseCode.OK->{
                       purchases?.map {
                           acknowledgeAndVerifyPurchase(it,listener)
                       }
                   }
                   else ->
                    listener.onError(getResponseDesc(billingResult.responseCode))
                }
            })
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                listener.onDisconnected()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when(billingResult.responseCode){
                    BillingResponseCode.OK->listener.onConnected()
                    else->listener.onError(getResponseDesc(billingResult.responseCode))
                }
            }
        })
    }

     fun fetchProductDetails(productId: String,listener: InAppSubscriptionListener){
         establishConnection(object :InAppSubscriptionListener{
             override fun onConnected() {
                 super.onConnected()
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
                 billingClient?.queryProductDetailsAsync(params,ProductDetailsResponseListener { billingResult, productDetails ->
                     when(billingResult.responseCode){
                         BillingResponseCode.OK->listener.onProductDetailsFetched(productId,productDetails)
                         else->listener.onError(getResponseDesc(billingResult.responseCode))
                     }
                 })
             }

             override fun onSuccess(purchases: Purchase) {
                 super.onSuccess(purchases)
                 listener.onSuccess(purchases)
             }

             override fun onProductDetailsFetched(
                 productId: String,
                 productDetails: MutableList<ProductDetails>
             ) {
                 super.onProductDetailsFetched(productId, productDetails)
                 listener.onProductDetailsFetched(productId,productDetails)
             }

             override fun onError(msg: String) {
                 super.onError(msg)
                 listener.onError(msg)
             }

             override fun onDisconnected() {
                 super.onDisconnected()
                 listener.onDisconnected()
             }
         })
    }



    fun purchase(productId: String,planId:String,listener: InAppSubscriptionListener){
        fetchProductDetails(productId =productId,object:InAppSubscriptionListener{
            override fun onProductDetailsFetched(
                productId: String,
                productDetails: MutableList<ProductDetails>
            ) {
                super.onProductDetailsFetched(productId, productDetails)
                val productDetailsParamsList = mutableListOf<ProductDetailsParams>()
                val filteredList = mutableListOf<ProductDetails>()
                productDetails?.map { product ->
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
            }

            override fun onError(msg: String) {
                super.onError(msg)
                listener.onError(msg)
            }

            override fun onConnected() {
                super.onConnected()
                listener.onConnected()
            }

            override fun onDisconnected() {
                super.onDisconnected()
                listener.onDisconnected()
            }

            override fun onSuccess(purchases: Purchase) {
                super.onSuccess(purchases)
                listener.onSuccess(purchases)
            }
        })
    }




    private fun acknowledgeAndVerifyPurchase(purchase: Purchase,listener: InAppSubscriptionListener) {
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

class SubscriptionCheckUtil(var context: Context, var listener:SubscriptionStateListener) {
    var billingClient: BillingClient? = null

    init {
        establishConnectionForCheckingSubscription()
    }


    fun establishConnectionForCheckingSubscription() {
        billingClient = BillingClient
            .newBuilder(context)
            .setListener{billingResult, purchases ->

            }
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    val params = QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                    billingClient?.queryPurchasesAsync(params.build()){billingResult,purcases->
                        if (billingResult.responseCode == BillingResponseCode.OK) {
                            listener.onPurchaseHistoryFetched(purcases)
                        }else listener.onError()
                    }

                }else listener.onError()

        }

                override fun onBillingServiceDisconnected() {
            establishConnectionForCheckingSubscription()
        }
    })
}

    interface  SubscriptionStateListener{
       fun onPurchaseHistoryFetched(purchases: MutableList<Purchase>)
       fun onError()
    }
}

object SubscriptionHelper {
    fun Long.getNextRenewingDate(subscriptionType: SubscriptionType): Date {
        val calendar = Calendar.getInstance()
        calendar.setTime(Date(this))
        when (subscriptionType) {
            SubscriptionType.MONTHLY -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }

            SubscriptionType.YEARLY -> {
                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }

            else -> {}
        }
        return calendar.time
    }
}









package com.dream.friend.util

//import com.anjlab.android.iab.v3.BillingProcessor
//import com.anjlab.android.iab.v3.PurchaseInfo

import android.app.Activity
import android.content.Context
import android.provider.Settings.Global
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class InAppPurchaseUtil(
    var context: Context
) {
    lateinit var billingClient: BillingClient

    fun purchase(productId: String, listener:InAppPurchaseListener) {
       billingClient = BillingClient.newBuilder(context)
            .setListener(object:PurchasesUpdatedListener{
                override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                        val filteredList= list.filter { it.products.contains(productId) }
                        if(filteredList.isNotEmpty()) {
                            verifyPurchase(productId, purchases = filteredList[0],
                                object : SubscriptionUtil.VerificationListener {
                                    override fun onPurchaseVerified(purchases: Purchase) {
                                        listener.onSuccess(planId =productId,purchases)
                                    }

                                    override fun onPurchaseFailed() {
                                        listener.onError("Something went wrong, please try again...")
                                    }
                                })
                        }else{
                            listener.onError("Something went wrong, please try again...")
                        }
                    }else if(billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED){
                        listener.onCancel()
                    }
                }
            })
            .enablePendingPurchases()
            .build()
        startConnection(productId)
    }

    fun verifyPurchase(
        productId: String,
        purchases: Purchase,
        verificationListener: SubscriptionUtil.VerificationListener
    ) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                GlobalScope.launch {
                    billingClient.consumePurchase(ConsumeParams.newBuilder().setPurchaseToken(purchases.purchaseToken).build())
                }
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

    fun startConnection(productId: String){
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    val queryProductDetailsParams =
                        QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(productId)
                                        .setProductType(BillingClient.ProductType.INAPP)
                                        .build()
                                )
                            )
                            .build()

                    billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                        productDetailsList[0]?.let { launchPurchaseFlow(it) }
                    }

                }
            }
            override fun onBillingServiceDisconnected() {
                startConnection(productId)
            }
        })
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        assert(productDetails.oneTimePurchaseOfferDetails != null)
        val productDetailsParamsList = ImmutableList.of(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(context as Activity, billingFlowParams)
    }
}
interface InAppPurchaseListener{
   fun onSuccess(planId:String,purchases: Purchase)
   fun onError(msg:String)
   fun onCancel()
}





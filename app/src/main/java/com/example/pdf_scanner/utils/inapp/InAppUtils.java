package com.example.pdf_scanner.utils.inapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

import static com.example.pdf_scanner.ConstantsKt.BASE64_GOOGLE_PLAY_KEY;


public class InAppUtils {

    private static final String TAG = "InAppUtil";
    public static BillingClient billingClient, billingClientBuy, billingClientBuyOneTime;
    public static List<String> skusList;
    public static HashMap<String, SkuDetails> mapSkus;
    private static PurchasesUpdatedListener listener;
    private static ConnectSuccessListener connectSuccessListener;
    private static boolean isBillingClientSuccess = false;
    private static boolean isBillingClientBuyOneTimeSuccess = false;
    private static boolean isBillingClientBuySuccess = false;
    private static List<Purchase> purchaseSupscriptionList;

    public static void configPurchase(final Context context, ConnectSuccessListener connectSuccessListener, List<String> sku) {
        InAppUtils.connectSuccessListener = connectSuccessListener;
        skusList = sku;
        mapSkus = new HashMap<>();
        if (!isBillingClientSuccess || !isBillingClientBuyOneTimeSuccess || !isBillingClientBuySuccess) {
            isBillingClientBuyOneTimeSuccess = false;
            isBillingClientSuccess = false;
            isBillingClientBuySuccess = false;
            configBillingClient(context, sku);
//            configBillingClientOneTime(context, sku);
//            configBillingClientBuy(context, sku);
        } else {
            callSuccess();
        }
    }

    /**
     * @param context
     * @param sku     dùng cho inapp mua nhiều lần
     */
    private static void configBillingClientBuy(Context context, List<String> sku) {
        billingClientBuy = BillingClient.newBuilder(context).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                Log.e(TAG, "onPurchasesUpdated: 1");
                if (listener != null) {
                    listener.onPurchasesUpdated(billingResult, list);
                }
                if (list != null)
                    for (Purchase purchase : list) {
                        Log.e(TAG, "onPurchasesUpdated: " + purchase.getPurchaseToken());
                        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
                            return;
                        }
                        handlePurchaseBuy(purchase);
                    }
            }
        }).build();
        billingClientBuy.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.e(TAG, "onBillingSetupFinished: billingClientBuy" + billingResult.getResponseCode());

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (billingClientBuy.isReady()) {
                        loadAllSkulBuy(context);
                        Log.e(TAG, "onBillingSetupFinishedbillingClientBuy: ");
                        Purchase.PurchasesResult result =
                                billingClientBuy.queryPurchases(BillingClient.SkuType.INAPP);
                        isBillingClientBuySuccess = true;
//                        if (result.getPurchasesList().size() > 0) {
//                            isRegisted = true;
//                        }
                        callSuccess();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectSuccessListener.disConnected();
            }
        });
    }

    /**
     * @param context
     * @param sku     dùng cho inapp mua 1 lần
     */
    private static void configBillingClientOneTime(Context context, List<String> sku) {
        billingClientBuyOneTime = BillingClient.newBuilder(context).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                Log.e(TAG, "onPurchasesUpdated: 1");
                if (list != null) {
                    for (Purchase purchase : list) {
                        Log.e(TAG, "onPurchasesUpdated: " + purchase.getPurchaseToken());
                        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
                            return;
                        }
                        handlePurchaseBuyOneTime(purchase);
                    }
                }
                if (listener != null) {
                    listener.onPurchasesUpdated(billingResult, list);
                }
            }
        }).build();
        billingClientBuyOneTime.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.e(TAG, "onBillingSetupFinished: billingClientBuyOneTime" + billingResult.getResponseCode());

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (billingClientBuyOneTime.isReady()) {
                        loadAllSkulBuyOneTime(context);
                        Log.e(TAG, "onBillingSetupFinished billingClientBuyOneTime: ");
                        Purchase.PurchasesResult result =
                                billingClientBuyOneTime.queryPurchases(BillingClient.SkuType.INAPP);
                        isBillingClientBuyOneTimeSuccess = true;
                        purchaseSupscriptionList = result.getPurchasesList();
                        callSuccess();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectSuccessListener.disConnected();
            }
        });
    }

    /**
     * @param context
     * @param sku     dùng cho inapp subscription
     */

    private static void configBillingClient(Context context, List<String> sku) {
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null) {
                    purchaseSupscriptionList = list;
                    for (Purchase purchase : list) {
                        Log.e(TAG, "onPurchasesUpdated: " + purchase.getPurchaseToken());

                        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                            Log.e(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
                            return;
                        }

                        handlePurchase(purchase);

                        if (listener != null) {
                            listener.onPurchasesUpdated(billingResult, list);
                        }
                    }
                }
            }
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.e(TAG, "onBillingSetupFinished: billingClient" + billingResult.getResponseCode());
                Log.e(TAG, "onBillingSetupFinished: billingClient" + billingClient.isReady());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (billingClient.isReady()) {
                        loadAllSkul(context);
                        Purchase.PurchasesResult result =
                                billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                        Log.e(TAG, "onBillingSetupFinished billingClient: " + result.getPurchasesList().size());
                        isBillingClientSuccess = true;
                        purchaseSupscriptionList = result.getPurchasesList();
                        callSuccess();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectSuccessListener.disConnected();
            }
        });
    }

    private static void callSuccess() {
        if (isBillingClientSuccess
                && connectSuccessListener != null) {
            connectSuccessListener.onSuccess();
        }
    }

    private static void handlePurchaseBuy(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
        billingClientBuy.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "onConsumeResponse: " + "consumeAsync");
                }
            }
        });
    }

    public static void setPurchaseUpdatedListener(PurchasesUpdatedListener l) {
        listener = l;
    }

    private static void loadAllSkul(Context context) {
        if (billingClient.isReady()) {
            Log.e(TAG, "loadAllSkul: " + skusList);
            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(skusList).setType(BillingClient.SkuType.SUBS).build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                        for (SkuDetails sku : list) {
                            mapSkus.put(sku.getSku(), sku);
                        }
                    Log.e(TAG, "onSkuDetailsResponse: " + list.size());
                }
            });
        }
    }

    private static void loadAllSkulBuy(Context context) {
        if (billingClientBuy.isReady()) {
            Log.e(TAG, "loadAllSkulBuy: " + skusList);
            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(skusList).setType(BillingClient.SkuType.INAPP).build();
            billingClientBuy.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                        for (SkuDetails sku : list) {
                            mapSkus.put(sku.getSku(), sku);
                        }
                    Log.e(TAG, "onSkuDetailsResponse: " + list.size());
                }
            });
        }
    }

    private static void loadAllSkulBuyOneTime(Context context) {
        if (billingClientBuyOneTime.isReady()) {
            Log.e(TAG, "loadAllSkulBuyOneTime: " + skusList);
            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(skusList).setType(BillingClient.SkuType.INAPP).build();
            billingClientBuyOneTime.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                        for (SkuDetails sku : list) {
                            mapSkus.put(sku.getSku(), sku);
                        }
                    Log.e(TAG, "onSkuDetailsResponse: " + list.size());
                }
            });
        }
    }

    private static void handlePurchase(Purchase purchase) {
        Timber.e("handlePurchase");
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.acknowledgePurchase(params, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "onAcknowledgePurchaseResponse: handlePurchase" + billingResult.getDebugMessage() + "   " + billingResult.getResponseCode());
                }
            }
        });

    }

    private static void handlePurchaseBuyOneTime(Purchase purchase) {
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClientBuyOneTime.acknowledgePurchase(params, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "onAcknowledgePurchaseResponse: handlePurchase" + billingResult.getDebugMessage() + "   " + billingResult.getResponseCode());
                }
            }
        });

    }

    private static boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(BASE64_GOOGLE_PLAY_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

    public interface ConnectSuccessListener {
        void onSuccess();

        void disConnected();
    }

    public static void buyOneTime(Activity activity, String sku) {
        BillingFlowParams billingFlowParams;
        if (checkValidSku(sku)) {
            billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(InAppUtils.mapSkus.get(sku)).build();
            billingClientBuyOneTime.launchBillingFlow(activity, billingFlowParams);
        }
    }

    private static boolean checkValidSku(String sku) {
        return !InAppUtils.mapSkus.isEmpty()
                && InAppUtils.mapSkus.get(sku) != null
                && !InAppUtils.mapSkus.isEmpty()
                && InAppUtils.mapSkus.get(sku) != null;
    }

    public static void subscription(Activity activity, String sku) {
        BillingFlowParams billingFlowParams;
        Log.d(TAG, "Subscription: " + sku);
        if (checkValidSku(sku)) {
            billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(InAppUtils.mapSkus.get(sku)).build();
            billingClient.launchBillingFlow(activity, billingFlowParams);
        }
    }

    public static void buy(Activity activity, String sku) {
        BillingFlowParams billingFlowParams;
        if (checkValidSku(sku)) {
            billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(InAppUtils.mapSkus.get(sku)).build();
            billingClientBuy.launchBillingFlow(activity, billingFlowParams);
        }
    }

    public static boolean isSubscription(String sku) {
        if (purchaseSupscriptionList == null) {
            return false;
        }

        boolean isSub = false;
        for (Purchase p : purchaseSupscriptionList) {
            if (p.getSku().equalsIgnoreCase(sku)) {
                isSub = true;
            }
        }
        return isSub;
    }
}
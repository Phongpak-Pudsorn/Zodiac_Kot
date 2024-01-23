package com.smileapp.zodiac.commonclass;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.smileapp.zodiac.R;
import com.starvision.AppPreferences;
import com.starvision.api.LoadData;
import com.starvision.bannersdk.BannerAds;
import com.starvision.bannersdk.MobileAdvertising;
import com.starvision.bannersdk.NativeBannerAds;
import com.starvision.bannersdk.PopupAds;

import java.util.List;

/**
 * Created by Muey on 10/31/2017.
 */

public class BannerShow {
    String TAG = this.getClass().getSimpleName();
    String strID = "";
    AdRequest adRequest;
    Activity activity;
    private static boolean checkOnAdClosed = false;
    public static onAdClosed mOnAdClosed = null;

    public interface onAdClosed {
        public void onAdClosed();
    }

    LoadData loadDatabanner;
    AppPreferences appPreferences;
    public BannerShow(Activity activity, String strUUID) {
        loadDatabanner = new LoadData(activity);
//        loadDatabanner.loadAdsData();
        appPreferences = AppPreferences.INSTANCE;
        if ((Boolean) appPreferences.getPreferences(activity,AppPreferences.KEY_CHECK_LOAD_ADS_API,true)){
            loadDatabanner.loadAdsData(true);
            appPreferences.setPreferences(activity,AppPreferences.KEY_CHECK_LOAD_ADS_API,false);
        }
        this.strID = strUUID;
        this.activity = activity;
            adRequest = new AdRequest.Builder()
                    .build();
//        List<String> testDeviceIds = Arrays.asList("E9152FC60BE0FCABA12CAA834C074312","4B0CA66D064D1D6C02924C14267C1998","CC55566542CF1D525880E2ECA81A3360",
//                "A92CA39E8FF67563722008636F808F6A,313432522EB1A50E5CAAFA10B671EAE3");
//        RequestConfiguration configuration =
//                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);
    }

    PopupAds popupAdstar;
    InterstitialAd popupAdMob;
    InterstitialAd popupVideoAdMob;
    AppOpenAd popupAdMobAppOpen;

     //0 เต็มจอ 1 Appopen 2 VDO
    public void loadPopupBanner(int type) {
//        Const.INSTANCE.log(TAG, "loadPopupBanner");
        getShowPopupAdMob(type);
        popupAdstar = new PopupAds(activity);

        popupAdstar.setPopupAdsListener(new PopupAds.PopupAdsListener() {
            @Override
            public void onSuccess(@NonNull String strJson) {
// Log the adapter patch version to 3 digits to represent the x.x.x.x versioning
// used by adapters.
//                Const.INSTANCE.log("json load", strJson);
                popupAdstar.show();
            }

            @Override
            public void onFailed(@NonNull String strErrorMessage) {
// Log the adapter patch version to 3 digits to represent the x.x.x.x versioning
// used by adapters.
//                Const.INSTANCE.log(TAG, "onFailed " + strErrorMessage);
//                if (popupAdMob != null && type ==0) {
//                    popupAdMob.show(activity);
////                }else if(popupVideoAdMob != null && type ==2){
////                    popupVideoAdMob.show(activity);
//                }else if (popupAdMobAppOpen!=null){
//                    popupAdMobAppOpen.show(activity);
//                }
//                else {
//                    if (mOnAdClosed != null && checkOnAdClosed == false) {
//                        Const.INSTANCE.log(TAG, "onFailed onClose");
//                        checkOnAdClosed = true;
//                        mOnAdClosed.onAdClosed();
//                    }
//                }
            }

            @Override
            public void onClose() {
//                Const.INSTANCE.log(TAG, "popupAdstar onClose");
                if (mOnAdClosed != null && !checkOnAdClosed) {
                    checkOnAdClosed = true;
                    mOnAdClosed.onAdClosed();
                }
            }

            @Override
            public void onBannerClick(@NonNull String strJson) {
//                Const.INSTANCE.log(TAG, "onBannerClick");
            }

            @Override
            public void onOtherAds(@NonNull String strAdsvertisingName) {
//                Const.INSTANCE.log(TAG, "onOtherAds " + strAdsvertisingName);
                if (strAdsvertisingName.equals("admob") || strAdsvertisingName.equals("facebook")) {
                    if (popupAdMob != null && type == 0) {
                        popupAdMob.show(activity);
                    }else if(popupVideoAdMob != null && type == 2){
                        popupVideoAdMob.show(activity);
                    } else if (popupAdMobAppOpen != null){
                        popupAdMobAppOpen.show(activity);
                    }else {
                        if (mOnAdClosed != null && !checkOnAdClosed) {
                            checkOnAdClosed = true;
                            mOnAdClosed.onAdClosed();
                        }
                    }
                } else {
                    if (mOnAdClosed != null && !checkOnAdClosed) {
//                        Const.INSTANCE.log(TAG, "popupAdstar onClose");
                        checkOnAdClosed = true;
                        mOnAdClosed.onAdClosed();
                    }
                }
            }
        });
//        Const.INSTANCE.log(TAG, "loadPopupBanner : popupAdMobAppOpen B: "+popupAdMobAppOpen);
//        Const.INSTANCE.log(TAG, "loadPopupBanner after push activity to PopupAds");
//        popupAdstar.loadAds(strPage, strID);
    }

    int checkErrorPopup = 0;
    String page = "";

    public void setShowPopupAdMob(final String page) {
        this.page = page;
    }

    //ad test "ca-app-pub-3940256099942544/1033173712"////activity.getString(R.string.interstitial)

    public void getShowPopupAdMob(int type) {
//        activity.getString(R.string.KEY_ADMOB_InterstitialBanner test ca-app-pub-3940256099942544/1033173712
        if (type == 0) {
//            Const.INSTANCE.log(TAG, "getShowPopupAdMob : type 0 ");
            InterstitialAd.load(activity, activity.getString(R.string.KEY_ADMOB_InterstitialBanner), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
//                    Const.INSTANCE.log(TAG, "ad is loaded. :"+interstitialAd);
                    popupAdMob = interstitialAd;
//                    Const.INSTANCE.log(TAG, "ad is loaded. :"+interstitialAd);
                    popupAdMob.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
//                            Const.INSTANCE.log(TAG, "The ad was dismissed.");
                            if (mOnAdClosed != null && checkOnAdClosed == false) {
                                checkOnAdClosed = true;
                                mOnAdClosed.onAdClosed();
                            } else {
                                if (checkOnAdClosed == false) {
                                    checkOnAdClosed = true;
                                    mOnAdClosed.onAdClosed();
                                }
                            }
//                            loadPopupBanner(type);
                            getShowPopupAdMob(type);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
//                            Const.INSTANCE.log(TAG, "The ad failed to show.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            popupAdMob = null;
//                            Const.INSTANCE.log(TAG, "The ad was shown.");
                        }
                    });
//                    popupAdMob.show(activity);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    // Const.INSTANCE.log(TAG, loadAdError.getMessage());
                    popupAdMob = null;
                    if (checkErrorPopup < 3) {
                        getShowPopupAdMob(type);
                    } else {
                        if (mOnAdClosed != null && checkOnAdClosed == false) {
//                        Const.INSTANCE.log("showPopupBanner", "popupAdMob onAdFailedToLoad : " + errorCode + " :");
                            checkOnAdClosed = true;
                            mOnAdClosed.onAdClosed();
                        }
                    }
                    checkErrorPopup++;
                }
            });
        }
        else if(type == 1) {
//            Const.INSTANCE.log(TAG, "getShowPopupAdMob : type 1 ");
            AppOpenAd.load(activity, activity.getString(R.string.KEY_ADMOB_AppOpen), adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
//                    Const.INSTANCE.log(TAG, "app open onAdFailedToLoad : "+loadAdError.getMessage());
                    popupAdMobAppOpen = null;
                    if (checkErrorPopup < 3) {
                        getShowPopupAdMob(type);
                    } else {
                        if (mOnAdClosed != null && checkOnAdClosed == false) {
//                            Const.INSTANCE.log("showPopupBanner", "popupAdMob onAdFailedToLoad : " + "errorCode" + " :");
                            checkOnAdClosed = true;
                            mOnAdClosed.onAdClosed();
                        }
                    }
                    checkErrorPopup++;
                }

                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
//                    Const.INSTANCE.log(TAG, "Ad was loaded.");
//                    Const.INSTANCE.log(TAG, "appOpenAd :"+appOpenAd);
                    popupAdMobAppOpen = appOpenAd;
//                    Const.INSTANCE.log(TAG, "popupAdMobAppOpen :"+popupAdMobAppOpen);
                    popupAdMobAppOpen.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
//                            Const.INSTANCE.log(TAG, "onAdClicked");
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
//                            Const.INSTANCE.log(TAG, "onAdDismissedFullScreenContent");
                            if (mOnAdClosed != null && checkOnAdClosed == false) {
                                checkOnAdClosed = true;
                                mOnAdClosed.onAdClosed();
                            } else {
                                if (checkOnAdClosed == false) {
                                    checkOnAdClosed = true;
                                    mOnAdClosed.onAdClosed();
                                }
                            }
                            getShowPopupAdMob(type);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
//                            Const.INSTANCE.log(TAG, "onAdShowedFullScreenContent");
                            popupAdMobAppOpen = null;
                        }
                    });
                }
            });
        }
//        else {
////                Const.INSTANCE.log(TAG, "getShowPopupAdMob : type 2 ");
//                InterstitialAd.load(activity, activity.getString(R.string.admob_ad_unit_pop), adRequest, new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
////                        Const.INSTANCE.log(TAG, "ad is loaded. :"+interstitialAd);
//                        popupVideoAdMob = interstitialAd;
////                        Const.INSTANCE.log(TAG, "ad is loaded. :"+interstitialAd);
//                        popupVideoAdMob.setFullScreenContentCallback(new FullScreenContentCallback() {
//                            @Override
//                            public void onAdDismissedFullScreenContent() {
//                                // Called when fullscreen content is dismissed.
////                                Const.INSTANCE.log(TAG, "The ad was dismissed.");
//                                if (mOnAdClosed != null && checkOnAdClosed == false) {
//                                    checkOnAdClosed = true;
//                                    mOnAdClosed.onAdClosed();
//                                } else {
//                                    if (checkOnAdClosed == false) {
//                                        checkOnAdClosed = true;
//                                        mOnAdClosed.onAdClosed();
//                                    }
//                                }
//                                getShowPopupAdMob(type);
//                            }
//
//                            @Override
//                            public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                // Called when fullscreen content failed to show.
////                                Const.INSTANCE.log(TAG, "The ad failed to show.");
//                            }
//
//                            @Override
//                            public void onAdShowedFullScreenContent() {
//                                // Called when fullscreen content is shown.
//                                // Make sure to set your reference to null so you don't
//                                // show it a second time.
//                                popupVideoAdMob = null;
////                                Const.INSTANCE.log(TAG, "The ad was shown.");
//                            }
//                        });
////                    popupAdMob.show(activity);
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        // Const.INSTANCE.log(TAG, loadAdError.getMessage());
//                        popupVideoAdMob = null;
//                        if (checkErrorPopup < 3) {
//                            getShowPopupAdMob(type);
//                        } else {
//                            if (mOnAdClosed != null && checkOnAdClosed == false) {
////                        Const.INSTANCE.log("showPopupBanner", "popupAdMob onAdFailedToLoad : " + errorCode + " :");
//                                checkOnAdClosed = true;
//                                mOnAdClosed.onAdClosed();
//                            }
//                        }
//                        checkErrorPopup++;
//                    }
//                });
//        }
    }

    public static int numberShowPopup = 3;
    public boolean starPopup = false;

    public void showPopupBanner(int intPage, onAdClosed onAdClosed) {
//        Const.INSTANCE.log("popupAdMob", ""+intPage);
        String strPage = intPage + "";
        checkOnAdClosed = false;
        mOnAdClosed = onAdClosed;
        if (numberShowPopup >=3) {
//            Const.INSTANCE.log("showPopupBanner", ">4");
            if (!starPopup) {
                starPopup = true;
                popupAdstar.loadAds(String.valueOf(intPage),strID);
                numberShowPopup = 0;
            } else {
                popupAdstar.loadAds(strPage,strID);
                numberShowPopup = 0;
            }
        } else {
            if (mOnAdClosed != null && checkOnAdClosed == false) {
                checkOnAdClosed = true;
                mOnAdClosed.onAdClosed();
//                Const.INSTANCE.log("showPopupBanner", "<4");
            }
        }
        numberShowPopup++;
    }

    public void showPopupBannerNow(int intPage, onAdClosed onAdClosed) {
//        Const.INSTANCE.log(TAG, "showPopupBannerNow");
        String strPage = intPage + "";
        checkOnAdClosed = false;
        mOnAdClosed = onAdClosed;
        popupAdstar.loadAds(strPage,strID);
    }

    public void showPopupAppOpenBannerNow(onAdClosed onAdClosed) {
        checkOnAdClosed = false;
        mOnAdClosed = onAdClosed;
        popupAdstar.loadAds(null,strID);
    }

    //----------------------------- SMALL --------------------------------

    BannerAds bannerAds;
    AdView adView;

    public void getShowBannerSmall(int intPage) {
        String strPage = intPage + "";
        bannerAds = activity.findViewById(R.id.bannerAds);
      //  Const.INSTANCE.log("bannerAds", "getShowBannerSmall " + activity);

//        bannerAds.setDebug(false);
        bannerAds.setBannerAdsListener(new BannerAds.BannerAdsListener(){
            @Override
            public void onSuccess(String strJson) {
                // TODO Auto-generated method stub
                bannerAds.setVisibility(View.VISIBLE);
//                Const.INSTANCE.log("bannerAds", "onSuccess " +bannerAds.getVisibility());
            }

            @Override
            public void onFailed(String strErrorMessage) {

//                Const.INSTANCE.log("bannerAds", "onFailed " + strErrorMessage);
                bannerAds.setVisibility(View.GONE);
            }

            @Override
            public void onBannerClick(String strJson) {

//                Const.INSTANCE.log("bannerAds", "onBannerClick " + strJson);
            }

            @Override
            public void onOtherAds(String strAdsvertisingName) {
//                Const.INSTANCE.log("bannerAds", "onOtherAds " + strAdsvertisingName);
//                if (strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_ADMOB()) || strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_FACEBOOK())) {
                if (strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_ADMOB) || strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_FACEBOOK)) {
                    getShowBannerAdMob(activity);
                }
            }
        });
        bannerAds.loadAds(strPage,strID);
    }


    public void getShowBannerAdMob(final Activity activity) {
        try {
            adView = activity.findViewById(R.id.adView);
            adView.setVisibility(View.VISIBLE);
            adView.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    //Const.INSTANCE.log("adView", "Loaded");
                    adView.setVisibility(View.VISIBLE);
                }

                public void onAdOpened() {

                }

                public void onAdFailedToLoad(int errorCode) {
                    adView.setVisibility(View.GONE);
//                    Const.INSTANCE.log("adView", "FailedToLoad:" + errorCode);
                }

                public void onAdClosed() {
                }

                public void onAdLeftApplication() {
                }
            });
            adView.loadAd(adRequest);
        } catch (Exception e) {
            adView.setVisibility(View.GONE);
        }
    }

    //----------------------------- NATIVE --------------------------------

    AdLoader.Builder builder;
    AdLoader adLoader;

    public BannerShow(final Activity activity, String strUUID, String ADMOB_UNIT_ID) {
        this.strID = strUUID;
        this.activity = activity;
        adRequest = new AdRequest.Builder()
//                .addTestDevice("3DE0BAB737D5B1D162FB8523876B7638")
//                .addTestDevice("B113D4E1A32002F9FFED1B6BA9745313")
//                .addTestDevice("F0E1C4555B775E016C41254AF9698A8A")
//                .addTestDevice("F22734974E29A11A332805766E133DE1")
//                .addTestDevice("E85BBCF4EEBEEC6614520ACA99A22066")
//                .addTestDevice("37833A3DAFF5E893599D3234AABAE7F3")//AAA
                .build();
        builder = new AdLoader.Builder(activity, ADMOB_UNIT_ID);

    }
    NativeBannerAds nativeBannerAds;
    NativeAdView nativeAdmobAds;

    BannerAds bannerAds2;
    AdView adView2;

    //  size 0 small / 1 middle
    public void showNativeList(String page,int size,View view) {
        //final LinearLayout layout_content_ads = adViewNative.findViewById(R.id.layout_content_ads);
        //layout_content_ads.setVisibility(View.GONE);
        //test ca-app-pub-3940256099942544/2247696110

        nativeBannerAds = view.findViewById(R.id.nativeBanner);
        nativeBannerAds.setVisibility(View.VISIBLE);
        nativeBannerAds.setBannerAdsListener(new NativeBannerAds.BannerAdsListener() {
            @Override
            public void onSuccess(@NonNull String strJson) {
//                Const.INSTANCE.log("nativeBannerAds", "onSuccess " + strJson);
            }

            @Override
            public void onOtherAds(@NonNull String strAdsvertisingName) {
//                Const.INSTANCE.log("nativeBannerAds", "onOtherAds " + strAdsvertisingName);
//                if (strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_ADMOB()) || strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_FACEBOOK())) {
                if (strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_ADMOB) || strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_FACEBOOK)){
//                    Const.INSTANCE.log("nativeBannerAds", "onOtherAds IF" + strAdsvertisingName);
                    nativeBannerAds.setVisibility(View.GONE);
                    getShowNativeAdMob(view);
                }else {
                    nativeBannerAds.setVisibility(View.GONE);
                    nativeAdmobAds.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBannerClick(@NonNull String strJson) {

            }

            @Override
            public void onFailed(@NonNull String strErrorMessage) {

            }
        });

        nativeBannerAds.loadAds(page,strID,size);
    }

    // native banner
    public void getShowNativeAdMob(final View view) {
        try {
            nativeAdmobAds = view.findViewById(R.id.adContent);
            nativeAdmobAds.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(activity, activity.getString(R.string.KEY_ADMOB_Native))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // Show the ad.
                            adViewNativeContent(nativeAd,nativeAdmobAds);
//                            NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
//                                    .build();
//                            templateNativeAdmobAds.setStyles(styles);
//                            templateNativeAdmobAds.setNativeAd(nativeAd);
//                            templateNativeAdmobAds.setVisibility(View.VISIBLE);
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getHeadline());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getBody());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getIcon());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getImages().size());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getCallToAction());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getAdvertiser());
//                            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+nativeAd.getStarRating());

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                            Const.INSTANCE.log("getShowNativeAdMob","onAdFailedToLoad "+adError);
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAd(adRequest);
        } catch (Exception e) {
//            Const.INSTANCE.log("getShowNativeAdMob","Exception "+e);
            nativeAdmobAds.setVisibility(View.GONE);
        }
    }

    //custom view Native
    public void adViewNativeContent(NativeAd contentAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setMediaView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
//        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));
//        adView.setIconView(adView.findViewById(R.id.contentad_logo));

//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getHeadline());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getBody());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getIcon());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getImages().size());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getCallToAction());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getAdvertiser());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getStarRating());
//            Const.INSTANCE.log("getShowNativeAdMob","onNativeAdLoaded "+contentAd.getStore());


        ((TextView) adView.getHeadlineView()).setText(contentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(contentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(contentAd.getCallToAction());

        List<NativeAd.Image> images = contentAd.getImages();

        if (images != null && images.size() > 0) {
            adView.getMediaView().setImageScaleType(ImageView.ScaleType.FIT_XY);
            adView.getMediaView().setMediaContent(contentAd.getMediaContent());
        }

        NativeAd.Image logoImage = contentAd.getIcon();

//        if (logoImage != null) {
//            ((ImageView) adView.getIconView()).setImageDrawable(logoImage.getDrawable());
//        }
        // assign native ad object to the native view and make visible
        adView.setNativeAd(contentAd);
    }

    public void getShowBannerSmall_Native(int intPage) {
        String strPage = intPage + "";
        bannerAds2 = activity.findViewById(R.id.bannerAds_2);
//        Const.log("Banneshow", "getShowBannerSmall " , activity.toString());
//        bannerAds2.setDebug(false);
        bannerAds2.setBannerAdsListener(new BannerAds.BannerAdsListener() {
            @Override
            public void onSuccess(String strJson) {
                bannerAds2.setVisibility(View.VISIBLE);
//                Const.log("Banneshow", "getShowBannerSmall onSuccess " , strJson);
            }

            @Override
            public void onFailed(String strErrorMessage) {
//                Const.log("Banneshow", "onFailed " , strErrorMessage);
                bannerAds2.setVisibility(View.GONE);
            }

            @Override
            public void onBannerClick(String strJson) {
//                Const.log("Banneshow", "getShowBannerSmall onBannerClick " , strJson);
            }

            @Override
            public void onOtherAds(String strAdsvertisingName) {
//                Const.log("Banneshow", "getShowBannerSmall onOtherAds ", strAdsvertisingName);
//                if (strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_ADMOB()) || strAdsvertisingName.equals(MobileAdvertising.INSTANCE.getADVERTISING_FACEBOOK())) {
                if (strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_ADMOB) || strAdsvertisingName.equals(MobileAdvertising.ADVERTISING_FACEBOOK)) {
                    getShowBannerAdMob_Native(activity);
//                    getShowNativeAdMob(view);
                }
            }
        });
        bannerAds2.loadAds(strPage,strID);
    }
    public void getShowBannerAdMob_Native(final Activity activity) {
        try {
            nativeAdmobAds = activity.findViewById(R.id.adContent_2);
            nativeAdmobAds.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(activity, activity.getString(R.string.KEY_ADMOB_Native))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            adViewNativeContent(nativeAd,nativeAdmobAds);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            nativeAdmobAds.setVisibility(View.GONE);
//                            Const.INSTANCE.log(TAG,"adError : "+adError);
//                            Const.log("getShowNativeAdMob","onAdFailedToLoad ",adError.toString());
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();
            adLoader.loadAd(adRequest);
        } catch (Exception e) {
            nativeAdmobAds.setVisibility(View.GONE);

        }
    }


//    public void showNativeLayout(Context mContext, ViewGroup view) {
//        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final NativeContentAdView adViewNative = (NativeContentAdView) inflater.inflate(R.layout.ads_content, null, true);
//        final LinearLayout layout_content_ads = adViewNative.findViewById(R.id.layout_content_ads);
//        layout_content_ads.setVisibility(View.GONE);
//        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//            @Override
//            public void onContentAdLoaded(NativeContentAd ad) {
//                adViewNativeContent(ad, adViewNative);
//            }
//        });
//        adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                layout_content_ads.setVisibility(View.VISIBLE);
//                super.onAdLoaded();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                layout_content_ads.setVisibility(View.GONE);
////                Toast.makeText(activity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show();
//            }
//        }).build();
//        // Add Content Advertise
//        adLoader.loadAd(adRequest);
//        view.addView(adViewNative);
//    }

//    public void showNativeList(final NativeContentAdView adViewNative, final int color) {
//        final LinearLayout layout_content_ads = adViewNative.findViewById(R.id.layout_content_ads);
////        layout_content_ads.setBackgroundResource(color);
//        layout_content_ads.setVisibility(View.GONE);
//        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//            @Override
//            public void onContentAdLoaded(NativeContentAd ad) {
//                adViewNativeContent(ad, adViewNative);
//            }
//        });
//        adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                layout_content_ads.setVisibility(View.VISIBLE);
//                super.onAdLoaded();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                layout_content_ads.setVisibility(View.GONE);
////                Toast.makeText(activity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show();
//            }
//        }).build();
//        // Add Content Advertise
//        adLoader.loadAd(adRequest);
//    }

//    public void adViewNativeContent(NativeContentAd contentAd, NativeContentAdView adView) {
//        adView.setHeadlineView(adView.findViewById(R.id.headline));
//        adView.setImageView(adView.findViewById(R.id.contentad_image));
//        adView.setBodyView(adView.findViewById(R.id.body));
//        adView.setCallToActionView(adView.findViewById(R.id.call_to_action));
//        adView.setAdvertiserView(adView.findViewById(R.id.advertiser));
//        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
//
//        ((TextView) adView.getHeadlineView()).setText(contentAd.getHeadline());
//        ((TextView) adView.getBodyView()).setText(contentAd.getBody());
//        ((TextView) adView.getCallToActionView()).setText(contentAd.getCallToAction());
//        ((TextView) adView.getAdvertiserView()).setText(contentAd.getAdvertiser());
//
//        List<NativeAd.Image> images = contentAd.getImages();
//
//        if (images != null && images.size() > 0) {
//            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
//        }
//
//        NativeAd.Image logoImage = contentAd.getLogo();
//
//        if (logoImage != null) {
//            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
//        }
//        // assign native ad object to the native view and make visible
//        adView.setNativeAd(contentAd);
//    }


    //------------------------------------------------------------------------------------------

    private final OnRewardedVideoAdListener mOnRewardedVideoAdListener = null;

    public interface OnRewardedVideoAdListener {
        public void onSuccess();

        public void onFailed(String strError);
    }

    RewardedAd mRewardedVideoAd;
    boolean rewardedSuccess = false;

//    public void loadRewarded(){
//        RewardedAd.load(activity, activity.getString(R.string.id_admob_reward), adRequest, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error.
//                        Const.INSTANCE.log(TAG, loadAdError.toString());
//                        mRewardedVideoAd = null;
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        mRewardedVideoAd = rewardedAd;
//                        Const.INSTANCE.log(TAG, "Ad was loaded.");
//                        rewardedSuccess = false;
//                        setCallback();
//                        mRewardedVideoAd.show(activity, new OnUserEarnedRewardListener() {
//                            @Override
//                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                                // Handle the reward.
//                                Const.INSTANCE.log(TAG, "The user earned the reward.");
//                                int rewardAmount = rewardItem.getAmount();
//                                String rewardType = rewardItem.getType();
//
//                                Const.INSTANCE.log(TAG, "The user earned the reward. : "+rewardAmount);
//                                Const.INSTANCE.log(TAG, "The user earned the reward. : "+rewardType);
//                            }
//                        });
//                    }
//                });
////        if (mRewardedVideoAd != null){
////            setCallback();
////            mRewardedVideoAd.show(activity, new OnUserEarnedRewardListener() {
////                @Override
////                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
////                    // Handle the reward.
////                    Const.INSTANCE.log(TAG, "The user earned the reward.");
////                    int rewardAmount = rewardItem.getAmount();
////                    String rewardType = rewardItem.getType();
////                    Const.INSTANCE.log(TAG, "The user earned the reward. : "+rewardAmount);
////                    Const.INSTANCE.log(TAG, "The user earned the reward. : "+rewardType);
////                }
////            });
////        }
//
//    }

    public void setCallback() {
        mRewardedVideoAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                //  Const.INSTANCE.log(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                //  Const.INSTANCE.log(TAG, "Ad dismissed fullscreen content.");
                mRewardedVideoAd = null;
                rewardedSuccess = true;
                mOnRewardedVideoAdListener.onSuccess();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                // Const.INSTANCE.log(TAG, "Ad failed to show fullscreen content.");
                mRewardedVideoAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                //Const.INSTANCE.log(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                // Const.INSTANCE.log(TAG, "Ad showed fullscreen content.");
            }
        });
    }

//    public void loadRewardedVideoAd(String strID_ADS) {
//        rewardedSuccess = false;
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
//        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//            @Override
//            public void onRewardedVideoAdLoaded() {
//                rewardedSuccess = false;
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoAdLoaded");
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoAdOpened");
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoStarted");
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoAdClosed");
//                if (mOnRewardedVideoAdListener != null && rewardedSuccess == true) {
//                    mOnRewardedVideoAdListener.onSuccess();
//                } else {
//                    mOnRewardedVideoAdListener.onFailed("Closed");
//                }
//            }
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewarded");
//                rewardedSuccess = true;
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoAdLeftApplication");
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int i) {
////                Const.INSTANCE.log("RewardedVideoAd", "onRewardedVideoAdFailedToLoad");
//                if (mOnRewardedVideoAdListener != null) {
//                    mOnRewardedVideoAdListener.onFailed("VideoAdFailed");
//                }
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//
//            }
//        });
//        mRewardedVideoAd.loadAd(strID_ADS, adRequest);
//    }
//
//    public void showRewardedVideoAd(OnRewardedVideoAdListener mOnRewardedVideoAdListener) {
//        this.mOnRewardedVideoAdListener = mOnRewardedVideoAdListener;
//        if (mRewardedVideoAd.isLoaded()) {
//            mRewardedVideoAd.show();
//        } else {
//            if (mOnRewardedVideoAdListener != null) {
//                mOnRewardedVideoAdListener.onFailed("isLoaded");
//            }
//        }
//    }


}

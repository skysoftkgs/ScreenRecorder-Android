package sim.ami.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.net.MalformedURLException;
import java.net.URL;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";
    private InterstitialAd mInterstitialAd;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private TabAdapter adapter;
    public static GalleryActivity gInstance;
    private ConsentForm mForm;
    public static String ADTEST_DEVICE = "B1BC7AF710D7903F427400DD8566E9A0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        String extraScreen = getIntent().getStringExtra(Constant.EXTRA_DATA);
        boolean isGotoSetting = Constant.EXTRA_SETTING_SCREEN.equals(extraScreen);

        highLightCurrentTab(isGotoSetting ? 1 : 0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                highLightCurrentTab(position); // for tab change
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(isGotoSetting ? 1 : 0);

        ImageButton plusImageButton = (ImageButton) findViewById(R.id.imageButton_plus);
        plusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatHeadService.gChatHeadService.startRecord();
                finish();
            }
        });

        gInstance = this;
        if (BuildConfig.DEBUG)
            setEEA();
        checkForConsent();
    }

    public void showFullAd(boolean isPersonal) {
        //Interstitial
        if (isPersonal) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_full));
            mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(ADTEST_DEVICE)
                .build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }
            });
        } else {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_full));
            mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(ADTEST_DEVICE)
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }
            });
        }
    }

    private void showBanner(boolean isPersonal) {
        //Banner
        if (isPersonal) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(ADTEST_DEVICE)
                .build();
            mAdView.loadAd(adRequest);
        } else {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(ADTEST_DEVICE)
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build();
            mAdView.loadAd(adRequest);
        }
    }

    private void setEEA() {
        ConsentInformation.getInstance(this).addTestDevice(ADTEST_DEVICE);
        ConsentInformation.getInstance(this).
            setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
    }


    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(adapter.getSelectedTabView(position));

        ImageView indicator1 = (ImageView) findViewById(R.id.imageView_indicator1);
        ImageView indicator2 = (ImageView) findViewById(R.id.imageView_indicator2);

        if (position == 0) {
            indicator1.setVisibility(View.VISIBLE);
            indicator2.setVisibility(View.INVISIBLE);
        } else {
            indicator1.setVisibility(View.INVISIBLE);
            indicator2.setVisibility(View.VISIBLE);
        }
    }

//    public void setupTabView(){
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab);
//            TextView tab_name = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.textView_name);
//            tab_name.setText("" + tabNames[i]);
//            ImageView tab_icon = (ImageView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.imageView_icon);
//            tab_icon.setImageResource(tabIcons[i]);
//        }
//    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new TabAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new VideoFragment());
        adapter.addFragment(new SettingsFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        //ChatHeadService.visiblePopupView();
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            // mInterstitialAd.show();
        }
        sendCommandToService();
        super.onBackPressed();
    }

    private void sendCommandToService() {
        Intent intent = new Intent(this, ChatHeadService.class);
        intent.putExtra(Constant.COMMAND, Constant.CMD_UPDATE_CONFIG);
        //stopService(intent);
        startService(intent);
    }

    /**
     * Implementation for GD
     */

    private void checkForConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(GalleryActivity.this);
        String[] publisherIds = {getString(R.string.pub_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.e(TAG, "Showing Personalized ads");
                        showPersonalizedAds();
                        break;
                    case NON_PERSONALIZED:
                        Log.e(TAG, "Showing Non-Personalized ads");
                        showNonPersonalizedAds();
                        break;
                    case UNKNOWN:
                        Log.e(TAG, "Requesting Consent");
                        if (ConsentInformation.getInstance(getBaseContext())
                            .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            showPersonalizedAds();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                Log.e(TAG, "onFailedToUpdateConsentInfo" + errorDescription);
            }
        });
    }

    private void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            /*
            watch this video how to create privacy policy in mint
            https://www.youtube.com/watch?v=lSWSxyzwV-g&t=140s
            */
            privacyUrl = new URL("https://google.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        mForm = new ConsentForm.Builder(GalleryActivity.this, privacyUrl)
            .withListener(new ConsentFormListener() {
                @Override
                public void onConsentFormLoaded() {
                    // Consent form loaded successfully.
                    Log.d(TAG, "Requesting Consent: onConsentFormLoaded");
                    showForm();
                }

                @Override
                public void onConsentFormOpened() {
                    // Consent form was displayed.
                    Log.d(TAG, "Requesting Consent: onConsentFormOpened");
                }

                @Override
                public void onConsentFormClosed(
                    ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    Log.d(TAG, "Requesting Consent: onConsentFormClosed");
                    if (userPrefersAdFree) {
                        // Buy or Subscribe
                        Log.d(TAG, "Requesting Consent: User prefers AdFree");
                    } else {
                        Log.d(TAG, "Requesting Consent: Requesting consent again");
                        switch (consentStatus) {
                            case PERSONALIZED:
                                showPersonalizedAds();
                                break;
                            case NON_PERSONALIZED:
                                showNonPersonalizedAds();
                                break;
                            case UNKNOWN:
                                showNonPersonalizedAds();
                                break;
                        }

                    }
                    // Consent form was closed.
                }

                @Override
                public void onConsentFormError(String errorDescription) {
                    Log.d(TAG, "Requesting Consent: onConsentFormError. Error - " + errorDescription);
                    // Consent form error.
                }
            })
            .withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
            .withAdFreeOption()
            .build();
        mForm.load();
    }

    /*
    want test your app watch this video https://youtu.be/_JOapnq8hrs?t=654
    */
    private void showPersonalizedAds() {
        ConsentInformation.getInstance(this).setConsentStatus(ConsentStatus.PERSONALIZED);
        showBanner(true);
        showFullAd(true);
    }

    private void showNonPersonalizedAds() {
        ConsentInformation.getInstance(this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
        showBanner(false);
        showFullAd(false);
    }

    public static Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");

        return extras;
    }

    private void showForm() {
        if (mForm == null) {
            Log.d(TAG, "Consent form is null");
        }
        if (mForm != null) {
            Log.d(TAG, "Showing consent form");
            mForm.show();
        } else {
            Log.d(TAG, "Not Showing consent form");
        }
    }

}

package sim.ami.com.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static sim.ami.com.myapplication.GalleryActivity.ADTEST_DEVICE;

public class PopupActivity extends AppCompatActivity {

    private static final String TAG = "PopupActivity";
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.exit)
    ImageButton exit;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.share)
    TextView share;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.gallery)
    TextView gallery;

    private ConsentForm mForm;
    private String mVideoPath;
    private File mFile;

    private Typeface mMediumTypeface;
    private Typeface mBookTypeFace;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(false);
        mVideoPath = getIntent().getStringExtra(Constant.EXTRA_DATA);
        if (!TextUtils.isEmpty(mVideoPath)) {
            mFile = new File(mVideoPath);
        }

        mBookTypeFace = Typeface.createFromAsset(getAssets(), "fonts/GothamRounded-Book.otf");
        mMediumTypeface =  Typeface.createFromAsset(getAssets(), "fonts/GothamRounded-Medium.otf");

        txtTitle.setTypeface(mMediumTypeface);
        share.setTypeface(mBookTypeFace);
        delete.setTypeface(mBookTypeFace);
        gallery.setTypeface(mBookTypeFace);

        if (BuildConfig.DEBUG)
            setEEA();
        checkForConsent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sendCommandToService() {
        Intent intent = new Intent(this, ChatHeadService.class);
        intent.putExtra(Constant.COMMAND, Constant.CMD_UPDATE_CONFIG);
        //stopService(intent);
        startService(intent);
    }

    public void confirmDeleteVideo(final int index) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new VideoDelete(mFile).run();
                    dialog.cancel();
                    finish();
                }
            })
            .setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
            .create();
        alertDialog.show();
    }


    @OnClick({R.id.exit, R.id.share, R.id.delete, R.id.gallery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit:
                finish();
                break;
            case R.id.share:
                if (mFile != null) {
                    shareVideo(mFile);
                }

                break;
            case R.id.delete:
                if (mFile != null) {
                    confirmDeleteVideo(0);
                }

                break;
            case R.id.gallery:
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void checkForConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(PopupActivity.this);
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
            privacyUrl = new URL("https://google.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        mForm = new ConsentForm.Builder(PopupActivity.this, privacyUrl)
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
    }

    private void showNonPersonalizedAds() {
        ConsentInformation.getInstance(this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
        showBanner(false);
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

    private static class VideoDelete implements Runnable {

        private final File mFile;

        private VideoDelete(File file) {
            mFile = file;
        }

        @Override
        public void run() {
            try {
                mFile.delete();

            } catch (Exception e) {
                Log.e("DeleteFile Exception", e.toString());
            }

        }
    }

    private void shareVideo(File file) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", file);
        Log.e("ShareVideo", "URI = " + screenshotUri);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.addFlags(1);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_video)));

    }

}

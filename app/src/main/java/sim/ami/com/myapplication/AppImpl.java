package sim.ami.com.myapplication;

import android.app.Application;
import android.content.Intent;

import com.ami.com.ami.utils.MyPreference;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Administrator on 4/25/2016.
 */
public class AppImpl extends Application{
    /**
     * The Analytics singleton. The field is set in onCreate method override when the application
     * class is initially created.
     */
    private static GoogleAnalytics analytics;

    /**
     * The default app tracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
       // Intent intent= new Intent(this, ChatHeadService.class);
       // startService(intent);
        initTracker();

        Intent intent = new Intent(this, RecordStartService.class);
        startService(intent);

//        Intent intent1;
//        if(MyPreference.getInstance(this).isTutorialPassed()){
//            intent1 = new Intent(this, HomeActivity.class);
//        }else{
//            intent1 = new Intent(this, TutorialActivity.class);
//        }
//
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent1);
    }

    void initTracker(){
        analytics = GoogleAnalytics.getInstance(this);

        // TODO: Replace the tracker-id with your app one from https://www.google.com/analytics/web/
        tracker = analytics.newTracker("UA-90619073-3");

        // Provide unhandled exceptions reports. Do that first after creating the tracker
        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);
    }

}

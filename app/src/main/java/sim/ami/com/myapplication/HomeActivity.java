package sim.ami.com.myapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.ami.com.ami.utils.MyPreference;

public class HomeActivity extends AppCompatActivity {

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!MyPreference.getInstance(this).isTutorialPassed()){
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
       // setContentView(R.layout.activity_home);
      //  getWindow().setFeatureDrawable(3,getResources().getDrawable(R.drawable.ic_menu_camera));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkAlertWindown();
        }else {
            Intent intent= new Intent(this, ChatHeadService.class);
            stopService(intent);
            startService(intent);
            finish();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAlertWindown(){
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }else {
            Intent intent= new Intent(this, ChatHeadService.class);
            stopService(intent);
            startService(intent);
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.accept_alert),Toast.LENGTH_LONG).show();
                finish();
            }else {
                Intent intent= new Intent(this, ChatHeadService.class);
                stopService(intent);
                startService(intent);
                finish();
            }
        }
    }
}

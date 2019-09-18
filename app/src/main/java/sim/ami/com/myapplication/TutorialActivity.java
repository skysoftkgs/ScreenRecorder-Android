package sim.ami.com.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ami.com.ami.utils.MyPreference;

import java.util.List;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import pub.devrel.easypermissions.EasyPermissions;

public class TutorialActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static TutorialActivity gInstance;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int OTHER_PERMISSION_REQ_CODE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        gInstance = this;

        getSupportActionBar().hide();

        showRate();

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/GothamRounded-Medium.otf");
        TextView recordAutoTextView = (TextView) findViewById(R.id.textView_record_auto);
        recordAutoTextView.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(), "fonts/GothamRounded-Book.otf");
        TextView toStartTextView = (TextView) findViewById(R.id.textView_to_start);
        toStartTextView.setTypeface(custom_font2);

        TextView startTextView = (TextView) findViewById(R.id.textView_start);
        TextPaint paint = startTextView.getPaint();
        float width = paint.measureText(startTextView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, 50,
            new int[]{
                Color.parseColor("#fd5d10"),
                Color.parseColor("#fd5d10"),
                Color.parseColor("#f0156a"),
                Color.parseColor("#f0156a"),
            }, null, Shader.TileMode.REPEAT);
        startTextView.getPaint().setShader(textShader);
        startTextView.setTypeface(custom_font1);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkAlertWindown();
                } else {
                    startRecord();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showAskOverDrawPermisson();
        }

    }

    private void startRecord() {
        MyPreference.getInstance(getApplicationContext()).setTutorialPassed(true);
        showHomeScreen();
    }

    public void showHomeScreen() {
        Intent intent = new Intent(TutorialActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showRate() {
        AppRate.with(this)
            .setInstallDays(0) // default 10, 0 means install day.
            .setLaunchTimes(3) // default 10
            .setRemindInterval(2) // default 1
            .setShowLaterButton(true) // default true
            .setDebug(false) // default false
            .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                @Override
                public void onClickButton(int which) {
                    Log.e(HomeActivity.class.getName(), Integer.toString(which));
                }
            })
            .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAlertWindown() {
        if (!Settings.canDrawOverlays(this)) {
            openOverDrawSetting();
        } else {
            checkOtherPermisson();
        }
    }

    private void openOverDrawSetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
    }

    void checkOtherPermisson() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
            startRecord();
            finish();
        } else {
            // Request the OTHERS permission via a user dialog
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access camera and write external storage.",
                OTHER_PERMISSION_REQ_CODE,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
        if (requestCode == OTHER_PERMISSION_REQ_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                startRecord();
                finish();
            }
        }

    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
        if (requestCode == OTHER_PERMISSION_REQ_CODE) {
            if (list.size() > 0) {
                Toast.makeText(TutorialActivity.this, "Please accept permissons " + list, Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showAskOverDrawPermisson() {
        if (!Settings.canDrawOverlays(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder
                .setMessage(getString(R.string.permisson_over_draw_content))
                .setPositiveButton(getString(R.string.dialog_open), (dialog, which) -> {
                    dialog.dismiss();
                    openOverDrawSetting();
                })
                .setNegativeButton(getString(R.string.dialog_exit), (dilog, which) -> {
                    dilog.dismiss();
                    finish();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
        }
    }
}

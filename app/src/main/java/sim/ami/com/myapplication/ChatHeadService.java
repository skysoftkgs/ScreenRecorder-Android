package sim.ami.com.myapplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ami.com.ami.utils.Config;
import com.ami.com.ami.utils.MyPreference;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Environment.DIRECTORY_MOVIES;
import static android.text.TextUtils.getLayoutDirectionFromLocale;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Administrator on 4/25/2016.
 */
public class ChatHeadService extends Service implements ShakeDetector.Listener {
    private InterstitialAd mInterstitialAd;
    public static ChatHeadService gChatHeadService;
    public static boolean isRunning;

    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    WindowManager.LayoutParams paramsCam;
    private View popupView;
    ImageButton captureImage;
    ImageButton showGallery;
    ImageButton showSetting;
    ImageButton exitApp;
    private View.OnTouchListener touchListener;
    private boolean delayTime = false;
    Handler handler = new Handler();


    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private int DISPLAY_WIDTH = 720;
    private int DISPLAY_HEIGHT = 1280;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private int FRAME_RATE = 30;
    private int BIT_RATE = 1500000;
    private int ORIENTATION = 90;
    private int COUNT_DOWN_VALUE = 3;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int CAM_HEIGHT = 320;
    private static final int CAM_WIDTH = 240;

    //For camera preview
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static boolean inPreview = false;
    private int id;

    //For Shake
    SensorManager sensorManager;
    ShakeDetector shakeDetector;
    public static boolean isInScreenRecoder = false;

    private Config mConfig;
    private String path;

    private static final String EXTRA_RESULT_CODE = "result-code";
    private static final String EXTRA_DATA = "data";

    private int MAGIC_BT_WIDTH = 96;
    private int MAGIC_BT_HEIGHT = 45;
    private ImageView mMagicButtonView;

    private TextView countDownTextView;
    private TextView tutorialStopTextView;
    private ImageView tutorialStopImageView;
    private File outputRoot;
    private final DateFormat fileFormat =
        new SimpleDateFormat("'VID_'yyyy-MM-dd-HH-mm-ss'.mp4'", Locale.US);
    private boolean isPrepareRecorderSuccess = false;

    private int mWindowType = 0;
    private String mOutPutPath;
    public static Intent newIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, ChatHeadService.class);
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(Constant.COMMAND, Constant.CMD_START_RECORD);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT < 26) {
            mWindowType = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mWindowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        initConfig();
        initView();
        initMagicButton();
        initListenner();
        initShake();
        initProjector();
        initCamera();
        initShowTouches();
        //updateConfig();
        initCountDown();
        //initTutorial();
        makeOutputFolder();
        updateProjectorConfig(mConfig);

        gChatHeadService = this;
        isRunning = true;

        //Interstitial
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_full));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }

    /*private void initTutorial() {
        tutorialStopTextView = new TextView(this);
        final WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                mWindowType,
                520,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | gravityEndLocaleHack();
        params.y = getResources().getDimensionPixelSize(R.dimen.magic_button_h);
        tutorialStopTextView.setText("Touch here to stop recording");
        tutorialStopTextView.setTextColor(getResources().getColor(android.R.color.white));


        tutorialStopImageView = new ImageView(this);
        tutorialStopImageView.setBackground(getDrawable(R.drawable.ic_touch_app_black_24dp));
        windowManager.addView(tutorialStopImageView, params);
        params.y += getResources().getDimensionPixelSize(R.dimen.magic_button_h);
        windowManager.addView(tutorialStopTextView, params);

        if (isInScreenRecoder)
            stopTutorial();
    }

    private void stopTutorial() {
        tutorialStopImageView.setVisibility(View.INVISIBLE);
        tutorialStopTextView.setVisibility(View.INVISIBLE);
    }

    private void removeTutorial() {
        if (tutorialStopImageView != null && tutorialStopImageView.isAttachedToWindow()) {
            windowManager.removeView(tutorialStopImageView);
        }
        if (tutorialStopTextView != null && tutorialStopTextView.isAttachedToWindow()) {
            windowManager.removeView(tutorialStopTextView);
        }
    }*/

    private void initCountDown() {
        countDownTextView = new TextView(this);
        final WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                mWindowType,
                520,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        countDownTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.magic_button_h1));
        countDownTextView.setText("3");
        windowManager.addView(countDownTextView, params);
        countDownTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        countDownTextView.setVisibility(View.INVISIBLE);
    }

    private Animation createAnimation() {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        return animation;
    }

    private void initMagicButton() {


        mMagicButtonView = new ImageView(this);
        mMagicButtonView.setImageResource(R.drawable.image_blink);

        //   mMagicButtonView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        MAGIC_BT_WIDTH = getResources().getDimensionPixelSize(R.dimen.magic_button_w);
        if (Build.VERSION.SDK_INT > LOLLIPOP_MR1 || "M".equals(Build.VERSION.RELEASE)) {
            MAGIC_BT_HEIGHT = 2 * getResources().getDimensionPixelSize(R.dimen.magic_button_h1);
        } else {
            MAGIC_BT_HEIGHT = 2 * getResources().getDimensionPixelSize(R.dimen.magic_button_h);
        }
        final WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(MAGIC_BT_WIDTH, MAGIC_BT_HEIGHT,
                mWindowType,
                520,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | gravityEndLocaleHack();
        params.y = getResources().getDimensionPixelSize(R.dimen.magic_button_h);

        windowManager.addView(mMagicButtonView, params);
        mMagicButtonView.setVisibility(View.INVISIBLE);

    }

    @SuppressLint("RtlHardcoded") // Gravity.END is not honored by WindowManager for added views.
    private static int gravityEndLocaleHack() {
        int direction = getLayoutDirectionFromLocale(Locale.getDefault());
        return direction == View.LAYOUT_DIRECTION_RTL ? Gravity.LEFT : Gravity.RIGHT;
    }

    private void initConfig() {
        mConfig = MyPreference.getInstance(this).getConfig();

    }

    private void initProjectorConfig() {

    }

    private void initCamera() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        getCameraApi23();
        if (currentapiVersion > android.os.Build.VERSION_CODES.FROYO) {
            id = findFrontFacingCameraEx();
            Log.d("TestLedActivity", "L'id trovato e': " + id);
            try {
                camera = Camera.open(id);
            } catch (Exception e) {

            }

        } else {
            Log.d("TestLedActivity", "La versione e' froyo");
            camera = getFrontFacingCamera();
        }

        preview = new SurfaceView(this);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        paramsCam = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            mWindowType,
            520,
            PixelFormat.TRANSLUCENT);

        paramsCam.gravity = Gravity.TOP | Gravity.LEFT;


        paramsCam.x = 0;
        paramsCam.y = 400;
        paramsCam.width = CAM_WIDTH;
        paramsCam.height = CAM_HEIGHT;
        preview.setOnTouchListener(new View.OnTouchListener() {
                                       private int initialX;
                                       private int initialY;
                                       private float initialTouchX;
                                       private float initialTouchY;

                                       @Override
                                       public boolean onTouch(View v, MotionEvent event) {
                                           switch (event.getAction()) {
                                               case MotionEvent.ACTION_DOWN:
                                                   initialX = paramsCam.x;
                                                   initialY = paramsCam.y;
                                                   initialTouchX = event.getRawX();
                                                   initialTouchY = event.getRawY();

                                                   return true;
                                               case MotionEvent.ACTION_UP:

                                                   return true;
                                               case MotionEvent.ACTION_MOVE:
                                                   paramsCam.x = initialX
                                                       + (int) (event.getRawX() - initialTouchX);
                                                   paramsCam.y = initialY
                                                       + (int) (event.getRawY() - initialTouchY);
                                                   windowManager.updateViewLayout(preview, paramsCam);

                                                   return true;
                                           }
                                           return false;
                                       }
                                   }
        );

        windowManager.addView(preview, paramsCam);
        if (!mConfig.isEnableShowCamera())
            preview.setVisibility(View.GONE);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (camera != null && inPreview == true) {
            Camera.Parameters parameters = camera.getParameters();
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRotation() == Surface.ROTATION_0) {
                Log.e("Camera", "RO: 0");
                parameters.setPreviewSize(CAM_HEIGHT, CAM_WIDTH);
                camera.setDisplayOrientation(90);
            }

            if (display.getRotation() == Surface.ROTATION_90) {
                parameters.setPreviewSize(CAM_WIDTH, CAM_HEIGHT);
                Log.e("Camera", "RO: 90");
                camera.setDisplayOrientation(0);
            }

            if (display.getRotation() == Surface.ROTATION_180) {
                parameters.setPreviewSize(CAM_WIDTH, CAM_HEIGHT);
                camera.setDisplayOrientation(270);
                Log.e("Camera", "RO: 180");
            }

            if (display.getRotation() == Surface.ROTATION_270) {
                parameters.setPreviewSize(CAM_WIDTH, CAM_HEIGHT);
                camera.setDisplayOrientation(180);
                Log.e("Camera", "RO: 270");
            }
        }

    }

    private void initProjector() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mProjectionManager = (MediaProjectionManager) this.getSystemService
            (Context.MEDIA_PROJECTION_SERVICE);

    }

    private void initView() {


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = (View) layoutInflater.inflate(R.layout.quick_setting, null);

        //View cricleView = (View)layoutInflater.inflate(R.layout.cricle_quick_setting,null);


        captureImage = (ImageButton) popupView.findViewById(R.id.capture_image);
        showGallery = (ImageButton) popupView.findViewById(R.id.gallery);
        showSetting = (ImageButton) popupView.findViewById(R.id.setting);
        exitApp = (ImageButton) popupView.findViewById(R.id.exit);

        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            mWindowType,
            520,
            PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        captureImage.setOnTouchListener(touchListener);
        showGallery.setOnTouchListener(touchListener);
        showSetting.setOnTouchListener(touchListener);
        exitApp.setOnTouchListener(touchListener);
        windowManager.addView(popupView, params);

    }

    private void initShake() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        startShake();

    }

    private void startShake() {
        shakeDetector.start(sensorManager);
    }

    private void stopShake() {
        // shakeDetector.stop();
    }

    public void startRecord() {
        if (delayTime) {
            delayTime = false;
            return;
        }

        updateConfig();
        //stopTutorial();

        mConfig = MyPreference.getInstance(this).getConfig();

        if (mConfig.isEnableCountdown()) {
            popupView.setVisibility(View.INVISIBLE);
            int countTime = Integer.parseInt(mConfig.getCountDownValue()) * 1000;
            countDownTextView.setVisibility(View.VISIBLE);
            new CountDownTimer(countTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    countDownTextView.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    countDownTextView.setVisibility(View.INVISIBLE);
                    onToggleScreenShare();
                }
            }.start();
        } else {
            onToggleScreenShare();
        }

    }

    private void initListenner() {
        mMagicButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Magic button",Toast.LENGTH_SHORT).show();
                stopRecording();
                mMagicButtonView.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(ChatHeadService.this,PopupActivity.class);
                intent.putExtra(Constant.EXTRA_DATA,mOutPutPath);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // AppImpl.tracker().setScreenName("Quick-Setting-StartRecord");
                // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                startRecord();

            }
        });


        showGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delayTime) {
                    delayTime = false;
                    return;
                }
                // AppImpl.tracker().setScreenName("Quick-Setting-Gallery");
                // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                Intent intent = new Intent(ChatHeadService.this, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                popupView.setVisibility(View.GONE);
                preview.setVisibility(View.GONE);
                stopCamera();
                //stopTutorial();

            }
        });


        showSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delayTime) {
                    delayTime = false;
                    return;
                }
                // AppImpl.tracker().setScreenName("Quick-Setting-Setting");
                //AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                Intent intent = new Intent(ChatHeadService.this, GalleryActivity.class);
                intent.putExtra(Constant.EXTRA_DATA,Constant.EXTRA_SETTING_SCREEN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                popupView.setVisibility(View.GONE);
                preview.setVisibility(View.GONE);
                stopCamera();
                //stopTutorial();
            }
        });


        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delayTime) {
                    delayTime = false;
                    return;
                }
                stopSelf();
            }
        });


        touchListener = new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    delayTime = true;
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        handler.postDelayed(runnable, 200);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(runnable);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX
                            + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                            + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(popupView, params);

                        break;
                }
                return false;
            }
        };


        captureImage.setOnTouchListener(touchListener);
        showGallery.setOnTouchListener(touchListener);
        showSetting.setOnTouchListener(touchListener);
        exitApp.setOnTouchListener(touchListener);

    }
    /*
    public static void setMediaProjection(MediaProjection mediaProjection){
        mMediaProjection = mediaProjection;
    }
    */
    /*
    public static void visiblePopupView(){
        popupView.setVisibility(View.VISIBLE);
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popupView != null && popupView.isAttachedToWindow())
            windowManager.removeView(popupView);
        if (mMagicButtonView != null && mMagicButtonView.isAttachedToWindow())
            windowManager.removeViewImmediate(mMagicButtonView);
        if (preview != null && preview.isAttachedToWindow())
            windowManager.removeView(preview);
        if (countDownTextView != null && countDownTextView.isAttachedToWindow())
            windowManager.removeView(countDownTextView);
        //removeTutorial();
        destroyMediaProjection();
        stopCamera();
        stopShowTouches();
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;


    }

    @Override
    public void hearShake() {
        Log.e(TAG, "hearShake");
        if (isInScreenRecoder) {
            mMagicButtonView.setVisibility(View.INVISIBLE);
            stopProjection();
            stopShake();
            // Toast.makeText(getApplicationContext(),"Stopping Recording",Toast.LENGTH_SHORT).show();
        }
    }


    public void stopRecording() {
        if (isInScreenRecoder) {
            isInScreenRecoder = false;
            stopProjection();
            /*
            if(mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }
            */

        }
    }


    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("MediaProjectionCallback", "onStop");
            /*
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mMediaProjection = null;
            stopScreenSharing();
            */


        }
    }

    private void stopScreenSharing() {

        destroyMediaProjection();

        if (mVirtualDisplay == null) {
            return;
        } else {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder == null)
            return;


        //If used: mMediaRecorder object cannot
        // be reused again


    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.e(TAG, "MediaProjection Stopped");
        //Toast.makeText(getApplicationContext(),"Destroy Media Projection",Toast.LENGTH_SHORT).show();
    }

    private void makeOutputFolder() {
        File picturesDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
        outputRoot = new File(picturesDir, "Recora");
        if (!outputRoot.exists()) {
            boolean mk = outputRoot.mkdirs();
            if (mk)
                Log.e("MK", "TRUE");
            else
                Log.e("MK", "FALSE");
        }


    }

    private void initRecorder() {

        try {

            Log.e("InitRecorder", "Init Recorder");
            Log.e("Media Recoder", "WIDTH-HEIGHT-FRAMERATE-BIT-ORIENTATION-->" + DISPLAY_WIDTH + "-" + DISPLAY_HEIGHT + "-" + "-" + FRAME_RATE + "-" + BIT_RATE + "-" + ORIENTATION);
            if (!outputRoot.exists() && !outputRoot.mkdirs()) {
                //Timber.e("Unable to create output directory '%s'.", outputRoot.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "Unable to create output directory.\nCannot record screen.",
                    LENGTH_SHORT).show();
                return;
            }

            String outputName = fileFormat.format(new Date());
            String videoPath = new File(outputRoot, outputName).getAbsolutePath();//outputRoot.getAbsolutePath() +"/VID_"+filename +".mp4";
            //getRecordingInfo();
            int resolutionId = Integer.parseInt(mConfig.getResolution()) - 1;
            mOutPutPath = videoPath;
            mMediaRecorder = new MediaRecorder();

            if (mConfig.isEnableRecordAudio()) {
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setVideoFrameRate(FRAME_RATE);

                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mMediaRecorder.setAudioEncodingBitRate(96 * 1024);
                mMediaRecorder.setAudioChannels(2);
                mMediaRecorder.setAudioSamplingRate(44100);
            } else {
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setVideoFrameRate(FRAME_RATE);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            }
            //Comment this because MediaRecorder only support full HD not 2k, 4k quality
            /*
            if(resolutionId == 0){
                RecordingInfo recordingInfo = getRecordingInfo();
                DISPLAY_WIDTH = recordingInfo.width;
                DISPLAY_HEIGHT = recordingInfo.height;
                mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            }else{
            */
            if (ORIENTATION == Configuration.ORIENTATION_UNDEFINED) {
                Configuration configuration = this.getResources().getConfiguration();
                int orientation = configuration.orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    int temp = DISPLAY_HEIGHT;
                    DISPLAY_HEIGHT = DISPLAY_WIDTH;
                    DISPLAY_WIDTH = temp;
                    mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
                } else {
                    mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
                }
            } else if (ORIENTATION == Configuration.ORIENTATION_LANDSCAPE) {
                int temp = DISPLAY_HEIGHT;
                DISPLAY_HEIGHT = DISPLAY_WIDTH;
                DISPLAY_WIDTH = temp;
                mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            } else if (ORIENTATION == Configuration.ORIENTATION_PORTRAIT) {
                mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            }
            // }
            mMediaRecorder.setVideoEncodingBitRate(BIT_RATE);

            mMediaRecorder.setOutputFile(videoPath);

            int rotation = windowManager.getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            // Log.e("ORIENTATION AUTO ","ORIENTATION AUTO = "+orientation);
            mMediaRecorder.setOrientationHint(orientation);

            mMediaRecorder.prepare();
            isPrepareRecorderSuccess = true;
        } catch (IOException e) {
            isPrepareRecorderSuccess = false;
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.e("Init Media recorder", "Fail prepare");
            e.printStackTrace();
        }


    }

    private VirtualDisplay createVirtualDisplay() {

        Surface surface = mMediaRecorder.getSurface();
        if (surface != null) {
            return mMediaProjection.createVirtualDisplay("Project",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
                surface, null, null
            );
        } else {
            Toast.makeText(this, "Cannot create video encoder. Please reduce Resolution or Frame Rate Or Both", Toast.LENGTH_SHORT).show();
            return null;
        }


    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            Intent intent = new Intent(ChatHeadService.this, Project.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        initRecorder();
        if (isPrepareRecorderSuccess) {
            mVirtualDisplay = createVirtualDisplay();
            if (mVirtualDisplay != null)
                mMediaRecorder.start();
            else {
                Toast.makeText(this, "Cannot create video encoder. Please reduce Resolution or Frame Rate Or Both", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot create video encoder. Please reduce Resolution or Frame Rate Or Both", Toast.LENGTH_SHORT).show();
        }
    }

    public void onToggleScreenShare() {
        isInScreenRecoder = true;
        mMagicButtonView.setVisibility(View.VISIBLE);
        //initRecorder();
        shareScreen();

    }

    private void stopProjection() {
        Log.e("StopProjection", "StopProjecttion");
        if (mMediaRecorder != null) {
            popupView.setVisibility(View.GONE);
            try {
                stopScreenSharing();
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (RuntimeException e) {

            }

        }

    }

    private void stopCamera() {
        Log.e("ChatHeadService", "stopCamera");
        if (inPreview) {
            camera.stopPreview();
        }
        if (null != camera) {
            camera.release();
            camera = null;
            inPreview = false;
        }

        if (preview != null && preview.getVisibility() == View.VISIBLE && preview.isAttachedToWindow()) {
            windowManager.removeView(preview);

        }
    }

    private void initShowTouches() {
        if (mConfig.isEnableShowTouches()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    // Do stuff here
                    try {
                        Settings.System.putInt(this.getContentResolver(),
                            "show_touches", 1);
                    } catch (IllegalArgumentException e) {

                    }
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                try {
                    Settings.System.putInt(this.getContentResolver(),
                        "show_touches", 1);
                } catch (IllegalArgumentException e) {
                }
            }

        }
    }

    private void stopShowTouches() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
                try {
                    Settings.System.putInt(this.getContentResolver(),
                        "show_touches", 0);
                } catch (IllegalArgumentException e) {

                }
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            try {
                Settings.System.putInt(this.getContentResolver(),
                    "show_touches", 0);
            } catch (IllegalArgumentException e) {
            }
        }

    }

    private void updateConfig() {
        Config newConfig = MyPreference.getInstance(this).getConfig();
        Log.e("NewConfig", newConfig.toString());
        mConfig = newConfig;
        if (newConfig.isEnableShowCamera()) {
            initCamera();
            preview.setVisibility(View.VISIBLE);
        } else {
            preview.setVisibility(View.GONE);
            stopCamera();
        }
        if (newConfig.isEnableShowTouches()) {
            initShowTouches();
        } else {
            stopShowTouches();
        }
        updateProjectorConfig(newConfig);
    }

    private void updateProjectorConfig(Config newConfig) {
        //stopProjection();
        int resolutionId = Integer.parseInt(newConfig.getResolution()) - 1;
        int frameRateId = Integer.parseInt(newConfig.getFrameRate()) - 1;
        int bitRateId = Integer.parseInt(newConfig.getBitRate()) - 1;
        int orientationId = Integer.parseInt(newConfig.getOrientation()) - 1;
        int countDownId = Integer.parseInt(newConfig.getCountDownValue()) - 1;

        //Init all variable here
        //For best solution
        //Commnent because screen recorder only support full hd 1920x1080
       /*
        if(resolutionId == 0){
            RecordingInfo recordingInfo = getRecordingInfo();
            DISPLAY_WIDTH = recordingInfo.width;
            DISPLAY_HEIGHT = recordingInfo.height;
        }else{
        */
        DISPLAY_WIDTH = Constant.RESOLUTION_WIDTH[resolutionId];
        DISPLAY_HEIGHT = Constant.RESOLUTION_HEIGHT[resolutionId];
        //}

        FRAME_RATE = Constant.FRAME_RATE[frameRateId];
        if (bitRateId != 0)
            BIT_RATE = Constant.BIT_RATE[bitRateId];
        else
            BIT_RATE = Constant.getBitRateAuto(DISPLAY_WIDTH, DISPLAY_HEIGHT);

        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degree = ORIENTATIONS.get(rotation);
        Log.e("orientationID ", "Orientation ID = " + orientationId);

        ORIENTATION = Constant.ORIENTATION[orientationId];

        COUNT_DOWN_VALUE = Constant.COUNT_DOWN_VALUE[countDownId];


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ChatHeadService", "onStartCommand");
        if (intent != null) {
            String extra = intent.getStringExtra(Constant.COMMAND);
            if (extra != null) {
                if (extra.equalsIgnoreCase(Constant.CMD_START_RECORD)) {
                    initRecorder();
                    if (isPrepareRecorderSuccess) {
                        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
                        Intent data = intent.getParcelableExtra(EXTRA_DATA);
                        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                        popupView.setVisibility(View.GONE);
                        mMediaProjectionCallback = new MediaProjectionCallback();
                        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                        mVirtualDisplay = createVirtualDisplay();
                        if (mVirtualDisplay != null)
                            mMediaRecorder.start();
                        mMagicButtonView.setVisibility(View.VISIBLE);
                        // mMagicButtonView.setAlpha((float)0.2);
                        // mMagicButtonView.startAnimation(createAnimation());
                        AnimationDrawable frameAnimation = (AnimationDrawable) mMagicButtonView.getDrawable();
                        frameAnimation.start();
                    } else {


                        Toast.makeText(this, "Cannot create video encoder. Please reduce Resolution or Frame Rate Or Both", Toast.LENGTH_SHORT).show();
                    }

                    // startShake();
                } else if (extra.equalsIgnoreCase(Constant.CMD_UPDATE_CONFIG)) {
                    Log.e("ChatHeadService", "UPDATE CONFIG");
                    popupView.setVisibility(View.VISIBLE);
                    mMagicButtonView.setVisibility(View.GONE);
                    updateConfig();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private int findFrontFacingCamera() {
        int idCamera = 0;
        // Look for front-facing camera, using the Gingerbread API.
        // Java reflection is used for backwards compatibility with pre-Gingerbread APIs.
        try {
            Class<?> cameraClass = Class.forName("android.hardware.Camera");
            Object cameraInfo = null;
            Field field = null;
            int cameraCount = 0;
            Method getNumberOfCamerasMethod = cameraClass.getMethod("getNumberOfCameras");
            if (getNumberOfCamerasMethod != null) {
                cameraCount = (Integer) getNumberOfCamerasMethod.invoke(null, (Object[]) null);
            }
            Class<?> cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
            if (cameraInfoClass != null) {
                cameraInfo = cameraInfoClass.newInstance();
            }
            if (cameraInfo != null) {
                field = cameraInfo.getClass().getField("facing");
            }
            Method getCameraInfoMethod = cameraClass.getMethod("getCameraInfo", Integer.TYPE, cameraInfoClass);
            if (getCameraInfoMethod != null && cameraInfoClass != null && field != null) {
                for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                    getCameraInfoMethod.invoke(null, camIdx, cameraInfo);
                    int facing = field.getInt(cameraInfo);
                    if (facing == 1) { // Camera.CameraInfo.CAMERA_FACING_FRONT
                        try {
                            Method cameraOpenMethod = cameraClass.getMethod("open", Integer.TYPE);
                            if (cameraOpenMethod != null) {
                                Log.d("TestLedActivity", "Id frontale trovato: " + camIdx);
                                //camera = (Camera) cameraOpenMethod.invoke( null, camIdx );
                                idCamera = camIdx;
                            }
                        } catch (RuntimeException e) {
                            Log.e("TestLedActivity", "Camera failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
        // Ignore the bevy of checked exceptions the Java Reflection API throws - if it fails, who cares.
        catch (ClassNotFoundException e) {
            Log.e("TestLedActivity", "ClassNotFoundException" + e.getLocalizedMessage());
        } catch (NoSuchMethodException e) {
            Log.e("TestLedActivity", "NoSuchMethodException" + e.getLocalizedMessage());
        } catch (NoSuchFieldException e) {
            Log.e("TestLedActivity", "NoSuchFieldException" + e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            Log.e("TestLedActivity", "IllegalAccessException" + e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            Log.e("TestLedActivity", "InvocationTargetException" + e.getLocalizedMessage());
        } catch (InstantiationException e) {
            Log.e("TestLedActivity", "InstantiationException" + e.getLocalizedMessage());
        } catch (SecurityException e) {
            Log.e("TestLedActivity", "SecurityException" + e.getLocalizedMessage());
        }

        if (camera == null) {
            Log.d("TestLedActivity", "Devo aprire la camera dietro");
            // Try using the pre-Gingerbread APIs to open the camera.
            idCamera = 0;
        }

        return idCamera;
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            Log.e("getBestPreviewSize", "W-H" + size.width + "-" + size.height);
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                Log.e("Camera", "setPreviewHolder");
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            Log.e("Camera", "Surface change");
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height,
                parameters);

            if (size != null) {
                Log.e("Came W-H = ", "" + size.width + "-" + size.height);
                //parameters.set("camera-id", 0);

                Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                if (display.getRotation() == Surface.ROTATION_0) {
                    // parameters.setPreviewSize(size.height, size.width);
                    camera.setDisplayOrientation(90);
                }

                if (display.getRotation() == Surface.ROTATION_90) {
                    parameters.setPreviewSize(size.width, size.height);
                }

                if (display.getRotation() == Surface.ROTATION_180) {
                    // parameters.setPreviewSize(size.height, size.width);
                    camera.setDisplayOrientation(270);
                }

                if (display.getRotation() == Surface.ROTATION_270) {
                    // parameters.setPreviewSize(size.width, size.height);
                    camera.setDisplayOrientation(180);
                }

                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                inPreview = true;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    Camera getFrontFacingCamera() throws NoSuchElementException {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < Camera.getNumberOfCameras(); cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return Camera.open(cameraIndex);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new NoSuchElementException("Can't find front camera.");
    }

    private int findFrontFacingCameraEx() {

        int cameraId = -1;

        // Search for the front facing camera

        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; i++) {

            Camera.CameraInfo info = new Camera.CameraInfo();

            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;

            }

        }

        return cameraId;

    }

    private void getCameraApi23() {
        if (!getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                .show();
            Log.e("getCameraApi23", "No camera on this device" +
                "");
        }
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.e("getCameraApi23", "AAA");
            String[] camlist = manager.getCameraIdList();
            Log.e("getCameraApi23 LENGTH =", "" + camlist.length);

            for (String camId : manager.getCameraIdList()) {
                Log.e("API23 Came ID =  ", camId);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private RecordingInfo getRecordingInfo() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        int displayDensity = displayMetrics.densityDpi;
        Log.e("Get Recording infor = ", "w - h =  " + displayWidth + "-" + displayHeight);
        //Timber.d("Display size: %s x %s @ %s", displayWidth, displayHeight, displayDensity);

        Configuration configuration = this.getResources().getConfiguration();
        boolean isLandscape = configuration.orientation == ORIENTATION_LANDSCAPE;
        //Timber.d("Display landscape: %s", isLandscape);

        // Get the best camera profile available. We assume MediaRecorder supports the highest.
        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        int cameraWidth = camcorderProfile != null ? camcorderProfile.videoFrameWidth : -1;
        int cameraHeight = camcorderProfile != null ? camcorderProfile.videoFrameHeight : -1;
        int cameraFrameRate = camcorderProfile != null ? camcorderProfile.videoFrameRate : 30;
        //Timber.d("Camera size: %s x %s framerate: %s", cameraWidth, cameraHeight, cameraFrameRate);

        int sizePercentage = 100;
        //Timber.d("Size percentage: %s", sizePercentage);

        return calculateRecordingInfo(displayWidth, displayHeight, displayDensity, isLandscape,
            cameraWidth, cameraHeight, cameraFrameRate, sizePercentage);
    }

    static RecordingInfo calculateRecordingInfo(int displayWidth, int displayHeight,
                                                int displayDensity, boolean isLandscapeDevice, int cameraWidth, int cameraHeight,
                                                int cameraFrameRate, int sizePercentage) {
        // Scale the display size before any maximum size calculations.
        displayWidth = displayWidth * sizePercentage / 100;
        displayHeight = displayHeight * sizePercentage / 100;

        if (cameraWidth == -1 && cameraHeight == -1) {
            // No cameras. Fall back to the display size.
            return new RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity);
        }

        int frameWidth = isLandscapeDevice ? cameraWidth : cameraHeight;
        int frameHeight = isLandscapeDevice ? cameraHeight : cameraWidth;
        if (frameWidth >= displayWidth && frameHeight >= displayHeight) {
            // Frame can hold the entire display. Use exact values.
            return new RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity);
        }

        // Calculate new width or height to preserve aspect ratio.
        if (isLandscapeDevice) {
            frameWidth = displayWidth * frameHeight / displayHeight;
        } else {
            frameHeight = displayHeight * frameWidth / displayWidth;
        }
        return new RecordingInfo(frameWidth, frameHeight, cameraFrameRate, displayDensity);
    }

    static final class RecordingInfo {
        final int width;
        final int height;
        final int frameRate;
        final int density;

        RecordingInfo(int width, int height, int frameRate, int density) {
            this.width = width;
            this.height = height;
            this.frameRate = frameRate;
            this.density = density;
        }
    }


}
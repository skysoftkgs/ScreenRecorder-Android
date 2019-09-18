package sim.ami.com.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ami.com.ami.utils.Config;
import com.ami.com.ami.utils.MyPreference;

public class RecordStartService extends Service {

    boolean volumeButtonDobuleClicked = false;
    int volume;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                Config newConfig = MyPreference.getInstance(getApplicationContext()).getConfig();
                if(!newConfig.isEnableAutoRecord()) return;

                int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);
                if(newVolume == volume) return;

                volume = newVolume;
                Log.d("wwww", String.valueOf(volume));

                if(ChatHeadService.isRunning || ChatHeadService.isInScreenRecoder)
                    return;

                if (volumeButtonDobuleClicked) {
                    if(GalleryActivity.gInstance != null)
                        GalleryActivity.gInstance.finish();

                    if(TutorialActivity.gInstance != null)
                        TutorialActivity.gInstance.finish();

//                    try {
//                        ChatHeadService.gChatHeadService.startRecord();
//                    }catch (Exception e) {
                        Intent intent1 = new Intent(getApplicationContext(), ChatHeadService.class);
                        stopService(intent1);
                        startService(intent1);
//                    }
                    return;
                }

                volumeButtonDobuleClicked = true;
                Toast.makeText(getApplicationContext(), "Please click volume button again to start recording.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        volumeButtonDobuleClicked = false;
                    }
                }, 1000);

            }
        }
    };

    public RecordStartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    Handler handler = new Handler();
//    boolean stop = false;
//
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.d("MyLog", "ppppp");
//
//            if (!stop) {
//                handler.postDelayed(this, 1000);
//            }
//        }
//    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


//        handler.post(runnable);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(broadcastReceiver, filter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
    }
}

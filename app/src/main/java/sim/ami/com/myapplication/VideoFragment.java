package sim.ami.com.myapplication;


import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ami.com.ami.utils.Utils;
import com.ami.customui.GalleryAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.os.Environment.DIRECTORY_MOVIES;

public class VideoFragment extends Fragment {
    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private LinearLayout noVideoLayout;
    InterstitialAd mInterstitialAd;
    private ProgressBar progress_circular;
    private MediaMetadataRetriever mMediaRetriever = new MediaMetadataRetriever();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        noVideoLayout = (LinearLayout) view.findViewById(R.id.layout_no_video);
        TextView holaTextView = (TextView) view.findViewById(R.id.textView_hola);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GothamRounded-Medium.otf");
        holaTextView.setTypeface(custom_font);
        progress_circular = view.findViewById(R.id.progress_circular);

        TextPaint paint = holaTextView.getPaint();
        float width = paint.measureText(holaTextView.getText().toString());

        /*Shader textShader = new LinearGradient(0, 0, width, 0,
                new int[]{
                        Color.parseColor("#f0156a"),
                        Color.parseColor("#fd5d10"),
                }, null, Shader.TileMode.REPEAT);
        holaTextView.getPaint().setShader(textShader);*/
//        holaTextView.getPaint().setShader(new LinearGradient(0,0,0,holaTextView.getLineHeight(), Color.parseColor("#ee0979"),
//                Color.parseColor("#ff6a00"), Shader.TileMode.REPEAT));

        TextView recordVideoTextView = (TextView) view.findViewById(R.id.textView_record_video);
        recordVideoTextView.setTypeface(custom_font);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //Interstitial
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_full));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            new LoadVideoAsync().execute();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showNoVideoLayout() {
        recyclerView.setVisibility(View.GONE);
        noVideoLayout.setVisibility(View.VISIBLE);
    }

    public void hideNoVideoLayout() {
        recyclerView.setVisibility(View.VISIBLE);
        noVideoLayout.setVisibility(View.GONE);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
            .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public class LoadVideoAsync extends AsyncTask<Void, Void, ArrayList<VideoModel>> {

        @Override
        protected ArrayList<VideoModel> doInBackground(Void... voids) {

            progress_circular.setVisibility(View.VISIBLE);
            File folder = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/Recora");
            ArrayList<File> listVideo = Utils.getAllFile(folder, "video");
            ArrayList<VideoModel> videoModels = new ArrayList<>();
            //use one of overloaded setDataSource() functions to set your data source
            int size = listVideo.size();
            for (int i = 0; i < size; i++) {
                try {
                    mMediaRetriever.setDataSource(getActivity(), Uri.fromFile(listVideo.get(i)));
                } catch (Exception e) {
                    Log.e("Exception", "Remove video i = " + i + " - " + listVideo.get(i).getName());
                    File file = listVideo.get(i);
                    listVideo.remove(i);
                    file.delete();
                    size = size - 1;
                    i = i - 1;
                }
            }

            for (File file: listVideo){
                mMediaRetriever.setDataSource(getActivity(), Uri.fromFile(file));
                String time = mMediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                String timeFormat = String.format("%d min : %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec)));
                videoModels.add(new VideoModel(timeFormat,file));
            }

            return videoModels;

        }

        @Override
        protected void onPostExecute(ArrayList<VideoModel> files) {
            super.onPostExecute(files);
            if (files == null || files.size() == 0) {
                showNoVideoLayout();
            } else {
                hideNoVideoLayout();
            }
            progress_circular.setVisibility(View.GONE);
            galleryAdapter = new GalleryAdapter(files, VideoFragment.this);
            recyclerView.setAdapter(galleryAdapter);
        }
    }
}

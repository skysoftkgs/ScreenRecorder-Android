package com.ami.customui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.analytics.HitBuilders;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.m4m.samples.ComposerAudioEffectActivity;
import org.m4m.samples.ComposerCutActivity;
import org.m4m.samples.ComposerTimeScalingActivity;
import org.m4m.samples.ComposerVideoEffectActivity;

import java.io.File;
import java.util.ArrayList;

import sim.ami.com.myapplication.AppImpl;
import sim.ami.com.myapplication.Constant;
import sim.ami.com.myapplication.GalleryActivity;
import sim.ami.com.myapplication.R;
import sim.ami.com.myapplication.VideoFragment;
import sim.ami.com.myapplication.VideoModel;

/**
 * Created by hi on 5/13/16.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private ArrayList<VideoModel> moviesList;
    private Context context;
    private VideoFragment videoFragment;
    RequestOptions options = new RequestOptions();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, duration, size;
        private CircularImageView thumbanail;
        private ImageView playVideo;
        private ImageView shareAction, magicButton, deleteButton;
        private ImageButton optionImageButton;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textViewName);
            duration = (TextView) view.findViewById(R.id.textViewDuration);
            size = (TextView) view.findViewById(R.id.textViewSize);
            thumbanail = (CircularImageView) view.findViewById(R.id.imageViewThumbanail);
            playVideo = (ImageView) view.findViewById(R.id.imageViewPlay);
            shareAction = (ImageView) view.findViewById(R.id.imageButtonShare);
            magicButton = (ImageView) view.findViewById(R.id.imageButtonEdit);
            deleteButton = (ImageView) view.findViewById(R.id.imageButtonDelete);
            optionImageButton = (ImageButton) view.findViewById(R.id.imageButton_option);
        }
    }


    public GalleryAdapter(ArrayList<VideoModel> moviesList, VideoFragment fragment) {
        this.moviesList = moviesList;
        this.context = fragment.getActivity();
        this.videoFragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.gallery_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        File movie = moviesList.get(position).getFile();
        String time = moviesList.get(position).getTime();
        Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");

        try {
            holder.duration.setText(time);
            holder.duration.setTypeface(custom_font1);
        } catch (Exception e) {

        }

        holder.name.setText(movie.getName());
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
        holder.name.setTypeface(custom_font);

        holder.size.setText("" + movie.length() / (1024 * 1024) + " Mb");
        holder.size.setTypeface(custom_font1);

        Glide.with(this.context)
            .load(movie)
            .apply(options.circleCrop())
            .into(holder.thumbanail);

        //Set tag
//        holder.thumbanail.setTag(holder);
        holder.playVideo.setTag(holder);
        holder.shareAction.setTag(holder);
        holder.deleteButton.setTag(holder);
        holder.magicButton.setTag(holder);
        holder.optionImageButton.setTag(holder);

        holder.thumbanail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
//                int index = myViewHolder.getPosition();
                playResult(position);
            }
        });
//        holder.playVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // Toast.makeText(context,"Click to play media",Toast.LENGTH_SHORT).show();
//                MyViewHolder myViewHolder = (MyViewHolder)v.getTag();
//                int index = myViewHolder.getPosition();
//                playResult(index);
//            }
//        });
        holder.shareAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Click to share",Toast.LENGTH_SHORT).show();
                MyViewHolder myViewHolder = (MyViewHolder) v.getTag();
                int index = myViewHolder.getPosition();
                shareVideo(index);
            }
        });
        holder.magicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder myViewHolder = (MyViewHolder) v.getTag();
                final int index = myViewHolder.getPosition();

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_cut_video:
                                // Toast.makeText(context,"Cut video",Toast.LENGTH_SHORT).show();
                                // AppImpl.tracker().setScreenName("Gallery-Cut-Video");
                                // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index, Constant.CMD_CUT_VIDEO);
                                return true;
                            case R.id.item_video_effect:
                                //Toast.makeText(context,"Video effect",Toast.LENGTH_SHORT).show();
                                // AppImpl.tracker().setScreenName("Gallery-Effect-Video");
                                // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index, Constant.CMD_EFFECT_VICEO);
                                return true;
                            case R.id.item_time_scaling:
                                //Toast.makeText(context,"Time Scaling",Toast.LENGTH_SHORT).show();
                                //  AppImpl.tracker().setScreenName("Gallery-TimeScaling-Video");
                                // AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
                                startEditVideo(index, Constant.CMD_TIME_SCALING);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.popup_edit_menu);
                popupMenu.show();
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context,"Click to delete",Toast.LENGTH_SHORT).show();

                MyViewHolder myViewHolder = (MyViewHolder) v.getTag();
                int index = myViewHolder.getPosition();
                confirmDeleteVideo(index);
            }
        });

        holder.optionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.popupmenu);
                popupMenu.show();
//                popupMenu.getMenu().getItem(3).setEnabled(PreferenceManager.getDefaultSharedPreferences(context)
//                        .getBoolean(
//                                context.getString(R.string.preference_save_gif_key), false));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                shareVideo(holder.getAdapterPosition());
                                break;
                            case R.id.delete:
                                confirmDeleteVideo(holder.getAdapterPosition());
                                break;
                            case R.id.item_cut_video:
                                startEditVideo(holder.getAdapterPosition(), Constant.CMD_CUT_VIDEO);
                                return true;
                            case R.id.item_video_effect:
                                startEditVideo(holder.getAdapterPosition(), Constant.CMD_EFFECT_VICEO);
                                return true;
                            case R.id.item_time_scaling:
                                startEditVideo(holder.getAdapterPosition(), Constant.CMD_TIME_SCALING);
                                return true;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void deleteVideo(int index) {
        //AppImpl.tracker().setScreenName("Gallery-Delete-Video");
        //AppImpl.tracker().send(new HitBuilders.ScreenViewBuilder().build());
        moviesList.remove(index);
        notifyDataSetChanged();
        if (moviesList.size() == 0) {
            videoFragment.showNoVideoLayout();
        }
    }

    public void confirmDeleteVideo(final int index) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    new VideoDelete(moviesList.get(index).getFile()).run();
                    deleteVideo(index);
                    dialog.cancel();
                    showFullAds();
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

    private void shareVideo(int index) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", moviesList.get(index).getFile());
        Log.e("ShareVideo", "URI = " + screenshotUri);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.addFlags(1);
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_video)));
        showFullAds();
    }

    private void startEditVideo(int index, String cmd) {
        Uri screenshotUri = Uri.fromFile(moviesList.get(index).getFile());
        String fileName = moviesList.get(index).getFile().getName();
        switch (cmd) {
            case Constant.CMD_CUT_VIDEO:
                startActivityByClazz(screenshotUri, ComposerCutActivity.class.getName(), fileName);
                break;
            case Constant.CMD_EFFECT_VICEO:
                startActivityByClazz(screenshotUri, ComposerVideoEffectActivity.class.getName(), fileName);
                break;
            case Constant.CMD_REPLACE_AUDIO:
                startActivityByClazz(screenshotUri, ComposerAudioEffectActivity.class.getName(), fileName);
                break;
            case Constant.CMD_TIME_SCALING:
                startActivityByClazz(screenshotUri, ComposerTimeScalingActivity.class.getName(), fileName);
                break;
            default:
                Log.e("ERROR", "UNKNOWN CMD TYPE");
                break;
        }

    }

    private void startActivityByClazz(Uri videoUri, String className, String fileName) {
        Intent intent = null;
        try {
            Log.e("Start Activity", "Video URI = " + videoUri + "fileName = " + fileName);
            intent = new Intent(context, Class.forName(className));
            intent.putExtra(Constant.EXTRA_DATA, videoUri.toString());
            intent.putExtra(Constant.EXTRA_DATA_FILE_NAME, fileName);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("ERROR", "Class " + className + " Not found");
        }

    }

    private void playResult(int index) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri screenshotUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", moviesList.get(index).getFile());
            intent.setDataAndType(screenshotUri, "video/mp4");
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.waring_choose_media, Toast.LENGTH_LONG).show();
        }

    }

    private void showFullAds() {
        GalleryActivity activity = (GalleryActivity) context;
        if (activity != null) {
            ConsentStatus consentStatus = ConsentInformation.getInstance(context).getConsentStatus();
            if (consentStatus.toString().equals("NON_PERSONALIZED")) {
                activity.showFullAd(false);
            } else {
                activity.showFullAd(true);
            }
        }
    }
}

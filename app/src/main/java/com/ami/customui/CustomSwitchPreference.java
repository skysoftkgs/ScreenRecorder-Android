package com.ami.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import sim.ami.com.myapplication.R;

public class CustomSwitchPreference extends SwitchPreferenceCompat {

    private Context context;

    public CustomSwitchPreference(Context context) {
        super(context);
        this.context = context;

    }

    public CustomSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CustomSwitchPreference(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

    }

    @Override
    public final void onBindViewHolder(final PreferenceViewHolder vh) {
        super.onBindViewHolder(vh);
        TextView txt = (TextView) vh.findViewById(android.R.id.title);
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
        txt.setTypeface(custom_font);
        txt.setTextColor(context.getResources().getColor(R.color.videoTitleColor));

        TextView txt1 = (TextView) vh.findViewById(android.R.id.summary);
        Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");
        txt1.setTypeface(custom_font1);
        txt1.setTextColor(context.getResources().getColor(R.color.videoSubtitleColor));

//        Switch theSwitch = (Switch) vh.findViewById(android.R.id.switch_widget);
//        if (theSwitch!=null) {
//            //do styling here
//            theSwitch.setThumbResource(R.drawable.ic_video);
//        }
    }

//    private Switch findSwitchInChildviews(View view) {
//        for (int i=0;i<view.getChildCount();i++) {
//            View thisChildview = view.getChildAt(i);
//            if (thisChildview instanceof Switch) {
//                return (Switch)thisChildview;
//            }
//            else if (thisChildview instanceof  ViewGroup) {
//                Switch theSwitch = findSwitchInChildviews((ViewGroup) thisChildview);
//                if (theSwitch!=null) return theSwitch;
//            }
//        }
//        return null;
//    }
}

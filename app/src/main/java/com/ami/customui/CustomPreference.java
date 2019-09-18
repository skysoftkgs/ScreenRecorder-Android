package com.ami.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import sim.ami.com.myapplication.R;

public class CustomPreference extends Preference {

    private Context context;

    public CustomPreference(Context context) {
        super(context);
        this.context = context;

    }

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CustomPreference(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

    }

    @Override
    public final void onBindViewHolder(final PreferenceViewHolder vh) {
        super.onBindViewHolder(vh);
        TextView txt = (TextView) vh.findViewById(android.R.id.title);
        Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
        txt.setTypeface(custom_font1);
        txt.setTextColor(context.getResources().getColor(R.color.videoTitleColor));
    }
}

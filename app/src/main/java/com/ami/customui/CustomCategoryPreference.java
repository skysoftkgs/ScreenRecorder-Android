package com.ami.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import sim.ami.com.myapplication.R;

public class CustomCategoryPreference extends PreferenceCategory {

    private Context context;

    public CustomCategoryPreference(Context context) {
        super(context);
        this.context = context;

    }

    public CustomCategoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CustomCategoryPreference(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

    }

    @Override
    public final void onBindViewHolder(final PreferenceViewHolder vh) {
        super.onBindViewHolder(vh);
        TextView txt = (TextView) vh.findViewById(android.R.id.title);
        txt.setTextColor(context.getResources().getColor(R.color.videoSubtitleColor));
        Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
        txt.setTypeface(custom_font1);
    }
}

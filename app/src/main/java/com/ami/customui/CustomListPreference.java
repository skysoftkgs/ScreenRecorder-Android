package com.ami.customui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import sim.ami.com.myapplication.R;

/**
 * Created by hi on 5/11/16.
 */
public class CustomListPreference extends ListPreference {

    private Context context;
    public CustomListPreference(Context context) { super(context); this.context = context;}
    public CustomListPreference(Context context, AttributeSet attrs) { super(context, attrs); this.context = context;}
    @Override
    public void setValue(String value) {
        super.setValue(value);
        setSummary(getEntry());
    }
    @Override
    public final void onBindViewHolder(final PreferenceViewHolder vh) {
        super.onBindViewHolder(vh);
        TextView txt = (TextView) vh.findViewById(android.R.id.title);
//        txt.setTextSize(context.getResources().getDimension(R.dimen.preference_list_title));
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
        txt.setTypeface(custom_font);
        txt.setTextColor(context.getResources().getColor(R.color.videoTitleColor));

        TextView txt1 = (TextView) vh.findViewById(android.R.id.summary);
        Typeface custom_font1 = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");
        txt1.setTypeface(custom_font1);
        txt1.setTextColor(context.getResources().getColor(R.color.videoSubtitleColor));
    }

}

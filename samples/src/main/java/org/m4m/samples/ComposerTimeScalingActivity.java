/*
 * Copyright 2014-2016 Media for Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.m4m.samples;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.m4m.samples.controls.TimelineItem;

public class ComposerTimeScalingActivity extends ActivityWithTimeline implements View.OnClickListener {
    TimelineItem mItem;

    private Spinner timeScaleSpinner;
    private int timeScale = 1;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.composer_time_scaling_activity);

        timeScaleSpinner = (Spinner) findViewById(R.id.timescale);
        timeScaleSpinner.setVisibility(View.VISIBLE);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String uriVideo = bundle.getString("EXTRA_DATA");
        String fileName = bundle.getString("EXTRA_DATA_FILE_NAME");
        Log.e("Video Effect","URI = "+uriVideo + "FileName: "+fileName);
        mItem = (TimelineItem) findViewById(R.id.timelineItem);
        mItem.setEventsListener(this);
        mItem.enableSegmentPicker(false);
//        mItem.enableMultipleSegmentPicker(true);
        mItem.setInvisbleOpenDelete();
        org.m4m.Uri uri = new org.m4m.Uri(uriVideo);
        mItem.setMediaUri(uri);
        mItem.setMediaFileName(fileName);

        timeScaleSpinner = (Spinner) findViewById(R.id.timescale);
        ArrayAdapter<CharSequence> adapter = createTimeScalingArrayAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeScaleSpinner.setAdapter(adapter);
        timeScaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = timeScaleSpinner.getSelectedItem().toString().substring(1);
                timeScale = Integer.valueOf(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        (findViewById(R.id.action)).setOnClickListener(this);
    }

    protected ArrayAdapter<CharSequence> createTimeScalingArrayAdapter() {

        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapter.add("x1");
        adapter.add("x2");
        adapter.add("x3");
        adapter.add("x4");

        return adapter;
    }

    public void action() {
        String mediaFileName = mItem.getMediaFileName();

        if (mediaFileName == null) {
            showToast("Please select a valid video file first.");

            return;
        }

        mItem.stopVideoView();

        Intent intent = new Intent();
        intent.setClass(this, ComposerTimeScalingCoreActivity.class);

        Bundle b = new Bundle();
        b.putString("srcMediaName", mItem.getMediaFileName());
        intent.putExtras(b);
        b.putString("dstMediaPath", mItem.genDstPath(mItem.getMediaFileName(), "time_scaling_x" + timeScale));
        intent.putExtras(b);
        b.putString("srcUri", mItem.getUri().getString());
        intent.putExtras(b);
        b.putInt("timeScale", timeScale);
        intent.putExtras(b);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.action) {
            action();

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mItem != null) {
            mItem.updateView();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.m4m.samples.controls.TimelineItem;

import java.util.ArrayList;
import java.util.List;

public class ComposerVideoEffectActivity extends ActivityWithTimeline implements View.OnClickListener {
    TimelineItem mItem;

    Spinner mEffects;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.composer_transcode_activity);

        ((Button)findViewById(R.id.action)).setText("Apply Video Effect");
        ((Spinner)findViewById(R.id.effect)).setVisibility(View.VISIBLE);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mItem != null) {
            mItem.updateView();
        }
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
        mItem.setInvisbleOpenDelete();
        org.m4m.Uri uri = new org.m4m.Uri(uriVideo);
        mItem.setMediaUri(uri);
        mItem.setMediaFileName(fileName);


        ((Button) findViewById(R.id.action)).setOnClickListener(this);

        mEffects = (Spinner) findViewById(R.id.effect);

        fillEffectsList();
    }

    private void fillEffectsList() {
        List<String> list = new ArrayList<String>();

        list.add("Sepia");
        list.add("Grayscale");
        list.add("Inverse");
        list.add("Text Overlay");
        list.add("Black And White");
        list.add("Brightness");
        list.add("Constrant");
        list.add("CrossProcessEffect");
        list.add("Documentary Effect");
        list.add("Duotone Effect");
        list.add("Fill Light Effect");
        list.add("Grain Effect");
        list.add("Lamoish Effect");
        list.add("No Effect");
        list.add("Posterize Effect");
        list.add("Saturation Effect");
        list.add("Sharpness Effect");
        list.add("Temprature Effect");
        list.add("Tint Effect");
        list.add("Vignette Effect");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mEffects.setAdapter(dataAdapter);
    }

    public void action() {
        String mediaFileName = mItem.getMediaFileName();

        if (mediaFileName == null) {
            showToast("Please select a valid video file first");

            return;
        }

        mItem.stopVideoView();

        Intent intent = new Intent();
        intent.setClass(this, ComposerVideoEffectCoreActivity.class);

        Bundle b = new Bundle();
        b.putString("srcMediaName1", mItem.getMediaFileName());
        intent.putExtras(b);
        b.putString("dstMediaPath", mItem.genDstPath(mItem.getMediaFileName(), mItem.getVideoEffectName(mEffects.getSelectedItemPosition())));
        b.putInt("effectIndex", mEffects.getSelectedItemPosition());
        intent.putExtras(b);
        b.putString("srcUri1", mItem.getUri().getString());
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

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
import android.widget.Button;

import org.m4m.samples.controls.TimelineItem;

public class ComposerCutActivity extends ActivityWithTimeline implements View.OnClickListener {
    TimelineItem mItem;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.composer_cut_activity);

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
        mItem.enableSegmentPicker(true);

        org.m4m.Uri uri = new org.m4m.Uri(uriVideo);
        mItem.setMediaUri(uri);
        mItem.setMediaFileName(fileName);
        mItem.setInvisbleOpenDelete();

        ((Button) findViewById(R.id.action)).setOnClickListener(this);
    }

    public void action() {
        String mediaFileName = mItem.getMediaFileName();

        if (mediaFileName == null) {
            showToast("Please select a valid video file first.");

            return;
        }

        mItem.stopVideoView();

        int segmentFrom = mItem.getSegmentFrom();
        int segmentTo = mItem.getSegmentTo();

        Intent intent = new Intent();
        intent.setClass(this, ComposerCutCoreActivity.class);

        Bundle b = new Bundle();
        b.putString("srcMediaName1", mItem.getMediaFileName());
        intent.putExtras(b);
        b.putString("dstMediaPath", mItem.genDstPath(mItem.getMediaFileName(), "cut_video"));
        intent.putExtras(b);
        b.putLong("segmentFrom", segmentFrom);
        intent.putExtras(b);
        b.putLong("segmentTo", segmentTo);
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

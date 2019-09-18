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

import android.os.Bundle;
import org.m4m.IVideoEffect;
import org.m4m.MediaComposer;
import org.m4m.Uri;
import org.m4m.domain.FileSegment;
import org.m4m.effects.BlackAndWhiteEffect;
import org.m4m.effects.BrightnessEffect;
import org.m4m.effects.ConstrantsEffect;
import org.m4m.effects.CrossProcessEffect;
import org.m4m.effects.DocumentaryEffect;
import org.m4m.effects.DuotoneEffect;
import org.m4m.effects.FillLightEffect;
import org.m4m.effects.GrainEffect;
import org.m4m.effects.GrayScaleEffect;
import org.m4m.effects.InverseEffect;
import org.m4m.effects.LamoishEffect;
import org.m4m.effects.NoEffect;
import org.m4m.effects.PosterizeEffect;
import org.m4m.effects.SaturationEffect;
import org.m4m.effects.SepiaEffect;
import org.m4m.effects.SharpnessEffect;
import org.m4m.effects.TempratureEffect;
import org.m4m.effects.TextOverlayEffect;
import org.m4m.effects.TintEffect;
import org.m4m.effects.VignetteEffect;

import java.io.IOException;


public class ComposerVideoEffectCoreActivity extends ComposerTranscodeCoreActivity {

    private int effectIndex;

    @Override
    protected void getActivityInputs() {

        Bundle b = getIntent().getExtras();
        srcMediaName1 = b.getString("srcMediaName1");
        dstMediaPath = b.getString("dstMediaPath");
        mediaUri1 = new Uri(b.getString("srcUri1"));

        effectIndex = b.getInt("effectIndex");
    }

    @Override
    protected void setTranscodeParameters(MediaComposer mediaComposer) throws IOException {
        mediaComposer.addSourceFile(mediaUri1);
        mediaComposer.setTargetFile(dstMediaPath);

        configureVideoEncoder(mediaComposer, videoWidthOut, videoHeightOut);
        configureAudioEncoder(mediaComposer);

        configureVideoEffect(mediaComposer);
    }

    private void configureVideoEffect(MediaComposer mediaComposer) {
        IVideoEffect effect = null;

        switch (effectIndex) {
            case 0:
                effect = new SepiaEffect(0, factory.getEglUtil());
                break;
            case 1:
                effect = new GrayScaleEffect(0, factory.getEglUtil());
                break;
            case 2:
                effect = new InverseEffect(0, factory.getEglUtil());
                break;
            case 3:
                effect = new TextOverlayEffect(0, factory.getEglUtil());
                break;
            case 4:
                effect = new BlackAndWhiteEffect(0,factory.getEglUtil());
                break;
            case 5:
                effect = new BrightnessEffect(0,factory.getEglUtil(),(float)1.4);
                break;
            case 6:
                effect = new ConstrantsEffect(0,factory.getEglUtil(),(float)1.5);
                break;
            case 7:
                effect = new CrossProcessEffect(0,factory.getEglUtil());
                break;
            case 8:
                effect = new DocumentaryEffect(0,factory.getEglUtil(),videoWidthIn,videoHeightOut);
                break;
            case 9:
                effect = new DuotoneEffect(0,factory.getEglUtil(),100,200);
                break;
            case 10:
                effect = new FillLightEffect(0,factory.getEglUtil(),(float)0.5);
                break;
            case 11:
                effect = new GrainEffect(0,factory.getEglUtil(),(float)0.5,videoWidthIn,videoHeightOut);
                break;
            case 12:
                effect = new LamoishEffect(0,factory.getEglUtil(),videoWidthIn,videoHeightOut);
                break;
            case 13:
                effect = new NoEffect(0,factory.getEglUtil());
                break;
            case 14:
                effect = new PosterizeEffect(0,factory.getEglUtil());
                break;
            case 15:
                effect = new SaturationEffect(0,factory.getEglUtil(),0);
                break;
            case 16:
                effect = new SharpnessEffect(0,factory.getEglUtil(),(float)0.5,videoWidthIn,videoHeightOut);
                break;
            case 17:
                effect = new TempratureEffect(0,factory.getEglUtil(),0);
                break;
            case 18:
                effect = new TintEffect(0,factory.getEglUtil(),128);
                break;
            case 19:
                effect = new VignetteEffect(0,factory.getEglUtil(),(float)0.5,videoWidthIn,videoHeightOut);
                break;
            default:
                break;
        }

        if (effect != null) {
            effect.setSegment(new FileSegment(0l, 0l)); // Apply to the entire stream
            mediaComposer.addVideoEffect(effect);
        }
    }

    @Override
    protected void printEffectDetails() {
        effectDetails.append(String.format("Video effect = %s\n", getVideoEffectName(effectIndex)));
    }

    private String getVideoEffectName(int videoEffectIndex) {
        switch (videoEffectIndex) {
            case 0:
                return   "Sepia";
            case 1:
                return "Grayscale";
            case 2:
                return "Inverse";
            case 3:
                return "Text Overlay";
            case 4:
                return "Black and White";
            case 5:
                return "Brighteness";
            case 6:
                return "Constrants";
            case 7:
                return "CrossProcess";
            case 8:
                return "Documentary Effect";
            case 9:
                return "DouTone Effect";
            case 10:
                return "FillLightEffect";
            case 11:
                return "GrainEffect";
            case 12:
                return "LamoishEffect";
            case 13:
                return "NoEffect";
            case 14:
                return "PosterizeEffect";
            case 15:
                return "SaturationEffect";
            case 16:
                return "SharpnessEffect";
            case 17:
                return "TemperatureEffect";
            case 18:
                return "TintEffect";
            case 19:
                return "VignetteEffect";
            default:
                return "Unknown";
        }
    }
}



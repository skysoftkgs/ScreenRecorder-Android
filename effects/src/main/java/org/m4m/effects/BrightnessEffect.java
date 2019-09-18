package org.m4m.effects;

import org.m4m.android.graphics.VideoEffect;
import org.m4m.domain.graphics.IEglUtil;

/**
 * Created by hi on 5/9/16.
 */
public class BrightnessEffect extends VideoEffect {
    public BrightnessEffect(int angle, IEglUtil eglUtil) {
        super(angle, eglUtil);
    }
    private float brightnessValue;
    /**
     * Initialize Effect
     *
     * @param brightnessValue
     *            Range should be between 0.1- 2.0 with 1.0 being normal.
     */
    public BrightnessEffect(int angle, IEglUtil eglUtil, float brightnessValue) {
        super(angle, eglUtil);
        if (brightnessValue < 0.1f)
            brightnessValue = 0.1f;
        if (brightnessValue > 2.0f)
            brightnessValue = 2.0f;

        this.brightnessValue = brightnessValue;
        setFragmentShader(getFragmentShader());
    }

    protected String getFragmentShader() {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "float brightness ;\n" + "varying vec2 vTextureCoord;\n"
                + "void main() {\n" + "  brightness =" + brightnessValue
                + ";\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  gl_FragColor = brightness * color;\n" + "}\n";
    }
}

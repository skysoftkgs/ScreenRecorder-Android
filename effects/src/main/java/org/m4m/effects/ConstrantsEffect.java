package org.m4m.effects;

import org.m4m.android.graphics.VideoEffect;
import org.m4m.domain.graphics.IEglUtil;

/**
 * Created by hi on 5/9/16.
 */
public class ConstrantsEffect extends VideoEffect {
    private float contrast;

    /**
     * Initialize Effect
     *
     * @paramcontrast
     *            Range should be between 0.1- 2.0 with 1.0 being normal.
     */

    public ConstrantsEffect(int angle, IEglUtil eglUtil) {
        super(angle, eglUtil);
        setFragmentShader(getFragmentShader());
    }
    public ConstrantsEffect(int angle, IEglUtil eglUtil, float contrast) {
        super(angle, eglUtil);
        if (contrast < 0.1f)
            contrast = 0.1f;
        if (contrast > 2.0f)
            contrast = 2.0f;

        this.contrast = contrast;
        setFragmentShader(getFragmentShader());
    }
    protected String getFragmentShader() {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + " float contrast;\n" + "varying vec2 vTextureCoord;\n"
                + "void main() {\n" + "  contrast =" + contrast + ";\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  color -= 0.5;\n" + "  color *= contrast;\n"
                + "  color += 0.5;\n" + "  gl_FragColor = color;\n" + "}\n";
    }

}

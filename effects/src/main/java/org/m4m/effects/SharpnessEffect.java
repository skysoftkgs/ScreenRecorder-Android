package org.m4m.effects;

import org.m4m.android.graphics.VideoEffect;
import org.m4m.domain.graphics.IEglUtil;

/**
 * Created by hi on 5/9/16.
 */
public class SharpnessEffect extends VideoEffect {
    private int mWidth;
    private int mHeight;
    private float scale = 0f;

    /**
     * Initialize Effect
     *
     * @param:scale
     *            Float, between 0 and 1. 0 means no change.
     */

    public SharpnessEffect(int angle, IEglUtil eglUtil, float scale, int mWidth, int mHeight) {
        super(angle, eglUtil);
        if (scale < 0.0f)
            scale = 0.0f;
        if (scale > 1.0f)
            scale = 1.0f;
        this.scale = scale;
        initValues(mWidth,mHeight);
        setFragmentShader(getShader(scale));
    }
    protected String getShader(float scale){
        String stepsizeXString = "stepsizeX = " + 1.0f / mWidth + ";\n";
        String stepsizeYString = "stepsizeY = " + 1.0f / mHeight + ";\n";
        String scaleString = "scale = " + scale + ";\n";

        String shader = "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + " float scale;\n"
                + " float stepsizeX;\n"
                + " float stepsizeY;\n"
                + "varying vec2 vTextureCoord;\n"
                + "void main() {\n"
                // Parameters that were created above
                + stepsizeXString
                + stepsizeYString
                + scaleString
                + "  vec3 nbr_color = vec3(0.0, 0.0, 0.0);\n"
                + "  vec2 coord;\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  coord.x = vTextureCoord.x - 0.5 * stepsizeX;\n"
                + "  coord.y = vTextureCoord.y - stepsizeY;\n"
                + "  nbr_color += texture2D(sTexture, coord).rgb - color.rgb;\n"
                + "  coord.x = vTextureCoord.x - stepsizeX;\n"
                + "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n"
                + "  nbr_color += texture2D(sTexture, coord).rgb - color.rgb;\n"
                + "  coord.x = vTextureCoord.x + stepsizeX;\n"
                + "  coord.y = vTextureCoord.y - 0.5 * stepsizeY;\n"
                + "  nbr_color += texture2D(sTexture, coord).rgb - color.rgb;\n"
                + "  coord.x = vTextureCoord.x + stepsizeX;\n"
                + "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n"
                + "  nbr_color += texture2D(sTexture, coord).rgb - color.rgb;\n"
                + "  gl_FragColor = vec4(color.rgb - 2.0 * scale * nbr_color, color.a);\n"
                + "}\n";

        return shader;

    }
    private void initValues(int mWidth, int mHeight){
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }
}

package org.m4m.effects;

import org.m4m.android.graphics.VideoEffect;
import org.m4m.domain.graphics.IEglUtil;

/**
 * Created by hi on 5/9/16.
 */
public class VignetteEffect extends VideoEffect {
    private int mWidth = 0;
    private int mHeight = 0;
    private float mScale = 0f;
    private final float mShade = 0.85f;

    /**
     * Initialize Effect
     *
     * @param:scale
     *            Float, between 0 and 1. 0 means no change.
     */

    public VignetteEffect(int angle, IEglUtil eglUtil, float mScale, int mWidth, int mHeight) {
        super(angle, eglUtil);
        if (mScale < 0.0f)
            mScale = 0.0f;
        if (mScale > 1.0f)
            mScale = 1.0f;
        this.mScale = mScale;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        setFragmentShader(getShader());
    }
    protected String getShader(){
        float scale[] = new float[2];
        if (mWidth > mHeight) {
            scale[0] = 1f;
            scale[1] = ((float) mHeight) / mWidth;
        } else {
            scale[0] = ((float) mWidth) / mHeight;
            scale[1] = 1f;
        }
        float max_dist = ((float) Math.sqrt(scale[0] * scale[0] + scale[1]
                * scale[1])) * 0.5f;

        String scaleString[] = new String[2];

        scaleString[0] = "scale[0] = " + scale[0] + ";\n";
        scaleString[1] = "scale[1] = " + scale[1] + ";\n";
        String inv_max_distString = "inv_max_dist = " + 1.0f / max_dist + ";\n";
        String shadeString = "shade = " + mShade + ";\n";

        // The 'range' is between 1.3 to 0.6. When scale is zero then range is
        // 1.3
        // which means no vignette at all because the luminousity difference is
        // less than 1/256 and will cause nothing.
        String rangeString = "range = "
                + (1.30f - (float) Math.sqrt(mScale) * 0.7f) + ";\n";

        String shader = "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + " float range;\n"
                + " float inv_max_dist;\n"
                + " float shade;\n"
                + " vec2 scale;\n"
                + "varying vec2 vTextureCoord;\n"
                + "void main() {\n"
                // Parameters that were created above
                + scaleString[0]
                + scaleString[1]
                + inv_max_distString
                + shadeString
                + rangeString
                + "  const float slope = 20.0;\n"
                + "  vec2 coord = vTextureCoord - vec2(0.5, 0.5);\n"
                + "  float dist = length(coord * scale);\n"
                + "  float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);\n"
                + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                + "  gl_FragColor = vec4(color.rgb * lumen, color.a);\n"
                + "}\n";

        return shader;

    }
}

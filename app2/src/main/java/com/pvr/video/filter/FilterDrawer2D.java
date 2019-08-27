package com.pvr.video.filter;

import android.opengl.GLES20;

public class FilterDrawer2D extends FilterDrawer {

    private final String vertexShaderCode =
            "attribute vec4 av_Position; \n" +
                    "attribute vec2 af_Position; \n" +
                    "varying vec2 v_texPo; \n" +
                    "void main() { \n" +
                    "   gl_Position = av_Position; \n" +
                    "   v_texPo = af_Position; \n" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float; \n" +
                    "varying vec2 v_texPo;\n" +
                    "uniform sampler2D s_Texture;\n" +
                    "void main() { \n" +
                    "   vec4 tc = texture2D(s_Texture, v_texPo);\n" +
                    "   float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;\n"+
                    "   gl_FragColor = vec4(color, color, color, 1.0);\n" +
                    "}";

    private int av_Position;
    private int af_Position;
    private int s_Texture;
    private int textureId;

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    @Override
    protected String getVertexSource() {
        return vertexShaderCode;
    }

    @Override
    protected String getFragmentSource() {
        return fragmentShaderCode;
    }

    @Override
    protected void onChanged(int width, int height) {
        av_Position = GLES20.glGetAttribLocation(mProgram, "av_Position");
        af_Position = GLES20.glGetAttribLocation(mProgram, "af_Position");
        s_Texture = GLES20.glGetUniformLocation(mProgram, "s_Texture");
    }

    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(av_Position);
        GLES20.glEnableVertexAttribArray(af_Position);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
        GLES20.glVertexAttribPointer(av_Position, 2, GLES20.GL_FLOAT, false, 0, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mDisplayTextureBufferId);
        GLES20.glVertexAttribPointer(af_Position, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(s_Texture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(af_Position);
        GLES20.glDisableVertexAttribArray(av_Position);
    }

}

package com.pvr.video.filter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FilterRenderTexture {

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
//                    "   gl_FragColor = texture2D(s_Texture, v_texPo);\n" +
                    "}";

    static float vertexData[] = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
    };

    static float textureData[] = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int av_Position;
    private int af_Position;
    private int s_Texture;

    private int program;

    private int textureId;

    private Context context;
    private int width;
    private int height;

    public FilterRenderTexture(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

        program = Utils.buildProgram(vertexShaderCode, fragmentShaderCode);
        av_Position = GLES20.glGetAttribLocation(program, "av_Position");
        af_Position = GLES20.glGetAttribLocation(program, "af_Position");
        s_Texture = GLES20.glGetUniformLocation(program, "s_Texture");
    }

    public void surfaceChanged(int width, int height, int textureId){
        this.width = width;
        this.height = height;
        this.textureId = textureId;
    }

    public void draw(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(program);

        GLES20.glViewport(0, 0, width, height);

        GLES20.glEnableVertexAttribArray(av_Position);
        GLES20.glVertexAttribPointer(av_Position, 2, GLES20.GL_FLOAT, false, 2*4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(af_Position);
        GLES20.glVertexAttribPointer(af_Position, 2, GLES20.GL_FLOAT, false, 2*4, textureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(s_Texture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(af_Position);

        GLES20.glDisableVertexAttribArray(av_Position);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

}

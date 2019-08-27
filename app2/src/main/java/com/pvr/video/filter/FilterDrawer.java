package com.pvr.video.filter;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class FilterDrawer {

    protected int mWidth;

    protected int mHeight;

    protected int mProgram;

    private FloatBuffer mVertexBuffer;
    protected int mVertexBufferId;

    private FloatBuffer mTextureBuffer;
    protected int mTextureBufferId;

    private FloatBuffer mDisplayTextureBuffer;
    protected int mDisplayTextureBufferId;

    protected float mVertexData[] = {
            -1f, -1f,// 左下角
            1f, -1f, // 右下角
            -1f, 1f, // 左上角
            1f, 1f,  // 右上角
    };

    protected float mTextureData[] = {
            0f, 1f, // 左上角
            0f, 0f, //  左下角
            1f, 1f, // 右上角
            1f, 0f  // 右上角
    };

    protected float mDisplayTextureData[] = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

    public void create() {
        mProgram = createProgram(getVertexSource(), getFragmentSource());
        initVertexBufferObjects();
    }

    public void surfaceChangedSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        onChanged(width, height);
    }

    public void draw(long timestamp, float[] transformMatrix){
        clear();
        GLES20.glUseProgram(mProgram);
        GLES20.glViewport(0, 0, mWidth, mHeight);
        onDraw();
    }

    protected void clear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    protected void initVertexBufferObjects() {
        int[] vbo = new int[3];
        GLES20.glGenBuffers(vbo.length, vbo, 0);

        mVertexBuffer = ByteBuffer.allocateDirect(mVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVertexData);
        mVertexBuffer.position(0);
        mVertexBufferId = vbo[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexData.length * 4, mVertexBuffer, GLES20.GL_STATIC_DRAW);

        mTextureBuffer = ByteBuffer.allocateDirect(mTextureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mTextureData);
        mTextureBuffer.position(0);
        mTextureBufferId = vbo[1];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTextureBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mTextureData.length * 4, mTextureBuffer, GLES20.GL_STATIC_DRAW);

        mDisplayTextureBuffer = ByteBuffer.allocateDirect(mDisplayTextureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mDisplayTextureData);
        mDisplayTextureBuffer.position(0);
        mDisplayTextureBufferId = vbo[2];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mDisplayTextureBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mDisplayTextureData.length * 4, mDisplayTextureBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, mVertexShader);
        GLES20.glAttachShader(program, mFragmentShader);
        GLES20.glLinkProgram(program);
        int [] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(program);
            return 0;
        }
        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteShader(mFragmentShader);
        return program;
    }

    private int loadShader(int shaderType, String shaderSource) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        int status[] = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    protected abstract String getVertexSource();
    protected abstract String getFragmentSource();
    protected abstract void onChanged(int width, int height);
    protected abstract void onDraw();

}

package com.pvr.video.filter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FilterGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private boolean mFrameAvailable;
    private int mVideoTextureId;
    private int mTextureId;
    private FilterFBOTexture mFilterFBOTexture;
    private FilterRenderTexture mFilter2DTexture;

    public FilterGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FilterGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("VideoFilter", "onSurfaceCreated");
        mVideoTextureId = Utils.createVideoTextureID();
        mSurfaceTexture = new SurfaceTexture(mVideoTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mFilterFBOTexture = new FilterFBOTexture(mContext, mVideoTextureId);
        mFilter2DTexture = new FilterRenderTexture(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i("VideoFilter", "onSurfaceChanged, width is "+width+", height is "+height);
        mTextureId = Utils.create2DTextureId(width, height);
        mFilterFBOTexture.surfaceChanged(width, height, mTextureId);
        mFilter2DTexture.surfaceChanged(width, height, mTextureId);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(mFrameAvailable){
            Log.i("VideoFilter", "onDrawFrame");
            mSurfaceTexture.updateTexImage();
            mFilterFBOTexture.draw();
            mFilter2DTexture.draw();
            mFrameAvailable = false;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i("VideoFilter", "onFrameAvailable");
        mFrameAvailable = true;
        requestRender();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}

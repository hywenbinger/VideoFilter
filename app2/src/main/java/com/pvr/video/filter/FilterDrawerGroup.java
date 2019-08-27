package com.pvr.video.filter;

import android.opengl.GLES20;

public class FilterDrawerGroup {

    private int width, height;

    private int cameraTextureId;
    private int textureId;
    private int fboId;

    private FilterDrawerOES drawerOES;
    private FilterDrawer2D drawer2D;

    public FilterDrawerGroup(int cameraTextureId) {
        this.cameraTextureId = cameraTextureId;
        drawerOES = new FilterDrawerOES();
        drawerOES.create();
        drawer2D = new FilterDrawer2D();
        drawer2D.create();
    }

    public void surfaceChangedSize(int width, int height) {
        this.width = width;
        this.height = height;
        createFBO();
        create2DTexture();
        drawerOES.setCameraTextureId(cameraTextureId);
        drawerOES.surfaceChangedSize(width, height);
        drawer2D.setTextureId(textureId);
        drawer2D.surfaceChangedSize(width, height);
    }

    public void draw(long timestamp, float[] mtx){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        drawerOES.draw(timestamp, mtx);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        drawer2D.draw(timestamp, mtx);
    }

    private void createFBO(){
        int[] fbo = new int[1];
        GLES20.glGenFramebuffers(fbo.length, fbo, 0);
        fboId = fbo[0];
    }

    private void create2DTexture(){
        int[] textures = new int[1];
        GLES20.glGenTextures(textures.length, textures ,0);
        textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
    }

}

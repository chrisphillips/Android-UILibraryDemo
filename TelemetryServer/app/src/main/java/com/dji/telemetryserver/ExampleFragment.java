package com.dji.telemetryserver;

import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;


/**
 * Created by chris on 2/15/2018.
 */
public class ExampleFragment extends Fragment implements TextureView.SurfaceTextureListener{

    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null; //Decodes the raw H264 video format

    protected TextureView mVideoSurface = null;
    private ImageView mOutputView = null;
    private static ExampleFragment mInstance = null;
    public static ExampleFragment getInstance(){
        if(mInstance == null){
            mInstance = new ExampleFragment();
        }
        return mInstance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initPreview()
    {
        //todo. check product here?
        TelemetryService.getInstance().log("Install Hook");

        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                //Log.d(TAG, "onResult: Data Received");
                if(mCodecManager != null){
                    TelemetryService.getInstance().log(""+size);
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        mVideoSurface = (TextureView) v.findViewById(R.id.videoTextureView);
        mOutputView = (ImageView) v.findViewById(R.id.videoImageView);
        return v;
        //return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
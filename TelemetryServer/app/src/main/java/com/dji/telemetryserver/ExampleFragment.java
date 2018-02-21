package com.dji.telemetryserver;

import android.app.Fragment;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;


/**
 * Created by chris on 2/15/2018.
 */
public class ExampleFragment extends Fragment implements TextureView.SurfaceTextureListener{

    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected VideoFeeder.VideoDataCallback mOrigReceivedVideoDataCallBack=null;
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
        TelemetryService.Log("Install Hook");
//        mVideoSurface = (TextureView) mView.findViewById(R.id.videoTextureView);
        //TelemetryService.Log("Using Surface"+mVideoSurface.toString());

        if(mReceivedVideoDataCallBack == null) {
            mOrigReceivedVideoDataCallBack=VideoFeeder.getInstance().getPrimaryVideoFeed().getCallback();
            mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {
                @Override
                public void onReceive(byte[] videoBuffer, int size) {
                    //Log.d(TAG, "onResult: Data Received");
//                    TelemetryService.Log("" + size);
                    if (mCodecManager != null) {
                        mCodecManager.sendDataToDecoder(videoBuffer, size);
                    }
                    mOrigReceivedVideoDataCallBack.onReceive(videoBuffer,size);
                }
            };
        }
        TelemetryService.Log("Pre Create Listener");
        if (null != mVideoSurface) {
            TelemetryService.Log("Create Listener");
//            mVideoSurface.setSurfaceTextureListener(this);
        }
        TelemetryService.Log("Post Create Listener");
        if(VideoFeeder.getInstance().getPrimaryVideoFeed().getCallback()!=mReceivedVideoDataCallBack)
            VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);
    }
    View mView=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_example, container, false);
        //mVideoSurface = (TextureView) v.findViewById(R.id.videoTextureView);

//        mView=v;

//        TelemetryService.Log("Created Surface"+mVideoSurface.toString());

        //mOutputView = (ImageView) v.findViewById(R.id.videoImageView);
        //return v;
        return inflater.inflate(R.layout.fragment_example, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        TelemetryService.Log("Create Codec");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(getContext(), surfaceTexture, width, height);

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        TelemetryService.Log("on Surface Texture Updated ");

    }
}
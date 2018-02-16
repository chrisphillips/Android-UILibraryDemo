package com.dji.telemetryserver;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.base.BaseProduct;

/**
 * Fragment. Displays live video feed of the drone and the people detected.
 * @author Stanley C
 * @since 10/31/16.
 */
public class VideoFragment extends Fragment implements TextureView.SurfaceTextureListener{
    private static final String TAG = "VideoFragment";


    public interface VideoListener{
        void onViewInit();
    }
    private VideoListener mListener;

    /**
     * Default Constructor. DO NOT USE. <br/>
     * Should only be called by getInstance!
     */
    public VideoFragment(){}

    /**
     * Returns instance of VideoFragment. Creates one if an instance does not exist.
     * @return VideoFragment The single instance.
     */
    //Singleton class
    private static VideoFragment mInstance = null;
    public static VideoFragment getInstance(VideoListener listener){
        if(mInstance == null){
            mInstance = new VideoFragment();
        }
        mInstance.mListener = listener;
        return mInstance;
    }

    //UI
    protected TextureView mVideoSurface = null;
    private ImageView mOutputView = null;

    //DJI
    //private DJICamera mCamera;
    //protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null; //Decodes the raw H264 video format

    //Video Processing
    private Executor mThread = Executors.newSingleThreadExecutor();
    private final static int NUM_FRAME_SKIP = 30;
    private int mCounter = 0;

    /*
     * Fragment startup configuration.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(true)return;
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                Log.d(TAG, "onResult: Data Received");
                if(mCodecManager != null){
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

    }

    /*
     * Initialize fragment layout UI
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        mVideoSurface = (TextureView) v.findViewById(R.id.videoTextureView);
        mOutputView = (ImageView) v.findViewById(R.id.videoImageView);
        return v;
    }

    /**
     * Prepares live video stream on fragment layout
     */
    public void initPreviewer(BaseProduct mProduct) {
        Log.d(TAG, "initPreviewer: ");
if(true)return;

        setResultToToast(TAG + ": initPreviewer start");

/*        try {
            mProduct = MainActivity.getProductInstance();
        }catch (Exception exception) {
            mProduct = null;
        }*/

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);

/*        if (null == mProduct || !mProduct.isConnected()) {
            if(mProduct == null)
                setResultToToast(TAG + ": Error: mProduct null");
            else
                setResultToToast(TAG + ": Error: mProduct not connected");

            mCamera = null;
        } else {
            if (!mProduct.getModel().equals(DJIBaseProduct.Model.UnknownAircraft)) {
                setResultToToast(TAG + ": Get Camera");
                mCamera = mProduct.getCamera();
                if (mCamera != null){
                    setResultToToast(TAG + ": Set Camera callback");
                    mCamera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
                }
            }
        }*/
    }

    /*
     * Reset live video stream callback
     */
    private void uninitPreviewer() {
/*        if (mCamera != null){
            // Reset the callback
            mCamera.setDJICameraReceivedVideoDataCallback(null);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
if(true)return;
        setResultToToast(TAG + ": onStart");
        if (mVideoSurface != null) {
            mVideoSurface.setSurfaceTextureListener(this);
            setResultToToast(TAG + ": mVideoSurface set");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
if(true)return;

        mListener.onViewInit();
    }

    /*
     * Callback function. Called when phone is about to pause/leave the app (user switches screen)
     */
    @Override
    public void onPause() {
        super.onPause();

        //Save battery. Turn off live video feed.
        //uninitPreviewer();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(getContext(), surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG,"onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    /*
     * Called whenever surface texture is updated.
     * In this context, when the mCodecManager is done decoding the byte stream
     * and has published the update on the screen.
     * @param surface
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.w(TAG, "onSurfaceTextureUpdated: Start");
if(true)return;

        //We need to skip people detection occasionally due to it being computationally expensive
        //Check counter
        if(mCounter < NUM_FRAME_SKIP){
            //Skip frame
            mCounter ++;
            return;
        } else{
            //Reset counter
            mCounter = 0;
        }

        /*
         * Use OpenCV to detect any people in the video feed.
         * If anyone is detected, display a bounding box around the person.
         */
        final Bitmap image = mVideoSurface.getBitmap();

        // Perform image processing here

        Log.w(TAG, "onSurfaceTextureUpdated: Start Output Bitamp");
        //Push to bitmap and update

        //Bitmap output2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rawOutput, output);
//        mOutputView.setImageBitmap(output);
    }

    /*
     * Push messages to the device screen
     * @param string
     */
    private void setResultToToast(final String string){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.dji.telemetryserver;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.dji.videostreamdecodingsample.media.DJIVideoStreamDecoder;
import com.dji.videostreamdecodingsample.media.NativeHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

/**
 * Created by djacc on 9/26/2016.
 */

public class VideoFragment extends Fragment implements TextureView.SurfaceTextureListener, View.OnClickListener,DJIVideoStreamDecoder.IYuvDataListener   {

    private static final String TAG = VideoFragment.class.getName();
    private VideoFragment.OnFragmentInteractionListenerVideo mListener;
    protected TextureView mVideo_texture = null;
    protected SurfaceView mVideo_surface = null;
    protected SurfaceView mVideo_mysurface = null;

    // Codec for video live view
    protected DJICodecManager mCodecManager = null;

    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    //private DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallback = null;

    //private DJILBAirLink.DJIOnReceivedVideoCallback mOnReceivedVideoCallback = null;

    private BaseProduct mProduct = null;
    private Camera mCamera = null;

    private static VideoFragment mInstance = null;
    public static VideoFragment getInstance(){
        return mInstance;
    }

    public VideoFragment() {
        // Required empty public constructor
        mInstance=this;
    }
    public void appendToFile(String filename, ByteBuffer bbuf)
    {
        // Write bbuf to filename
        //ByteBuffer bbuf = getMyData();
        File file = new File(filename);
        boolean append = true;
        try {
            // Create a writable file channel
            FileChannel wChannel = new FileOutputStream(file, append).getChannel();

            // Write the ByteBuffer contents; the bytes between the ByteBuffer's
            // position and the limit is written to the file
            wChannel.write(bbuf);

            // Close the file
            wChannel.close();
        } catch (IOException e) {
        }
    }
    private byte[] getDefaultKeyFrame(int width) throws IOException {
        //int iframeId=getIframeRawId(product.getModel(), width);
        int iframeId = R.raw.iframe_1280x720_ins;
        if (iframeId >= 0){

            InputStream inputStream = getContext().getResources().openRawResource(iframeId);
            int length = inputStream.available();
            //logd("iframeId length=" + length);
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            inputStream.close();

            return buffer;
        }
        return null;
    }
    private String videoOutName = Environment.getExternalStorageDirectory() + "/DJI_Telemetry/out.h264";
    public void createH264(String fileName) {
        byte[] iframe= new byte[0];
        try {
            iframe = getDefaultKeyFrame(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String videoOutName = fileName;
        //udpClient.send(ByteBuffer.wrap(iframe,0,iframe.length));

        File file = new File(videoOutName);
        file.delete();
        appendToFile(videoOutName,ByteBuffer.wrap(iframe,0,iframe.length));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);

        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newViewCreated(view);

        try {
            initVideoCallback();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initVideoCallback() throws IOException {

        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive( byte[] videoBuffer, int size ) {
                //TelemetryService.LogDebug"codec onReceive");

                if(origCallback!=null)
                {
                    //TelemetryService.LogDebug"codec passthru");
                    //origCallback.onReceive(videoBuffer,size);
                }
                if (mCodecManager != null) {
                    // Send the raw H264 video data to codec manager for decoding
                    //TelemetryService.LogDebug"codec onReceive to DJI");
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }

                boolean useFF = true;
                if (useFF) {
                    //TelemetryService.LogDebug"codec onReceive to DJIVideoStreamDecoder");
                    DJIVideoStreamDecoder.getInstance().parse(videoBuffer, size);
                }

            }
        };



    }

    @Override
    public void onResume() {
        TelemetryService.LogDebug("codec onResume");
//new
 //createSurfaceListeners();
//        setVideoCallbacks();

        super.onResume();
        if (mVideo_texture == null) {
            Log.e(TAG, "video texture surface is null");
        }
    }

    @Override
    public void onPause() {
        TelemetryService.LogDebug("codec onPause");
//        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        TelemetryService.LogDebug("codec onDestroy");
//        uninitPreviewer();
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (VideoFragment.OnFragmentInteractionListenerVideo) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        uninitPreviewer();
        mListener = null;
    }



    @Override
    public void onClick(View v) {
    //mCodecManager = new DJICodecManager(getActivity(), mVideo_texture.getSurfaceTexture(), mVideo_texture.getWidth(), mVideo_texture.getHeight());
    //setVideoCallbacks();
    //DJIVideoStreamDecoder.getInstance().changeSurface(null);

    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        TelemetryService.LogDebug("codec onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(getActivity(), surfaceTexture, width, height);
            setVideoCallbacks();

            //todo: cleanup in destroyed.
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        //TelemetryService.LogDebug"codec onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        //TelemetryService.LogDebug"codec onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    public interface OnFragmentInteractionListenerVideo {
        public void onFragmentInteractionVideo(boolean bl_OnTopJustClicked);
    }


/////////////////////////////////////////////////////////////////

    private SurfaceView videostreamPreviewSf;

    private View myView=null;
    private void newViewCreated(View view)
    {
        //todo factor this out.
        mVideo_texture = (TextureView) view.findViewById(R.id.livestream_preview_ttv);
        TelemetryService.LogDebug("codec newViewCreated!");
        myView=view;
        //videostreamPreviewSf = (SurfaceView) view.findViewById(R.id.mypreview);
    }


    private boolean listenerInstalled=false;
    public void notifyProductUpdate()
    {
        TelemetryService.LogDebug("codec notifyProductUpdate");

        if(VideoFeeder.getInstance()==null || VideoFeeder.getInstance().getPrimaryVideoFeed()==null)
        {
            TelemetryService.LogDebug("codec notifyProductUpdate Camera unavalible");
            return;
        }

        if(myView!=null) {
            TelemetryService.LogDebug("codec myView "+listenerInstalled);

            if(myView==null)
                mVideo_texture = (TextureView) myView.findViewById(R.id.livestream_preview_ttv);

            if (mVideo_texture!=null && !listenerInstalled) {
                TelemetryService.LogDebug("codec notifyProductUpdate setSurfaceTextureListener");
                mVideo_texture.setSurfaceTextureListener(this);
                listenerInstalled=true;
            }
            if (mCodecManager == null) {
                TelemetryService.LogDebug("codec notifyProductUpdate new DJICodecManager");

                //force init at start.
                mCodecManager = new DJICodecManager(getActivity(), mVideo_texture.getSurfaceTexture(), mVideo_texture.getWidth(), mVideo_texture.getHeight());
                setVideoCallbacks();
            }

        }
        //for other codec.
        createSurfaceListeners();
    }
    private VideoFeeder.VideoDataCallback origCallback = null;
    private void setVideoCallbacks() {
        if(VideoFeeder.getInstance()!=null && VideoFeeder.getInstance().getPrimaryVideoFeed()!=null) {
            VideoFeeder.VideoDataCallback curCallback = VideoFeeder.getInstance().getPrimaryVideoFeed().getCallback();
            if ((curCallback == null) || (curCallback != mReceivedVideoDataCallBack)) {
                TelemetryService.LogDebug("codec setVideoCallbacks");
                if(origCallback==null)
                    TelemetryService.LogDebug("codec origCallback is NULL");
                origCallback=curCallback;
                VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);
//            createH264(videoOutName);
            }
        }
//        mProduct = DjiApplication.getProductInstance();
//        mCamera = mProduct.getCamera();
//        if (mCamera != null) {
//            mCamera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallback);
//        }
    }
    boolean surfaceReady=false;
    private void createSurfaceListeners() {
        if(surfaceReady)
            return;
        surfaceReady=true;
        TelemetryService.LogDebug("codec createSurfaceListeners()");

        videostreamPreviewSf = (SurfaceView) myView.findViewById(R.id.mypreview);
        SurfaceHolder videostreamPreviewSh = videostreamPreviewSf.getHolder();
        videostreamPreviewSh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                TelemetryService.LogDebug("codec surfaceCreated");
                NativeHelper.getInstance().init();
                DJIVideoStreamDecoder.getInstance().init(getContext(), videostreamPreviewSh.getSurface());
                DJIVideoStreamDecoder.getInstance().setYuvDataListener(VideoFragment.this);
DJIVideoStreamDecoder.getInstance().changeSurface(null);

                DJIVideoStreamDecoder.getInstance().resume();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                TelemetryService.LogDebug("codec surfaceChanged");
                DJIVideoStreamDecoder.getInstance().changeSurface(holder.getSurface());
//       DJIVideoStreamDecoder.getInstance().changeSurface(null);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                TelemetryService.LogDebug("codec surfaceDestroyed");
                DJIVideoStreamDecoder.getInstance().stop();
                DJIVideoStreamDecoder.getInstance().destroy();
                NativeHelper.getInstance().release();
            }
        });
//force start on init.
        DJIVideoStreamDecoder.getInstance().init(getContext(), videostreamPreviewSh.getSurface());
        DJIVideoStreamDecoder.getInstance().setYuvDataListener(VideoFragment.this);
DJIVideoStreamDecoder.getInstance().changeSurface(null);
        DJIVideoStreamDecoder.getInstance().resume();
    }


    private int screenShotInterval = 30;
    @Override
    public void onYuvDataReceived(byte[] yuvFrame, int width, int height) {
//        TelemetryService.LogDebug"codec onYuvDataReceived");
        //In this demo, we test the YUV data by saving it into JPG files.
        if (screenShotInterval> 0 && (DJIVideoStreamDecoder.getInstance().frameIndex % screenShotInterval == 0)) {
            byte[] y = new byte[width * height];
            byte[] u = new byte[width * height / 4];
            byte[] v = new byte[width * height / 4];
            byte[] nu = new byte[width * height / 4]; //
            byte[] nv = new byte[width * height / 4];
            System.arraycopy(yuvFrame, 0, y, 0, y.length);
            for (int i = 0; i < u.length; i++) {
                v[i] = yuvFrame[y.length + 2 * i];
                u[i] = yuvFrame[y.length + 2 * i + 1];
            }
            int uvWidth = width / 2;
            int uvHeight = height / 2;
            for (int j = 0; j < uvWidth / 2; j++) {
                for (int i = 0; i < uvHeight / 2; i++) {
                    byte uSample1 = u[i * uvWidth + j];
                    byte uSample2 = u[i * uvWidth + j + uvWidth / 2];
                    byte vSample1 = v[(i + uvHeight / 2) * uvWidth + j];
                    byte vSample2 = v[(i + uvHeight / 2) * uvWidth + j + uvWidth / 2];
                    nu[2 * (i * uvWidth + j)] = uSample1;
                    nu[2 * (i * uvWidth + j) + 1] = uSample1;
                    nu[2 * (i * uvWidth + j) + uvWidth] = uSample2;
                    nu[2 * (i * uvWidth + j) + 1 + uvWidth] = uSample2;
                    nv[2 * (i * uvWidth + j)] = vSample1;
                    nv[2 * (i * uvWidth + j) + 1] = vSample1;
                    nv[2 * (i * uvWidth + j) + uvWidth] = vSample2;
                    nv[2 * (i * uvWidth + j) + 1 + uvWidth] = vSample2;
                }
            }
            //nv21test
            byte[] bytes = new byte[yuvFrame.length];
            System.arraycopy(y, 0, bytes, 0, y.length);
            for (int i = 0; i < u.length; i++) {
                bytes[y.length + (i * 2)] = nv[i];
                bytes[y.length + (i * 2) + 1] = nu[i];
            }
            Log.d(TAG,
                    "onYuvDataReceived: frame index: "
                            + DJIVideoStreamDecoder.getInstance().frameIndex
                            + ",array length: "
                            + bytes.length);
            screenShot(bytes);
        }
    }

    /**
     * Save the buffered data into a JPG image file
     */

    private void screenShot(byte[] buf) {

        //todo. Get timestamp from when image was taken instead of saved.
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String shotDir = Environment.getExternalStorageDirectory() + "/DJI_Telemetry/screens/";
        final String path =shotDir + "/"+ timestamp  + ".jpg";
        String remotePath="/screens/"+ timestamp  + ".jpg";//web relative path used for log message

        File dir = new File(shotDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        YuvImage yuvImage = new YuvImage(buf,
                ImageFormat.NV21,
                DJIVideoStreamDecoder.getInstance().width,
                DJIVideoStreamDecoder.getInstance().height,
                null);
        OutputStream outputFile;
        //final String path = dir + "/ScreenShot_" + System.currentTimeMillis() + ".jpg";

        try {
            outputFile = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "test screenShot: new bitmap output file error: " + e);
            return;
        }
        if (outputFile != null) {
            yuvImage.compressToJpeg(new Rect(0,
                    0,
                    DJIVideoStreamDecoder.getInstance().width,
                    DJIVideoStreamDecoder.getInstance().height), 90, outputFile);
        }
        try {
            outputFile.close();
        } catch (IOException e) {
            Log.e(TAG, "test screenShot: compress yuv image error: " + e);
            e.printStackTrace();
            return;
        }

        TelemetryService.LogDebug("PSH:Phone.screenshot="+remotePath);

        //runOnUiThread(new Runnable() {
        //    @Override
        //    public void run() {
        //        displayPath(path);
        //    }
        //});
    }





}

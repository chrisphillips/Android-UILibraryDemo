package com.dji.telemetryserver;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

public class DemoApplication extends Application {

    public static final String FLAG_CONNECTION_CHANGE = "uilibrary_demo_connection_change";

    private static BaseProduct mProduct;
    private Handler mHandler;
    private DJISDKManager.SDKManagerCallback mDJISDKManagerCallback;
    private BaseProduct.BaseProductListener mDJIBaseProductListener;
    private BaseComponent.ComponentListener mDJIComponentListener;

    private static Application instance;
    public static void setContext(Application application) {
        instance = application;
    }
    public static Application getContext(){
        return instance;
    }
    @Override
    public Context getApplicationContext() {
        return instance;
    }
    public DemoApplication() {

    }

    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (Aircraft) getProductInstance();
    }

    public TelemetryService telemetryService;
    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(Looper.getMainLooper());

        //Todo. make this class static?
        telemetryService = new TelemetryService();


        /**
         * When starting SDK services, an instance of interface DJISDKManager.DJISDKManagerCallback will be used to listen to
         * the SDK Registration result and the product changing.
         */
        mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

            //Listens to the SDK registration result
            @Override
            public void onRegister(DJIError error) {
                if(error == DJISDKError.REGISTRATION_SUCCESS) {

                    //start SDK key listerners after registered but before anything else.
                    DJIKeyedInterface.initListeners();

                    DJISDKManager.getInstance().startConnectionToProduct();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();
//                            DJISDKManager.getInstance().enableBridgeModeWithBridgeAppIP("192.168.1.2");
                        }
                    });
                    //loginAccount();//crashes for some reason.

                    notifyStatusChange();

                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Register Failed, check network is available", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                Log.e("TAG", error.toString());
            }

            //Listens to the connected product changing, including two parts, component changing or product connection changing.
            @Override
            public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {

                Toast.makeText(getApplicationContext(), "onProductChange", Toast.LENGTH_LONG).show();
                mProduct = newProduct;
                if(mProduct != null) {
                    mProduct.setBaseProductListener(mDJIBaseProductListener);
                }

                notifyStatusChange();
            }
        };

        mDJIBaseProductListener = new BaseProduct.BaseProductListener() {

            @Override
            public void onComponentChange(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {

                TelemetryService.LogDebug("mDJIBaseProductListener onComponentChange "+key);
                Toast.makeText(getApplicationContext(), "onComponentChange "+key, Toast.LENGTH_LONG).show();
                if(newComponent != null) {
                    newComponent.setComponentListener(mDJIComponentListener);
//                    ExampleFragment.getInstance().initPreview();

                }
//tell camera something has changed.
VideoFragment.getInstance().notifyProductUpdate();


//                    telemetryService.log("Install Hook"+key);
//                if(key.name().equals("CAMERA"))
//                    ExampleFragment.getInstance().initPreview();

                notifyStatusChange();
            }

            @Override
            public void onConnectivityChange(boolean isConnected) {
                TelemetryService.LogDebug("mDJIBaseProductListener onConnectivityChange "+isConnected);
                Toast.makeText(getApplicationContext(), "mDJIBaseProductListener onConnectivityChange", Toast.LENGTH_LONG).show();

                notifyStatusChange();
            }

        };

        mDJIComponentListener = new BaseComponent.ComponentListener() {

            @Override
            public void onConnectivityChange(boolean isConnected) {
                TelemetryService.LogDebug("mDJIComponentListener onConnectivityChange "+isConnected);
                Toast.makeText(getApplicationContext(), "XXXXXX mDJIComponentListener onConnectivityChange", Toast.LENGTH_LONG).show();
if(isConnected) {
//    DJIKeyedInterface.startListeners();

//    TelemetryService.getInstance().startListeners();
//    DJIKeyedInterface.startListeners();
}

                notifyStatusChange();
            }

        };


        //Check the permissions before registering the application for android system 6.0 above.
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (permissionCheck == 0 && permissionCheck2 == 0)) {

            //This is used to start SDK services and initiate SDK.
            DJISDKManager.getInstance().registerApp(getApplicationContext(), mDJISDKManagerCallback);
        } else {
            Toast.makeText(getApplicationContext(), "Please check if the permission is granted.", Toast.LENGTH_LONG).show();
        }
    }

    private void loginAccount(){

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Log.e("TAG", "Login Success");
                    }
                    @Override
                    public void onFailure(DJIError error) {
                        Log.e("TAG", "Login Error:" + error.getDescription());
                    }
                });
    }

    private void notifyStatusChange() {
//DJIKeyedInterface.doGetAll();
//DJIKeyedInterface.startListeners();
//TelemetryService.getInstance().startListeners();

        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    public static void startSimulation()
    {
        Aircraft aircraft = getAircraftInstance();
        if(aircraft==null)
        {
            TelemetryService.Log("Error starting simulation. No aircraft connected.");
            return;
        }

        FlightController flightController = aircraft.getFlightController();
        if(flightController.getSimulator().isSimulatorActive())
        {
            TelemetryService.Log("WARN simulation already started.");
        }
        TelemetryService.Log("Init SIMULATION");

        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
        flightController.getSimulator().setStateCallback(new SimulatorState.Callback() {
            @Override
            public void onUpdate(final SimulatorState stateData) {
/*                String yaw = String.format("%.2f", stateData.getYaw());
                String pitch = String.format("%.2f", stateData.getPitch());
                String roll = String.format("%.2f", stateData.getRoll());
                String positionX = String.format("%.2f", stateData.getPositionX());
                String positionY = String.format("%.2f", stateData.getPositionY());
                String positionZ = String.format("%.2f", stateData.getPositionZ());
                TelemetryService.Log("Yaw : " + yaw + ", Pitch : " + pitch + ", Roll : " + roll + "\n" + ", PosX : " + positionX +
                        ", PosY : " + positionY +
                        ", PosZ : " + positionZ);
*/
            }
        });
        flightController.getSimulator()
                .start(InitializationData.createInstance(new LocationCoordinate2D(23, 113), 10, 10),
                        new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    TelemetryService.Log("Error starting simulation. ERROR:"+djiError.getDescription());
                                }else
                                {
                                    TelemetryService.Log("Start Simulator Success");
                                }
                            }
                        });
    }


    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            getApplicationContext().sendBroadcast(intent);
        }
    };

}

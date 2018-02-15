package com.dji.telemetryserver;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dji.common.Stick;
import dji.common.remotecontroller.ChargeRemaining;
import dji.common.remotecontroller.HardwareState;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.RemoteControllerKey;
import dji.keysdk.callback.KeyListener;
import dji.sdk.sdkmanager.DJISDKManager;


public class TelemetryService {

    public Context context;

    //These are the keys that will be logged in the Remote group.
    public String[] remoteControllerListenKeys = {
            RemoteControllerKey.DISPLAY_NAME,
            RemoteControllerKey.SERIAL_NUMBER,
            RemoteControllerKey.LEFT_STICK_VALUE,
            RemoteControllerKey.RIGHT_STICK_VALUE,
            RemoteControllerKey.LEFT_WHEEL,
            RemoteControllerKey.RIGHT_WHEEL,
            RemoteControllerKey.TRANSFORMATION_SWITCH,
            RemoteControllerKey.FLIGHT_MODE_SWITCH_POSITION,
            RemoteControllerKey.GO_HOME_BUTTON,
            RemoteControllerKey.RECORD_BUTTON,
            RemoteControllerKey.SHUTTER_BUTTON,
            RemoteControllerKey.CUSTOM_BUTTON_1,
            RemoteControllerKey.CUSTOM_BUTTON_2,
            RemoteControllerKey.PAUSE_BUTTON,
            RemoteControllerKey.PLAYBACK_BUTTON,
            RemoteControllerKey.FIVE_D_BUTTON,
            RemoteControllerKey.GPS_DATA,
            RemoteControllerKey.CHARGE_REMAINING,
    };

    //These are the keys that will be logged in the FlightController group.
    public String[] flightControllerListenKey = {
            FlightControllerKey.HOME_LOCATION,
            FlightControllerKey.COMPASS_HEADING,
            FlightControllerKey.SATELLITE_COUNT,
            FlightControllerKey.AIRCRAFT_LOCATION,
            FlightControllerKey.VELOCITY_X,
            FlightControllerKey.VELOCITY_Y,
            FlightControllerKey.VELOCITY_Z,
            FlightControllerKey.ALTITUDE,
            FlightControllerKey.ATTITUDE_PITCH,
            FlightControllerKey.ATTITUDE_ROLL,
            FlightControllerKey.ATTITUDE_YAW,
            FlightControllerKey.IS_FLYING,
            FlightControllerKey.IS_LANDING,
            FlightControllerKey.FLIGHT_MODE_STRING,
            FlightControllerKey.GPS_SIGNAL_LEVEL,
            FlightControllerKey.ULTRASONIC_HEIGHT_IN_METERS,
            FlightControllerKey.ARE_MOTOR_ON,
            FlightControllerKey.HOME_LOCATION_LATITUDE,
            FlightControllerKey.HOME_LOCATION_LONGITUDE,
            FlightControllerKey.REMAINING_FLIGHT_TIME,
            FlightControllerKey.FLY_TIME_IN_SECONDS,
            FlightControllerKey.IS_GOING_HOME,
            FlightControllerKey.HOME_POINT_ALTITUDE,
            FlightControllerKey.ORIENTATION_MODE,
            FlightControllerKey.FLIGHT_WIND_WARNING,
            FlightControllerKey.MULTI_MODE_OPEN,
            FlightControllerKey.FLIGHT_MODE,
            FlightControllerKey.CONTROL_MODE
    };
    public void appendToFile(String filename, String message)
    {
        File file = new File(filename);
        boolean append = true;
        try {
            FileChannel wChannel = new FileOutputStream(file, append).getChannel();

            //TODO replace with something more efficent!
            wChannel.write(ByteBuffer.wrap(message.getBytes()));

            // Close the file
            wChannel.close();
        } catch (IOException e) {
        }
    }
    public String logPath=null;

    //enable to broadcast to a upd port on another machine
    private UDPClient udpClient=null;// new UDPClient("192.168.1.17",9001);

    public void log(String message)
    {
        //add timestamp.
        String timeStamp = new SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS ").format(new Date());
        message=timeStamp+message;

        //write to log file
        if(logPath!=null)
            appendToFile(logPath,message+"\n");

        //broadcast to connected websockets
        for(WebSocket socket : webSockets)
        {
            socket.send(message);
        }

        //broadcast via udp.
        if(udpClient!=null)
            udpClient.send(message);

    }
    ArrayList<WebSocket> webSockets = new ArrayList<WebSocket>();
    AsyncHttpServer websocketServer = new AsyncHttpServer();

    AsyncHttpServer httpServer = new AsyncHttpServer();

    //Create a websocket server on port 3001 that accepts connections
    //for sending future log messages.
    public void createWebserver()
    {
        websocketServer.websocket("/",new AsyncHttpServer.WebSocketRequestCallback() {
                    @Override
                public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {

                    //save connection for future messages.
                    webSockets.add(webSocket);
                    webSocket.send("Welcome Client");

                    //Cleanup socket on close.
                    webSocket.setClosedCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(Exception ex) {
                            try {
                                if (ex != null)
                                    Log.e("WebSocket", "Error");
                            } finally {
                                webSockets.remove(webSocket);
                            }
                        }
                    });
                    //recive not used yet.
                    webSocket.setStringCallback(new WebSocket.StringCallback() {
                        @Override
                        public void onStringAvailable(String s) {
                            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        );
        //start server on port 3001
        websocketServer.listen(3001);

        httpServer.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //response.send("<body>Hello!!!</body> ");

                //Context context = DemoApplication.getContext();
                AssetManager am =context.getAssets();
                try {
                    InputStream ins = am.open("cesium.html");
                    byte[] bytes = new byte[0];
                    bytes = new byte[ins.available()];
                    ins.read(bytes);
                    ins.close();
                    response.send("text/html; charset=utf-8",bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //response.s
            }
        });
        httpServer.listen(5001);
    }
    //Start the services on construction.
    public TelemetryService(Context context)
    {
        this.context=context;
        //create log dir if needed.
        String logDir = Environment.getExternalStorageDirectory() + "/DJI_Telemetry/logs/";
        new File(logDir).mkdirs();

        //set output log file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        logPath = logDir + "Log_" + timeStamp + ".txt";
        log("Logging started "+logPath);

        //Listen for websocket connections.
        createWebserver();

        //Start all the key listners.
        startListeners();
    }

    //Start all the SDK Key listners.
    public void startListeners() {
        log("Starting listners");
        KeyManager keyManager = DJISDKManager.getInstance().getKeyManager();

        // Create a listener for each key in each group (Remote, Battery, FlightController etc).
        for (String key: remoteControllerListenKeys) {
            DJIKey djikey = RemoteControllerKey.create(key);
            String keyName = "R:"+key;

            //Do an initial get of the keys value.
            // TODO. This should probably be done in a separately. You might not always want to get an inital.
            log(keyName+":"+DJIValueToString(keyManager.getValue(djikey)));

            DJISDKManager.getInstance().getKeyManager().addListener(djikey, new KeyListener() {
                @Override public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
                    //Format the change message
                    String message = keyName+":"+DJIValueToString(newValue);
                    log(message);//send
                }
            });
        }

        //same as above for flight keys.
        for (String key: flightControllerListenKey) {
            DJIKey djikey = FlightControllerKey.create(key);
            String keyName = "F:"+key;
            log(keyName+":"+DJIValueToString(keyManager.getValue(djikey)));

            DJISDKManager.getInstance().getKeyManager().addListener(djikey, new KeyListener() {
                @Override public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
                    String message = keyName+":"+DJIValueToString(newValue);
                    log(message);
                }
            });
        }
    }

    //Convert the value that comes back to a string for a log message.
    //Support new types as they are found.
    public String DJIValueToString(Object value) {
        if(value == null)
            return "null";

        if (value instanceof Float) {
            return String.valueOf((Float) value);
        }else if (value instanceof ChargeRemaining) {
            return String.valueOf(((ChargeRemaining)value).getRemainingChargeInPercent());
        }else if (value instanceof Stick) {
            Stick stick = (Stick)value;
            String msg = "x:"+stick.getHorizontalPosition()+",y:"+stick.getVerticalPosition();
            return msg;
        }else if (value instanceof HardwareState.Button) {
            HardwareState.Button button = (HardwareState.Button)value;
            String msg =""+button.isClicked();
            return msg;
        }else if (value instanceof HardwareState.TransformationSwitch) {
            HardwareState.TransformationSwitch button = (HardwareState.TransformationSwitch)value;
            String msg =""+button.getState();
            return msg;        }
        else
        {
            return value.toString();
        }
    }

}

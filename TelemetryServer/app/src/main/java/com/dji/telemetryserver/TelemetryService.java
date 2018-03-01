package com.dji.telemetryserver;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.location.Location;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static TelemetryService instance=null;
    public static TelemetryService getInstance()
    {
        return instance;
    }



    public static void Log(String message)
    {
        if(instance!=null)
            instance._log(message);

    }
    public static void LogDebug(String message)
    {
        if(instance!=null)
            instance._log(message);

    }
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

    public String getTimeStamp()
    {
        return new SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS ").format(new Date());
    }
    private String outputBuffer="";
    private void flushOutputBuffer()
    {
        for(WebSocket socket : webSockets)
        {
            if(outputBuffer.length()>0)
            {
                socket.send(outputBuffer);
                outputBuffer="";
            }
        }
    }
    private void _log(String message)
    {
        //add timestamp.
        String timeStamp = getTimeStamp();
        message=timeStamp+message;

        //write to log file
        if(logPath!=null)
            appendToFile(logPath,message+"\r\n");

        //broadcast to connected websockets
        outputBuffer+=message+"\n";
        if(outputBuffer.length()>1024)
        {
            flushOutputBuffer();
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
                    //log("Log Connected.");
                    TelemetryService.LogDebug("Client connected.");

                    //send log so far.
                    try {
                        RandomAccessFile f = null;
                        f = new RandomAccessFile(logPath, "r");
                        f.seek(0);
                        byte[] b = new byte[(int)f.length()];
                        f.readFully(b);
                        webSocket.send(new String(b));
                        f.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //This might not be needed. The gets are probably redundant.
                    DJIKeyedInterface.doGetAll();

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
                            if(s.compareToIgnoreCase("start simulation")==0)
                            {
                                TelemetryService.Log("Staring simulation");
                                DemoApplication.startSimulation();
                            }
                            if(s.compareToIgnoreCase("verbose")==0)
                            {
                                TelemetryService.Log("Verbose Mode");
                                DJIKeyedInterface.startAllListeners();
                                DJIKeyedInterface.doGetAll();
                            }
                            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        );
        //start server on port 3001
        websocketServer.listen(3001);

        //web serve screenshots.
        httpServer.get("/screens/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                AssetManager am =DemoApplication.getContext().getAssets();
                try {
                    String path = Environment.getExternalStorageDirectory() + "/DJI_Telemetry"+request.getPath();
                    InputStream ins =new FileInputStream(path);
                    byte[] bytes = new byte[ins.available()];
                    ins.read(bytes);
                    ins.close();
                    response.send("image/jpeg",bytes);
                } catch (IOException e) {
                    response.code(404);//todo is this right way to send 404?
                    response.send("");
                    e.printStackTrace();
                }

                //response.s
            }
        });
        //web serve screenshots.
        httpServer.get("/logs/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                AssetManager am =DemoApplication.getContext().getAssets();
                try {
                    String path = Environment.getExternalStorageDirectory() + "/DJI_Telemetry"+request.getPath();
                    InputStream ins =new FileInputStream(path);
                    byte[] bytes = new byte[ins.available()];
                    ins.read(bytes);
                    ins.close();
                    response.send("text/html; charset=utf-8",bytes);
                } catch (IOException e) {
                    response.code(404);
                    response.send("");
                    e.printStackTrace();
                }

                //response.s
            }
        });
        //web serve html files.
        httpServer.get("/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                AssetManager am =DemoApplication.getContext().getAssets();
                try {
                    String path = request.getPath();
                    if(path.equals("/"))
                    {
                        path = "/index.html";//default to index if root
                    }
                    path=path.substring(1);//get rid of leading /
                    InputStream ins = am.open(path);
                    byte[] bytes = new byte[ins.available()];
                    ins.read(bytes);
                    ins.close();

/*                    response.getHeaders().set("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
                    response.getHeaders().set("Access-Control-Allow-Origin", "*");
                    response.getHeaders().set("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
*/
                    setResponseHeaders(request, response);

                    if(path.endsWith(".jpg"))
                        response.send("image/jpeg",bytes);
                    else if(path.endsWith(".glb"))
                        response.send("application/octet-stream",bytes);
                    else
                    response.send("text/html; charset=utf-8",bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    //response.code(404);
                    //response.send("");
                }
            //todo handle 404 etc.

            }
        });
        httpServer.addAction("OPTIONS",".+", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                setResponseHeaders(request, response);
                response.send("ok");
            }
        });
        httpServer.listen(5001);
    }
    void setResponseHeaders(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        Headers allHeaders = response.getHeaders();
        Headers reqHeaders = request.getHeaders();

        String corsHeaders = reqHeaders.get("access-control-request-headers");

        if(corsHeaders!=null) {
            // common headers
            allHeaders.set("Access-Control-Allow-Origin", "*");
            allHeaders.set("Access-Control-Allow-Credentials", "true");
            allHeaders.set("Access-Control-Allow-Headers", corsHeaders);
            allHeaders.set("Access-Control-Allow-Methods", "HEAD,OPTIONS,GET,POST,PUT,PATCH,DELETE,CONNECT");
            allHeaders.set("Access-Control-Max-Age", "86400");
        }
    }
    //Start the services on construction.
    public TelemetryService()
    {
        instance=this;
        //this.context=context;
        //create log dir if needed.
        String logDir = Environment.getExternalStorageDirectory() + "/DJI_Telemetry/logs/";
        new File(logDir).mkdirs();

        //set output log file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        logPath = logDir + "Log_" + timeStamp + ".txt";
        Log("Logging started "+logPath);

        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        //start heartbeat thread.
        handler.postDelayed(new Runnable(){
           public void run(){
               try {
                   Location loc = MainActivity.getCurrentLocation();
                   if (loc != null) {
                       //hack. send phone loc as heat beat. probably better way to do this.
                       Log("PSH:Phone.Location=" + String.valueOf(loc.getLatitude()) + "," +
                               String.valueOf(loc.getLongitude()) + "," +
                               String.valueOf(loc.getBearing())
                       );
                       flushOutputBuffer();
                   }
                   handler.postDelayed(this, delay);
               }catch(Exception e)
               {
                   //no errors on heart send.
               }
           }
        }, delay);

        //Listen for websocket connections.
        createWebserver();

        //Start all the key listners.
        //startListeners();
    }



    //Start all the SDK Key listners.
    public void startListeners() {
        TelemetryService.Log("Starting listners");

        if(DJISDKManager.getInstance()==null || DJISDKManager.getInstance().getKeyManager()==null )
            return;//not ready yet.

        KeyManager keyManager = DJISDKManager.getInstance().getKeyManager();

        // Create a listener for each key in each group (Remote, Battery, FlightController etc).
        for (String key: remoteControllerListenKeys) {
            DJIKey djikey = RemoteControllerKey.create(key);
            String keyName = "R:"+key;//R: means remote message.

            //Do an initial get of the keys value.
            // TODO. This should probably be done in a separately. You might not always want to get an inital.
//            log(keyName+"="+DJIValueToString(keyManager.getValue(djikey)));

            DJISDKManager.getInstance().getKeyManager().addListener(djikey, new KeyListener() {
                @Override public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
                    //Format the change message
                    String message = keyName+"="+DJIValueToString(newValue);
                    TelemetryService.Log(message);//send
                }
            });
        }

        //same as above for flight keys.
        for (String key: flightControllerListenKey) {
            DJIKey djikey = FlightControllerKey.create(key);
            String keyName = "F:"+key;
//            log(keyName+"="+DJIValueToString(keyManager.getValue(djikey)));

            DJISDKManager.getInstance().getKeyManager().addListener(djikey, new KeyListener() {
                @Override public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
                    String message = keyName+"="+DJIValueToString(newValue);
                    TelemetryService.Log(message);
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
            String msg = ""+stick.getHorizontalPosition()+","+stick.getVerticalPosition();
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

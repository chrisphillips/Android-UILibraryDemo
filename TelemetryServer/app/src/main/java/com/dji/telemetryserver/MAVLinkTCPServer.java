package com.dji.telemetryserver;


import android.location.Location;
import android.os.Handler;

import com.jeremydyer.mavlink.Messages.MAVLinkMessage;
import com.jeremydyer.mavlink.Messages.MAVLinkPacket;
import com.jeremydyer.mavlink.Messages.enums.MAV_AUTOPILOT;
import com.jeremydyer.mavlink.Messages.enums.MAV_MODE;
import com.jeremydyer.mavlink.Messages.enums.MAV_MODE_FLAG;
import com.jeremydyer.mavlink.Messages.enums.MAV_PARAM_TYPE;
import com.jeremydyer.mavlink.Messages.enums.MAV_STATE;
import com.jeremydyer.mavlink.Messages.enums.MAV_TYPE;
import com.jeremydyer.mavlink.Messages.pixhawk.msg_heartbeat;
import com.jeremydyer.mavlink.Messages.pixhawk.msg_param_set;
import com.jeremydyer.mavlink.Messages.pixhawk.msg_param_value;
import com.jeremydyer.mavlink.Parser;
import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;

import org.mavlink.MAVLinkReader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.jeremydyer.mavlink.Messages.pixhawk.msg_param_request_list.MAVLINK_MSG_ID_PARAM_REQUEST_LIST;

public class MAVLinkTCPServer {

    private InetAddress host;
    private int port;

    public MAVLinkTCPServer(String host, int port) {
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        this.port = port;

        setup();
    }

    private void setup() {
        AsyncServer.getDefault().listen(host, port, new ListenCallback() {

            @Override
            public void onAccepted(final AsyncSocket socket) {
                handleAccept(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket) {
                System.out.println("[MAVLinkTCPServer] MAVLinkTCPServer started listening for connections");
            }

            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[MAVLinkTCPServer] Successfully shutdown server");
            }
        });

        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        //start heartbeat thread.
        handler.postDelayed(new Runnable(){
            public void run(){
                try {
                    if (theSocket != null) {
                        msg_heartbeat heartMsg = new msg_heartbeat();
                        heartMsg.sysid=1;
                        heartMsg.compid=1;
                        heartMsg.type = MAV_TYPE.MAV_TYPE_QUADROTOR;
                        heartMsg.autopilot = MAV_AUTOPILOT.MAV_AUTOPILOT_GENERIC;
                        heartMsg.system_status = MAV_STATE.MAV_STATE_STANDBY;
                        heartMsg.base_mode = MAV_MODE.MAV_MODE_MANUAL_DISARMED;

                        heartMsg.mavlink_version=1;

                        MAVLinkPacket newPacket = heartMsg.pack();

                        Util.writeAll(theSocket, newPacket.encodePacket(), new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                //if (ex != null) throw new RuntimeException(ex);
                                //System.out.println("[MAVLinkTCPServer] Successfully wrote message");
                            }
                        });


                        msg_param_value paramMsg = new msg_param_value();
                        //heartMsg.target_system= (byte) -1;
                        //heartMsg.sysid=1;
                        //heartMsg.compid=1;
                        paramMsg.param_type= MAV_PARAM_TYPE.MAV_PARAM_TYPE_INT32;
                        paramMsg.param_value=0;
                        paramMsg.param_count=1;
                        paramMsg.param_index=0;
                        paramMsg.param_id="STAT_RUNTIME".getBytes();

                        MAVLinkPacket newPacket2 = paramMsg.pack();

                        Util.writeAll(theSocket, newPacket2.encodePacket(), new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                //if (ex != null) throw new RuntimeException(ex);
                                //System.out.println("[MAVLinkTCPServer] Successfully wrote message");
                            }
                        });
                    }
                    handler.postDelayed(this, delay);

                }catch(Exception e) {
                    //no errors on heart send.
                }

            }
        }, delay);
    }

    private Parser mavParser = new Parser();
    private MAVLinkReader mavReader=null;
    private AsyncSocket theSocket = null;
    private void handleAccept(final AsyncSocket socket) {
        System.out.println("[MAVLinkTCPServer] New Connection " + socket.toString());

        theSocket=socket;
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                System.out.println("[MAVLinkTCPServer] Received Message len " + bb.size());

                MAVLinkPacket packet=null;
                byte[] bytes = bb.getAllByteArray();
                for (byte b :
                        bytes) {
                    packet = mavParser.mavlink_parse_char((int)(b&0xff));
                    if(packet!=null)
                    {
                        MAVLinkMessage msg = packet.unpack();
                        System.out.println("[MAVLinkTCPServer] Mavlink Message " + msg.toString());
                        if(msg.msgid==MAVLINK_MSG_ID_PARAM_REQUEST_LIST) {


                        }


                    }

                }

            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[MAVLinkTCPServer] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[MAVLinkTCPServer] Successfully end connection");
            }
        });
    }
}
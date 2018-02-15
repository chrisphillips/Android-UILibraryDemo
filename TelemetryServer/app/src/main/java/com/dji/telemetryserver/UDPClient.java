package com.dji.telemetryserver;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class UDPClient {

    private final InetSocketAddress host;
    private AsyncDatagramSocket asyncDatagramSocket;

    public UDPClient(String host, int port) {
        this.host = new InetSocketAddress(host, port);
        setup();
    }

    private void setup() {
        try {
            asyncDatagramSocket = AsyncServer.getDefault().connectDatagram(host);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully closed connection");
            }
        });

        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully end connection");
            }
        });
    }

    public void send(String msg) {
        asyncDatagramSocket.send(host, ByteBuffer.wrap(msg.getBytes()));
    }
    public void send(ByteBuffer byteBuffer) {
        asyncDatagramSocket.send(host, byteBuffer);
    }
}
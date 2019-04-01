package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiverThreadmain extends Thread {
    interface OnReceiveListener {
        void onReceive(String message) throws JSONException;
    }
    ReceiverThreadmain.OnReceiveListener mListener;
    public void setOnReceiveListener(ReceiverThreadmain.OnReceiveListener listener) {
        mListener = listener;
    }
    Socket socket;
    public ReceiverThreadmain(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("receiver", "run: ");
            while (true) {
                String str = reader.readLine();
                if (mListener != null) {
                    mListener.onReceive(str);
                }
            }
        } catch (Exception e) {
            Log.e("chat", "run: " + e.getMessage());
        }
    }
}

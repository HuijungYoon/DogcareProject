package yoonhuijung.dogcareproject.chatting;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiverThread extends Thread {
    interface OnReceiveListener {
        void onReceive(String message);
    }
    OnReceiveListener mListener;
    public void setOnReceiveListener(OnReceiveListener listener) {
        mListener = listener;
    }
    Socket socket;
    public ReceiverThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
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

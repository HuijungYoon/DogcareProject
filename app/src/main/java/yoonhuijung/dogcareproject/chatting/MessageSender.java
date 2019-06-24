package yoonhuijung.dogcareproject.chatting;


import android.os.AsyncTask;

import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.DataOutputStream;
public class MessageSender extends AsyncTask<String,Void,Void> {

    Socket s;
    DataOutputStream dos;
    PrintWriter pw;


    @Override
    protected Void doInBackground(String... voids) {

        String message = voids[0];
        try {
            s= new Socket("192.168.0.113",7800);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

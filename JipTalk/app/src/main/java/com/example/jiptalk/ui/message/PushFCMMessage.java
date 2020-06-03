package com.example.jiptalk.ui.message;

import com.google.firebase.auth.GoogleAuthCredential;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PushFCMMessage {

    String token;
    String msg;

    public PushFCMMessage() {

    }

    public PushFCMMessage(String token, String msg) {


        URL url = null;
        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");
        } catch (MalformedURLException e) {
            System.out.println("Error while creating Firebase URL | MalformedURLException");
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.out.println("Error while createing connection with Firebase URL | IOException");
            e.printStackTrace();
        }
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        // set my firebase server key
        conn.setRequestProperty("Authorization", "key="
                + "AAAAIYo4Wjo:APA91bFEUYnGcHbhHRuyVqy6799pA171LTI1W7Q84sU91UiTc5a3HcwDrmTBRTcaVkz-9VplzfQiTDPeG29zCr7Uzs_d3LPrpO925Li8R6l6xSAqDfZutNBx5nehGKD3jHXYcz5EbzFB");


        // create notification message into JSON format
        try {
            JSONObject message = new JSONObject();
            message.put("to",
                    token);
            message.put("priority", "high");
            JSONObject notification = new JSONObject();
            notification.put("title", "집똑 알림이 왔어요~!");
            notification.put("body", msg);
            message.put("notification", notification);

            // send data to firebase (http method)
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(message.toString());
            out.flush();
            conn.getInputStream();
        } catch (Exception e) {
            System.out.println("Error while writing outputstream| IOException");
            e.printStackTrace();
        }


    }


}

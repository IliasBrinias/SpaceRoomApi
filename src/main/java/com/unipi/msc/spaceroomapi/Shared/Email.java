package com.unipi.msc.spaceroomapi.Shared;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;

public class Email {
    public static void send(String emailTo,String subject,String html){
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        try {
            JSONObject mainObject = new JSONObject();
            JSONArray personalizationsArray = new JSONArray();
//            Receiver
            JSONObject receiverObject = new JSONObject();
            JSONArray mailArray = new JSONArray();
            JSONObject mailObject = new JSONObject();
            mailObject.put("email",emailTo);
            mailArray.add(mailObject);
            receiverObject.put("to", mailArray);
            receiverObject.put("subject",subject);
            personalizationsArray.add(receiverObject);
//             Sender
            JSONObject senderObject = new JSONObject();
            JSONArray bodyLineArray = new JSONArray();
            JSONObject bodyJson = new JSONObject();
            senderObject.put("email","info@spaceroom.com");
            mainObject.put("personalizations", personalizationsArray);
            mainObject.put("from",senderObject);
//            body
            bodyJson.put("type","text/html");
            bodyJson.put("value",html);
            bodyLineArray.add(bodyJson);
            mainObject.put("content", bodyLineArray);
//            call
            client.prepare("POST", "https://rapidprod-sendgrid-v1.p.rapidapi.com/mail/send")
                    .setHeader("content-type", "application/json")
                    .setHeader("X-RapidAPI-Key", "d8455cfac5mshb2e12524fc60827p13bf2fjsna477236d177e")
                    .setHeader("X-RapidAPI-Host", "rapidprod-sendgrid-v1.p.rapidapi.com")
                    .setBody(mainObject.toJSONString())
                    .execute()
                    .toCompletableFuture()
                    .thenAccept(Response::getStatusCode)
                    .join();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

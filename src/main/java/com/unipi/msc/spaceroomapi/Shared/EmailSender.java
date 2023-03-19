package com.unipi.msc.spaceroomapi.Shared;

import com.google.zxing.WriterException;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.asynchttpclient.Response;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class EmailSender {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String HTML_PATH = System.getProperty("user.dir")+"/src/main/java/com/unipi/msc/spaceroomapi/Shared/EmailTemplate/";
    private static void send(String emailTo, String subject, String body){
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
            bodyJson.put("value", body);
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
    public static void sendAcceptReservation(String emailTo, Reservation r){
        try {
            Document doc = getDocument("AcceptReservation.html",r);
            send(emailTo,"RESERVATION ACCEPTED", doc.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendRejectReservation(String emailTo, Reservation r){
        try {
            Document doc = getDocument("RejectReservation.html",r);
            send(emailTo,"RESERVATION REJECTED", doc.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Document getDocument(String fileName,Reservation r) throws IOException {
        Document doc = Jsoup.parse(new File(HTML_PATH+fileName));
        doc.body().getElementById("client-name").attr("value", r.getClient().getFirstName()+" "+ r.getClient().getLastName());
        doc.body().getElementById("house-title").attr("value", r.getHouse().getTitle());
        doc.body().getElementById("price").attr("value", r.getHouse().getPrice().toString());
        doc.body().getElementById("location").attr("value", r.getHouse().getLocation());
        doc.body().getElementById("num-guests").attr("value", r.getHouse().getMaxCapacity().toString());
        doc.body().getElementById("check-in").attr("value",dateFormat.format(r.getDateFrom()));
        doc.body().getElementById("check-out").attr("value",dateFormat.format(r.getDateTo()));
        return doc;
    }
}

package com.rendu.backend.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.secret.key}")
    private String secretKey;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    @Value("${mailjet.sender.name}")
    private String senderName;

    private MailjetClient client;

    @PostConstruct
    public void init() {
        client = new MailjetClient(apiKey, secretKey);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        MailjetRequest request;
        MailjetResponse response;

        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put("From", new JSONObject()
                                        .put("Email", senderEmail)
                                        .put("Name", senderName))
                                .put("To", new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", toEmail)
                                                .put("Name", ""))
                                )
                                .put("Subject", subject)
                                .put("TextPart", body)
                        ));

        try {
            response = client.post(request);
            System.out.println("Mailjet response: " + response.getStatus() + " -> " + response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.mindgulp.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import static javax.mail.Message.RecipientType.TO;
import static javax.mail.Session.getDefaultInstance;

/**
 * Created by asikprad on 9/23/17.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    @Value("${google.api.userid}")
    private String userId;
    @Value("${google.api.client.id}")
    private String clientId;
    @Value("${google.api.client.secret}")
    private String clientSecret;
    @Value("${google.api.refreshToken}")
    private String refreshToken;
    @Value("${google.api.token.server.url}")
    private String tokenServerUrl;
    @Value("${google.api.application.name}")
    private String applicationName;

    private Credential createCredentialWithRefreshToken(
            HttpTransport transport){
        TokenResponse response = new TokenResponse();
        response.setRefreshToken(refreshToken);
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
                transport)
                .setJsonFactory(JSON_FACTORY)
                .setTokenServerUrl(
                        new GenericUrl(tokenServerUrl))
                .setClientAuthentication(new BasicAuthentication(clientId, clientSecret))
                .build()
                .setFromTokenResponse(response);
    }

    private Gmail gmailService() throws GeneralSecurityException, IOException {
        final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        TokenResponse response = new TokenResponse();
        response.setRefreshToken(refreshToken);

        return new Gmail.Builder(transport, JSON_FACTORY, createCredentialWithRefreshToken(transport))
                .setApplicationName(applicationName).build();

    }

     private Message createMessageWithEmail(final MimeMessage emailContent) throws MessagingException, IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        final byte[] bytes = buffer.toByteArray();
        final String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        final Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public void send(String to, String subject, String content) {

        try {

            final MimeMessage emailMessage = new MimeMessage(getDefaultInstance(new Properties(), null));
            emailMessage.addRecipient(TO, new InternetAddress(to));
            emailMessage.setSubject(subject);
            emailMessage.setText(content);

            gmailService().users().messages().send(userId, createMessageWithEmail(emailMessage)).execute();

        } catch (MessagingException | IOException | GeneralSecurityException ex) {
            log.error("Error sending mail", ex);
        }
    }


}

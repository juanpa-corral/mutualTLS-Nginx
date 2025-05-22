package com.example.aclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

@RestController
@RequestMapping("/api/a")
public class AController {

    @GetMapping("/call-b")
    public ResponseEntity<String> callB() {
        try {
            // Load client keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream ksStream = new FileInputStream("/home/juanpacorralh/certs/servicea.p12")) {
                keyStore.load(ksStream, "changeit".toCharArray());
            }

            // Load truststore containing CA cert
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream tsStream = new FileInputStream("/home/juanpacorralh/certs/servicea-truststore.jks")) {
                trustStore.load(tsStream, "changeit".toCharArray());
            }

            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, "changeit".toCharArray())
                    .loadTrustMaterial(trustStore, null)
                    .build();

            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext);
            HttpClient client = HttpClients.custom()
                    .setSSLSocketFactory(factory)
                    .build();

            HttpGet request = new HttpGet("https://sabanus.online/api/b/hello");
            HttpResponse response = client.execute(request);

            String body = EntityUtils.toString(response.getEntity());
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error mTLS A->B: " + e.getMessage());
        }
    }
}

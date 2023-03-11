package com.seokwon.common.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

@Slf4j
@Setter
public class SlackSender {

    private ObjectMapper objectMapper;
    private HttpClient client;

    public SlackSender() {
        this.objectMapper = new ObjectMapper();
        this.client = HttpClientBuilder.create().build();
    }

    public boolean send(SlackChannel target, SlackMessage message) {
        try {
            HttpPost request = new HttpPost(target.getWebHookUrl());
            request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(message)));
            HttpResponse response = client.execute(request);

            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.junodx.functions;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.Map;

public class InsuranceService implements RequestHandler<Map<String,String>, String> {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String handleRequest(Map<String, String> stringStringMap, Context context) {

        //final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAVD5K7Y5MSAT5SVMF", "71HHX/dfAHqL1dtLEQTHHduZrgAntyxkA0hfK+cR");

        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();


        String queueUrl = "https://sqs.us-east-2.amazonaws.com/352009045849/jdx-insurance-update-queue.fifo";

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(10)
                .withMaxNumberOfMessages(10);

        List<Message> sqsMessages = sqs.receiveMessage(receiveMessageRequest).getMessages();


        try {
            for(Message m : sqsMessages)
                System.out.println(mapper.writeValueAsString(m.getBody()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return "200 OK: ";
    }
}

package com.junodx.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class InsuranceService implements RequestHandler<Map<String,String>, String> {
    @Override
    public String handleRequest(Map<String, String> stringStringMap, Context context) {


        return "200 OK: ";
    }
}

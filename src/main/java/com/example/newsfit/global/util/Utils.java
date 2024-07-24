package com.example.newsfit.global.util;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Utils {
    public static JSONObject jsonObjectParser(String requestBody) throws ParseException {
        JSONParser parser = new JSONParser();
        Object parsedBody = parser.parse(requestBody);
        return (JSONObject) parsedBody;
    }
}

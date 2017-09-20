package com.example.cocoy.foursquare_app;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cocoy on 18/09/2017.
 */

public class NetworkServices {

    private OkHttpClient client;
    public NetworkServices() {
        client = new OkHttpClient();
    }

    public String makeRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        System.out.println("Request: " + request);
        Response response = client.newCall(request).execute();
        System.out.println("Response: " + response);
        return response.body().string();
    }
}

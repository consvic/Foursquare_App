package com.example.cocoy.foursquare_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cocoy on 18/09/2017.
 */

public class JSONParser {
    String result;
    public JSONParser(String res) {
        result = res;
    }

    public JSONObject getParser() throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        return jsonObject;

    }



    public JSONArray getArray() throws JSONException {
        JSONObject jsonObject = getParser();
        String photosString = jsonObject.getString("photos");
        JSONObject photoObject = new JSONObject(photosString);
        String photoString = photoObject.getString("photo");
        Log.d("photo string: ",photoString);

        JSONArray jsonArray = new JSONArray(photoString);

        return  jsonArray;
    }

    public String getTitle() throws JSONException{
        JSONObject jsonObject = getParser();
        String photo = jsonObject.getString("photo");
        String content = new JSONObject(photo).getString("title");
        String title = new JSONObject(content).getString("_content");

        return title;
    }

    public String getUser() throws JSONException{
        JSONObject jsonObject = getParser();
        String photo = jsonObject.getString("photo");
        String owner = new JSONObject(photo).getString("owner");
        String username = new JSONObject(owner).getString("username");
        return username;
    }
}

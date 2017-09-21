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
        String response = jsonObject.getString("response");
        JSONObject responseObject = new JSONObject(response);
        return responseObject;

    }



    public JSONArray getArray() throws JSONException {
        JSONObject jsonObject = getParser();
        String venues = jsonObject.getString("venues");
        JSONArray jsonArray = new JSONArray(venues);

        return  jsonArray;
    }

    public int getSizeArray() throws JSONException {
        return getArray().length();
    }

    public String getVenueID(int n) throws JSONException {
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String venueID = jsonObject.getString("id");

        return venueID;

    }

    public String getVenueName(int n) throws JSONException {
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String venueName = jsonObject.getString("name");

        return venueName;

    }

    public String getVenueAddress(int n) throws JSONException {
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String locationString = jsonObject.getString("location");
        JSONObject jsonObject1 = new JSONObject(locationString);
        String location="";
        String address = jsonObject1.getString("address");
        String city = jsonObject1.getString("city");
        String country = jsonObject1.getString("country");

        if (address != "") location = location + address + ". ";
        if(city != "") location = location + city + ", ";
        if (country != "") location = location + country;

        return location;

    }

    public String getCategoryName(int n) throws JSONException {
        String categoryName;
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String locationString = jsonObject.getString("categories");
        JSONArray jsonArray1 = new JSONArray(locationString);
        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

        categoryName = jsonObject2.getString("name");

        return categoryName;
    }

    /*public String getPhone(int n) throws JSONException {
        String phone;
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String contactString = jsonObject.getString("contact");
        JSONObject jsonObject1 = new JSONObject(contactString);
        phone = jsonObject1.getString("phone");

        return phone;
    }*/

    public String getCheckIns(int n) throws JSONException {
        String checkins;
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String contactString = jsonObject.getString("stats");
        JSONObject jsonObject1 = new JSONObject(contactString);
        checkins = jsonObject1.getString("checkinsCount");

        return checkins;
    }

    public String getPrefix(int n) throws JSONException{
        String prefix;
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String categoriesString = jsonObject.getString("categories");
        JSONArray jsonArray1 = new JSONArray(categoriesString);
        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
        prefix = new JSONObject(jsonObject2.getString("icon")).getString("prefix");

        return prefix;
    }

    public String getSufix(int n) throws JSONException{
        String sufix;
        JSONArray jsonArray = getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(n);
        String categoriesString = jsonObject.getString("categories");
        JSONArray jsonArray1 = new JSONArray(categoriesString);
        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
        sufix = new JSONObject(jsonObject2.getString("icon")).getString("suffix");

        return sufix;
    }
}

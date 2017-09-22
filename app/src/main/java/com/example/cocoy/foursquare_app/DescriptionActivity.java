package com.example.cocoy.foursquare_app;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DescriptionActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "0R1KYFSLIKTDHB0ED4JJD4MHZ3WF10S4EQFGICDTHSQLKUMG"; // clave ID
    private static final String CLIENT_SECRET = "TO05OOMSDXIVYMWYLEU0LJ0G5PXX15YIV2LWROHIBMJYAPEM"; // clave secreta

    public static final String EXTRA_POSITION = "position";
    String id,name,address,category,checkins;

    NetworkServices client;

    ImageView imageView;

    String url;
    String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        ArrayList<String> arrayList = intent.getStringArrayListExtra("content");
        id = arrayList.get(0);
        name = arrayList.get(1);
        address = arrayList.get(2);
        category = arrayList.get(3);
        checkins = arrayList.get(4);

        url = "https://api.foursquare.com/v2/venues/"+ id +"/photos?oauth_token="+ token +"&v=20170921";

        setContentView(R.layout.activity_description);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);
        //Resources resources = getResources();
        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(name);

        //String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText("Category: "+ category + "\nCheckins:" + checkins);

        //String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText(address);

        imageView = (ImageView) findViewById(R.id.image);

        client = new NetworkServices();

        new AsyncTask<Void,Void,Void>() {

            String result;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Log.d("Url: ", url);
                    result = client.makeRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response").getJSONObject("photos");
                    Log.d("Photo count: ",jsonObject1.getInt("count")  + "");
                    if(jsonObject1.getInt("count") != 0){
                        JSONArray jsonArray = jsonObject1.getJSONArray("items");
                        JSONObject photoObject = jsonArray.getJSONObject(0);
                        String prefix = photoObject.getString("prefix");
                        String suffix = photoObject.getString("suffix");
                        //Log.d("Photos",jsonObject1.toString());
                        String url = prefix + "300x300" + suffix;
                        Picasso.with(getApplicationContext()).load(url).skipMemoryCache().fit().into(imageView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute();

    }
}

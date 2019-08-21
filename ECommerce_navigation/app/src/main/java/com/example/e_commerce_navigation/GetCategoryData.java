package com.example.e_commerce_navigation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for getting all category data using HTTP Request and Response
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class GetCategoryData extends AsyncTask<Void, Void, JSONArray> {


    private Context ctx;
    private AddProductMenu apmenu;
    private View view;


    //API Category Controller address for category data.
    private String url4request = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/categories";


    //The constructor for SearchProductMenu

    /**
     * Constructor of the class for getting all category data using HTTP Request and Response
     * @param ctx
     * @param apmenu
     * @param view
     */
    public GetCategoryData(Context ctx, AddProductMenu apmenu, View view) {
        this.ctx = ctx;
        this.apmenu = apmenu;
        this.view = view;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {

        HttpURLConnection urlConn = null;
        BufferedReader bufferedReader = null;


        Handler handler = new Handler(ctx.getMainLooper());


        try {
            URL url = new URL(url4request);
            urlConn = (HttpURLConnection) url.openConnection();

            if (200 != urlConn.getResponseCode()) {

                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }


            JSONArray jsonAr = new JSONArray(stringBuffer.toString());

            return jsonAr;
        } catch (Exception ex) {
            Log.e("App", ex.getMessage(), ex);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    protected void onPostExecute(JSONArray jsonAr) {

        if (jsonAr != null) {
            try {

                List<Category> aList = new ArrayList<Category>();
                //Change JSONArray to ArrayList<Category> type data.
                for (int i = 0; i < jsonAr.length(); i++) {
                    JSONObject obj = jsonAr.getJSONObject(i);
                    aList.add(new Category(obj.getInt("id")
                            , obj.getString("descriptions")));

                }
                //Making a category spinner using the category data retrieved from DB.
                apmenu.fillCategoryAsyncResponse(view,aList);

            } catch (Exception ex) {

            }
        }
    }

}

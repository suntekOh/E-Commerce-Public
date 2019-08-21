package com.example.e_commerce_navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Class related to adding stock or ordering Product using HTTP request and response
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class PostInventoryData extends AsyncTask<Void, Void, String> {
    private String url4req;
    private Map<String, String> args;
    private Context ctx;
    private FragmentManager fm;
    private Enum en;

    /**
     * Constructor of the class related to adding stock or ordering Product using HTTP request and response
     * @param args
     * @param ctx
     * @param fm
     * @param en
     */
    public PostInventoryData(Map<String, String> args, Context ctx, FragmentManager fm, Enum en) {
        this.args = args;
        this.ctx = ctx;
        this.fm = fm;
        this.en = en;

        if(en == PostInventoryQuery.AddStock){
            this.url4req="http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/inventories";
        }else{
            this.url4req="http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/inventories/order";
        }

    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        Handler handler = new Handler(ctx.getMainLooper());

        try {


            URL url = new URL(url4req);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());
            // Upload POST Data
            Iterator<String> keys = args.keySet().iterator();

            while (keys.hasNext()) {
                String key = keys.next();
                String value = args.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            //If ordered quantity is greater than the current stock quantity, show the error message.
            if (412 == connection.getResponseCode()) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ctx, "The ordered quantity is greater than the current quantity.\nPlease change the quantity", Toast.LENGTH_LONG).show();
                    }
                });
                throw new Exception("412 Error" + connection.getResponseCode() + " " + connection.getResponseMessage());

            }else if(201 != connection.getResponseCode()){
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ctx, "Unexpected system error happened.\nFew minutes later, please try it again.", Toast.LENGTH_LONG).show();
                    }
                });
                throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());

            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            Log.e("App", "yourDataTask", ex);
            return null;
        }

        return result;

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            try {
                if(en == PostInventoryQuery.AddStock){
                    Toast.makeText(ctx, "" +
                            "Stock has been increased successfully", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ctx, "" +
                            "Your order has been placed successfully", Toast.LENGTH_LONG).show();
                }

                Log.e("App", "Success: " + response);


                String productId = args.get("ProductId");

                Fragment fragment = new ViewProductDetail();

                Bundle args = new Bundle();
                args.putString("productId", productId);
//                Toast.makeText(ctx, productId, Toast.LENGTH_LONG).show();

                fragment.setArguments(args);

                //Move to ViewProductDetail fragment.
                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();



            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

}
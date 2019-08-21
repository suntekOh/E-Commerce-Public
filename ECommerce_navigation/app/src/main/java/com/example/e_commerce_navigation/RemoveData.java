package com.example.e_commerce_navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoveData extends AsyncTask<Void, Void, String> {
    private String urlTo="http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/products/";
    private int productId;
    private Context ctx;

    public RemoveData(int productId, Context ctx) {
        this.productId = productId;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection conn = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String result = "";


        try {
            urlTo = urlTo + productId;
            URL url = new URL(urlTo);
            conn = (HttpURLConnection) url.openConnection();

            //HTTP DELETE Request.
            conn.setRequestMethod("DELETE");
            conn.setDoInput(true);
            conn.setDoOutput(false);

            switch (conn.getResponseCode()) {
                case 200:
                    break;
                case 201:
                    break;
                default:
                    throw new Exception("Failed to upload code:" + conn.getResponseCode() + " " + conn.getResponseMessage());
            }

            inputStream = conn.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();

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
                Toast.makeText(ctx, "" +
                        "The product has been successfully removed.", Toast.LENGTH_LONG).show();
                Log.e("App", "Success: " + response);
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

}
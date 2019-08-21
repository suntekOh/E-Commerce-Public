package com.example.e_commerce_navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Class related to inserting a new customer row sending HTTP Request to the API.
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class PostCustomerData extends AsyncTask<Void, Void, String> {
    private String urlTo;
    private Map<String, String> args;
    private Context ctx;
    private FragmentManager fm;

    /**
     * the constructor of the class related to inserting a new customer row sending HTTP Request to the API.
     * @param urlTo
     * @param args
     * @param ctx
     * @param fm
     */
    public PostCustomerData(String urlTo, Map<String, String> args, Context ctx, FragmentManager fm) {
        this.urlTo = urlTo;
        this.args = args;
        this.ctx = ctx;
        this.fm = fm;
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


        try {


            URL url = new URL(urlTo);
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

            if (201 != connection.getResponseCode()) {
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
                Toast.makeText(ctx, "" +
                        "You signed up successfully", Toast.LENGTH_LONG).show();
                Log.e("App", "Success: " + response);
                //Move to Login()
                Fragment fragment = new LoginMenu();
                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

}
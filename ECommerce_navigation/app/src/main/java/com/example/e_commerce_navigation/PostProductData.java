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
import java.util.Iterator;
import java.util.Map;

/**
 * Class related to inserting a product row into DB sending HTTP Request POST to API
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class PostProductData extends AsyncTask<Void, Void, String> {
    private String urlTo;
    private Map<String, String> args;
    private FileInputStream uploadFileInputStream;
    private String filefield;
    private String fileMimeType;
    private String fileName;
    private Context ctx;

    /**
     * constructor of the class related to inserting a product row into DB sending HTTP Request POST to API
     * @param urlTo
     * @param args
     * @param uploadFileInputStream
     * @param filefield
     * @param fileMimeType
     * @param fileName
     * @param ctx
     */
    public PostProductData(String urlTo, Map<String, String> args, FileInputStream uploadFileInputStream, String filefield, String fileMimeType, String fileName, Context ctx) {
        this.urlTo = urlTo;
        this.args = args;
        this.uploadFileInputStream = uploadFileInputStream;
        this.filefield = filefield;
        this.fileMimeType = fileMimeType;
        this.fileName = fileName;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        FileInputStream fileInputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;



        try {
            fileInputStream = uploadFileInputStream;

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
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + fileName + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
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
            fileInputStream.close();
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
                        "The product has been successfully added.", Toast.LENGTH_LONG).show();
                Log.e("App", "Success: " + response);
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }

}
package com.example.testrest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GetData extends AsyncTask<Void, Void, String>
{
    @Override
    protected String doInBackground(Void... params)
    {

        String str="http://recipeapi-prod.us-east-2.elasticbeanstalk.com/api/recipe/1";
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            return stringBuffer.toString();
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String response)
    {
        if(response != null)
        {
            try {
                Log.e("App", "Success: " + response );
            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}
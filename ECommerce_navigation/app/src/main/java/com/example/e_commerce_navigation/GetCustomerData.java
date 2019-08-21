package com.example.e_commerce_navigation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class related to allowing a user sign in and up using HTTP Request and Response
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class GetCustomerData extends AsyncTask<Void, Void, JSONArray> {


    private Context ctx;
    private FragmentManager fm;
    private Customer customer;
    private Activity activity;
    private GetCustomerQuery en;

    //API Product Controller address for products data.
    private String url4GetCustomer = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/customers";


    /**
     * the constructor of the class related to allowing a user sign in and up using HTTP Request and Response
     * @param ctx
     * @param fm
     * @param customer
     * @param activity
     * @param en
     */
    public GetCustomerData(Context ctx, FragmentManager fm, Customer customer, Activity activity, GetCustomerQuery en) {
        this.ctx = ctx;
        this.fm = fm;
        this.customer = customer;
        this.activity = activity;
        this.en = en;

    }

    @Override
    protected JSONArray doInBackground(Void... params) {


        if (this.en == GetCustomerQuery.SIGNIN) {
            this.url4GetCustomer += "?email=" + this.customer.getEmail() + "&password=" + this.customer.getPassword();

            HttpURLConnection urlConn = null;
            BufferedReader bufferedReader = null;


            Handler handler = new Handler(ctx.getMainLooper());


            try {
                URL url = new URL(url4GetCustomer);
                urlConn = (HttpURLConnection) url.openConnection();

                if (200 != urlConn.getResponseCode()) {
                    //Log.d("test12",urlConn.getResponseCode() + " " + urlConn.getResponseMessage() );
                    //throw new Exception( urlConn.getResponseCode() + " " + urlConn.getResponseMessage());

                    //According to the HTTP error code, show the error message.
                    if (urlConn.getResponseCode() == 404) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(ctx, "The email is not registered.", Toast.LENGTH_LONG).show();
                            }
                        });

//                    Toast.makeText(ctx, "The email is not registered.", Toast.LENGTH_LONG).show();
                    } else if (urlConn.getResponseCode() == 409) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(ctx, "You entered the wrong password.", Toast.LENGTH_LONG).show();
                            }
                        });
//                    Toast.makeText(ctx, "You entered the wrong password.", Toast.LENGTH_LONG).show();

                    }
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

        } else {
            this.url4GetCustomer += "/check4duplicate?email=" + this.customer.getEmail();

            HttpURLConnection urlConn = null;
            BufferedReader bufferedReader = null;


            Handler handler = new Handler(ctx.getMainLooper());


            try {
                URL url = new URL(url4GetCustomer);
                urlConn = (HttpURLConnection) url.openConnection();
                //According to the HTTP error code, show the error message.
                if (200 == urlConn.getResponseCode()) {

                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ctx, "The email is already in use.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }else {
                    JSONArray jsonAr = new JSONArray("[{test:test}]");

                    return jsonAr;

                }

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

    }


    @Override
    protected void onPostExecute(JSONArray jsonAr) {


        if (this.en == GetCustomerQuery.SIGNIN) {

            SharedPreferences myPreference =
                    ctx.getSharedPreferences("MyCustomSharedPreferences", 0);
            SharedPreferences.Editor prefEditor = myPreference.edit();


            if (jsonAr != null) {
                try {

                    for (int i = 0; i < jsonAr.length(); i++) {
                        JSONObject obj = jsonAr.getJSONObject(i);
                        //If a user succeeds to sign in, save the necessary information in the preference variable
                        prefEditor.putString("userId", obj.getString("id"));
                        prefEditor.putString("userEmail", obj.getString("email"));
                        prefEditor.putString("userType", obj.getString("type"));
                        prefEditor.commit();

                        //According the user type, adjust the menu
                        ((MainActivity) activity).adjustMenu(obj.getString("type"));

                        Toast.makeText(ctx, obj.getString("email") + ", you've succeeded in signing in", Toast.LENGTH_LONG).show();

                        //Move to SearchProductMenu()
                        Fragment fragment = new SearchProductMenu();
                        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        break;

                    }


                } catch (Exception ex) {

                }
            }
        } else {

            if(jsonAr == null){
                return;
            }

            String urlTo = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/customers";

            Map<String, String> args = new HashMap<String, String>(2);
            args.put("Email", this.customer.getEmail());
            args.put("Password", this.customer.getPassword());
            args.put("Type", this.customer.getType());

            //
            PostCustomerData postDataTask = new PostCustomerData(urlTo,args,ctx,fm);

            //Execute the aysnc post task.
            postDataTask.execute();

        }
    }
}

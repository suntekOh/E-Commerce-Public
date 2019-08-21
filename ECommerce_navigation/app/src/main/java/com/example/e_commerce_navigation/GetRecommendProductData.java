package com.example.e_commerce_navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class related to getting the recommended product information sending HTTP request to API
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class GetRecommendProductData extends AsyncTask<Void, Void, JSONArray> {

    private LinearLayout aLinearLayout;
    private Context ctx;
    private FragmentManager fm;

    //AWS S3 directory for images.
    private String url4Img = "https://s3.us-east-2.amazonaws.com/image4ecommerce/";

    //API Product Controller address for products data.
    private String url4GetProduct = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/products/recommend";

    private int productId;
    private View rootView;


    //The constructor for SearchProductMenu

    /**
     * Constructor of the class related to getting the recommended product information sending HTTP request to API
     * @param productId
     * @param aLinearLayout
     * @param ctx
     * @param fm
     */
    public GetRecommendProductData(int productId, LinearLayout aLinearLayout, Context ctx, FragmentManager fm) {
        this.productId = productId;
        this.aLinearLayout = aLinearLayout;
        this.ctx = ctx;
        this.fm = fm;
    }


    @Override
    protected JSONArray doInBackground(Void... params) {


        this.url4GetProduct += "/" + +this.productId;


        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(url4GetProduct);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }


            JSONArray jsonAr = new JSONArray(stringBuffer.toString());

            return jsonAr;
        } catch (Exception ex) {
            Log.e("App", "yourDataTask", ex);
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


        ArrayList<Product> products = new ArrayList<Product>();
        if (jsonAr != null) {
            try {

                //Change JSONArray to ArrayList<Product> type data.
                for (int i = 0; i < jsonAr.length(); i++) {
                    JSONObject obj = jsonAr.getJSONObject(i);
                    products.add(new Product(obj.getInt("id")
                                    , obj.getString("title")
                                    , obj.getString("description")
                                    , obj.getDouble("price")
                                    , obj.getString("pic")
                                    , obj.getInt("categoryId")
                                    , new Date()
                                    , obj.getInt("stock")
                            )
                    );

                }



                    for (Product aProduct : products) {
                        //Process the image download task
                        new DownloadRecommendedProductImageTask(aLinearLayout, aProduct, ctx, fm).execute(url4Img + aProduct.getPic());
                    }


            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}

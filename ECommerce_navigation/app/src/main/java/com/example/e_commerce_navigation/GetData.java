package com.example.e_commerce_navigation;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

public class GetData extends AsyncTask<Void, Void, JSONArray> {

    private LinearLayout aLinearLayout;
    private Context ctx;
    private FragmentManager fm;
    private String keyword;

    //AWS S3 directory for images.
    private String url4Img = "https://s3.us-east-2.amazonaws.com/image4ecommerce/";

    //API Product Controller address for products data.
    private String url4GetProduct = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/products";

    //If the value of kind is 1, it is from SearchProductMenu fragment.
    //Otherwise it is from ViewProductDetail fragment.
    private int kind;

    private int productId;
    private View rootView;



    //The constructor for SearchProductMenu
    public GetData(LinearLayout aLinearLayout, Context ctx, FragmentManager fm, String keyword, int kind) {
        this.aLinearLayout = aLinearLayout;
        this.ctx = ctx;
        this.keyword = keyword;
        this.fm = fm;
        this.kind = kind;
    }

    //The constructor for ViewProductDetail
    public GetData(int productId, View rootView, Context ctx, FragmentManager fm, int kind) {
        this.productId = productId;
        this.ctx = ctx;
        this.keyword = keyword;
        this.fm = fm;
        this.rootView = rootView;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {

        //If the value of kind is 1, it is from SearchProductMenu fragment.
        //Otherwise it is from ViewProductDetail fragment.
        if(kind == 1){
            if(keyword.length() > 0){
                this.url4GetProduct += "?keyword=" + keyword;
            }
        }else{
            this.url4GetProduct += "/" + +this.productId;
        }

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
                            , new Date())
                    );

                }



                if(kind == 1){
                    for(Product aProduct : products){
                        //Process the image download task
                        new DownloadImageTask(aLinearLayout, aProduct, ctx, fm).execute(url4Img + aProduct.getPic());
                    }
                }else{
                    Product aProduct = null;

//In case of ViewProductDetail fragment, a row is required to create the layout.
                    for(Product p : products){
                        aProduct = p;
                        break;
                    }

                    new DownloadImage4ViewDetail(this.rootView, aProduct, ctx, fm).execute(url4Img + aProduct.getPic());

                }

            } catch (Exception ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}

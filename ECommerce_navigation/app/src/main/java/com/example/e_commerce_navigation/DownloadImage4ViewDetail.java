package com.example.e_commerce_navigation;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class related to downloading a image and creating a layout according to the retrieved product information
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class DownloadImage4ViewDetail extends AsyncTask<String, Void, Bitmap> {
    private Product product;
    private View rootView;
    private Context ctx;
    private FragmentManager fm;

    /**
     * Constructor of the class related to downloading a image and creating a layout according to the retrieved product information
     * @param rootView
     * @param product
     * @param ctx
     * @param fm
     */
    public DownloadImage4ViewDetail(View rootView, Product product, Context ctx, FragmentManager fm) {
        this.rootView = rootView;
        this.product = product;
        this.ctx = ctx;
        this.fm = fm;
    }

    protected Bitmap doInBackground(String... urls) {
        return DownloadImage(urls[0]);
    }

    protected void onPostExecute(Bitmap result) {

        TextView tvProductTitle = (TextView) rootView.findViewById(R.id.tvProductTitle);
        TextView tvPrice = (TextView) rootView.findViewById(R.id.tvPrice);
        TextView tvStock = (TextView) rootView.findViewById(R.id.tvStock);

        ImageView iv = (ImageView) rootView.findViewById(R.id.imgProduct);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
        iv.setImageBitmap(result);
        iv.setLayoutParams(layoutParams);

        tvProductTitle.setText("Name: " + product.getTitle());

        tvPrice.setText("Price: " + String.valueOf(product.getPrice()));

        tvStock.setText("Stock: " + String.valueOf(product.getStock()));

//        //Add the relevant event to Remove button
//        final Button btnRemove = (Button) rootView.findViewById(R.id.btnRemove);
//        btnRemove.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //Remove this product using the API application
//                RemoveProductData RemoveDataTask = new RemoveProductData(product.getId(),ctx);
//                RemoveDataTask.execute();
//
//                //Move to SearchProductMenu()
//                Fragment fragment = new SearchProductMenu();
//                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//            }
//        });

//            tv2.setText("Price: " + String.valueOf(product.getPrice()) +
//                    "\nStock: " + String.valueOf(product.getStock()));

    }

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);

            if(in != null){
                in.close();
            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return bitmap;
    }



}
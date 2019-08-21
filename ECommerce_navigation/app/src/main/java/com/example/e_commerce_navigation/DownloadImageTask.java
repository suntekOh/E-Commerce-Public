package com.example.e_commerce_navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/**
 * Class related to downloading a image and creating a layout according to the retrieved product information
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private LinearLayout llProductList;
    private LayoutParams layoutParams;
    private Product product;
    private Context ctx;
    private FragmentManager fm;


    /**
     * Constructor of the class related to downloading a image and creating a layout according to the retrieved product information
     * @param llProductList
     * @param product
     * @param ctx
     * @param fm
     */
    public DownloadImageTask(LinearLayout llProductList, Product product, Context ctx, FragmentManager fm) {
        this.llProductList = llProductList;
        this.layoutParams = new LinearLayout.LayoutParams(200, 200);
        this.product = product;
        this.ctx = ctx;
        this.fm = fm;
    }

    protected Bitmap doInBackground(String... urls) {
        return DownloadImage(urls[0]);
    }

    protected void onPostExecute(Bitmap result) {

        LinearLayout childLayout = new LinearLayout(
                ctx);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);


        childLayout.setLayoutParams(linearParams);
        childLayout.setOrientation(LinearLayout.HORIZONTAL);

        //Create ImageView by using imageFile through networking.
        ImageView iv = new ImageView(ctx);
        iv.setImageBitmap(result);
        iv.setLayoutParams(layoutParams);


        LinearLayout grandChildLayout = new LinearLayout(
                ctx);
        grandChildLayout.setLayoutParams(linearParams);
        grandChildLayout.setOrientation(LinearLayout.VERTICAL);

        //Stock TextView start
        TextView tvProductStock = new TextView(ctx);


        tvProductStock.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f));


        tvProductStock.setTextSize(17);
        tvProductStock.setPadding(5, 3, 0, 3);
        tvProductStock.setTypeface(Typeface.DEFAULT_BOLD);
        tvProductStock.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        tvProductStock.setText("Stock: " + String.valueOf(product.getStock()));
        //Stock TextView end
        

        TextView tvProductPrice = new TextView(ctx);


        tvProductPrice.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f));


        tvProductPrice.setTextSize(17);
        tvProductPrice.setPadding(5, 3, 0, 3);
        tvProductPrice.setTypeface(Typeface.DEFAULT_BOLD);
        tvProductPrice.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        tvProductPrice.setText("Price: " + String.valueOf(product.getPrice()));

        TextView tvProductName = new TextView(ctx);


        tvProductName.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 1f));


        tvProductName.setTextSize(17);
        tvProductName.setPadding(5, 3, 0, 3);
        tvProductName.setTypeface(Typeface.DEFAULT_BOLD);
        tvProductName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        //Use hint as a variable storage.
        tvProductName.setHint(String.valueOf(product.getId()));
        tvProductName.setText(Html.fromHtml("<u>" + product.getTitle() + "</u>"));
        tvProductName.setTextColor(Color.parseColor("#0000FF"));

        tvProductName.setClickable(true);
        //register this event to each product name TextView object
        // so that it can move to the Product Detail page.
        tvProductName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView aTextView = (TextView) v;
                String productId = aTextView.getHint().toString();

                Fragment fragment = new ViewProductDetail();
                //Pass the productId parameter to be used
                Bundle args = new Bundle();
                args.putString("productId", productId);
//                Toast.makeText(ctx, productId, Toast.LENGTH_LONG).show();

                fragment.setArguments(args);

                //Move to ViewProductDetail fragment.
                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();


            }
        });

        grandChildLayout.addView(tvProductStock, 0);
        grandChildLayout.addView(tvProductPrice, 0);
        grandChildLayout.addView(tvProductName, 0);


        childLayout.addView(grandChildLayout, 0);
        childLayout.addView(iv, 0);


        llProductList.addView(childLayout);
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
            if(in!=null){
                in.close();
            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return bitmap;
    }



}


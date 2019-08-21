package com.example.testrest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    static final int READ_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getRecipe(View v) {

        TextView aTextView = (TextView) findViewById(R.id.tvRegisteredProgram);

        try {
            new GetData().execute();

        } catch (Exception e) {

        }
    }

    public void postRecipe(View v) {

        //TextView aTextView = (TextView) findViewById(R.id.tvRegisteredProgram);


        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);


//        PostData postDataTask = new PostData();
//        postDataTask.setUrlTo("http://recipeapi-prod.us-east-2.elasticbeanstalk.com/api/recipe/");
//        postDataTask.setFilefield("File");
//        postDataTask.setFileMimeType("image/jpeg");
//
//
//        Map<String, String> args = new HashMap<String, String>(2);
//        args.put("Foodname", "android1");
//        args.put("Instruction", "android1");
//        args.put("Email", "android1");
//        args.put("Author", "android1");
//
//        postDataTask.setArgs(args);
//
//
//        try {
//
//            InputStream is = getAssets().open("maxresdefault.jpg");
//            File tempfile = File.createTempFile("tempfile", ".jpg", getDir("filez", 0));
//
//            FileOutputStream os = new FileOutputStream(tempfile);
//            byte[] buffer = new byte[16000];
//            int length = 0;
//            while ((length = is.read(buffer)) != -1) {
//                os.write(buffer, 0, length);
//            }
//
//            FileInputStream fis = new FileInputStream(tempfile);
//            postDataTask.setUploadFileInputStream(fis);
//
//
//        } catch (IOException e) {
//
//        }
//
//
//        postDataTask.execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //Log.e("App", "Uri: " + uri.toString());
                //Toast.makeText(MainActivity.this, "Uri: " + uri.toString(), Toast.LENGTH_LONG).show();
            }
            try {


                PostData postDataTask = new PostData();
                postDataTask.setUrlTo("http://recipeapi-prod.us-east-2.elasticbeanstalk.com/api/recipe/");
                postDataTask.setFilefield("File");
                postDataTask.setFileMimeType("image/jpeg");


                Map<String, String> args = new HashMap<String, String>(2);
                args.put("Foodname", "android1");
                args.put("Instruction", "android1");
                args.put("Email", "android1");
                args.put("Author", "android1");

                postDataTask.setArgs(args);

                InputStream is = getContentResolver().openInputStream(uri);
                File tempfile = File.createTempFile("tempfile", ".jpg", getDir("filez", 0));

                FileOutputStream os = new FileOutputStream(tempfile);
                byte[] buffer = new byte[16000];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }

                FileInputStream fis = new FileInputStream(tempfile);
                postDataTask.setUploadFileInputStream(fis);

                postDataTask.execute();
            } catch (IOException e) {

            }

        }
    }

}

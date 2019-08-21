package com.example.e_commerce_navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add Product Menu
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class AddProductMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final int READ_REQUEST_CODE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String req4productDesc;
    private String req4productTitle;
    private String req4productPrice;
    private String req4productCategoryTypeText;
    private HashMap<String ,Integer> hmProductCategoryType = new HashMap();


    private OnFragmentInteractionListener mListener;

    public AddProductMenu() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddProductMenu newInstance(String param1, String param2) {
        AddProductMenu fragment = new AddProductMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Call the AsyncTask to get the category data
     * Register the relevant event to the Post Product Button
     * When POST button is clicked, get the new product information a user enters and pass the information to the next function
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_product_menu, container, false);

        //Call the AsyncTask to get the category data
        fillCategoryRequest(getContext(),rootView);

        final Button btnPostProduct = (Button) rootView.findViewById(R.id.btnPostProduct);
        final EditText etProductDesc = (EditText) rootView.findViewById(R.id.productDesc);
        final EditText etProductTitle = (EditText) rootView.findViewById(R.id.productTitle);
        final EditText etProductPrice = (EditText) rootView.findViewById(R.id.productPrice);

        final Spinner aSpinner = (Spinner) rootView.findViewById(R.id.spProductCategoryType);



        //Register the relevant event to the Post Product Button
        btnPostProduct.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");


                req4productDesc = etProductDesc.getText().toString().trim();
                req4productTitle = etProductTitle.getText().toString().trim();
                req4productPrice = etProductPrice.getText().toString().trim();
                req4productCategoryTypeText = aSpinner.getSelectedItem().toString().trim();

                //validation check
                if (req4productDesc == null ||
                        req4productTitle == null ||
                        req4productPrice == null ||
                        req4productCategoryTypeText == null ||
                        req4productDesc.length()==0 ||
                        req4productTitle.length() == 0 ||
                        req4productPrice.length() == 0 ||
                        req4productCategoryTypeText.length() == 0 )
                 {
                    Toast.makeText(getContext(), "Error.\nPlease check the value you entered again.", Toast.LENGTH_LONG).show();
                    return;
                }


                startActivityForResult(intent, READ_REQUEST_CODE);

            }
        });


        return rootView;

    }


    /**
     * Call an asynchronous task to insert a new product into DB
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

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
            }
            try {


                String urlTo = "http://ecommerceapi-prod.us-east-2.elasticbeanstalk.com/api/products";
                String fileField = "File";
                String fileMimeType = "image/jpeg";

                String uriString = uri.toString();
                File myFile = new File(uriString);
                String fileName = null;

                //Get the file name to post.
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    fileName = myFile.getName();
                }

                Map<String, String> args = new HashMap<String, String>(2);
                args.put("Description", this.req4productDesc);
                args.put("Price", this.req4productPrice);
                args.put("Title", this.req4productTitle);
                args.put("CategoryId",(hmProductCategoryType.get(req4productCategoryTypeText).toString()));

                InputStream is = getContext().getContentResolver().openInputStream(uri);
                File tempfile = File.createTempFile("tempfile", ".jpg", getContext().getDir("filez", 0));

                FileOutputStream os = new FileOutputStream(tempfile);
                byte[] buffer = new byte[16000];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }

                FileInputStream fis = new FileInputStream(tempfile);

                PostProductData postDataTask = new PostProductData(urlTo,args,fis,fileField,fileMimeType,fileName,getContext());

                //Execute the aysnc post task.
                postDataTask.execute();
            } catch (IOException e) {

            }

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Add Product");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Call the AsyncTask to get the category data
     * @param ctx
     * @param view
     */
    public void fillCategoryRequest(Context ctx, View view) {

        try {
            //Call the AsyncTask to get the category data
            new GetCategoryData(ctx, this, view).execute();


        } catch (Exception e) {

        }
    }

    /**
     * Fill a category spinner according to the acquired category data from DB.
     * @param view
     * @param aList
     */
    public void fillCategoryAsyncResponse(View view, List<Category> aList){

        ArrayList<String> arrProductCategoryType = new ArrayList<String>();
        //Insert courses into course Spinner object.
        for(Category i:aList){
            arrProductCategoryType.add(i.getDescriptions());
            hmProductCategoryType.put(i.getDescriptions() ,i.getId());
        }



        ArrayAdapter<String> adptProductCategoryType = new ArrayAdapter<String>(
                view.getContext(), android.R.layout.simple_spinner_item, arrProductCategoryType);

        adptProductCategoryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner aSpinner = view.findViewById(R.id.spProductCategoryType);
        aSpinner.setAdapter(adptProductCategoryType);

        aSpinner.setSelection(0);

    }


}

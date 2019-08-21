package com.example.e_commerce_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class related to showing the product detail information
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class ViewProductDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public ViewProductDetail() {

    }


    public static ViewProductDetail newInstance(String param1, String param2) {
        ViewProductDetail fragment = new ViewProductDetail();
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
     * take action according to user type and the clicked button.
     * Add Stock
     * Remove Product
     * Order Product
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view_product_detail, container, false);
        String userType = getContext().getSharedPreferences("MyCustomSharedPreferences", MODE_PRIVATE).getString("userType", "");
        final String userId = getContext().getSharedPreferences("MyCustomSharedPreferences", MODE_PRIVATE).getString("userId", "");
        final String productId = getArguments().getString("productId");
        final Button btnAddStock = (Button) rootView.findViewById(R.id.btnAddStock);
        final Button btnRemove = (Button) rootView.findViewById(R.id.btnRemove);
        final Button btnOrder = (Button) rootView.findViewById(R.id.btnOrder);

        LinearLayout llAdd = rootView.findViewById(R.id.llAdd);
        LinearLayout llRemove = rootView.findViewById(R.id.llRemove);
        LinearLayout llOrder = rootView.findViewById(R.id.llOrder);
        LinearLayout llRecommendedProducts = rootView.findViewById(R.id.llRecommendedProductList);

        final FragmentManager fm = getFragmentManager();
        final View alertDialogueView = inflater.inflate(R.layout.alert_dialogue_inventories_qty_change, container, false);

        //If a user is an anonymous user, make all buttons invisible.
        if (userType == null || userType.trim().length() <= 0) {
            llAdd.setVisibility(View.INVISIBLE);
            llRemove.setVisibility(View.INVISIBLE);
            llOrder.setVisibility(View.INVISIBLE);

        } else if (userType.equals("C") || userType.equals("S") || userType.equals("A")) {

            //If a user is customer
            if (userType.equals("C")) {
                llAdd.setVisibility(View.INVISIBLE);
                llRemove.setVisibility(View.INVISIBLE);


            //If a user is supplier or administrator
            } else if (userType.equals("S") || userType.equals("A")) {

                btnAddStock.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (alertDialogueView != null) {
                            ViewGroup parentViewGroup = (ViewGroup) alertDialogueView.getParent();
                            if (parentViewGroup != null) {
                                parentViewGroup.removeAllViews();
                            }
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("Question");
                        alertDialog.setMessage("Decide the quantity to add");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText etQuantity = (EditText) alertDialogueView.findViewById(R.id.etQuantity);
                                Map<String, String> args = new HashMap<String, String>(2);
                                args.put("Qty", etQuantity.getText().toString());
                                args.put("CustomerId", userId);
                                args.put("ProductId", productId);

                                //Call the asynchronous task to insert the new inventory row.
                                PostInventoryData postDataTask = new PostInventoryData(args, getContext(), fm, PostInventoryQuery.AddStock);

                                //Execute the aysnc post task.
                                postDataTask.execute();


                            }
                        });


                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        });




                        alertDialog.setView(alertDialogueView);
                        alertDialog.show();

                    }
                });


                btnRemove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Remove this product using the API application
                        RemoveProductData RemoveDataTask = new RemoveProductData(Integer.valueOf(productId), getContext());
                        RemoveDataTask.execute();

                        //Move to SearchProductMenu()
                        Fragment fragment = new SearchProductMenu();
                        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }
                });


            }

            btnOrder.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (alertDialogueView != null) {
                        ViewGroup parentViewGroup = (ViewGroup) alertDialogueView.getParent();
                        if (parentViewGroup != null) {
                            parentViewGroup.removeAllViews();
                        }
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Question");
                    alertDialog.setMessage("Decide the quantity to order");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etQuantity = (EditText) alertDialogueView.findViewById(R.id.etQuantity);
//                            Toast.makeText(getContext(), etQuantity.getText().toString(), Toast.LENGTH_LONG).show();

                            Map<String, String> args = new HashMap<String, String>(2);
                            args.put("Qty", etQuantity.getText().toString());
                            args.put("CustomerId", userId);
                            args.put("ProductId", productId);

                            //Call the asynchronous task to order the product.
                            PostInventoryData postDataTask = new PostInventoryData(args, getContext(),fm, PostInventoryQuery.Order);

                            //Execute the aysnc post task.
                            postDataTask.execute();


                        }
                    });


                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });


                    alertDialog.setView(alertDialogueView);
                    alertDialog.show();
                }
            });


        }


        //After getting a product information, create the layout.
        new GetProductData(Integer.parseInt(productId), rootView, getContext(), fm, GetProductQuery.ViewProductDetail).execute();

        //After getting recommended products information, create the layout.
        new GetRecommendProductData(Integer.parseInt(productId), llRecommendedProducts, getContext(), fm).execute();

        return rootView;
    }


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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}

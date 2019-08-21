package com.example.e_commerce_navigation;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Search Product Menu
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class SearchProductMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private static final int kind = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchProductMenu() {
        // Required empty public constructor
    }

    public static SearchProductMenu newInstance(String param1, String param2) {
        SearchProductMenu fragment = new SearchProductMenu();
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
     * If a user enters a keyword in the search box and hit ENTER,
     * bring the relevant products through the API application.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search_product_menu, container, false);
        // This FragmentManager will be used to move to another fragment.
        final FragmentManager fm = getFragmentManager();

        //If a user enters a keyword in the search box and hit ENTER,
        // bring the relevant products through the API application.
        final EditText edittext = (EditText) rootView.findViewById(R.id.keyword);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    TextView aTextView = (TextView) v;
                    String keyword = aTextView.getText().toString().trim();

                    LinearLayout llProductList = rootView.findViewById(R.id.llProductList);
                    //Remove the previous layout.
                    llProductList.removeAllViews();



                    getProductsList(llProductList, getContext(), fm, keyword);

                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Search Product");
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Call the AsyncTask to get the products list according to the keyword
     * @param aLinearLayout
     * @param ctx
     * @param fm
     * @param keyword
     */
    public void getProductsList(LinearLayout aLinearLayout, Context ctx, FragmentManager fm, String keyword) {

        try {
            //Call the AsyncTask to get the products list according to the keyword
            new GetProductData(aLinearLayout, ctx, fm, keyword, GetProductQuery.SearchProduct).execute();

        } catch (Exception e) {

        }
    }

}

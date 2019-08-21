package com.example.e_commerce_navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


/**
 * Login Menu
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class LoginMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginMenu newInstance(String param1, String param2) {
        LoginMenu fragment = new LoginMenu();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_login_menu, container, false);
        // This FragmentManager will be used to move to another fragment.
        final FragmentManager fm = getFragmentManager();


        //Add the relevant event to Login button
        final Button btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String email = ((EditText) rootView.findViewById(R.id.etEmail)).getText().toString().trim();
                String password = ((EditText) rootView.findViewById(R.id.etPassword)).getText().toString().trim();

                //validation check
                if (email == null ||
                        password == null ||
                        email.length()==0 ||
                        password.length() == 0)
                {
                    Toast.makeText(getContext(), "Error.\nPlease check the value you entered again.", Toast.LENGTH_LONG).show();
                    return;
                }

                signIn(getContext(), fm, new Customer(email,password,""), GetCustomerQuery.SIGNIN);
            }
        });

        //Add the relevant event to Register button
        final Button btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                ((MainActivity)getActivity()).displaySelectedScreen(R.id.registerMenu);
            }
        });

//
//
//        etEmail.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//
//                    TextView aTextView = (TextView) v;
//                    String keyword = aTextView.getText().toString().trim();
//
//                    //getProductsList(llProductList, getContext(), fm, keyword);
//
//                    return true;
//                }
//                return false;
//            }
//        });

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Login");
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
     * This interface must be implemented by activities that contain thisGetCustomerData
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
     * call the AsyncTask to get the products list according to the keyword
     * @param ctx
     * @param fm
     * @param customer
     * @param en
     */
    public void signIn(Context ctx, FragmentManager fm, Customer customer, GetCustomerQuery en) {

        try {
            //Call the AsyncTask to get the products list according to the keyword
            new GetCustomerData(ctx, fm, customer, getActivity(),en).execute();


        } catch (Exception e) {

        }
    }
}

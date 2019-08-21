package com.example.e_commerce_navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Register Menu
 *
 * @author Seontaek Oh
 * @version 1.0
 * @since 1.0
 */
public class RegisterMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HashMap<String, String> hmCustomerType = new HashMap<String, String>();


    private OnFragmentInteractionListener mListener;

    public RegisterMenu() {
        // Required empty public constructor
    }


    public static RegisterMenu newInstance(String param1, String param2) {
        RegisterMenu fragment = new RegisterMenu();
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
        final View rootView = inflater.inflate(R.layout.fragment_register_menu, container, false);


        //Make a customer type spinner
        ArrayList<String> arrCustomerType = new ArrayList<String>();

        arrCustomerType.add("Customer");
        arrCustomerType.add("Supplier");

        hmCustomerType.put("Customer", "C");
        hmCustomerType.put("Supplier", "S");

        ArrayAdapter<String> adptCustomerType = new ArrayAdapter<String>(
                rootView.getContext(), android.R.layout.simple_spinner_item, arrCustomerType);

        adptCustomerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner aSpinner = rootView.findViewById(R.id.spCustomerType);
        aSpinner.setAdapter(adptCustomerType);

        aSpinner.setSelection(0);


        final FragmentManager fm = getFragmentManager();

        final EditText etUserEmail = (EditText) rootView.findViewById(R.id.userEmail);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.password);
        final EditText etConfirmPassword = (EditText) rootView.findViewById(R.id.confirmPassword);


        //Add the relevant event to Register button
        final Button btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String userEmail = etUserEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                Spinner bSpinner = (Spinner) rootView.findViewById(R.id.spCustomerType);
                String customerType = bSpinner.getSelectedItem().toString().trim();

                //validation check
                if (userEmail == null ||
                        password == null ||
                        customerType == null ||
                        userEmail.length() == 0 ||
                        password.length() == 0 ||
                        customerType.length() == 0) {
                    Toast.makeText(getContext(), "Error.\nPlease check the value you entered again.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!password.equals(confirmPassword)){
                    Toast.makeText(getContext(), "Error.\nPlease check the value you entered again.", Toast.LENGTH_LONG).show();
                    return;
                }

                check4duplication(getContext(), fm, new Customer(userEmail, password, hmCustomerType.get(customerType)), GetCustomerQuery.SIGNUP);


            }
        });


        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Register");
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

    /**
     * Check whether the email is already in use
     * If not, make a user
     *
     * @param ctx
     * @param fm
     * @param customer
     * @param en
     */
    public void check4duplication(Context ctx, FragmentManager fm, Customer customer, GetCustomerQuery en) {

        try {
            //Call the AsyncTask to get the products list according to the keyword
            new GetCustomerData(ctx, fm, customer, getActivity(), en).execute();


        } catch (Exception e) {

        }
    }

}

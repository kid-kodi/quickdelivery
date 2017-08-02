package com.alaqos.kodi.opium.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.helper.SessionManager;
import com.alaqos.kodi.opium.model.User;
import com.alaqos.kodi.opium.network.ApiClient;
import com.alaqos.kodi.opium.network.UserApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInFragment extends Fragment {

    private EditText inputPassword;
    private EditText inputPhone;
    private Button btnLogin;
    private ProgressDialog pDialog;
    private List<User> user_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        inputPhone = (EditText) v.findViewById(R.id.phone);
        inputPassword = (EditText) v.findViewById(R.id.password);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);

        user_list = new ArrayList<User>();

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String phone = inputPhone.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!phone.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(phone, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        return v;
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String phone, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        Map<String, String> options = new HashMap();
        options.put("phone", phone);
        options.put("password", password);

        UserApiInterface apiService =
                ApiClient.getClient().create(UserApiInterface.class);
        Call<List<User>> call = apiService.getList( options );
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Toast.makeText(getActivity(), "###"+response.body(), Toast.LENGTH_LONG).show();

                user_list = response.body();

                if (user_list.size()>0){
                    User user = user_list.get(0);
                    SessionManager sessionManager = new SessionManager(getActivity());
                    sessionManager.createLoginSession(
                            user.getId(),
                            user.getFirst_name(),
                            user.getLast_name(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getImage());

                    Intent i = new Intent(getActivity(),
                            MainActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
                else{
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static SignInFragment newInstance(String text) {

        SignInFragment f = new SignInFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}

package com.bcsa.movieproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bcsa.movieproject.DatabaseHelper;
import com.bcsa.movieproject.R;
import com.bcsa.movieproject.User;
import com.bcsa.movieproject.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    BottomNavigationView navView;

    public LoginFragment () {
    }

    public LoginFragment (BottomNavigationView navView) {
        this.navView = navView;
    }

    EditText editTextEmail;
    EditText editTextPassword;

    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;

    Button buttonLogin;

    DatabaseHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final TextView textView = root.findViewById(R.id.text_login);
        textView.setText("Welcome!");

        databaseHelper = new DatabaseHelper(getActivity());

        TextView textViewCreateAccount = (TextView) root.findViewById(R.id.textViewCreateAccount);
        textViewCreateAccount.setText(fromHtml("<font color='#ffffff'>I don't have account yet. </font><font color='#0c0099'>create one</font>"));
        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LoginFragment.this, RegisterFragment.class);
                //startActivity(intent);

                getFragmentManager().beginTransaction().replace(R.id.container, new RegisterFragment()).commit();
            }
        });

        editTextEmail = (EditText) root.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) root.findViewById(R.id.editTextPassword);
        textInputLayoutEmail = (TextInputLayout) root.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) root.findViewById(R.id.textInputLayoutPassword);
        buttonLogin = (Button) root.findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {

                    String Email = editTextEmail.getText().toString();
                    String Password = editTextPassword.getText().toString();

                    User currentUser = databaseHelper.Authenticate(new User(null, null, Email, Password));

                    if (currentUser != null) {
                        Snackbar.make(buttonLogin, "Successfully Logged in!", Snackbar.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("LOGGED_USER", getActivity().MODE_PRIVATE).edit();
                        editor.putString("idName", Email);
                        editor.apply();

                        User.LOGGEDIN = currentUser;

                        navView.setVisibility(View.VISIBLE);

                        //getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                        getFragmentManager().popBackStack();
                    } else {

                        Snackbar.make(buttonLogin, "Failed to log in , please try again", Snackbar.LENGTH_LONG).show();

                    }
                }
            }
        });

        return root;
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public boolean validate() {
        boolean valid = false;

        String Email = editTextEmail.getText().toString();
        String Password = editTextPassword.getText().toString();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            valid = false;
            textInputLayoutEmail.setError("Please enter valid email!");
        } else {
            valid = true;
            textInputLayoutEmail.setError(null);
        }

        if (Password.isEmpty()) {
            valid = false;
            textInputLayoutPassword.setError("Please enter valid password!");
        } else {
            if (Password.length() > 4) {
                valid = true;
                textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textInputLayoutPassword.setError("Password is to short!");
            }
        }

        return valid;
    }
}
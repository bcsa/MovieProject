package com.bcsa.movieproject;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.bcsa.movieproject.LoginFragment;
import com.bcsa.movieproject.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    //DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sec);
        //databaseHelper = new DatabaseHelper(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_favourites, R.id.navigation_nowplaying)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setVisibility(View.GONE);

        //SharedPreferences.Editor editor = getSharedPreferences("LOGGED_USER", MODE_PRIVATE).edit();
        //editor.putString("idName", "").apply();
        //editor.putInt("idName", 12);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        if(!sharedPreferences.getString("idName", "").equals("")){
            //Intent intent = new Intent(getApplicationContext(), MainSectionActivity.class);
            //startActivity(intent);
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            navView.setVisibility(View.VISIBLE);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment(navView)).addToBackStack(null).commit();
        }

    }

}

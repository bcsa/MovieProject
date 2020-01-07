package com.bcsa.movieproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public int PAGE = 1;
    public static String API_KEY = "807b3d01ed22930f0d6447da91528e1f";
    public static String LANGUAGE = "en-US";
    public static String CATEGORY = "popular";

    RecyclerView recyclerView;
    RecyclerViewAdaptor recyclerViewAdaptor;

    //private Context context;
    //private List<Result> movies = new ArrayList<Result>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        //textView.setText("Home fragment");

        recyclerView = root.findViewById(R.id.recyclerView_top);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<MovieResults> movieResultsCall = retrofitInterface.getPopularMovies(API_KEY,PAGE);

        movieResultsCall.enqueue(new Callback<MovieResults>() {

            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {

                MovieResults movieResults = response.body();
                List<Result> listofMovies = movieResults.getResults();


                recyclerViewAdaptor = new RecyclerViewAdaptor(getContext(),listofMovies);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(recyclerViewAdaptor);
                recyclerViewAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView =(SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                recyclerViewAdaptor.getFilter().filter(text);
                return false;
            }
        });

    }
}
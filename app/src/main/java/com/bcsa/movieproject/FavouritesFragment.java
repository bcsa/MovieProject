package com.bcsa.movieproject;

import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.bcsa.movieproject.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouritesFragment extends Fragment {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public int PAGE = 1;
    public static String API_KEY = "807b3d01ed22930f0d6447da91528e1f";

    RecyclerView recyclerView;
    RecyclerViewAdaptor recyclerViewAdaptor;
    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        recyclerView = root.findViewById(R.id.recyclerView_fav);

        databaseHelper = new DatabaseHelper(getContext());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<MovieResults> movieResultsCall = retrofitInterface.getPopularMovies(API_KEY,PAGE);

        movieResultsCall.enqueue(new Callback<MovieResults>() {

            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {

                MovieResults movieResults = response.body();
                List<Result> listofMovies = movieResults.getResults();
                //List<Result> listofMovies2 = databaseHelper.getFavourites(User.LOGGEDIN.userName);

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
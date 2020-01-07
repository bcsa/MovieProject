package com.bcsa.movieproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("search/movie?api_key=807b3d01ed22930f0d6447da91528e1f")
    Call<MovieResults> getAllData(@Query("query")String query);

    @GET("movie/popular")
    Call<MovieResults> getPopularMovies(@Query("api_key") String apiKey, @Query ("page") int page);

}

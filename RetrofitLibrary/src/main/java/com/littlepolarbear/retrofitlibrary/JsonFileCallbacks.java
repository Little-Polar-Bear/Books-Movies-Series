package com.littlepolarbear.retrofitlibrary

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/*A interface designed to get callbacks on data once loaded
from the json file from the remote cloud service.*/
public interface JsonFileCallbacks {

    /*Key value passed into
    URL path files in order to retrieve the specific JSON file for that category*/
    @GET("JSON%20-%20book%20files/{bookGenre}_bestSellingBooks.json")
    Call<JsonPOJO> getBooks(@Path(value = "bookGenre") String genre);

    @GET("JSON%20-%20movie%20files/{movieGenre}_movies.json")
    Call<JsonPOJO> getMovies(@Path(value = "movieGenre") String genre);

    @GET("JSON%20-%20tv%20files/{tvGenre}_tv.json")
    Call<JsonPOJO> getTvShows(@Path(value = "tvGenre") String genre);


    /*Default Json path file for movies, tv shows and books
     * These overloaded methods get called when no key is specified*/
    @GET("JSON%20-%20movie%20files/action_movies.json")
    Call<JsonPOJO> getMovies();

    @GET("JSON%20-%20tv%20files/actionadventure_tv.json")
    Call<JsonPOJO> getTvShows();

    @GET("JSON%20-%20book%20files/combined-print-and-e-book-fiction_bestSellingBooks.json")
    Call<JsonPOJO> getBooks();
}

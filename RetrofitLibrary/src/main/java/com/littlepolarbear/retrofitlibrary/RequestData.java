package com.littlepolarbear.retrofitlibrary;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*A class designed to handle the connection of interacting and retrieving data from a api.
 * Most commonly a scenario of getting data from a JSon file hosted on a remote server/cloud service.*/
public class RequestData {

    private JsonFileCallbacks jsonFileCallbacks;
    private final RequestDataCallbacks requestDataCallbacks;

    public RequestData(RequestDataCallbacks requestDataCallbacks) {
        this.requestDataCallbacks = requestDataCallbacks;
    }

    /**
     * @param baseUrlJsonFile Pass in the base api url string.
     *                        Connect/create Retrofit to use with the api service
     */
    public void initRetrofitConnection(String baseUrlJsonFile) {
        InitRetrofit initRetrofit = new InitRetrofit(baseUrlJsonFile);
        jsonFileCallbacks = initRetrofit.createRetrofit();
    }

    /*public method used for the calling class to specify which Json file to read from.*/
    public void getDataFromFile(String fileName) {
        Call<JsonPOJO> jsonCall;
        switch (fileName) {
            case "movies":
                jsonCall = jsonFileCallbacks.getMovies();
                break;
            case "series":
                jsonCall = jsonFileCallbacks.getTvShows();
                break;
            default:
                jsonCall = jsonFileCallbacks.getBooks();
        }
        retrieveDataFromOnlineJsonFile(jsonCall);
    }

    /*public method used for the calling class to specify which Json file to read from.*/
    public void getDataFromFile(String fileName, String key) {
        Call<JsonPOJO> jsonCall;
        switch (fileName) {
            case "movies":
                jsonCall = jsonFileCallbacks.getMovies(key);
                break;
            case "series":
                jsonCall = jsonFileCallbacks.getTvShows(key);
                break;
            default:
                jsonCall = jsonFileCallbacks.getBooks(key);
        }
        retrieveDataFromOnlineJsonFile(jsonCall);
    }

    /*method to connect to the api and retrieve a list of categories and their children lists*/
    private void retrieveDataFromOnlineJsonFile(Call<JsonPOJO> jsonCall) {
        // make sure callbacks are not null, so we can get the data once it arrives
        if (jsonFileCallbacks == null) {
            throw new NullPointerException("Must call method @initRetroFitConnection first");
        }

        // start the process and get data on enqueue (background thread)
        jsonCall.enqueue(new Callback<JsonPOJO>() {
            @Override
            public void onResponse(@NonNull Call<JsonPOJO> call, @NonNull Response<JsonPOJO> response) {
                Log.v("TAG_URL", call.request().url().toString());

                if (response.body() == null) {
                    requestDataCallbacks.requestIsEmpty();
                    return;
                }

                requestDataCallbacks.requestSuccessful(response.body().getCategoryList());
            }

            @Override
            public void onFailure(@NonNull Call<JsonPOJO> call, @NonNull Throwable t) {
                requestDataCallbacks.requestUnsuccessful(t.getMessage());
            }
        });
    }

    /*Calling class package project needs to implement retrofit in the build module.*/
    public interface RequestDataCallbacks {
        void requestSuccessful(List<JsonPOJO.Category> categoryList);

        void requestUnsuccessful(String errorMessage);

        void requestIsEmpty();
    }
}

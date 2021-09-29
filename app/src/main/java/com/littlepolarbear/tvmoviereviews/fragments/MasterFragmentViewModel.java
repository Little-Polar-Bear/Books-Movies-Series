package com.littlepolarbear.tvmoviereviews.fragments;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.littlepolarbear.retrofitlibrary.JsonPOJO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MasterFragmentViewModel extends ViewModel {

    /*observable String list of genre categories a user can choose from*/
    private final MutableLiveData<List<String>> genreCategories = new MutableLiveData<>();

    /*observable JsonPOJO.Category list of items*/
    private List<JsonPOJO.Category> itemList;

    /*Populate our genreCategories
     * This list is observable, meaning that anytime it is updated we can subscribe to get notified*/
    public void setGenreCategories(String urlPath) {
        // create a list to add to
        final List<String> keyList = new ArrayList<>();
        // read all the text returned by the server/file
        //must call network on background thread
        Thread thread = new Thread(() -> {
            // initialize reader so we can close in finally.
            BufferedReader in = null;
            try {
                Log.v("DEBUG_TAG", "opening text file on " + Thread.currentThread());
                // create a url from the string path
                URL url = new URL(urlPath);
                // create reader and open the url stream.
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String key;
                // iterate over values while the text file has a next line to go to.
                while ((key = in.readLine()) != null) {
                    // add to list
                    keyList.add(key);
                }
                Log.v("DEBUG_TAG", "returning the keyList with a size of: " + keyList.size() + " on " +
                        Thread.currentThread());
                // post the value as we are on a background thread. 
                genreCategories.postValue(keyList);
            } // catch the exceptions
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // close our reader in finally,
                // if there is any error in the above section  finally
                // will still get called and hopefully close with no exception of it's own.
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // start background thread.
        thread.setName("View Model Background thread");
        thread.start();
    }

    /*returns the genre categories list*/
    public MutableLiveData<List<String>> getGenreCategories() {
        return genreCategories;
    }

    /*get and set items list*/
    public List<JsonPOJO.Category> getItemList() {
        return itemList;
    }

    public void setItemList(List<JsonPOJO.Category> itemList) {
        this.itemList = itemList;
    }
}

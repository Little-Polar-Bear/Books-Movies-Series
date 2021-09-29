package com.littlepolarbear.tvmoviereviews.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;

import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.utils.UIHelper;

/*A search results activity  that complies with the android framework practices.
 * Although search function is not set up as of yet.
 * Need to decide how the search will be executed and what data to use.
 * There is no master database set up to be able to search, just individual JSon files.
 * When the api is complete, a search GET will most likely take care of this issue.*/
public class SearchResultsActivity extends AppCompatActivity {

    private AppCompatTextView searchTextView;

    // debug tag
    private static final String TAG = "SEARCH_RESULTS_ACTIVITY";

    // UI class that has convenience methods to help
    // override some methods here if needed.
    private final UIHelper uiHelper = new UIHelper() {
        /*Return the id of the toolbar */
        @Override
        public int getToolbarById() {
            return R.id.material_toolbar;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchTextView = findViewById(R.id.search_result);

        // retrieve the search string via a intent.
        Intent intent = getIntent();
        handleSearchIntent(intent);

        // set toolbar as support action bar
        try {
            uiHelper.setToolbarAsSupportActionBar(this, null, true);
        } catch (Exception e) {
            Log.v(TAG, "Error setting up toolbar in SearchResultsActivity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*Handle search intent*/
    private void handleSearchIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // todo need to implement searching
            searchTextView.setText("Nothing found for " + query);
        }
    }

    /*Override onCreateOptions to handle our search function in the toolbar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu, menu);

        // onCreateOptions find, expand view and clear the focus
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.expandActionView();
        searchView.clearFocus();

        // set the hint
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            searchView.setQuery(getIntent().getStringExtra(SearchManager.QUERY), false);
        }

        // Associate searchable configuration with the SearchView
        // so user can perform more searches from within the search activity
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        // set on query text listener to clear focus again on submit search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // set on actionListener to finish activity if back pressed in search view
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // close activity on back arrow press in search view
                finish();
                return false;
            }
        });

        return true;
    }

    /*Handle and override the new search intent if the SearchActivity is still alive.
     * lets user perform more searches using the intent while still in the searchActivity.*/
    @Override
    protected void onNewIntent(Intent intent) {
        Log.v("newIntent", "New intent: " + intent.toString());
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }
}

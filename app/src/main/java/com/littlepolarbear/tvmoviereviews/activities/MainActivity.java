package com.littlepolarbear.tvmoviereviews.activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.fragments.MasterFragment;

/*Main activity inflates a layout to house the fragments.
 * Initiates and configures the 'Bottom Navigation' so user can move between the different fragments*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Adding the Book fragment on run time start up, this is the page that loads on cold start.
         * only add if null to avoid re-adding duplicates
         * on config change because it is auto saved and restored for us.*/
        if (savedInstanceState == null) {
            replaceFragmentInContainer("books");
        }

        // bottom nav configure
        initBottomNavigation();
    }

    /*Bottom navigation initiation/configuration*/
    private void initBottomNavigation() {
        // find bottom navigation.
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // set the bottom navigation to have rounded top corners.
//        MaterialShapeDrawable shapeDrawable = (MaterialShapeDrawable) bottomNavigation.getBackground();
//        float radius = getResources().getDimension(R.dimen.rounded_bottom_nav);
//        UtilityClass.doMaterialShapeDesignTop(shapeDrawable, radius);

        // Now set selected menu item listener,
        // when a item in navigation is clicked the corresponding fragment loads.
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {

            // get menu id that is selected.
            int menuId = item.getItemId();
            // if/else to decide what fragment to replace with.
            // The fragment is the same but the data it loads changes.
            // use a string to determine what file data set to load.
            String fileName = "";
            if (menuId == R.id.menu_id_books) {
                // load books file
                fileName = "books";
            } else if (menuId == R.id.menu_id_movies) {
                // load movies file
                fileName = "movies";
            } else if (menuId == R.id.menu_id_tv) {
                // load tv/series file
                fileName = "series";
            }
            // call replace method
            replaceFragmentInContainer(fileName);
            // return true because the fragment has been replaced.
            return true;
        });
    }

    /*The method which handles replacing a fragment in a the view container*/
    private void replaceFragmentInContainer(String fileName) {
        // get frag manager and transaction and set a android fade animation and re-ordering.
        // replace fragment with the correct file set to load.
        //commit new fragment to container.

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MasterFragment.newInstance(fileName), "NewInstance_" + fileName)
                .commit();
    }

    /*Override 'onCreateOptions' so we can inflate our xml layout "toolbar_menu"
     * and implement our search function to open another activity with results for that
     * particular query.
     * NB: The activity support toolbar gets set from within the master fragment.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        // find menu item (search view icon)
        final MenuItem menuItem = menu.findItem(R.id.menu_search);

        // Associate searchable configuration with the SearchView
        // manifest file that contains metaData links this to our SearchResultsActivity.
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        // set query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // collapse search view when submit is pressed
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            launchActivity(SettingsActivity.class);
            return true;
        } else if (itemId == R.id.menu_view_list) {
            launchActivity(UserFavListActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Launch activity with class intent*/
    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        // set animation of how activity is to be launched
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
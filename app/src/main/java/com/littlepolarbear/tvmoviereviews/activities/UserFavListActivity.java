package com.littlepolarbear.tvmoviereviews.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.littlepolarbear.retrofitlibrary.JsonPOJO;
import com.littlepolarbear.tvmoviereviews.InternalAppStorage;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.StandardRecyclerAdapter;
import com.littlepolarbear.tvmoviereviews.utils.UIHelper;

import java.util.List;

/*Activity to show the users favourite items*/
public class UserFavListActivity extends AppCompatActivity implements
        StandardRecyclerAdapter.StandardRecyclerAdapterCallbacks<JsonPOJO.Item>,
        InternalAppStorage.InternalStorageCallbacks {

    // debug tag
    private static final String TAG = "USER_FAV_LIST_ACTIVITY";
    private StandardRecyclerAdapter<JsonPOJO.Item> recyclerAdapter;
    private ActivityExtensionViewModel viewModel;
    private final InternalAppStorage appStorage = new InternalAppStorage(this);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fav_list);

        // set viewModel
        viewModel = new ViewModelProvider(this).get(ActivityExtensionViewModel.class);
        // observe callbacks through viewModel
        observeStorageCallbacks();

        // set adapter
        recyclerAdapter = uiHelper.getNewRecyclerAdapter();
        recyclerAdapter.setCallbacks(this);

        // set recycler view
        RecyclerView recyclerView = findViewById(R.id.activity_user_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        // set toolbar
        uiHelper.setToolbarTitle("My List");
        uiHelper.setToolbarSubtitle("Books, Movies, Series");
        try {
            uiHelper.setToolbarAsSupportActionBar(this, null, true);
        } catch (Exception e) {
            Log.v(TAG, "Error setting up toolbar in UserListActivity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*On Resume get list of saved items*/
    @Override
    protected void onResume() {
        // get the users items
        // call here and not on create. Because if user opens a item and decides to remove it from the list,
        // on return from the detail activity, this method will be called again.
        // The list returned will now show that item removed and pass on to recycler view.
        appStorage.doGetAllFromStorage(this);

        super.onResume();
    }

    /*Set up the observation of changing data*/
    private void observeStorageCallbacks() {
        // observe changes in the storage added to list value boolean
        // UI respond to the changes
        viewModel.getItemList().observe(this, items -> {
            recyclerAdapter.setItemsList(items);
        });
    }

    /*Recycler click event methods*/
    @Override
    public void itemClick(int position, ShapeableImageView imageView) {
        // launch detail activity on click with transition.
        uiHelper.launchNewActivityWithSharedElementTransition(
                imageView,
                recyclerAdapter.getListItem(position),
                this,
                DetailActivity.class);
    }

    @Override
    public void itemLongClick(int position) {
        //todo implement
    }


    /*Internal Storage Callbacks*/

    /*Called when a object is either added or removed from a file.
     * Or checked to see if object is in the file. */
    @Override
    public void itemAddedToFile(boolean isItemAddedToList) {

    }

    /*Called when a request to get all the objects in the file are asked for.*/
    @Override
    public void onGetAllObjectsFromFile(List<JsonPOJO.Item> itemList) {
        // post results to view model,
        // which will then allow UI observable to get them and update recycler view.
        if (itemList != null) {
            viewModel.setItems(itemList);
        }
    }

    /*Catch the back button press in toolbar*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*define what happens when the back button in pressed
     * outward transition*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
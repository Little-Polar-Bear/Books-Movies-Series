package com.littlepolarbear.tvmoviereviews.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.littlepolarbear.retrofitlibrary.JsonPOJO;
import com.littlepolarbear.retrofitlibrary.RequestData;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.StandardRecyclerAdapter;
import com.littlepolarbear.tvmoviereviews.activities.DetailActivity;
import com.littlepolarbear.tvmoviereviews.utils.UIHelper;

import java.util.List;

public class MasterFragment extends Fragment implements
        StandardRecyclerAdapter.StandardRecyclerAdapterCallbacks<JsonPOJO.Item>,
        RequestData.RequestDataCallbacks {

    private MasterFragmentViewModel mViewModel;
    private StandardRecyclerAdapter<JsonPOJO.Item> recyclerAdapter;
    private RecyclerView recyclerView;
    private AppCompatButton spinnerBtn;
    private static final String FILE_NAME = "FILE_NAME";
    private String fileName = "";

    private RequestData requestData;

    // UI class that has convenience methods.
    // override some methods here if needed.
    private final UIHelper uiHelper = new UIHelper() {
        /*Return the id of the toolbar */
        @Override
        public int getToolbarById() {
            return R.id.material_toolbar;
        }
    };

    public static MasterFragment newInstance(String file) {
        MasterFragment masterFragment = new MasterFragment();
        Bundle extras = new Bundle();
        extras.putString(FILE_NAME, file);
        masterFragment.setArguments(extras);
        return masterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (this.getArguments() != null) {
            fileName = this.getArguments().getString(FILE_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MasterFragmentViewModel.class);

        // find the views
        spinnerBtn = view.findViewById(R.id.app_bar_spinner_btn);

        // set adapter
        recyclerAdapter = uiHelper.getNewRecyclerAdapter();
        recyclerAdapter.setCallbacks(this);

        // set recycler view
        recyclerView = view.findViewById(R.id.fragment_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

        // toolbar titles
        String title, subtitle;
        if (fileName.equals("books")) {
            title = "New York Times";
            subtitle = "Best sellers";
        } else {
            title = "Metacritic";
            subtitle = "Top rated";
        }

        // set the title and subtitle
        uiHelper.setToolbarTitle(title);
        uiHelper.setToolbarSubtitle(subtitle);
        // set toolbar as support action bar
        try {
            uiHelper.setToolbarAsSupportActionBar((AppCompatActivity) getActivity(), view, false);
        } catch (Exception e) {
            Log.v("DEBUG", "Error setting up toolbar in fragment: " + e.getMessage());
            e.printStackTrace();
        }

        // observe changes in the genre list a user can use to choose from
        mViewModel.getGenreCategories().observe(MasterFragment.this.getViewLifecycleOwner(), genreList -> {
            if (genreList != null && !genreList.isEmpty()) {
                // genre list is now available to use
                // default set the spinner btn to first in the list
                spinnerBtn.setText(genreList.get(0));
            }
        });

        // set the genre url path
        String urlPath = "https://raw.githubusercontent.com/Little-Polar-Bear/web-scraping/main/";
        switch (fileName) {
            case "movies":
                urlPath = urlPath.concat("keysMovies.txt");
                break;
            case "series":
                urlPath = urlPath.concat("keysTv.txt");
                break;
            default:
                urlPath = urlPath.concat("keysBooks.txt");
        }
        // get the categories.
        mViewModel.setGenreCategories(urlPath);

        // load json file
        connectToOnlineJsonFile();

        // configure spinner so user can choose sub categories
        setSpinnerBtnFunctionality();
    }

    /*Method that requests Json data and updates the recycler view when data has been received.*/
    private void connectToOnlineJsonFile() {
        // construct object and attach callbacks
        requestData = new RequestData(this);
        // init retrofit and set base url (absolute)
        requestData.initRetrofitConnection("https://raw.githubusercontent.com/Little-Polar-Bear/web-scraping/main/");
        // get data from the relative path file
        requestData.getDataFromFile(fileName);
    }

    /*Standard recycler click event callbacks
     * onclick callback*/
    @Override
    public void itemClick(int position, ShapeableImageView imageView) {
        // launch detail activity on click with transition.
        uiHelper.launchNewActivityWithSharedElementTransition(
                imageView,
                recyclerAdapter.getListItem(position),
                getActivity(),
                DetailActivity.class);
    }

    /*OnLong click callback*/
    @Override
    public void itemLongClick(int position) {
        // todo implement
    }

    /*request Data callbacks
     * Successful*/
    @Override
    public void requestSuccessful(List<JsonPOJO.Category> itemList) {
        // set the items list in view model
        mViewModel.setItemList(itemList);
        // initially set the first items in the list as the recycler view items and spinner text.
        setRecyclerViewItems(mViewModel.getItemList().get(0).getCategoryItemList());
    }

    /*set the spinnerBtn text and recycler view items*/
    private void setRecyclerViewItems(List<JsonPOJO.Item> childrenList) {
        recyclerAdapter.setItemsList(childrenList);
        recyclerView.smoothScrollToPosition(0);
    }

    /*set the spinner functionality
     * When a user clicks the spinner, a pop up menu will open showing all the categories.
     * When they choose a category, the menu closes and the corresponding list will now be shown*/
    private void setSpinnerBtnFunctionality() {
        spinnerBtn.setOnClickListener(v -> {
            // do not go any further if categories are null
            if (mViewModel.getGenreCategories().getValue() == null) {
                return;
            }

            // open dialog showing all available sub-categories that a user can choose from:
            AlertDialog.Builder alertDialogBuilder = uiHelper.createAlertDialog(
                    R.drawable.ic_baseline_arrow_drop_down_24,
                    "Select a genre:",
                    requireContext());

            // add extra information to the dialog

            // construct an adapter that will hold our string list of headers
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    MasterFragment.this.getContext(), R.layout.layout_select_dialog_item);
            // populate array adapter with list of categories from map:
            for (String title : mViewModel.getGenreCategories().getValue()) {
                arrayAdapter.add(title);
            }

            // set array and item click listener for array string header items.
            alertDialogBuilder.setAdapter(arrayAdapter, (dialog, which) -> {
                // get the string item selected
                String categorySelected = arrayAdapter.getItem(which);
                // get data from the relative path file
                requestData.getDataFromFile(fileName, categorySelected);
                // set spinner header text
                spinnerBtn.setText(categorySelected);
                // dismiss the dialog, work is complete
                dialog.dismiss();
            });
            alertDialogBuilder.show();
        });
    }

    /*Unsuccessful*/
    @Override
    public void requestUnsuccessful(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    /*Successful (connected to json) but list returned was empty - debugging purposes. */
    @Override
    public void requestIsEmpty() {
        Toast.makeText(getContext(), "Request body was returned empty", Toast.LENGTH_SHORT).show();
    }
}
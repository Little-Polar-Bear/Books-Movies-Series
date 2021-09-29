package com.littlepolarbear.tvmoviereviews.activities

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.littlepolarbear.retrofitlibrary.JsonPOJO;
import com.littlepolarbear.tvmoviereviews.InternalAppStorage;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.utils.FormatClass;
import com.littlepolarbear.tvmoviereviews.utils.UIHelper;

import java.util.List;

import static com.littlepolarbear.tvmoviereviews.utils.FormatClass.numberFormatOrdinal;

public class DetailActivity extends AppCompatActivity implements InternalAppStorage.InternalStorageCallbacks {

    // view model
    private ActivityExtensionViewModel viewModel;
    // construct a storage class to communicate with files
    private final InternalAppStorage appStorage = new InternalAppStorage(this);

    //  TAG for debugging
    private static final String TAG = "DETAIL_ACTIVITY";

    // UI class that has convenience methods to help
    // override some methods here if needed.
    private final UIHelper uiHelper = new UIHelper() {
        /*Return the id of the toolbar */
        @Override
        public int getToolbarById() {
            return R.id.detail_toolbar;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // postpone transition until everything has loaded.
        supportPostponeEnterTransition();

        // set viewModel
        viewModel = new ViewModelProvider(this).get(ActivityExtensionViewModel.class);
        // observe callbacks through viewModel
        observeStorageCallbacks();

        // set toolbar
        uiHelper.setToolbarTitle("Go Back");
        try {
            uiHelper.setToolbarAsSupportActionBar(this, null, true);
        } catch (Exception e) {
            Log.v(TAG, "Error setting up toolbar in DetailActivity: " + e.getMessage());
            e.printStackTrace();
        }

        // find views
        ShapeableImageView imageView = findViewById(R.id.detail_image_view);
        MaterialButton materialButton = findViewById(R.id.detail_action_button);

        AppCompatTextView title, subtitle, description, weeksInList, publisher, rank;

        title = findViewById(R.id.detail_title_view);
        subtitle = findViewById(R.id.detail_author_view);
        description = findViewById(R.id.detail_description_view);
        weeksInList = findViewById(R.id.detail_weeks_in_list_view);
        publisher = findViewById(R.id.detail_publisher_view);
        rank = findViewById(R.id.detail_rank_view);

        // put the detail item into viewModel
        // will only load on first onCreate, will not get called again for a configuration change.
        if (viewModel.getDetailItem() == null) {
            // get the data_bundle from the extras we passed using our intent
            Bundle extras = getIntent().getExtras();
            // get the transition name we set from the data_extras_bundle.
            viewModel.setTransitionName(extras.getString("TRANSITION_NAME"));
            // load object using extras
            viewModel.setDetailItem((JsonPOJO.Item) extras.getSerializable("LIST_OBJECT"));
        }

        // set transition name on our image resource
        // so the view can move as a shared element
        imageView.setTransitionName(viewModel.getTransitionName());

        // cache the detail view to re-use
        JsonPOJO.Item detailItem = viewModel.getDetailItem();

        // check to see if item is in the users list
        // do we need to show add sign or added sign in toolbar options menu?
        isItemSavedToFile(detailItem);

        // slight data difference for books and movie/series.
        String subtitleText, publisherText;
        // book
        if (detailItem.getBookPurchaseWebsites() != null) {
            subtitleText = detailItem.getBookAuthor();
            publisherText = getResources().getString(R.string.publisher, detailItem.getBookPublisher());
            // set book btn function
            setBuyBtnFunctionality(materialButton, detailItem);
        } else {
            // movie and series
            subtitleText = detailItem.getMediaUserScore();
            publisherText = detailItem.getMediaMetaScore();
            // set btn function
            setTrailerBtnFunctionality(materialButton, detailItem);
        }

        // different strings shown depending on book/movie/series for weeks in list textView
        // and of the book is new to the list or not
        String weeksInListText = detailItem.getWeeksInBookList();
        // if null is a movie/series
        if (weeksInListText == null) {
            // set appropriate text
            weeksInListText = detailItem.getMediaReleaseDateAndRating();
        } else {
            // extract number of weeks a book has been in the list.
            int value = FormatClass.extractNumber(weeksInListText);
            // if value = -1, the book is new to the list and has no number in the string.
            // so we just leave the string unchanged which will say something like "New This Week"
            if (value != -1) {
                weeksInListText = getResources().getString(R.string.weeks_on_list, value);
            }
        }

        //set the data to the views
        title.setText(detailItem.getTitle());
        subtitle.setText(subtitleText);
        publisher.setText(publisherText);
        rank.setText(getResources().getString(R.string.book_rank,
                numberFormatOrdinal(detailItem.getListRank())));
        description.setText(detailItem.getDescription());
        weeksInList.setText(weeksInListText);

        // load image url using glide
        // start transition after image has loaded async using glide.
        setImageResource(detailItem.getImageUrl(), imageView);
    }

    /*redraw options menu (check, add)
     * when item changes from added to list to removed from list or vice versa.*/
    private void observeStorageCallbacks() {
        // observe changes in the stored value boolean
        // UI respond to the changes if the object is in storage or not.
        // showAdd boolean reflects the two options available, add or checked icon.
        viewModel.isItemAddedToList().observe(this, value -> {
            showAdd = !value;
            invalidateOptionsMenu();
        });
    }

    /*Save item to users list*/
    private void saveItemToList() {
        // save item to file
        appStorage.doWriteToStorage(this, viewModel.getDetailItem());
    }

    /*Remove item from users list*/
    private void removeItemFromList() {
        // remove item from file
        appStorage.doRemoveFromStorage(this, viewModel.getDetailItem());
    }

    /*Check if item is in user list*/
    private void isItemSavedToFile(JsonPOJO.Item item) {
        appStorage.doIsItemInStorage(this, item);
    }

    /*Internal Storage Callbacks*/

    /*Called when a object is either added or removed from a file.
     * Or checked to see if object is in the file. */
    @Override
    public void itemAddedToFile(boolean isItemAddedToList) {
        viewModel.setItemAddedToListBoolean(isItemAddedToList);
    }

    /*Called when a request to get all the objects in the file are asked for.*/
    @Override
    public void onGetAllObjectsFromFile(List<JsonPOJO.Item> itemList) {
        // not used in this class
    }

    /*Return the menu options layout for this activity*/
    private boolean showAdd = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_toolbar_menu, menu);
        if (showAdd) {
            menu.findItem(R.id.add_menu_btn).setVisible(true);
            menu.findItem(R.id.check_menu_btn).setVisible(false);
        } else {
            menu.findItem(R.id.add_menu_btn).setVisible(false);
            menu.findItem(R.id.check_menu_btn).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /*Describes what happens when an option item is selected.
     * Alternate between add and check that shows an item added or can be added to the wish list*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (menuItemId == R.id.add_menu_btn) {
            // add the item to the users list
            saveItemToList();
        } else if (menuItemId == R.id.check_menu_btn) {
            // item is in the list, user wishes to remove it from the list.
            removeItemFromList();
        }
        // select has been handled.
        return true;
    }

    /*Describes what happens when the trailer btn is pressed*/
    private void setTrailerBtnFunctionality(MaterialButton trailerBtn, final JsonPOJO.Item detailItem) {
        // set button text
        trailerBtn.setText("Rent");
        // set drawable left image to video icon
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable img = trailerBtn.getContext().getResources().getDrawable(R.drawable.ic_baseline_rent_24, null);
        trailerBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        // set click lister
        trailerBtn.setOnClickListener(v -> {
            // just open google market place for now
            Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q=" + detailItem.getTitle()));
            startActivity(goToMarket);
        });
    }

    /*Describes what happens when the buy button is pressed.*/
    private void setBuyBtnFunctionality(MaterialButton buyButton, final JsonPOJO.Item detailItem) {
        // set click listener
        buyButton.setOnClickListener(v -> {
            // open dialog showing all available purchase websites a user can buy from
            // construct alert dialog builder.
            AlertDialog.Builder alertDialogBuilder = uiHelper.createAlertDialog(
                    R.drawable.ic_baseline_shopping_cart_24,
                    "Select a store to purchase from:",
                    this);

            // construct an adapter that will hold our string list of headers
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    DetailActivity.this, R.layout.layout_select_dialog_item);
            // populate array adapter with the list of website names.
            for (JsonPOJO.BookPurchaseWebsite website : detailItem.getBookPurchaseWebsites()) {
                arrayAdapter.add(website.getStoreName());
            }
            // set arrayAdapter and item click listener.
            alertDialogBuilder.setAdapter(arrayAdapter, (dialog, position) -> {
                // use the position of the item selected,
                // (position corresponds to the websiteObject that contains store name, url, etc)
                // open the url in a webView
                launchWebViewIntent(detailItem.getBookPurchaseWebsites().get(position).getWebAddress());
                // dismiss the dialog, work is complete
                dialog.dismiss();
            });
            alertDialogBuilder.show();
        });
    }

    /*Launch a web intent with any app on user device that supports browsing*/
    private void launchWebViewIntent(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /*Use glide to load and set the image*/
    private void setImageResource(String url, ShapeableImageView imageView) {
        Glide.with(imageView)
                .load(url)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.v("TAG_URL", "Image is ready, start transition");
                        imageView.setImageDrawable(resource);
                        // allow activity transition to start now url resource has loaded into imageVew.
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }
}

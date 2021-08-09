package com.littlepolarbear.tvmoviereviews.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.littlepolarbear.retrofitlibrary.JsonPOJO;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.StandardRecyclerAdapter;

/*Class designed for common methods to be easily implemented
 * and shared in and for different UI containers*/
public class UIHelper {

    /*Style the material shape drawable
     * common through out this app to give this design functionality to our material shape drawables*/
    public void doMaterialShapeDesignRight(MaterialShapeDrawable shapeDrawable) {
        if (shapeDrawable != null) {
            shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 100)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 100)
                    .build());
        }
    }

    /*Style the material shape drawable
     * common through out this app to give this design functionality to our material shape drawables*/
    public void doMaterialShapeDesignTop(MaterialShapeDrawable shapeDrawable, float radius) {
        if (shapeDrawable != null) {
            shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                    .build());
        }
    }

    /*Style the material shape drawable
     * common through out this app to give this design functionality to our material shape drawables*/
    public void doMaterialShapeDesignLeft(MaterialShapeDrawable shapeDrawable) {
        if (shapeDrawable != null) {
            shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, 100)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 100)
                    .build());
        }
    }

    /*Style the material shape drawable
     * common through out this app to give this design functionality to our material shape drawables*/
    public void doMaterialShapeDesignAll(MaterialShapeDrawable shapeDrawable) {
        if (shapeDrawable != null) {
            shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, 100)
                    .setTopRightCorner(CornerFamily.ROUNDED, 100)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 100)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 100)
                    .build());
        }
    }

    /**
     * launch activity with transition and object bundled.
     *
     * @param activity      activity that is calling method from
     * @param classToLaunch class that needs to launch
     * @param imageView     image view that is the shared element of both UI views
     * @param item          the object which is being passed to the next class. Must be serializable
     */
    public void launchNewActivityWithSharedElementTransition(
            ShapeableImageView imageView,
            JsonPOJO.Item item,
            Activity activity,
            Class<?> classToLaunch) {

        // get the activity options and create a shared element with unique name
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair.create(imageView, imageView.getTransitionName()));

        // bundle data that needs to be passed to the next class and launch.
        Intent intent = new Intent(activity, classToLaunch);
        intent.putExtra("TRANSITION_NAME", imageView.getTransitionName());
        intent.putExtra("LIST_OBJECT", item);
        activity.startActivity(intent, options.toBundle());
    }

    /**
     * Convenience method to create a standard dialog builder
     *
     * @param title        dialog title
     * @param iconDrawable dialog icon image
     * @param context      of where to assign the builder to
     */
    public AlertDialog.Builder createAlertDialog(
            int iconDrawable,
            String title,
            @NonNull Context context) {
        // construct
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // add icon
        alertDialogBuilder.setIcon(iconDrawable);
        // add title
        alertDialogBuilder.setTitle(title);
        // set cancel btn for dialog
        alertDialogBuilder.setNegativeButton("Back", (dialog, which) -> dialog.dismiss());
        // return dialog
        return alertDialogBuilder;
    }

    /*Set up methods of Material Toolbar*/

    /*return the layout id for the toolbar*/
    public int getToolbarById() {
        return 0;
    }

    private String toolbarTitle, toolbarSubtitle;

    /*Set a toolbar title*/
    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    /*Set a toolbar subtitle*/
    public void setToolbarSubtitle(String toolbarSubtitle) {
        this.toolbarSubtitle = toolbarSubtitle;
    }

    /*Return a toolbar title*/
    public String getToolbarTitle() {
        return toolbarTitle;
    }

    /*Return a toolbar subtitle*/
    public String getToolbarSubtitle() {
        return toolbarSubtitle;
    }

    /**
     * Set toolbar as action bar
     *
     * @param activity activity associated with the SupportAction toolbar
     * @param view     associated with the material toolbar layout id
     */
    public void setToolbarAsSupportActionBar(AppCompatActivity activity, View view, boolean displayBackBtn) throws Exception {
        // make sure toolbar id has been given, title and subtitle allow nulls.
        int toolbarId = getToolbarById();
        if (toolbarId == 0) {
            throw new Exception("Toolbar id == 0. A layout id needs to be specified. (Override 'getToolbarById')");
        }

        // find the toolbar
        MaterialToolbar toolbar;
        if (view == null) {
            // used for activity
            toolbar = activity.findViewById(toolbarId);
        } else {
            // used for fragment
            toolbar = view.findViewById(toolbarId);
        }

        // set as the support action bar
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            // set the title and subtitle
            activity.getSupportActionBar().setTitle(getToolbarTitle());
            activity.getSupportActionBar().setSubtitle(getToolbarSubtitle());
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(displayBackBtn);
        }
    }

    /*Return the recycler adapter for a
     * calling class so it is able to show the items in a recycler view*/
    public RecyclerAdapter getNewRecyclerAdapter() {
        return new RecyclerAdapter();
    }

    /*Recycler adapter that extends from standard recycler adapter
     * Returns title, subtitle and image for each item in the list
     * All the logic is hidden in @StandardRecyclerAdapter, this class lets us only
     * define what info is displayed without worrying how it is displayed.*/
    private static class RecyclerAdapter extends StandardRecyclerAdapter<JsonPOJO.Item> {

        @Override
        protected int standardLayoutResource() {
            return R.layout.layout_standard_grid_item;
        }

        @Override
        protected String setItemTitle(JsonPOJO.Item object) {
            return object.getTitle();
        }

        @Override
        protected String setItemSubTitle(JsonPOJO.Item object) {
            if (object.getBookAuthor() != null) {
                return object.getBookAuthor();
            } else {
                return object.getMediaReleaseDateAndRating();
            }
        }

        @Override
        protected String setItemListRank(JsonPOJO.Item item) {
            return String.valueOf(item.getListRank());
        }

        @Override
        protected String setItemImage(JsonPOJO.Item object) {
            return object.getImageUrl();
        }
    }
}

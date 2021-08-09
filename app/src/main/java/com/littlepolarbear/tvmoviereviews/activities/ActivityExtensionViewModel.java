package com.littlepolarbear.tvmoviereviews.activities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.littlepolarbear.retrofitlibrary.JsonPOJO;

import java.util.List;

/*View model for the detail activity*/
/*todo make only new object when needed lazy load in essence*/
public class ActivityExtensionViewModel extends ViewModel {

    /*observable boolean for whether item is added to user's list or not*/
    private final MutableLiveData<Boolean> isItemAddedToList = new MutableLiveData<>();

    /*UI will be be updated by the detail activity observing,
     * do it this way because setItem is called on a background thread callback*/
    public void setItemAddedToListBoolean(boolean value) {
        isItemAddedToList.postValue(value);
    }

    /*Get the observable value*/
    public MutableLiveData<Boolean> isItemAddedToList() {
        return isItemAddedToList;
    }

    /*observable list of JSonPOJO.Items */
    private final MutableLiveData<List<JsonPOJO.Item>> mutableLiveData = new MutableLiveData<>();

    // post value to UI from background thread
    public void setItems(List<JsonPOJO.Item> itemList) {
        this.mutableLiveData.postValue(itemList);
    }

    public MutableLiveData<List<JsonPOJO.Item>> getItemList() {
        return mutableLiveData;
    }

    /*Detail Item*/
    private JsonPOJO.Item detailItem;
    /*TransitionName*/
    private String transitionName;

    /*Getter and setters form item and transition name*/

    public JsonPOJO.Item getDetailItem() {
        return detailItem;
    }

    public void setDetailItem(JsonPOJO.Item detailItem) {
        this.detailItem = detailItem;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }
}

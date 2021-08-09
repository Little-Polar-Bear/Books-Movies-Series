package com.littlepolarbear.tvmoviereviews;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.littlepolarbear.retrofitlibrary.JsonPOJO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/*A class to write and read rudimentary non sensitive user data.
 * Messy and inefficient but good for learning purposes of using concurrent threads to
 * read and write to a file with the java.io classes
 *  */
public class InternalAppStorage {

    private static final String TAG = "DEBUG_LOG_TAG";
    private static final String FILE_NAME_DOT_DAT = "non_sensitive_user_data.dat";

    private final InternalStorageCallbacks callbacks;

    /*Constructor*/
    public InternalAppStorage(InternalStorageCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /*Write a object to a file on a background thread*/
    private void saveObjectToFile(final Context context, final JsonPOJO.Item item) {
        // cache file to re-use
        File binaryFile = getBinaryFile(context);
        // read from file to create list avoid overriding.
        List<JsonPOJO.Item> itemList = readObjectsFromFile(binaryFile);
        // add our object to list.
        itemList.add(item);
        // write updated list to the file,
        // callback to notify calling class if write was successful or not.
        callbacks.itemAddedToFile(writeObjectsToFile(itemList, binaryFile));
    }

    /*Remove a object from file*/
    private void removeObjectFromFile(final Context context, final JsonPOJO.Item object) {
        // cache file to re-use
        File binaryFile = getBinaryFile(context);
        // construct list
        List<JsonPOJO.Item> itemList = readObjectsFromFile(binaryFile);
        // find the common object
        int index = compareObjectsInList(itemList, object);
        // if 0 or greater, item is in the list and a valid index has been returned.
        if (index >= 0) {
            // found common item, remove from users list.
            Log.v(TAG, "Removing " + object.getTitle() + " from the users' list");
            itemList.remove(index);
        }
        // write edited list back to file
        boolean success = writeObjectsToFile(itemList, binaryFile);
        if (success) {
            // notify that the item has successfully been removed from the file
            // by using false as file is no longer in the list
            callbacks.itemAddedToFile(false);
        }
    }

    /*Return the list objects in the file*/
    private void getAllObjectsFromFile(final Context context) {
        Log.v(TAG, "Returning all objects: ListSize");
        // callback with the returned list of all objects in file.
        callbacks.onGetAllObjectsFromFile(readObjectsFromFile(getBinaryFile(context)));
    }

    /*Iterate over objects in the users list and determine if item is in list*/
    private void isItemInFile(final Context context, JsonPOJO.Item object) {
        boolean isThere = false;
        if (compareObjectsInList(readObjectsFromFile(getBinaryFile(context)), object) >= 0) {
            isThere = true;
        }

        // callback that notifies calling class if item is in the users list.
        callbacks.itemAddedToFile(isThere);
    }


    /*check if object is in a list.
     * return -1 for not found and >= 0 for is found, this will be the index
     * of where the item is in the list.
     * cannot use the list.contains method because comparing object to object in file
     * does not equal the same even if they contain the same information.
     * Can equal the same if series/movies use the same cover art.
     * need to update and have a unique key or hash or something.
     * Can use list rank for a double check. This stops bug for items in the same list
     * not fool proof but helps and the only bug
     * will be if a movie uses the same art as a series and are at the same rank/position in both lists
     * which is rare but not impossible. */
    private int compareObjectsInList(List<JsonPOJO.Item> items, JsonPOJO.Item object) {
        // start at -1 so the first iteration will be zero and could be the first in the list match.
        int index = -1;
        for (JsonPOJO.Item item : items) {
            index++;
            if (item.getImageUrl().equals(object.getImageUrl()) &&
                    item.getListRank() == object.getListRank()) {
                // object image url's are the same.
                // These are unique so object is the same.
                return index;
            }
        }
        return -1;
    }

    private void clearObjectsFromFile(Context context) {
        // try with resources, write nothing to file (overwrite existing file with nothing).
        try (ObjectOutputStream file = new ObjectOutputStream
                (new BufferedOutputStream(new FileOutputStream(getBinaryFile(context))))) {
            Log.v(TAG, "Nothing has been written to file in order to clear list");
        } catch (IOException e) {
            Log.e(TAG, "Exception when writing nothing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /****Methods that interact directly with Java.io File classes****/

    /*Method to write items to a binary file and return a boolean if successful or not*/
    private boolean writeObjectsToFile(List<JsonPOJO.Item> itemList, File binaryFile) {
        // try with resources, writing an object to the file created destination.
        try (ObjectOutputStream file = new ObjectOutputStream
                (new BufferedOutputStream(new FileOutputStream(binaryFile)))) {
            // for each object write it to the file
            for (JsonPOJO.Item item : itemList) {
                file.writeObject(item);
                Log.v(TAG, "Object was successfully written to file");
            }
            Log.v(TAG, "All objects have been written to file");
            // file successfully written to.
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Exception when writing to file: " + e.getMessage());
            e.printStackTrace();
            // error file not successfully written to.
            return false;
        }
    }

    /*Iterate and return a list of objects from a binary file*/
    private List<JsonPOJO.Item> readObjectsFromFile(File binaryFile) {
        List<JsonPOJO.Item> itemList = new ArrayList<>();
        // end of file exception breaks out of while loop.
        // This occurs when file has reached its end.
        boolean eof = false;
        try (ObjectInputStream file = new ObjectInputStream
                (new BufferedInputStream(new FileInputStream(binaryFile)))) {
            Log.v(TAG, "Iterating through objects stored in this file");
            while (!eof) {
                try {
                    // add objects found in the file to the array list
                    itemList.add((JsonPOJO.Item) file.readObject());
                } catch (EOFException e) {
                    eof = true;
                    Log.e(TAG, "End of file exception thrown: " + e);
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "Class not found exception thrown: " + e);
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found exception thrown: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Input/Output exception thrown: " + e);
            e.printStackTrace();
        }

        Log.v(TAG, "All objects found in list: Size = " + itemList.size());
        // return list
        return itemList;
    }

    /**************************************/

    /*Methods that act as instructions from the calling classes*/
    public void doClearStorage(Context context) {
        runFileThread("ClearAllObjectsThread", "clearObjectsFromFile", context, null);
    }

    public void doWriteToStorage(Context context, JsonPOJO.Item item) {
        runFileThread("WriteObjectThread", "saveObjectToFile", context, item);
    }

    public void doRemoveFromStorage(Context context, JsonPOJO.Item item) {
        runFileThread("RemoveObjectThread", "removeObjectFromFile", context, item);
    }

    public void doGetAllFromStorage(Context context) {
        runFileThread("GetAllObjectsThread", "getAllObjectsFromFile", context, null);
    }

    public void doIsItemInStorage(Context context, JsonPOJO.Item item) {
        runFileThread("CheckingObjectInListThread", "isItemInFile", context, item);
    }

    /**
     * Helper method to avoid creating/initiating the same thread work and structure.
     *
     * @param methodToRun to define what code/method executes in run.
     * @param context     what instance is been assigned to.
     * @param threadName  give a name to the thread in the form of what work it is doing.
     * @param item        Json.Item object that methods need to either save, remove or check if object is in file.
     */
    private void runFileThread(String threadName, final String methodToRun, Context context, JsonPOJO.Item item) {
        FileThread fileThread = new FileThread(threadName) {
            @Override
            public void run() {

                // based on the string
                // run the specific code
                switch (methodToRun) {
                    // write a object to file
                    case "saveObjectToFile":
                        saveObjectToFile(context, item);
                        break;
                    // read and return all objects from file
                    case "getAllObjectsFromFile":
                        getAllObjectsFromFile(context);
                        break;
                    // remove object from file
                    case "removeObjectFromFile":
                        removeObjectFromFile(context, item);
                        break;
                    // clear all objects from file
                    case "clearObjectsFromFile":
                        clearObjectsFromFile(context);
                        break;
                    // check if object is in file
                    case "isItemInFile":
                        isItemInFile(context, item);
                        break;
                }

                // common clean up code
                super.run();
            }
        };
        // start thread
        fileThread.start();
    }

    /*Return the file we save to****
     * Helper method to get the directory path and specific file path of
     * where internal files for this app are saved on android devices.*/
    private File getBinaryFile(Context context) {
        // get the internal storage app directory for where android allows app files to save.
        // files will be deleted when the app is un-installed.
        File dir = new File(context.getFilesDir(), "mydir");
        // check if directory exists, otherwise create it.
        if (!dir.exists()) {
            // directory does not exist
            // make it and confirm by the boolean it returns if one was created.
            boolean hasMadeDirectory = dir.mkdir();
            Log.v(TAG, "Directory not present, one was created: " + hasMadeDirectory);
        }

        Log.v(TAG, "Using directory: " + dir.getPath());

        // now we have the directory, we can load/create the file using the file name.
        // find the binary file we want to overwrite within the app directory.

        return new File(dir, FILE_NAME_DOT_DAT);
    }

    /*background thread used to perform actions off of the main UI thread
     * Can implement common functionality in this class for things like:
     * What happens when thread finished?
     * What happens when thread interrupted?
     * etc...*/
    private static class FileThread extends Thread implements Runnable {

        /*Constructor, set name. Mainly for debugging purpose so we can see what is running*/
        public FileThread(@NonNull String name) {
            super(name);
            Log.v(TAG, name + ": is running");
        }

        @Override
        public void run() {
            Log.v(TAG, getName() + ": has finished running");
        }
    }

    /*Interface for callbacks
     * notify calling class when the background thread has completed*/
    public interface InternalStorageCallbacks {
        // work complete, item will either be added or not.
        // this boolean returned will determine how the UI will be displayed.
        void itemAddedToFile(boolean isItemAddedToList);

        // return list of objects stored in a file
        void onGetAllObjectsFromFile(List<JsonPOJO.Item> itemList);
    }
}

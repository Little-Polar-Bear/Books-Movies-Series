package com.littlepolarbear.retrofitlibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/*A PoJo class designed to serialize a JSON file and convert its' arrays and objects into
 * java objects that this application can use
 * - NB: This class is used for all JSON files in this app
 * - @Expose will be null sometimes because this POJO contains a list for all Json files combined,
 *  to limit methods as we cannot use generics with retrofit.
 * KEY string start with: media == movie/series, book == Book, ambiguous == common to both*/
public class JsonPOJO implements Serializable {
    /*House all the categories in a Json file*/
    @SerializedName("contentList")
    @Expose
    private List<Category> categoryList;

    /*Return the list of categories*/
    public List<Category> getCategoryList() {
        return categoryList;
    }

    /*House the categories inner list*/
    public static class Category implements Serializable {
        @SerializedName("categoryHeader")
        @Expose
        private String categoryHeader;

        @SerializedName("listItems")
        @Expose
        private List<Item> categoryItemList;

        public String getCategoryHeader() {
            return categoryHeader;
        }

        public List<Item> getCategoryItemList() {
            return categoryItemList;
        }
    }

    /*House the individual books*/
    public static class Item implements Serializable {
        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("author")
        @Expose
        private String bookAuthor;

        @SerializedName("image_url")
        @Expose
        private String imageUrl;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("publisher")
        @Expose
        private String bookPublisher;

        @SerializedName("weeks_in_list")
        @Expose
        private String weeksInBookList;

        @SerializedName("list_rank")
        @Expose
        private int listRank;

        @SerializedName("releaseDateAndRating")
        @Expose
        private String mediaReleaseDateAndRating;

        @SerializedName("meta_score")
        @Expose
        private String mediaMetaScore;

        @SerializedName("user_score")
        @Expose
        private String mediaUserScore;

        @SerializedName("purchase_websites")
        @Expose
        private List<BookPurchaseWebsite> bookPurchaseWebsites;

        @SerializedName("reviews")
        @Expose
        private List<Review> reviews;

        public String getTitle() {
            return title;
        }

        public String getBookAuthor() {
            return bookAuthor;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getDescription() {
            return description;
        }

        public String getBookPublisher() {
            return bookPublisher;
        }

        public String getWeeksInBookList() {
            return weeksInBookList;
        }

        public int getListRank() {
            return listRank;
        }

        public String getMediaReleaseDateAndRating() {
            return mediaReleaseDateAndRating;
        }

        public String getMediaMetaScore() {
            return mediaMetaScore;
        }

        public String getMediaUserScore() {
            return mediaUserScore;
        }

        public List<BookPurchaseWebsite> getBookPurchaseWebsites() {
            return bookPurchaseWebsites;
        }

        public List<Review> getReviews() {
            return reviews;
        }
    }

    public static class BookPurchaseWebsite implements Serializable {
        @SerializedName("storeName")
        @Expose
        private String storeName;

        @SerializedName("webAdress")
        @Expose
        private String webAddress;

        public String getStoreName() {
            return storeName;
        }

        public String getWebAddress() {
            return webAddress;
        }
    }

    public static class Review implements Serializable {
        @SerializedName("reviewAuthor")
        @Expose
        private String reviewAuthor;

        @SerializedName("reviewBody")
        @Expose
        private String reviewBody;

        @SerializedName("reviewRating")
        @Expose
        private int reviewRating;

        public String getReviewAuthor() {
            return reviewAuthor;
        }

        public String getReviewBody() {
            return reviewBody;
        }

        public int getReviewRating() {
            return reviewRating;
        }
    }
}

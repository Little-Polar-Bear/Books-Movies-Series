package com.littlepolarbear.tvmoviereviews

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public abstract class StandardRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface StandardRecyclerAdapterCallbacks<T> {
        void itemClick(int position, ShapeableImageView imageView);

        void itemLongClick(int position);
    }

    protected int standardLayoutResource() {
        return R.layout.layout_standard_item;
    }

    protected abstract String setItemTitle(T item);

    protected abstract String setItemSubTitle(T item);

    protected abstract String setItemListRank(T item);

    protected abstract String setItemImage(T item);

    private List<T> itemsList = new ArrayList<>();

    private StandardRecyclerAdapterCallbacks<T> callbacks;

    public void setCallbacks(StandardRecyclerAdapterCallbacks<T> callbacks) {
        this.callbacks = callbacks;
    }

    public void setItemsList(List<T> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public List<T> getItemsList() {
        return this.itemsList;
    }

    /**
     * @param position 'x' index for object in the list.
     */
    public T getListItem(int position) throws IndexOutOfBoundsException {
        if (position > -1 && position < itemsList.size()) {
            return itemsList.get(position);
        }
        throw new IndexOutOfBoundsException("No object found for position " + position +
                " in the list size of: " + itemsList.size());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StandardViewHolder<T>) holder).getTitleView().setText(setItemTitle(itemsList.get(position)));
        ((StandardViewHolder<T>) holder).getSubtitleView().setText(setItemSubTitle(itemsList.get(position)));
        ((StandardViewHolder<T>) holder).getListRankView().setText(setItemListRank(itemsList.get(position)));
        setItemImage(setItemImage(itemsList.get(position)), ((StandardViewHolder<T>) holder).getImageView());

        // set the unique transition name using positions to make different.
        ((StandardViewHolder<T>) holder).getImageView().setTransitionName("UniqueTransitionName: " + position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(standardLayoutResource(), parent, false);
        return new StandardViewHolder<>(view, callbacks);
    }

    protected void setItemImage(String url, ShapeableImageView imageView) {
        Glide.with(imageView)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    protected static class StandardViewHolder<T> extends RecyclerView.ViewHolder {

        private final AppCompatTextView titleView, subtitleView, listRankView;
        private final ShapeableImageView imageView;

        public StandardViewHolder(@NonNull View itemView, StandardRecyclerAdapterCallbacks<T> callbacks) {
            super(itemView);

            titleView = itemView.findViewById(R.id.title_view);
            subtitleView = itemView.findViewById(R.id.subtitle_view);
            listRankView = itemView.findViewById(R.id.list_rank_view);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(v -> {
                if (callbacks != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        callbacks.itemClick(position, imageView);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (callbacks != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        callbacks.itemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }

        public AppCompatTextView getTitleView() {
            return titleView;
        }

        public AppCompatTextView getSubtitleView() {
            return subtitleView;
        }

        public AppCompatTextView getListRankView() {
            return listRankView;
        }

        public ShapeableImageView getImageView() {
            return imageView;
        }
    }
}


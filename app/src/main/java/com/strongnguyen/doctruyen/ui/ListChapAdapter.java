package com.strongnguyen.doctruyen.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.model.Chapter;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ListChapAdapter extends RecyclerView.Adapter {

    private ArrayList<Chapter> listChaps;
    private OnListener onListener;

    public ListChapAdapter(ArrayList<Chapter> listChaps, OnListener onListener) {
        this.listChaps = listChaps;
        this.onListener = onListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_listchap, parent, false);
        return new ListChapViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ListChapViewHolder)holder).setInfo(listChaps.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return listChaps.size();
    }

    public class ListChapViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChaptitle;

        public ListChapViewHolder(View itemView) {
            super(itemView);
            tvChaptitle = itemView.findViewById(R.id.tv_item_listchap);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListener.onClickListener(listChaps.get(getLayoutPosition()).getUrl());
                }
            });
        }

        public void setInfo(Chapter chapter) {
            if (chapter != null) {
                tvChaptitle.setText(chapter.getName());
            } else {
                tvChaptitle.setText("--");
            }
        }
    }

    public interface OnListener {
        void onClickListener(String url);
    }
}

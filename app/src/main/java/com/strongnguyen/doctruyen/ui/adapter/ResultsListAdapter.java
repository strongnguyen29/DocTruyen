package com.strongnguyen.doctruyen.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.model.Book;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 6/6/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ResultsListAdapter extends RecyclerView.Adapter {

    private ArrayList<Book> listBooks;
    private OnListener onListener;

    public ResultsListAdapter(ArrayList<Book> listBooks, OnListener onListener) {
        this.listBooks = listBooks;
        this.onListener = onListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_listchap, parent, false);
        return new ResultListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ResultListViewHolder)holder).setInfo(listBooks.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    public class ResultListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChaptitle;

        public ResultListViewHolder(View itemView) {
            super(itemView);
            tvChaptitle = itemView.findViewById(R.id.tv_item_listchap);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListener.onClickListener(listBooks.get(getLayoutPosition()).getUrl());
                }
            });
        }

        public void setInfo(Book book) {
            if (book != null) {
                tvChaptitle.setText(book.getName());
            } else {
                tvChaptitle.setText("--");
            }
        }
    }

}

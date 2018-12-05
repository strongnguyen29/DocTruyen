package com.strongnguyen.doctruyen.ui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.util.LogUtils;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ListChapPageAdapter extends RecyclerView.Adapter {
    private static final String TAG = ListChapPageAdapter.class.getSimpleName();

    private ArrayList<Integer> listPages;
    private OnListener onListener;

    private int currenPage;

    public ListChapPageAdapter(ArrayList<Integer> listPages, int currenPage, OnListener onListener) {
        this.listPages = listPages;
        this.onListener = onListener;
        this.currenPage = currenPage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_listchap_page, parent, false);
        return new ListChapPageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ListChapPageViewHolder)holder).setInfo(listPages.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return listPages.size();
    }

    public void setPageAcitve(int page) {
        currenPage = page;
        notifyDataSetChanged();
    }

    public class ListChapPageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChapPage;

        public ListChapPageViewHolder(View itemView) {
            super(itemView);
            tvChapPage = itemView.findViewById(R.id.tv_item_listchap_page);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListener.onClickListener(listPages.get(getLayoutPosition()));
                }
            });
        }

        public void setInfo(int numPage) {
            tvChapPage.setText(String.valueOf(numPage));

            if (numPage != currenPage) {
                tvChapPage.setBackgroundColor(Color.argb(50, 0,0,0));
            } else {
                tvChapPage.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public interface OnListener {
        void onClickListener(int number);
    }
}

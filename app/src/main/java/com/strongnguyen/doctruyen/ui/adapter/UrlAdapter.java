package com.strongnguyen.doctruyen.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 10/27/2018.
 * Email: vancuong2941989@gmail.com
 */
public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlHolder> {

    private ArrayList<String> listUrl;
    private OnListener onListener;

    public UrlAdapter(OnListener onListener) {
        this.onListener = onListener;
        this.listUrl = new ArrayList<>();
    }

    @NonNull
    @Override
    public UrlHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_listchap, parent, false);
        return new UrlHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlHolder holder, int position) {
        holder.setTextView(listUrl.get(position));
    }

    @Override
    public int getItemCount() {
        return listUrl.size();
    }

    public void setListUrl(ArrayList<String> listUrl) {
        this.listUrl = listUrl;
        notifyDataSetChanged();
    }

    public class UrlHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView;

        public UrlHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_item_listchap);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListener.onClickListener(listUrl.get(getLayoutPosition()));
        }

        public void setTextView(String text) {
            textView.setText(text);
        }
    }

}

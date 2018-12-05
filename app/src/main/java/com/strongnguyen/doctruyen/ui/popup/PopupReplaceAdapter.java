package com.strongnguyen.doctruyen.ui.popup;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.model.ReplaceText;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class PopupReplaceAdapter extends RecyclerView.Adapter<PopupReplaceAdapter.ReplaceViewHolder> {
    private static final String TAG = PopupReplaceAdapter.class.getSimpleName();
    private ArrayList<ReplaceText> listText;
    private OnListener onListener;

    public PopupReplaceAdapter(OnListener onListener) {
        listText = new ArrayList<>();
        this.onListener = onListener;
    }

    @NonNull
    @Override
    public ReplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list_replace_text, parent, false);
        return new ReplaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplaceViewHolder holder, int position) {
        holder.setInfo(listText.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return listText.size();
    }

    public void setListText(ArrayList<ReplaceText> listText) {
        if (listText == null) return;
        this.listText = listText;
        notifyDataSetChanged();
    }

    public ArrayList<ReplaceText> getListText() {
        return listText;
    }

    public class ReplaceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCurrentText, tvNewText;
        private ImageView btnDelete;

        public ReplaceViewHolder(View itemView) {
            super(itemView);
            tvCurrentText = itemView.findViewById(R.id.item_tv_current_text);
            tvNewText = itemView.findViewById(R.id.item_tv_new_text);
            btnDelete = itemView.findViewById(R.id.item_btn_del);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int id = getLayoutPosition();
                    ReplaceText replaceText = listText.get(id);
                    replaceText.setActive(!replaceText.isActive());
                    listText.set(id, replaceText);
                    notifyDataSetChanged();
                    onListener.onLongClichListener(listText, replaceText.isActive());
                    return false;
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = getLayoutPosition();
                    listText.remove(id);
                    notifyDataSetChanged();
                    onListener.onDeleteItem(listText);
                }
            });

        }

        public void setInfo(ReplaceText replaceText) {
            tvCurrentText.setText(replaceText.getCurrentText());
            tvNewText.setText(replaceText.getNewText());

            if (replaceText.isActive()) {
                tvNewText.setTextColor(Color.WHITE);
            } else {
                tvNewText.setTextColor(Color.LTGRAY);
            }
        }
    }

    public interface OnListener {

        void onLongClichListener(ArrayList<ReplaceText> listText, boolean isActive);

        void onDeleteItem(ArrayList<ReplaceText> listText);
    }
}

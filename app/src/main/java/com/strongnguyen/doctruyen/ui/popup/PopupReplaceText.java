package com.strongnguyen.doctruyen.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.model.ReplaceText;
import com.strongnguyen.doctruyen.util.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 11/12/2018.
 * Email: vancuong2941989@gmail.com
 */
public class PopupReplaceText implements PopupReplaceAdapter.OnListener {
    public static final String PREF_REPLACE_TEXT = "PREF_REPLACE_TEXT";
    private Context context;
    private Dialog dialog;
    private EditText edCurrentText, edNewText;
    private Button btnCancel, btnSave;
    private RecyclerView recyclerView;
    private PopupReplaceAdapter adapter;
    private OnPopupListener mOnPopupListener;

    public PopupReplaceText(Context context, final OnPopupListener mOnPopupListener) {
        this.context = context;
        this.mOnPopupListener = mOnPopupListener;

        dialog = new Dialog(context, R.style.AppTheme_Dialog_FullScreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_replace_text);

        edCurrentText = dialog.findViewById(R.id.pop_rep_current_text);
        edNewText = dialog.findViewById(R.id.pop_rep_new_text);
        btnCancel = dialog.findViewById(R.id.pop_rep_btn_cancel);
        btnSave = dialog.findViewById(R.id.pop_rep_btn_save);
        recyclerView = dialog.findViewById(R.id.pop_rep_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PopupReplaceAdapter(this);
        recyclerView.setAdapter(adapter);
        loadData();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curText = edCurrentText.getText().toString();
                String newText = edNewText.getText().toString();

                if (curText.length() > 0) {
                    ArrayList<ReplaceText> list = adapter.getListText();
                    ReplaceText replaceText = new ReplaceText(curText, newText);
                    list.add(replaceText);
                    saveData(list);
                    mOnPopupListener.onReplaceTexts(replaceText);
                    dismiss();
                }
            }
        });
    }


    public boolean isShowing() {
        return !(dialog == null || !dialog.isShowing());
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void destroy() {
        if (dialog != null) {
            dismiss();
            adapter = null;
            dialog = null;
        }
    }

    private void loadData() {
        String json = Preferences.getString(context, PREF_REPLACE_TEXT, "");

        if (json.length() > 0) {
            ArrayList<ReplaceText> list = new Gson()
                    .fromJson(json, new TypeToken<List<ReplaceText>>(){}.getType());
            adapter.setListText(list);
        }
    }

    private void saveData(ArrayList<ReplaceText> listText) {
        if (listText.size() == 0) return;
        Preferences.saveString(context, PREF_REPLACE_TEXT,new Gson().toJson(listText));
    }

    @Override
    public void onLongClichListener(ArrayList<ReplaceText> listText, boolean isActive) {
        String text = isActive? "Text thay thế được bật!" : "Text thay thế bị vô hiệu hóa!";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        saveData(listText);
    }

    @Override
    public void onDeleteItem(ArrayList<ReplaceText> listText) {
        saveData(listText);
    }

    public interface OnPopupListener {
        void onReplaceTexts(ReplaceText replaceText);
    }
}

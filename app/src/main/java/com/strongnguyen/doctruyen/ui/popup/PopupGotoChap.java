package com.strongnguyen.doctruyen.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.strongnguyen.doctruyen.R;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 6/7/2018.
 * Email: vancuong2941989@gmail.com
 */
public class PopupGotoChap {
    private Dialog dialog;
    private EditText editText;
    private OnPopupListener mOnPopupListener;

    public PopupGotoChap(Context context, OnPopupListener mOnPopupListener1) {
        this.mOnPopupListener = mOnPopupListener1;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_input_chapnum);

        editText = dialog.findViewById(R.id.pop_ed_chapnum);
        Button btnSubmit = dialog.findViewById(R.id.pop_btn_go);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupListener.onGoToChap(Integer.parseInt(editText.getText().toString()));
                dismiss();
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

    public interface OnPopupListener {
        void onGoToChap(int chapnum);
    }
}

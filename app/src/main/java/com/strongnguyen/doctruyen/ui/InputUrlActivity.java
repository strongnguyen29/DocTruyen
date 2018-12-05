package com.strongnguyen.doctruyen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.ui.adapter.OnListener;
import com.strongnguyen.doctruyen.ui.adapter.UrlAdapter;
import com.strongnguyen.doctruyen.util.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 6/6/2018.
 * Email: vancuong2941989@gmail.com
 */
public class InputUrlActivity extends AppCompatActivity {
    public static final String PREF_URL_BOOK = "PREF_URL_BOOK";
    public static final String PREF_LIST_URL_BOOK = "PREF_LIST_URL_BOOK";

    private RecyclerView recyclerView;
    private EditText edInputUrl;

    private ArrayList<String> listUrls = new ArrayList<>();
    private String urlBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_url);

        urlBook = Preferences.getString(this, PREF_URL_BOOK, "");

        edInputUrl = findViewById(R.id.ed_url_book);
        edInputUrl.setText(urlBook);

        Button btnOk = findViewById(R.id.btn_accept);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlBook = edInputUrl.getText().toString();
                if (urlBook.length() > 1) {
                    Preferences.saveString(getApplicationContext(),PREF_URL_BOOK, urlBook);
                    saveListBooks(urlBook);

                    startActivity(new Intent(InputUrlActivity.this, ListChapActivity.class));
                }
            }
        });

        findViewById(R.id.btn_read_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InputUrlActivity.this, ReaderLocalActivity.class));
            }
        });

        setRecyclerView ();
    }

    private void setRecyclerView () {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        UrlAdapter urlAdapter = new UrlAdapter(new OnListener() {
            @Override
            public void onClickListener(String url) {
                edInputUrl.setText(url);
            }
        });
        recyclerView.setAdapter(urlAdapter);

        String json = Preferences.getString(getApplicationContext(), PREF_LIST_URL_BOOK, "[]");
        listUrls = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
        urlAdapter.setListUrl(listUrls);
    }

    private void saveListBooks(String url) {

        if (listUrls == null || listUrls.size() == 0 || url.equals(listUrls.get(0))) {
            return;
        }
        listUrls.add(0, url);
        if (listUrls.size() > 20) {
            listUrls.remove(listUrls.size() - 1);
        }
        String json = new Gson().toJson(listUrls);
        Preferences.saveString(getApplicationContext(), PREF_LIST_URL_BOOK, json);
    }

}

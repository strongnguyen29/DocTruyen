package com.strongnguyen.doctruyen.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.strongnguyen.doctruyen.R;

public class SearchActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }


}

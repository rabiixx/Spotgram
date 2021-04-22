package com.example.ayps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recycler_view);


        RecyclerView rv = findViewById(R.id.rv);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(sglm);

        List<String> imageList = new ArrayList<>();
        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        imageList.add("https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af.appspot.com/o/images%2Fspots%2F0c483eed-e27c-4948-93c2-95448e26bc2a.jpg?alt=media&token=d4e98d64-5e75-4a5a-bbec-70918fd6da7f");

        ImageGridAdapter iga = new ImageGridAdapter(MyRecyclerView.this, imageList);
        rv.setAdapter(iga);

    }
}
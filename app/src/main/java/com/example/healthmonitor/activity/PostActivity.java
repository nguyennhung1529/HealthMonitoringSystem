package com.example.healthmonitor.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.healthmonitor.R;
import com.example.healthmonitor.adapter.PostAdapter;
import com.example.healthmonitor.object.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private RecyclerView rcvPost;
    private ArrayList<Post> mPostList;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initUI();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mPostList = new ArrayList<>();

        mDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Result will be holded Here
                for (DataSnapshot itemPost : snapshot.getChildren()) {
                    mPostList.add(itemPost.getValue(Post.class)); //add result into array list
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        postAdapter = new PostAdapter(mPostList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPost.setLayoutManager(linearLayoutManager);

//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        rcvPost.addItemDecoration(decoration);

        rcvPost.setAdapter(postAdapter);
    }

    public void initUI() {
        rcvPost = findViewById(R.id.rcvPost);
    }
}
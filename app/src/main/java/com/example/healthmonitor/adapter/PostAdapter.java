package com.example.healthmonitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthmonitor.R;
import com.example.healthmonitor.object.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    // Khai bao list du lieu
    private ArrayList<Post> mPostList;
    private Context mContext;

    public PostAdapter(ArrayList<Post> mPostList, Context mContext) {
        this.mPostList = mPostList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = mPostList.get(position);
        if (post == null) {
            return;
        }

        holder.tvTitlePost.setText(post.getTitle());
        holder.tvDescriptionPost.setText(post.getDescription());
        Glide.with(mContext).load(Uri.parse(post.getUrlImage())).error(R.drawable.img_post_default).into(holder.ivImagePost);

        // bat su kien onclick
        holder.layoutItemPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPostUrl(post.getSource());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPostList != null) {
            return mPostList.size();
        }
        return 0;
    }

    // Thuc hien tro toi bai viet thong qua source url
    private void openWebPostUrl(String source) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(source));
        mContext.startActivity(browse);
    }

    class PostHolder extends RecyclerView.ViewHolder {
        // Dinh nghia cac view da khai bao trong item
        private CardView layoutItemPost;
        private ImageView ivImagePost;
        private TextView tvTitlePost, tvDescriptionPost;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemPost = itemView.findViewById(R.id.layout_item_post);
            ivImagePost = itemView.findViewById(R.id.ivImagePost);
            tvTitlePost = itemView.findViewById(R.id.tvTitlePost);
            tvDescriptionPost = itemView.findViewById(R.id.tvDescriptionPost);
        }
    }
}

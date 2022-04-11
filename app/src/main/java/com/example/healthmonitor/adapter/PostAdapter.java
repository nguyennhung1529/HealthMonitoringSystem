package com.example.healthmonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthmonitor.PostActivity;
import com.example.healthmonitor.R;
import com.example.healthmonitor.object.Post;
import com.example.healthmonitor.object.User;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    // Khai bao list du lieu
    private ArrayList<Post> mPostList;
    private Context context;

    public PostAdapter(ArrayList<Post> mPostList, Context context) {
        this.mPostList = mPostList;
        this.context = context;
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
        Glide.with(context).load(post.getUrlImage()).error(R.drawable.img_post_default).into(holder.ivImagePost);
    }

    @Override
    public int getItemCount() {
        if (mPostList != null) {
            return mPostList.size();
        }
        return 0;
    }

    class PostHolder extends RecyclerView.ViewHolder {
        // Dinh nghia cac view da khai bao trong item
        private ImageView ivImagePost;
        private TextView tvTitlePost, tvDescriptionPost;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            ivImagePost = itemView.findViewById(R.id.ivImagePost);
            tvTitlePost = itemView.findViewById(R.id.tvTitlePost);
            tvDescriptionPost = itemView.findViewById(R.id.tvDescriptionPost);
        }
    }
}

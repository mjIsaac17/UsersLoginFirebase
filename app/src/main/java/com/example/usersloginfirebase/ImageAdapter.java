package com.example.usersloginfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{
    private Context mContext;
    private List<PostClass> postsList;

    public ImageAdapter(Context context, List<PostClass> posts){
        mContext = context;
        postsList = posts;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        PostClass postCurrent = postsList.get(position);
        holder.txtPostName.setText(postCurrent.getName());
        holder.txtPostDescription.setText(postCurrent.getDescription());
        Picasso.with(mContext).load(postCurrent.getImageUrl()).fit().centerCrop().into(holder.ivPostImage);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView txtPostName, txtPostDescription;
        public ImageView ivPostImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPostName = itemView.findViewById(R.id.txtItemTitle);
            txtPostDescription = itemView.findViewById(R.id.txtItemDescription);
            ivPostImage = itemView.findViewById(R.id.ivItemPost);
        }
    }
}

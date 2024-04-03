package com.example.task51c;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<RelatedDataModel> newsList;

    public RelatedAdapter(Context mContext, ArrayList<RelatedDataModel> newsList) {
        this.mContext = mContext;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.related_recycle_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.topTitleTextView.setText(newsList.get(position).getTitle());
        Glide.with(holder.topImageView).load(newsList.get(position).getImageUrl()).into(holder.topImageView);
        holder.topDescriptionTextView.setText(newsList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView topTitleTextView;
        ImageView topImageView;

        TextView topDescriptionTextView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            topTitleTextView = itemView.findViewById(R.id.topTitleTextView);
            topImageView = itemView.findViewById(R.id.newsImageView);
            topDescriptionTextView = itemView.findViewById(R.id.topDescriptionTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment newStoryFragment = new NewsStoriesFragment();

            Bundle args = new Bundle();
            int position = getLayoutPosition();
            args.putSerializable("data", (Serializable) newsList.get(position));
            args.putString("type", "relatedAdaptor");

            newStoryFragment.setArguments(args);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out
                    )
                    .replace(R.id.main, newStoryFragment)
                    .addToBackStack(null).commit();

        }
    }
}
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

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<DataModel> topStoryList;

    public HorizontalAdapter(Context mContext, ArrayList<DataModel> topStoryList) {
        this.mContext = mContext;
        this.topStoryList = topStoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.horizontal_recycle_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.topTitleTextView.setText(topStoryList.get(position).getTitle());
        Glide.with(holder.topImageView).load(topStoryList.get(position).getImageUrl()).into(holder.topImageView);
    }

    @Override
    public int getItemCount() {
        return topStoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView topTitleTextView;
        ImageView topImageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            topTitleTextView = itemView.findViewById(R.id.topTitleTextView);
            topImageView = itemView.findViewById(R.id.newsImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment newStoryFragment = new NewsStoriesFragment();

            Bundle args = new Bundle();
            int position = getLayoutPosition();
            args.putSerializable("data", (Serializable) topStoryList.get(position));
            newStoryFragment.setArguments(args);
            args.putString("type", "standardAdaptor");

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out
                    )
                    .replace(R.id.main, newStoryFragment)
                    .addToBackStack(null)
                    .commit();

        }
    }
}
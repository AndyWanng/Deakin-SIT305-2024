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

public class HorizontalAdaptor extends RecyclerView.Adapter<HorizontalAdaptor.MyViewHolder> {

    Context mContext;
    ArrayList<MyDataModel> topStoryList;

    //constructor for use in MainAcitivty
    public HorizontalAdaptor(Context mContext, ArrayList<MyDataModel> topStoryList) {
        this.mContext = mContext;
        this.topStoryList = topStoryList;
    }

    @NonNull
    @Override
    public HorizontalAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.horizontalrecyclerview_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdaptor.MyViewHolder holder, int position) {
        //attach data to the layout
        holder.topTitleTextView.setText(topStoryList.get(position).getTitle());
        Glide.with(holder.topImageView).load(topStoryList.get(position).getImageUrl()).into(holder.topImageView);
    }

    @Override
    public int getItemCount() {
        //get the size of the arrayList
        return topStoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView topTitleTextView;
        ImageView topImageView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //attach items in view to variables
            topTitleTextView = itemView.findViewById(R.id.topTitleTextView);
            topImageView = itemView.findViewById(R.id.newsImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment newStoryFragment = new NewsStoryFragment();

            //settings up bundle to send data of specific recyclerview clicked item to fragment
            Bundle args = new Bundle();
            int position = getLayoutPosition();
            args.putSerializable("data", (Serializable) topStoryList.get(position));

            newStoryFragment.setArguments(args);

            args.putString("type", "standardAdaptor");

            //show fragment with animation
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    )
                    .replace(R.id.main, newStoryFragment)
                    .addToBackStack(null)
                    .commit();

        }
    }
}
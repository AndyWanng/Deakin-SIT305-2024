package com.example.task51c;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Objects;

public class NewsStoriesFragment extends Fragment {
    private DataModel newsItem;

    private RelatedDataModel relatedItem;

    public NewsStoriesFragment() {
        this.newsItem = newsItem;
    }

    public static NewsStoriesFragment newInstance(DataModel newsItem) {
        NewsStoriesFragment fragment = new NewsStoriesFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) newsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(Objects.equals(getArguments().getString("type"), "standardAdaptor")) {
                newsItem = (DataModel) getArguments().getSerializable("data");
                Log.v("received", newsItem.getTitle());
            } else if (Objects.equals(getArguments().getString("type"), "relatedAdaptor")) {
                relatedItem = (RelatedDataModel) getArguments().getSerializable("data");
                Log.v("received", relatedItem.getTitle());
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_stories, null);

        if(getArguments() != null) {
            TextView newsStoryTitleTextView = view.findViewById(R.id.newsStoryTitleTextView);
            ImageView newsImageView = view.findViewById(R.id.newsImageView);
            TextView newsStoryDescriptionTextView = view.findViewById(R.id.newsStoryDescriptionTextView);
            TextView newsStoryContentTextView = view.findViewById(R.id.newsStoryContentTextView);
            TextView relatedTitleTextView = view.findViewById(R.id.relatedTitleTextView);
            FloatingActionButton floatingActionButton =view.findViewById(R.id.floatingActionButton);

            if(Objects.equals(getArguments().getString("type"), "standardAdaptor")) {
                Glide.with(requireContext()).load(newsItem.getImageUrl()).into(newsImageView);
                newsStoryTitleTextView.setText(newsItem.getTitle());
                newsStoryDescriptionTextView.setText(newsItem.getDescription());
                newsStoryContentTextView.setText(newsItem.getContent());
                relatedTitleTextView.setText("Related Stories");

                RecyclerView storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);

                LinearLayoutManager storiesLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
                storiesRecyclerView.setLayoutManager(storiesLayoutManager);

                RelatedAdapter relatedAdapter = new RelatedAdapter(requireContext(), newsItem.getRelatedStories());

                storiesRecyclerView.setAdapter(relatedAdapter);
            } else if (Objects.equals(getArguments().getString("type"), "relatedAdaptor")) {
                Glide.with(requireContext()).load(relatedItem.getImageUrl()).into(newsImageView);
                newsStoryTitleTextView.setText(relatedItem.getTitle());
                newsStoryDescriptionTextView.setText(relatedItem.getDescription());
                newsStoryContentTextView.setText(relatedItem.getContent());

                View bar1 = view.findViewById(R.id.bar1);
                View bar2 = view.findViewById(R.id.bar2);

                ((ViewGroup) bar1.getParent()).removeView(bar1);
                ((ViewGroup) bar2.getParent()).removeView(bar2);
                ((ViewGroup) relatedTitleTextView.getParent()).removeView(relatedTitleTextView);



            }
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });

        }

        return view;
    }
}
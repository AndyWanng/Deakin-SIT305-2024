package com.example.personalizedquizapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalizedquizapp.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HomeFragment extends Fragment {



    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentHomeBinding binding;
    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);

        database = FirebaseFirestore.getInstance();


        ArrayList<TopicModel> categories = new ArrayList<>();

        TopicAdapter adapter = new TopicAdapter(getContext(),categories);

        database.collection("topics")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            TopicModel model = new TopicModel();
                            model.setTopicId(snapshot.getId());
                            model.setTopicName(snapshot.getString("topicName"));
                            model.setTopicImage(snapshot.getString("topicImage"));
                            if (model.getTopicName() != null) {
                                Log.d("HomeFragment", "Topic ID: " + snapshot.getId() + ", Topic Name: " + model.getTopicName() + ", Topic Image: " + model.getTopicImage());
                                categories.add(model);
                            } else {
                                Log.e("HomeFragment", "Topic Name is null for ID: " + snapshot.getId());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        binding.topicList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.topicList.setAdapter(adapter);
        return binding.getRoot();
    }
}
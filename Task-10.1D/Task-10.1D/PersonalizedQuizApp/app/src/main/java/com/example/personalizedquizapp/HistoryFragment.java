package com.example.personalizedquizapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.personalizedquizapp.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {
    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        setupRecyclerView();
        loadHistory();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(historyItems, this);
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.historyRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(HistoryItem item) {
        showDetailFragment(new Gson().toJson(item));
    }

    private void showDetailFragment(String historyItemJson) {
        DetailFragment fragment = DetailFragment.newInstance(historyItemJson);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("HistoryFragment", "User not logged in");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").document(userId).collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        historyItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HistoryItem item = document.toObject(HistoryItem.class);
                            historyItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("HistoryFragment", "Error loading history", task.getException());
                        Toast.makeText(getContext(), "Error loading history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

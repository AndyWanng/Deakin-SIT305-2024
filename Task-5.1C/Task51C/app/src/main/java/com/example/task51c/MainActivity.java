package com.example.task51c;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.example.task51c.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView HorizontalRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    RecyclerView.LayoutManager verticalLayoutManager;

    ArrayList<DataModel> topStoryList;
    ArrayList<DataModel> newsList;

    ArrayList<RelatedDataModel> relatedList;

    HorizontalAdapter horizontalAdapter;
    VerticalAdapter verticalAdapter;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topStoryList = new ArrayList<>();
        newsList = new ArrayList<>();
        relatedList = new ArrayList<>();



        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayoutManager = new LinearLayoutManager(this);

        horizontalAdapter = new HorizontalAdapter(this, topStoryList);
        verticalAdapter = new VerticalAdapter(this, newsList);


        binding.HorizontalRecycleView.setAdapter(horizontalAdapter);
        binding.HorizontalRecycleView.setLayoutManager(layoutManager);
        binding.VerticalRecycleView.setAdapter(verticalAdapter);
        binding.VerticalRecycleView.setLayoutManager(new GridLayoutManager(this,2));
        loadNewsDataFromJson();

    }
    private void loadNewsDataFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("news_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            parseJsonData(jsonObject);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void parseJsonData(JSONObject jsonObject) throws JSONException {
        JSONArray relatedStoriesArray = jsonObject.getJSONArray("relatedStories");
        for (int i = 0; i < relatedStoriesArray.length(); i++) {
            JSONObject story = relatedStoriesArray.getJSONObject(i);
            RelatedDataModel model = new RelatedDataModel(
                    story.getString("title"),
                    story.getString("description"),
                    story.getString("imageUrl"),
                    story.getString("detailText")
            );
            relatedList.add(model);
        }

        JSONArray topStoriesArray = jsonObject.getJSONArray("topStories");
        for (int i = 0; i < topStoriesArray.length(); i++) {
            JSONObject story = topStoriesArray.getJSONObject(i);
            DataModel model = new DataModel(
                    story.getString("title"),
                    story.getString("description"),
                    story.getString("imageUrl"),
                    story.getString("detailText"),
                    relatedList
            );
            topStoryList.add(model);
        }
        horizontalAdapter.notifyDataSetChanged();

        JSONArray newsListArray = jsonObject.getJSONArray("newsList");
        for (int i = 0; i < newsListArray.length(); i++) {
            JSONObject news = newsListArray.getJSONObject(i);
            DataModel model = new DataModel(
                    news.getString("title"),
                    news.getString("description"),
                    news.getString("imageUrl"),
                    news.getString("detailText"),
                    relatedList
            );
            newsList.add(model);
        }
        verticalAdapter.notifyDataSetChanged();
    }
}
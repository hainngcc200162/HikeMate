package com.example.hikemate;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Plant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;

public class PlantList extends AppCompatActivity {
    private NestedScrollView nestedScrollView;
    private TextView txtEmpty;

    private RecyclerView recyclerView, resultRecView;
    private SearchBar searchBar;
    private SearchView searchView;

    private PlantAdapter plantAdapter, plantAdapter2;
    private List<Plant> plantList;
    private List<Plant> plantList2 = new ArrayList<>();
    private FloatingActionButton fabScrollToTop;
    private HikeDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_plant_list);
        Glide.get(getApplicationContext()).setMemoryCategory(MemoryCategory.HIGH);
        initView();
        initListener();

        db = HikeDatabase.getInstance(PlantList.this);
        plantList = db.plantDao().getPlan();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantAdapter = new PlantAdapter(this);
        plantAdapter.setItemList(plantList);
        recyclerView.setAdapter(plantAdapter);

        resultRecView.setLayoutManager(new LinearLayoutManager(this));
        plantAdapter2 = new PlantAdapter(plantList2,this);
        resultRecView.setAdapter(plantAdapter2);

        fabScrollToTop.hide();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check the scroll position
                if (scrollY == 0) {
                    // Scroll is at the top, hide the FloatingActionButton
                    fabScrollToTop.hide();
                }else {
                    // Scrolling upwards, show the FloatingActionButton
                    fabScrollToTop.show();
                }
            }
        });

        //FAB scroll up
        // Set an OnClickListener for the FloatingActionButton
        fabScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the top of the NestedScrollView
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void initListener(){
        searchBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                finish();
            }
        });
        searchBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.search){
                    searchView.show();
                }
                return false;
            }
        });

        searchView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String name = "%" + String.valueOf(searchView.getText()) + "%";
                plantList2 = db.plantDao().searchPlant(name);
                if(plantList2.size()!=0){
                    resultRecView.setVisibility(View.VISIBLE);
                    plantAdapter2.setItemList(plantList2);
                    txtEmpty.setVisibility(View.GONE);
                }
                else {
                    resultRecView.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.PlantsRecyclerView);
        fabScrollToTop = findViewById(R.id.fabScrollToTop);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        txtEmpty = findViewById(R.id.txtEmpty);
        resultRecView = findViewById(R.id.resultRecView);
        searchBar = findViewById(R.id.search_bar);
        searchView = findViewById(R.id.search_view);
    }
}

package com.example.promanage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DisplayProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private TextView noPostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);


        noPostTextView = findViewById(R.id.nopost);
        recyclerView = findViewById(R.id.lost_list_view);


        dbHelper = new DatabaseHelper(this);


        productList = new ArrayList<>();
        productAdapter = new DisplayProductAdapter(this, productList);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {

        productList.clear();


        List<Product> fetchedProducts = dbHelper.getAllProducts();


        productList.addAll(fetchedProducts);


        productAdapter.notifyDataSetChanged();


        if (productList.isEmpty()) {
            noPostTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noPostTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


        Log.d("DisplayActivity", "Loaded products: " + productList.size());
    }
}
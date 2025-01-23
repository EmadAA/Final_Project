package com.example.promanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private TextView noPostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);


        noPostTextView = findViewById(R.id.nopost);
        recyclerView = findViewById(R.id.lost_list_view);
        Button addProductButton = findViewById(R.id.btn_lost_post_create);


        dbHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);


        loadProducts();


        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(dbHelper.getAllProducts());
        productAdapter.notifyDataSetChanged();

        if (noPostTextView != null && recyclerView != null) {
            if (productList.isEmpty()) {
                noPostTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noPostTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
package com.example.promanage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DisplayProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private TextView noPostTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        noPostTextView = findViewById(R.id.nopost);
        recyclerView = findViewById(R.id.lost_list_view);

        // Setup logout button
        ImageButton logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> performLogout());

        dbHelper = new DatabaseHelper(this);

        productList = new ArrayList<>();
        productAdapter = new DisplayProductAdapter(this, productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        loadProducts();
    }

    private void performLogout() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear any stored session/preferences if needed
        // For example:
        // SharedPreferences preferences = getSharedPreferences("your_prefs", MODE_PRIVATE);
        // preferences.edit().clear().apply();

        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to login screen
        Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
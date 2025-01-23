package com.example.promanage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private DatabaseHelper dbHelper;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

        if (product.getImageByteArray() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImageByteArray(), 0, product.getImageByteArray().length);
            holder.ivProductImage.setImageBitmap(bitmap);
        }

        holder.btnDelete.setOnClickListener(v -> deleteProduct(product, position));
        holder.btnUpdate.setOnClickListener(v -> showUpdateDialog(product, position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void deleteProduct(Product product, int position) {
        dbHelper.deleteProduct(product.getId());
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
    }

    private void showUpdateDialog(Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Product");


        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_product, null);
        builder.setView(dialogView);

        EditText etProductName = dialogView.findViewById(R.id.et_product_name);
        EditText etProductQuantity = dialogView.findViewById(R.id.et_product_quantity);


        etProductName.setText(product.getName());
        etProductQuantity.setText(String.valueOf(product.getQuantity()));

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = etProductName.getText().toString().trim();
            String quantityStr = etProductQuantity.getText().toString().trim();

            if (!newName.isEmpty() && !quantityStr.isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(quantityStr);
                    updateProduct(product, newName, newQuantity, position);
                } catch (NumberFormatException e) {

                    Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProduct(Product product, String newName, int newQuantity, int position) {

        dbHelper.updateProductNameAndQuantity(product.getId(), newName, newQuantity);


        product.setName(newName);
        product.setQuantity(newQuantity);

        notifyItemChanged(position);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvQuantity;
        Button btnUpdate;
        Button btnDelete;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_show_item_image);
            tvProductName = itemView.findViewById(R.id.tv_show_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_show_quantity);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
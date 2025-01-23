package com.example.promanage;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, quantityEditText;
    private Button addButton, selectImageButton;
    private ImageView imageView;
    private DatabaseHelper dbHelper;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        nameEditText = findViewById(R.id.et_product_name);
        quantityEditText = findViewById(R.id.et_product_quantity);
        addButton = findViewById(R.id.btn_add_product);
        selectImageButton = findViewById(R.id.btn_uploadimage);
        imageView = findViewById(R.id.iv_selectedImage);

        selectImageButton.setOnClickListener(v -> openFileChooser());

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String quantityStr = quantityEditText.getText().toString();

            if (name.isEmpty() || quantityStr.isEmpty() || imageUri == null) {
                Toast.makeText(AddProductActivity.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            // Convert the selected image to byte array
            byte[] imageByteArray = getImageByteArray(imageUri);

            // Insert product with image
            dbHelper.insertProduct(name, quantity, imageByteArray);

            Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getImageByteArray(Uri uri) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);


            options.inSampleSize = calculateInSampleSize(options, 500, 500);


            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



}

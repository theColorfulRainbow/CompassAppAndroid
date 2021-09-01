package com.example.compassexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Galery activity used to dispaly images taken by app
 */
public class GalleryActivity extends AppCompatActivity {
    // VARIABLES
    public static final int REQUEST_CODE_GALLERY = 105;                                             // request code for public dir
    Button btn_choseFromGallery;                                                                    // gallery button
    ImageView selectedImage;                                                                        // current image displayed
    TextView txt_fileName;                                                                          // current bearing or image name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // Initialize variables
        btn_choseFromGallery = findViewById(R.id.galleryBtn2);
        selectedImage = findViewById(R.id.galleryImg);
        txt_fileName = findViewById(R.id.txt_fileName);
        // Set Listeners
        btn_choseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(GalleryActivity.this,
//                        "Choose From Gallery Button pressed", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    /**
     * If request code is to access public gallery, grant it & display image chosen
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY){                                                    // make sure correct request code
            Uri contentUri = data.getData();
            String imageFileName = getFileName(contentUri).split("_")[0];                     // get bearing from filename
            Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
            selectedImage.setImageURI(contentUri);
            selectedImage.setRotation(90);                                                          // make image vertical
            txt_fileName.setText(imageFileName);
        }
    }

    /**
     * Get the image file name
     * @param uri: of the image chosen
     * @return: string filename
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}

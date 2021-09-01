package com.example.compassexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main Activity responsible for the compass and camera
 */
public class CompassActivity extends AppCompatActivity {
    private static final String TAG = "CompassActivity";                    // Log TAG
    // COMPASS VARIABLES
    public Compass compass;                                                // Compass Class
    UpdateUI ui;                                                            // Ui Class
    // CAMERA VARIABLES
    public static final int CAMERA_PERM_CODE = 101;                         // permission code
    public static final int CAMERA_REQUEST_CODE = 102;                      // request code
    Button btn_comaera, btn_gallery, btn_battery;                           // buttons
    String currentPhotoPath, fileName;                                      // path & filename
    // Accuracy STATUS
    private int SENSOR_STATUS_ACCURACY_HIGH     = 3;
    private int SENSOR_STATUS_ACCURACY_MEDIUM   = 2;
    private int SENSOR_STATUS_ACCURACY_LOW      = 1;
    private int SENSOR_STATUS_UNRELIABLE        = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        // Setup Variables
        ui = new UpdateUI(this);
        setupCompass();
        btn_comaera = findViewById(R.id.cameraBtn);
        btn_gallery = findViewById(R.id.galleryBtn);
        btn_battery = findViewById(R.id.batteryBtn);
        // Listeners
        btn_comaera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ask4cameraPermission();
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CompassActivity.this, "Gallery Button is clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CompassActivity.this, GalleryActivity.class));
            }
        });

        btn_battery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(CompassActivity.this, "Battery Button is clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CompassActivity.this, BatteryActivity.class));
            }
        });
    }

    @Override
    /**
     * Start Sensors
     */
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    /**
     * Pause Sensors
     */
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    /**
     * Resume sensors activity
     */
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    /**
     * Stop Sensors
     */
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    /**
     * Setup Compass Class
     */
    public void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    /**
     * Set compass Listener
     * @return Azimuth value
     */
    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final int azimuth, int accuracy) {
                if (accuracy < SENSOR_STATUS_UNRELIABLE) {
                    Toast.makeText(CompassActivity.this, "SESNOR ACCURACY IS UNRELIABLE", Toast.LENGTH_SHORT).show();
                }
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread(new Runnable() {
                    @Override
                    /**
                     * Update UI thread
                     */
                    public void run() {
                        fileName = ui.updateCompassUI(azimuth);                                     // Update compass UI
                    }
                });
            }
        };
    }

    // -- CAMERA METHODS

    /**
     * Check for camera permissions & ask if not granted
     * Only gets called when camera button is pressed
     */
    private void ask4cameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    /**
     * Check if permission sent is for camera; Grant permissions if request code is from camera
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this,"Camera Permission is required to use the camera",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    /**
     * Save picture to public media directory if image is saved
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {                                                 // Add photo to gallery
                File file = new File(currentPhotoPath);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
    }

    /**
     * Create an image file when picture is saved
     * @return File for the image
     * @throws IOException if image file fails to be created
     */
    private File createImageFile() throws IOException {
        String imageFileName = fileName+"_";
        File storageDir = Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);              // public directory
        File image = File.createTempFile(
                imageFileName,          //prefix
                ".jpg",          //suffix
                storageDir             //directory
        );
        currentPhotoPath = image.getAbsolutePath();                                                 // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    /**
     * Take picture using the camera at the press of camera button
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {                       // Ensure that there's a camera activity to handle the intent
            File photoFile = null;                                                                  // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {                                                              // Error occurred while creating the File
                Toast.makeText(CompassActivity.this,
                        "Error occurred while creating the File", Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {                                                                // Continue only if the File was successfully created
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider3",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}

package jp.ac.titech.itpro.sdl.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQ_PHOTO = 1234;
    private Bitmap photoImage = null;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button photoButton = findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                PackageManager manager = getPackageManager();

                if (!manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast.makeText(MainActivity.this, "No cameras detected", Toast.LENGTH_LONG).show();
                    return;
                }

                File photoFile = null;
                try {
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    photoFile = File.createTempFile("tmp", ".jpg", storageDir);
                    currentPhotoPath = photoFile.getAbsolutePath();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Failed to create a temporary file", Toast.LENGTH_LONG).show();
                }

                List activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!activities.isEmpty()) {
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQ_PHOTO);
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_no_activities, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showPhoto() {
        if (photoImage == null) {
            return;
        }
        ImageView photoView = findViewById(R.id.photo_view);
        photoView.setImageBitmap(photoImage);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == REQ_PHOTO) {
            if (resCode == RESULT_OK) {
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                try {
                    photoImage = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPhoto();
    }
}
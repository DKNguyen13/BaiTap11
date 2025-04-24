package vn.hcmute.videoshort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import vn.hcmute.videoshort.activities.UploadVideoActivity;
import vn.hcmute.videoshort.activities.UploadVideoFirebase;
import vn.hcmute.videoshort.activities.VideoShortActivity;

public class MainActivity extends AppCompatActivity {
    Button btnVideo, btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnVideo = findViewById(R.id.btnVideo);
        btnUpload = findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, UploadVideoFirebase.class);
            startActivity(myIntent);
        });

        btnVideo.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, VideoShortActivity.class);
            startActivity(myIntent);
        });

    }
}
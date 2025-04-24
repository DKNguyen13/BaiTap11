package vn.hcmute.videoshort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import vn.hcmute.videoshort.activities.LoginActivity;
import vn.hcmute.videoshort.activities.UploadVideoFirebase;
import vn.hcmute.videoshort.activities.VideoShortActivity;

public class MainActivity extends AppCompatActivity {
    Button btnVideo, btnUpload, btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnVideo = findViewById(R.id.btnVideo);
        btnUpload = findViewById(R.id.btnUpload);
        btnLogout = findViewById(R.id.btnLogOut);

        btnUpload.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, UploadVideoFirebase.class);
            startActivity(myIntent);
        });

        btnVideo.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, VideoShortActivity.class);
            startActivity(myIntent);
        });

        btnLogout.setOnClickListener(v ->{
            // Đăng xuất khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa backstack
            startActivity(intent);
            finish(); // Đóng activity hiện tại
        });

    }
}
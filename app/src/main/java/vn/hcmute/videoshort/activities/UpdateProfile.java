package vn.hcmute.videoshort.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.hcmute.videoshort.R;

public class UpdateProfile extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText edtEmail;
    private Button btnSave;
    private Uri selectedImageUri;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);

        // Lấy UID người dùng hiện tại
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy thông tin người dùng từ Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Lấy email và avatarUrl
            String currentEmail = currentUser.getEmail(); // Email từ Firebase
            Uri currentAvatarUrl = currentUser.getPhotoUrl(); // URL avatar từ Firebase

            // Cập nhật email vào EditText
            edtEmail.setText(currentEmail);

            // Cập nhật avatar nếu có URL
            if (currentAvatarUrl != null) {
                // Sử dụng Glide để tải ảnh vào ImageView
                Glide.with(this)
                        .load(currentAvatarUrl)
                        .into(imgAvatar);
            }
        }

    }
}

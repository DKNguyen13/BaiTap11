package vn.hcmute.videoshort.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.hcmute.videoshort.R;

public class UpdateProfile extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText edtEmail, edtAvatarUrl;
    private TextView txtVideoCount;
    private Button btnSave;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        edtEmail = findViewById(R.id.edtEmail);
        edtAvatarUrl = findViewById(R.id.edtAvatarUrl);
        txtVideoCount = findViewById(R.id.txtVideoCount);
        btnSave = findViewById(R.id.btnSave);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserUid = currentUser.getUid();
        String currentEmail = currentUser.getEmail();
        edtEmail.setText(currentEmail);

        loadUserAvatar();
        countUserVideos(currentEmail);

        btnSave.setOnClickListener(v -> {
            String newAvatarUrl = edtAvatarUrl.getText().toString().trim();
            if (!newAvatarUrl.isEmpty()) {
                updateAvatarUrl(newAvatarUrl);
            } else {
                Toast.makeText(this, "Vui lòng nhập URL ảnh!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserAvatar() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(UpdateProfile.this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_default_avatar1)
                            .error(R.drawable.ic_default_avatar1)
                            .into(imgAvatar);
                    edtAvatarUrl.setText(avatarUrl);
                } else {
                    imgAvatar.setImageResource(R.drawable.ic_default_avatar1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, "Không thể tải ảnh đại diện!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAvatarUrl(String newUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid);
        userRef.child("avatarUrl").setValue(newUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(UpdateProfile.this)
                        .load(newUrl)
                        .placeholder(R.drawable.ic_default_avatar1)
                        .error(R.drawable.ic_default_avatar1)
                        .into(imgAvatar);
                Toast.makeText(this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cập nhật ảnh thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void countUserVideos(String email) {
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
        videosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long videoCount = 0;
                for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
                    String videoEmail = videoSnapshot.child("email").getValue(String.class);
                    if (email != null && email.equals(videoEmail)) {
                        videoCount++;
                    }
                }
                txtVideoCount.setText("Số lượng video: " + videoCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtVideoCount.setText("Không thể tải số lượng video");
            }
        });
    }
}

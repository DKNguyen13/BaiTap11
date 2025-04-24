package vn.hcmute.videoshort.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import vn.hcmute.videoshort.model.VideoModel;
import vn.hcmute.videoshort.R;

public class UploadVideoFirebase extends AppCompatActivity {

    private EditText edtTitle, edtDesc;
    private Spinner spinnerUrl;
    private Button btnUpload;

    private DatabaseReference videoRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video_firebase); // Sửa lại nếu layout bạn đặt tên khác

        edtTitle = findViewById(R.id.edtTitleUL);
        edtDesc = findViewById(R.id.edtDesc);
        spinnerUrl = findViewById(R.id.spinnerUrl);
        btnUpload = findViewById(R.id.btnUpload);

        videoRef = FirebaseDatabase.getInstance().getReference("videos");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnUpload.setOnClickListener(view -> uploadVideo());
    }

    private void uploadVideo() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String selectedUrl = spinnerUrl.getSelectedItem().toString();

        if (title.isEmpty() || selectedUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề và chọn video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng đăng nhập
        String email = currentUser != null ? currentUser.getEmail() : "unknown";
        String avatar = currentUser != null && currentUser.getPhotoUrl() != null
                ? currentUser.getPhotoUrl().toString() : "";

        // Tạo đối tượng VideoModel
        VideoModel video = new VideoModel();
        video.setTitle(title);
        video.setDesc(desc);
        video.setUrl(selectedUrl);
        video.setEmail(email);
        video.setLikeCount(0);

        // Xử lý tiêu đề để tạo thành key hợp lệ
        String validKey = sanitizeTitleForFirebase(title);

        // Kiểm tra xem key đã tồn tại trong database chưa
        final String finalKey = validKey;  // Make validKey final or effectively final

        videoRef.child("videos").child(finalKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Nếu key đã tồn tại, thêm một yếu tố duy nhất (ví dụ: thời gian hoặc id người dùng)
                String newValidKey = finalKey + "_" + System.currentTimeMillis(); // Thêm timestamp để đảm bảo tính duy nhất

                // Lưu video với key mới
                videoRef.child("videos").child(newValidKey).setValue(video)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Đăng video thành công!", Toast.LENGTH_SHORT).show();
                            edtTitle.setText("");
                            edtDesc.setText("");
                            spinnerUrl.setSelection(0);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                videoRef.child(finalKey).setValue(video)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Đăng video thành công!", Toast.LENGTH_SHORT).show();
                            edtTitle.setText("");
                            edtDesc.setText("");
                            spinnerUrl.setSelection(0);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private String sanitizeTitleForFirebase(String title) {
        // Loại bỏ khoảng trắng thừa ở đầu và cuối
        title = title.trim();

        // Thay thế các ký tự không hợp lệ thành dấu gạch dưới "_"
        title = title.replaceAll("[^a-zA-Z0-9 ]", "_"); // Giữ chữ cái, số và khoảng trắng

        // Nếu tiêu đề quá ngắn hoặc trống, có thể đặt một giá trị mặc định
        if (title.isEmpty()) {
            title = "Untitled_Video";
        }
        return title;
    }
}

package vn.hcmute.videoshort.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import vn.hcmute.videoshort.R;
import vn.hcmute.videoshort.configs.CloudinaryConfig;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgAvatar;
    private TextView lgLink;
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private Uri selectedImageUri;
    private static final int PERMISSION_REQUEST_CODE = 100;


    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private Cloudinary cloudinary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mapping();

        //Cloudinary
        CloudinaryConfig config = new CloudinaryConfig();
        cloudinary = new Cloudinary(config.getCloudinaryConfigMap());

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");


        imgAvatar.setOnClickListener(v -> openImagePicker());
        btnRegister.setOnClickListener(v -> registerUser());
        lgLink.setOnClickListener(v -> loginLink());
        
    }

    private void loginLink() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                    uploadAvatarImage();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Log.d("SignUpError", "SignUp Failed" + task.getException().getMessage());
                    Toast.makeText(RegisterActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Error" , task.getException().getMessage());
                }
            }
        });
    }

    private void uploadAvatarImage() {
        if (selectedImageUri != null) {
            File file = new File(getRealPathFromURI(selectedImageUri));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                        String imageUrl = (String) uploadResult.get("secure_url");

                        // Save URL to Firebase
                        saveAvatarUrlToFirebase(imageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Please select an avatar image", Toast.LENGTH_SHORT).show();
        }
    }

    // Save avatar URL to Firebase
    private void saveAvatarUrlToFirebase(String avatarUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            databaseReference.child("avatarUrl").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Avatar uploaded and URL saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save URL in Firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Get real path from URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return uri.getPath();
        }
    }
    // Xử lý quyền khi người dùng yêu cầu
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE_REQUEST);
    }

    private void mapping() {
        imgAvatar = findViewById(R.id.imgAvatarRegister);
        etEmail = findViewById(R.id.etEmailRegis);
        etPassword = findViewById(R.id.etPasswordRegis);
        btnRegister = findViewById(R.id.btnRegister);
        lgLink = findViewById(R.id.tvLoginLink);
    }


}
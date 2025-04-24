package vn.hcmute.videoshort.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import vn.hcmute.videoshort.MainActivity;
import vn.hcmute.videoshort.R;
import vn.hcmute.videoshort.adapter.ShortVideoAdapter;
import vn.hcmute.videoshort.model.VideoModel;

public class VideoShortActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ImageView imgProfile;
    private ShortVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_short);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager2 = findViewById(R.id.vpager2);
        imgProfile = findViewById(R.id.btnProfile);

        getVideoShorts();
        loadUserAvatar();

        imgProfile.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        startActivity(new Intent(VideoShortActivity.this, UpdateProfile.class));
    }

    private void loadUserAvatar() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                Glide.with(VideoShortActivity.this)
                        .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.ic_default_avatar1)
                        .placeholder(R.drawable.ic_default_avatar1)
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoShortActivity.this, "Error loading avatar", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VideoShortActivity.this, LoginActivity.class));
            }
        });
    }

    private void getVideoShorts() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("videos");
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(mDataBase, VideoModel.class).build();
        adapter = new ShortVideoAdapter(options);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}

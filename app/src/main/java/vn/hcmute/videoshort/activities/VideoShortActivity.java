package vn.hcmute.videoshort.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.hcmute.videoshort.R;
import vn.hcmute.videoshort.adapter.ShortVideoAdapter;
import vn.hcmute.videoshort.model.VideoModel;

public class VideoShortActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    private ShortVideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_short);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewPager2 = findViewById(R.id.vpager2);
        getVideoShorts();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getVideoShorts() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference( "videos");
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(mDataBase, VideoModel.class).build();
        adapter = new ShortVideoAdapter(options);

        viewPager2.setOrientation(viewPager2.ORIENTATION_HORIZONTAL);
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
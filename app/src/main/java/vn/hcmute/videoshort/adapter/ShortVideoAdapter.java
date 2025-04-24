package vn.hcmute.videoshort.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.hcmute.videoshort.R;
import vn.hcmute.videoshort.model.VideoModel;

public class ShortVideoAdapter extends FirebaseRecyclerAdapter<VideoModel, ShortVideoAdapter.MyHolder> {

    public ShortVideoAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull VideoModel model) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("favorites");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String videoId = getRef(position).getKey(); // Lấy key của video từ Firebase

        holder.textVideoDesc.setText(model.getDesc());
        holder.textVideoTitle.setText(model.getTitle());
        holder.tvEmail.setText(model.getEmail());
        holder.progressBar.setVisibility(View.VISIBLE);

        Uri videoUri = Uri.parse(model.getUrl());
        holder.videoView.setVideoURI(videoUri);

        // Cập nhật likeCount khi video được tải
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("videos").child(videoId);
        videoRef.child("likeCount").get().addOnSuccessListener(snapshot -> {
            long likeCountValue = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            holder.likeCount.setText(String.valueOf(likeCountValue));  // Hiển thị likeCount
        });

        // Kiểm tra xem người dùng đã like video này chưa
        favRef.child(videoId).child(uid).get().addOnSuccessListener(snapshot -> {
            boolean isFav = snapshot.exists();
            if (isFav) {
                holder.favorites.setImageResource(R.drawable.ic_fill_favorite);
                holder.favorites.setTag(true);
            } else {
                holder.favorites.setImageResource(R.drawable.icon_favorite);
                holder.favorites.setTag(false);
            }
        });

        holder.videoView.setOnPreparedListener(mp -> {
            holder.progressBar.setVisibility(View.GONE);
            mp.setLooping(true);
            mp.start();

            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
            float scale = videoRatio / screenRatio;

            if (scale >= 1f) {
                holder.videoView.setScaleX(scale);
                holder.videoView.setScaleY(1f);
            } else {
                holder.videoView.setScaleX(1f);
                holder.videoView.setScaleY(1f / scale);
            }
        });

        holder.videoView.setOnErrorListener((mp, what, extra) -> {
            holder.progressBar.setVisibility(View.GONE);
            return true;
        });

        holder.favorites.setOnClickListener(v -> {
            Object tag = holder.favorites.getTag();
            boolean isFav = tag != null && (Boolean) tag;

            if (!isFav) {
                // Lưu uid đã like video
                favRef.child(videoId).child(uid).setValue(true);

                // Tăng likeCount
                videoRef.child("likeCount").get().addOnSuccessListener(snapshot -> {
                    long currentLikes = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                    long newLikeCount = currentLikes + 1;
                    videoRef.child("likeCount").setValue(newLikeCount);
                    holder.likeCount.setText(String.valueOf(newLikeCount));  // Cập nhật UI
                });

                holder.favorites.setImageResource(R.drawable.ic_fill_favorite);
                holder.favorites.setTag(true);
            } else {
                // Xóa uid khỏi like list
                favRef.child(videoId).child(uid).removeValue();

                // Giảm likeCount (không nhỏ hơn 0)
                videoRef.child("likeCount").get().addOnSuccessListener(snapshot -> {
                    long currentLikes = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                    long newLikeCount = Math.max(currentLikes - 1, 0);
                    videoRef.child("likeCount").setValue(newLikeCount);
                    holder.likeCount.setText(String.valueOf(newLikeCount));  // Cập nhật UI
                });

                holder.favorites.setImageResource(R.drawable.icon_favorite);
                holder.favorites.setTag(false);
            }
        });
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyHolder(view);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        TextView likeCount, textVideoTitle, textVideoDesc, tvEmail;
        ImageView imPerson, favorites, imShare, imMore;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.videoProgressBar);
            textVideoTitle = itemView.findViewById(R.id.textVideoTitle);
            textVideoDesc = itemView.findViewById(R.id.textVideoDescription);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            likeCount = itemView.findViewById(R.id.likeCount);
            imPerson = itemView.findViewById(R.id.imPerson);
            favorites = itemView.findViewById(R.id.favorites);
            imShare = itemView.findViewById(R.id.imShare);
            imMore = itemView.findViewById(R.id.imMore);
            favorites.setTag(false); // Default is not favorite
        }
    }
}

package com.example.safety_kick;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safety_kick.databinding.ItemClassificationResultBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.tensorflow.lite.support.label.Category;

/** Adapter for displaying the list of classifications for the image */
public class ClassificationResultAdapter
        extends RecyclerView.Adapter<ClassificationResultAdapter.ViewHolder> {
    private static final String NO_VALUE = "--";
    private static final long NO_HELMET_THRESHOLD = 5000; // 5 seconds threshold
    private List<Category> categories = new ArrayList<>();
    private int adapterSize = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClassificationResultBinding binding = ItemClassificationResultBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateResults(List<Category> categories) {
        List<Category> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories, new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                return category1.getIndex() - category2.getIndex();
            }
        });
        this.categories = new ArrayList<>(Collections.nCopies(adapterSize, null));

        // Check if 'crash helmet' is present and update lastHelmetTime
        for (Category category : sortedCategories) {
            if ("crash helmet".equals(category.getLabel())) {
                ViewHolder.lastHelmetTime = System.currentTimeMillis();
                break;
            }
        }

        int min = Math.min(sortedCategories.size(), adapterSize);
        for (int i = 0; i < min; i++) {
            this.categories.set(i, sortedCategories.get(i));
        }

        notifyDataSetChanged();
    }

    public void updateAdapterSize(int size) {
        adapterSize = size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLabel;
        private static long lastHelmetTime = 0;
        private static MediaPlayer mediaPlayer;

        public ViewHolder(@NonNull ItemClassificationResultBinding binding) {
            super(binding.getRoot());
            tvLabel = binding.tvLabel;
            tvLabel.setGravity(Gravity.CENTER);
            mediaPlayer = MediaPlayer.create(binding.getRoot().getContext(), R.raw.helmet);
        }

        public void bind(Category category) {
            if (category != null) {
                if ("crash helmet".equals(category.getLabel())) {
                    tvLabel.setText("헬멧 착용중");
                    tvLabel.setTypeface(null, Typeface.BOLD);
                    tvLabel.setTextColor(Color.rgb(0, 128, 0)); // 짙은 초록색
                } else {
                    tvLabel.setText("");
                }
            } else {
                // Check if '헬멧 착용중' text hasn't been shown for the last 5 seconds
                if (System.currentTimeMillis() - lastHelmetTime >= NO_HELMET_THRESHOLD) {
                    tvLabel.setText("헬멧 미착용");
                    tvLabel.setTypeface(null, Typeface.BOLD);
                    tvLabel.setTextColor(Color.rgb(255, 0, 0)); // 빨강색
                    playSound(); // 음성 재생
                } else {
                    tvLabel.setText(NO_VALUE);
                    tvLabel.setTypeface(null, Typeface.NORMAL);
                    tvLabel.setTextColor(Color.BLACK);
                }
            }
        }

        private void playSound() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }
}

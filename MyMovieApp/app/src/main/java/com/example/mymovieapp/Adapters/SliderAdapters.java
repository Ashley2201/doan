package com.example.mymovieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mymovieapp.Domain.SliderItems;
import com.example.mymovieapp.R;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SliderAdapters extends RecyclerView.Adapter<SliderAdapters.SliderViewHolder> {
    private List<SliderItems> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;

    // Constructor đã sửa - nhận cả sliderItems và viewPager2
    public SliderAdapters(List<SliderItems> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapters.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slide_item_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapters.SliderViewHolder holder, int position) {
        // Sử dụng modulo để tạo infinite scroll
        int realPosition = position % sliderItems.size();
        holder.setImageView(sliderItems.get(realPosition));

        // Khi đến gần cuối, thêm items để tạo infinite scroll
        if (position == getItemCount() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng lớn để tạo infinite scroll
        return Integer.MAX_VALUE;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImageView(SliderItems sliderItem) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));

            // Sửa: load từ image resource của SliderItems
            Glide.with(context)
                    .load(sliderItem.getImage()) // ← Giả sử SliderItems có method getImage()
                    .apply(requestOptions)
                    .into(imageView);
        }
    }

    // Runnable để tạo infinite scroll - đã sửa logic
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Chuyển đến item tiếp theo thay vì duplicate list
            int currentItem = viewPager2.getCurrentItem();
            viewPager2.setCurrentItem(currentItem + sliderItems.size(), false);
        }
    };
}
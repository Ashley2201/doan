package com.example.mymovieapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mymovieapp.Adapters.CategoryListAdapter;
import com.example.mymovieapp.Adapters.FilmListAdapter;
import com.example.mymovieapp.Adapters.SliderAdapters;
import com.example.mymovieapp.Domain.FilmList;
import com.example.mymovieapp.Domain.GenresItem;
import com.example.mymovieapp.Domain.SliderItems;
import com.example.mymovieapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterBestMovies, adapterUpComming, adapterCategory;
    private RecyclerView recyclerViewBestMovie, recyclerViewUpComming, recyclerViewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1, mStringRequest2, mStringRequest3;
    private ProgressBar loading1, loading2, loading3;
    private TextView upcomingTitle;

    private ViewPager2 viewPager2;
    private final Handler slideHandler = new Handler(Looper.getMainLooper());

    // Biến để kiểm tra quyền admin
    private boolean isAdmin = false;
    private String username = "";
    private SharedPreferences sharedPreferences;

    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        checkUserLogin();
        setupUpcomingMovieAccess();
        banners();
        sendRequestBestMovies();
        sendRequestCategory();

        // Chỉ load upcoming movies nếu là admin
        if (isAdmin) {
            sendRequestUpComming();
        }
    }

    private void checkUserLogin() {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Kiểm tra xem đã đăng nhập chưa
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Chưa đăng nhập, chuyển về LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Lấy thông tin user
        username = sharedPreferences.getString("username", "");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        Log.d("UserInfo", "Username: " + username + ", IsAdmin: " + isAdmin);

        // Hiển thị thông báo chào mừng
        String role = isAdmin ? "Admin" : "User";
        Toast.makeText(this, "Xin chào " + username + " (" + role + ")", Toast.LENGTH_SHORT).show();
    }

    private void setupUpcomingMovieAccess() {
        upcomingTitle = findViewById(R.id.textView11);
        recyclerViewUpComming = findViewById(R.id.view3);
        loading3 = findViewById(R.id.progressBar3);

        if (!isAdmin) {
            // Ẩn phần upcoming movie cho user thường
            upcomingTitle.setVisibility(View.GONE);
            recyclerViewUpComming.setVisibility(View.GONE);
            loading3.setVisibility(View.GONE);

            // Hoặc có thể hiển thị thông báo
            upcomingTitle.setVisibility(View.VISIBLE);
            upcomingTitle.setText("Upcoming Movie (Admin Only)");
            upcomingTitle.setOnClickListener(v -> {
                Toast.makeText(this, "Bạn không phải admin. Không thể xem phần này!", Toast.LENGTH_LONG).show();
            });

        } else {
            // Hiển thị bình thường cho admin
            upcomingTitle.setVisibility(View.VISIBLE);
            recyclerViewUpComming.setVisibility(View.VISIBLE);
            upcomingTitle.setText("Upcoming Movie");
        }
    }

    // Method để đăng xuất
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }

    private void sendRequestBestMovies() {
        mRequestQueue= Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson=new Gson();
                loading1.setVisibility(View.GONE);
                FilmList items=gson.fromJson(response, FilmList.class);
                adapterBestMovies=new FilmListAdapter(items);
                recyclerViewBestMovie.setAdapter(adapterBestMovies);
            }
        }, error -> {
            loading1.setVisibility(View.GONE);
            Log.i("BaNguyen", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void sendRequestUpComming() {
        // Chỉ gọi API nếu là admin
        if (!isAdmin) {
            return;
        }

        mRequestQueue= Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);
        mStringRequest3=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=2", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson=new Gson();
                loading3.setVisibility(View.GONE);
                FilmList items=gson.fromJson(response, FilmList.class);
                adapterUpComming=new FilmListAdapter(items);
                recyclerViewUpComming.setAdapter(adapterUpComming);
            }
        }, error -> {
            loading3.setVisibility(View.GONE);
            Log.i("BaNguyen", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest3);
    }

    private void sendRequestCategory() {
        mRequestQueue= Volley.newRequestQueue(this);
        loading2.setVisibility(View.VISIBLE);
        mStringRequest2=new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/genres", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson=new Gson();
                loading2.setVisibility(View.GONE);
                ArrayList<GenresItem> catList=gson.fromJson(response, new TypeToken<ArrayList<GenresItem>>() {}.getType());
                adapterCategory=new CategoryListAdapter(catList);
                recyclerViewCategory.setAdapter(adapterCategory);
            }
        }, error -> {
            loading2.setVisibility(View.GONE);
            Log.i("BaNguyen", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest2);
    }

    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        int startPosition = Integer.MAX_VALUE / 2;
        startPosition = startPosition - (startPosition % sliderItems.size());
        viewPager2.setCurrentItem(startPosition);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
                slideHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 2000);
    }

    private void initView() {
        viewPager2=findViewById(R.id.viewpagerSlider);
        recyclerViewBestMovie=findViewById(R.id.view1);
        recyclerViewBestMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpComming=findViewById(R.id.view3);
        recyclerViewUpComming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerViewCategory=findViewById(R.id.view2);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loading1=findViewById(R.id.progressBar1);
        loading2=findViewById(R.id.progressBar2);
        loading3=findViewById(R.id.progressBar3);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout explorerTab = findViewById(R.id.explorerTab);
        LinearLayout favoritesTab = findViewById(R.id.favoritesTab);
        LinearLayout cartsTab = findViewById(R.id.cartsTab);
        LinearLayout profileTab = findViewById(R.id.profileTab);

        explorerTab.setOnClickListener(v -> {
            Log.d("Navigation", "Explorer clicked");
        });

        favoritesTab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        cartsTab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        profileTab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    // Override onBackPressed để ngăn user quay lại LoginActivity
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có muốn thoát ứng dụng?")
                .setPositiveButton("Có", (dialog, which) -> {
                    super.onBackPressed();
                    finishAffinity(); // Thoát hoàn toàn ứng dụng
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
package com.example.mymovieapp.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymovieapp.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, userRoleTextView, accountTypeTextView;
    private ImageView profileImageView;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;

    private String username = "";
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserInfo();
        setupProfileImage();
        setupLogout();
    }

    private void initViews() {
        usernameTextView = findViewById(R.id.textView17);
        userRoleTextView = findViewById(R.id.textView18);
        accountTypeTextView = findViewById(R.id.accountTypeTextView);
        profileImageView = findViewById(R.id.imageView7);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupProfileImage() {
        if (profileImageView != null) {
            try {
                // Thử set hình từ drawable
                profileImageView.setImageResource(R.drawable.profile);
            } catch (Exception e) {
                // Nếu không có file profile.png, dùng hình mặc định
                try {
                    profileImageView.setImageResource(android.R.drawable.ic_menu_myplaces);
                } catch (Exception ex) {
                    // Nếu vẫn lỗi, tạo hình tròn với background color
                    profileImageView.setBackgroundResource(R.drawable.circle_background);
                }
            }
        }
    }

    private void loadUserInfo() {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Lấy thông tin user
        username = sharedPreferences.getString("username", "Unknown User");
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        // Hiển thị thông tin
        if (usernameTextView != null) {
            usernameTextView.setText(username);
        }

        if (userRoleTextView != null) {
            if (isAdmin) {
                userRoleTextView.setText("@" + username + " • Administrator");
            } else {
                userRoleTextView.setText("@" + username + " • Standard User");
            }
        }

        // Hiển thị loại tài khoản
        if (accountTypeTextView != null) {
            if (isAdmin) {
                accountTypeTextView.setText("Admin");
                accountTypeTextView.setTextColor(0xFFFF0000); // Màu đỏ
            } else {
                accountTypeTextView.setText("User");
                accountTypeTextView.setTextColor(0xFF2196F3); // Màu xanh
            }
        }
    }

    private void setupLogout() {
        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutDialog();
                }
            });
        }
    }

    private void showLogoutDialog() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản " + username + "?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        performLogout();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } catch (Exception e) {
            performLogout();
        }
    }

    private void performLogout() {
        try {
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
            }

            Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
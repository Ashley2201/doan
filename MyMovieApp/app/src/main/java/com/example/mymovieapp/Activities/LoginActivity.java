package com.example.mymovieapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mymovieapp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText userEdt, passEdt;
    private Button loginBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        initView();
    }

    private void initView(){
        userEdt=findViewById(R.id.editTextUserName);
        passEdt=findViewById(R.id.editTextPassword);
        loginBtn=findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = userEdt.getText().toString().trim();
                String password = passEdt.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill your username and password!!!", Toast.LENGTH_SHORT).show();
                } else if (validateLogin(username, password)) {
                    // Đăng nhập thành công
                    saveLoginInfo(username, password);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // Đóng màn hình login
                } else {
                    Toast.makeText(LoginActivity.this, "Your username or password is not correct!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        // Kiểm tra user thường
        if (username.equals("test") && password.equals("test")) {
            return true;
        }

        // Kiểm tra admin
        if (username.equals("admin") && password.equals("admin123")) {
            return true;
        }

        // Có thể thêm nhiều user khác
        if (username.equals("user1") && password.equals("pass1")) {
            return true;
        }

        return false;
    }

    private void saveLoginInfo(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lưu thông tin đăng nhập
        editor.putString("username", username);
        editor.putBoolean("isLoggedIn", true);

        // Xác định quyền admin dựa trên username
        boolean isAdmin = username.equals("admin");
        editor.putBoolean("isAdmin", isAdmin);

        // Lưu thông tin user
        editor.putString("userRole", isAdmin ? "admin" : "user");

        editor.apply();

        // Hiển thị thông báo đăng nhập
        if (isAdmin) {
            Toast.makeText(this, "Đăng nhập thành công với quyền Admin!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra xem đã đăng nhập chưa
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Đã đăng nhập, chuyển thẳng đến MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
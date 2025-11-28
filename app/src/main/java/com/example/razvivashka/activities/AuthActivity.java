package com.example.razvivashka.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.razvivashka.R;
import com.example.razvivashka.model.User;
import com.example.razvivashka.repository.UserRepository;

public class AuthActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnTabLogin, btnTabRegister, btnLogin, btnRegister;
    private LinearLayout usernameLayout;
    private boolean isLoginMode = true;

    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initViews();
        setupClickListeners();

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);

        // Проверяем, авторизован ли пользователь
        if (isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabRegister = findViewById(R.id.btnTabRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        usernameLayout = findViewById(R.id.usernameLayout);

        switchToLogin();
    }

    private void setupClickListeners() {
        btnTabLogin.setOnClickListener(v -> switchToLogin());
        btnTabRegister.setOnClickListener(v -> switchToRegister());
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void switchToLogin() {
        isLoginMode = true;
        usernameLayout.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.GONE);
        btnTabLogin.setBackgroundColor(getColor(R.color.white));
        btnTabRegister.setBackgroundColor(getColor(R.color.white));
        btnTabLogin.setTextColor(getColor(R.color.purple_500));
        btnTabRegister.setTextColor(getColor(R.color.text_primary));
    }

    private void switchToRegister() {
        isLoginMode = false;
        usernameLayout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        btnRegister.setVisibility(View.VISIBLE);
        btnTabLogin.setBackgroundColor(getColor(R.color.white));
        btnTabRegister.setBackgroundColor(getColor(R.color.white));
        btnTabLogin.setTextColor(getColor(R.color.text_primary));
        btnTabRegister.setTextColor(getColor(R.color.purple_500));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            User user = userRepository.loginUser(email, password);
            if (user != null) {
                // Сохраняем ID пользователя в SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", user.getId());
                editor.putString("user_name", user.getUsername());
                editor.putString("user_email", user.getEmail());
                editor.putInt("user_score", user.getTotalScore());
                editor.putBoolean("is_logged_in", true);
                editor.apply();

                currentUserId = user.getId();
                Toast.makeText(this, "Добро пожаловать, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка входа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Проверяем, нет ли уже пользователя с таким email
            User existingUser = userRepository.getUserByEmail(email);
            if (existingUser != null) {
                Toast.makeText(this, "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            // ИСПРАВЛЕННАЯ СТРОКА - используем конструктор с 3 параметрами
            User newUser = new User(username, email, password);
            long userId = userRepository.registerUser(newUser);

            if (userId > 0) {
                // Сохраняем данные в SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", (int) userId);
                editor.putString("user_name", username);
                editor.putString("user_email", email);
                editor.putInt("user_score", 0);
                editor.putBoolean("is_logged_in", true);
                editor.apply();

                currentUserId = (int) userId;
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }
}
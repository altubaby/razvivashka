package com.example.razvivashka.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.razvivashka.R;
import com.example.razvivashka.model.User;
import com.example.razvivashka.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private TextView textGames, textRating, textProfile;
    private LinearLayout navGames, navRating, navProfile;
    private ConstraintLayout gamesContainer;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        initViews();
        setupClickListeners();
        updateNavigation();
    }

    private void initViews() {
        gamesContainer = findViewById(R.id.gamesContainer);
        navGames = findViewById(R.id.navGames);
        navRating = findViewById(R.id.navRating);
        navProfile = findViewById(R.id.navProfile);
        textGames = findViewById(R.id.textGames);
        textRating = findViewById(R.id.textRating);
        textProfile = findViewById(R.id.textProfile);
    }

    private void setupClickListeners() {
        // Обработчики для игр
        findViewById(R.id.btnFlag).setOnClickListener(v -> startFlagGame());
        findViewById(R.id.btnPaint).setOnClickListener(v -> startColoringGame());
        findViewById(R.id.btnMath).setOnClickListener(v -> startMathGame());
        findViewById(R.id.btnLogic).setOnClickListener(v -> startLogicGame());

        // Обработчики для навигации
        navGames.setOnClickListener(v -> showGamesSection());
        navRating.setOnClickListener(v -> showRatingSection());
        navProfile.setOnClickListener(v -> showProfileSection());
    }

    private void startFlagGame() {
        startActivity(new Intent(this, FlagGameActivity.class));
    }

    private void startMathGame() {
        startActivity(new Intent(this, MathGameActivity.class));
    }

    private void startColoringGame() {
        startActivity(new Intent(this, ColoringActivity.class));
    }

    private void startLogicGame() {
        startActivity(new Intent(this, LogicGameActivity.class));
    }

    private void showGamesSection() {
        // Мы уже в играх, просто обновляем навигацию
        updateNavigationColors(textGames, textRating, textProfile);
    }

    private void showRatingSection() {
        Intent intent = new Intent(this, RatingActivity.class);
        startActivity(intent);
        // Не вызываем finish(), чтобы можно было вернуться назад
    }

    private void showProfileSection() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        // Не вызываем finish(), чтобы можно было вернуться назад
    }

    private void updateNavigationColors(TextView active, TextView... inactive) {
        active.setTextColor(getResources().getColor(R.color.purple_500));
        for (TextView textView : inactive) {
            textView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void updateNavigation() {
        updateNavigationColors(textGames, textRating, textProfile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData();
    }

    private void refreshUserData() {
        if (currentUserId != -1) {
            new Thread(() -> {
                try {
                    User currentUser = userRepository.getUserById(currentUserId);
                    if (currentUser != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_score", currentUser.getTotalScore());
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
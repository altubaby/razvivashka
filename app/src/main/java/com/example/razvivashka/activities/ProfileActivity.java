package com.example.razvivashka.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.razvivashka.R;
import com.example.razvivashka.model.User;
import com.example.razvivashka.repository.UserRepository;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail, profileScore;
    private Button logoutButton;
    private LinearLayout navGames, navRating, navProfile;
    private TextView textGames, textRating, textProfile;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        initViews();
        loadUserData();
        setupClickListeners();
        setupNavigation();
    }

    private void initViews() {
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profileScore = findViewById(R.id.profile_score);
        logoutButton = findViewById(R.id.logout_button);

        // Навигация
        navGames = findViewById(R.id.navGames);
        navRating = findViewById(R.id.navRating);
        navProfile = findViewById(R.id.navProfile);
        textGames = findViewById(R.id.textGames);
        textRating = findViewById(R.id.textRating);
        textProfile = findViewById(R.id.textProfile);
    }

    private void setupNavigation() {
        // Подсвечиваем текущий раздел (Профиль)
        updateNavigationColors(textProfile, textGames, textRating);

        // Обработчики навигации
        navGames.setOnClickListener(v -> navigateToGames());
        navRating.setOnClickListener(v -> navigateToRating());
        navProfile.setOnClickListener(v -> {
            // Мы уже в профиле, ничего не делаем
        });
    }

    private void navigateToGames() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToRating() {
        Intent intent = new Intent(this, RatingActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        new Thread(() -> {
            try {
                User currentUser = userRepository.getUserById(currentUserId);

                runOnUiThread(() -> {
                    if (currentUser != null) {
                        profileName.setText(currentUser.getUsername());
                        profileEmail.setText(currentUser.getEmail());
                        profileScore.setText(formatScore(currentUser.getTotalScore()));
                    } else {
                        profileName.setText("Пользователь");
                        profileEmail.setText("Не удалось загрузить");
                        profileScore.setText("0 очков");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    profileName.setText("Ошибка");
                    profileEmail.setText("Не удалось загрузить данные");
                    profileScore.setText("0 очков");
                });
            }
        }).start();
    }

    private String formatScore(int score) {
        return score + " " + getScoreWord(score);
    }

    private String getScoreWord(int score) {
        if (score % 10 == 1 && score % 100 != 11) {
            return "очко";
        } else if (score % 10 >= 2 && score % 10 <= 4 && (score % 100 < 10 || score % 100 >= 20)) {
            return "очка";
        } else {
            return "очков";
        }
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> performLogout())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void performLogout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateNavigationColors(TextView active, TextView... inactive) {
        active.setTextColor(getResources().getColor(R.color.purple_500));
        for (TextView textView : inactive) {
            textView.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onBackPressed() {
        navigateToGames();
    }
}
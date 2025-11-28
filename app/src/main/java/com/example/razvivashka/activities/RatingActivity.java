package com.example.razvivashka.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.razvivashka.R;
import com.example.razvivashka.model.User;
import com.example.razvivashka.repository.UserRepository;
import com.example.razvivashka.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView ratingRecyclerView;
    private TextView currentUserName, currentUserScore;
    private LinearLayout navGames, navRating, navProfile;
    private TextView textGames, textRating, textProfile;
    private UserAdapter adapter;
    private UserRepository userRepository;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        initViews();
        setupNavigation();
        setupRecyclerView();
        loadRatingData();
    }

    private void initViews() {
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);
        currentUserName = findViewById(R.id.current_user_name);
        currentUserScore = findViewById(R.id.current_user_score);

        // Навигация
        navGames = findViewById(R.id.navGames);
        navRating = findViewById(R.id.navRating);
        navProfile = findViewById(R.id.navProfile);
        textGames = findViewById(R.id.textGames);
        textRating = findViewById(R.id.textRating);
        textProfile = findViewById(R.id.textProfile);
    }

    private void setupNavigation() {
        // Подсвечиваем текущий раздел (Рейтинг)
        updateNavigationColors(textRating, textGames, textProfile);

        // Обработчики навигации
        navGames.setOnClickListener(v -> navigateToGames());
        navRating.setOnClickListener(v -> {
            // Мы уже в рейтинге, ничего не делаем
        });
        navProfile.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToGames() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView() {
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadRatingData() {
        new Thread(() -> {
            try {
                List<User> users = userRepository.getTopUsers();
                User currentUser = userRepository.getUserById(currentUserId);

                runOnUiThread(() -> {
                    if (users != null && !users.isEmpty()) {
                        adapter = new UserAdapter(users, currentUser != null ? currentUser.getUsername() : "");
                        ratingRecyclerView.setAdapter(adapter);
                    } else {
                        adapter = new UserAdapter(new ArrayList<>(), "");
                        ratingRecyclerView.setAdapter(adapter);
                    }

                    if (currentUser != null) {
                        currentUserName.setText(currentUser.getUsername());
                        currentUserScore.setText(formatScore(currentUser.getTotalScore()));
                    } else {
                        currentUserName.setText("Пользователь");
                        currentUserScore.setText("0 очков");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    adapter = new UserAdapter(new ArrayList<>(), "");
                    ratingRecyclerView.setAdapter(adapter);
                    currentUserName.setText("Ошибка");
                    currentUserScore.setText("0 очков");
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
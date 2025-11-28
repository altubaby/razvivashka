package com.example.razvivashka.activities;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.razvivashka.repository.UserRepository;

public abstract class BaseGameActivity extends AppCompatActivity {
    protected SharedPreferences sharedPreferences;
    protected UserRepository userRepository;
    protected int currentUserId;
    protected int gameScore = 0;
    protected Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);
        currentUserId = sharedPreferences.getInt("user_id", -1);
    }

    // Метод для добавления очков во время игры
    protected void addScore(int points) {
        gameScore += points;

        // Показываем всплывающее уведомление о начислении очков
        showPointsNotification(points);

        // Обновляем UI если нужно
        updateScoreDisplay();
    }

    // ДОБАВЛЯЕМ метод updateTotalScore (синоним для addScore)
    protected void updateTotalScore(int points) {
        addScore(points);
    }

    // Всплывающее уведомление о начислении очков
    protected void showPointsNotification(int points) {
        mainHandler.post(() -> {
            // Можно сделать красивую анимацию появления очков
            // Toast.makeText(this, "+" + points + " очков!", Toast.LENGTH_SHORT).show();
        });
    }

    // Обновление отображения счета (переопределить в дочерних классах)
    protected void updateScoreDisplay() {
        // Переопределить в игровых активностях
    }

    // Сохранение результата игры
    protected void saveGameResult() {
        if (currentUserId != -1 && gameScore > 0) {
            try {
                // Добавляем очки к общему счету
                userRepository.updateUserScore(currentUserId, gameScore);

                // Обновляем SharedPreferences
                int currentTotalScore = sharedPreferences.getInt("user_score", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_score", currentTotalScore + gameScore);
                editor.apply();

                System.out.println("Сохранено " + gameScore + " очков для пользователя " + currentUserId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для принудительного сохранения
    protected void saveGameProgress() {
        saveGameResult();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Автосохранение при выходе из игры
        if (gameScore > 0) {
            saveGameResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Финальное сохранение
        if (gameScore > 0) {
            saveGameResult();
        }
    }
}
package com.example.razvivashka.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.razvivashka.R;
import com.example.razvivashka.repository.UserRepository;

import java.util.Random;

public class FlagGameActivity extends AppCompatActivity {

    private TextView tvQuestion, tvScore, tvLevel;
    private ImageView ivFlag;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnHint, btnSkip, btnBack;
    private int currentScore = 0;
    private int currentLevel = 1;
    private int questionsAnswered = 0;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private int currentUserId;

    // Данные для игры - исправленные имена
    private String[] countries = {"Россия", "США", "Франция", "Германия", "Япония", "Китай",
            "Великобритания", "Италия", "Испания", "Канада", "Бразилия", "Австралия"};

    private int[] flags = {
            R.drawable.flag_russia,
            R.drawable.flag_usa,
            R.drawable.flag_france,
            R.drawable.flag_germany,
            R.drawable.flag_japan,
            R.drawable.flag_china,
            R.drawable.flag_uk,
            R.drawable.flag_italy,
            R.drawable.flag_spain,
            R.drawable.flag_canada,
            R.drawable.flag_brazil,
            R.drawable.flag_australia
    };

    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_game);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRepository = new UserRepository(this);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        initViews();
        setupGame();
        generateQuestion();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        ivFlag = findViewById(R.id.ivFlag);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnHint = findViewById(R.id.btnHint);
        btnSkip = findViewById(R.id.btnSkip);
        btnBack = findViewById(R.id.btnBack);

        // Получаем текущий счет пользователя из SharedPreferences
        currentScore = sharedPreferences.getInt("user_score", 0);
        updateScoreDisplay();
        updateLevelDisplay();

        // Обработчик кнопки назад
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProgress();
                finish();
            }
        });

        // Обработчик кнопки подсказки
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });

        // Обработчик кнопки пропуска
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQuestion();
                Toast.makeText(FlagGameActivity.this, "Вопрос пропущен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGame() {
        View.OnClickListener answerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                checkAnswer(clickedButton.getText().toString());
            }
        };

        btnOption1.setOnClickListener(answerListener);
        btnOption2.setOnClickListener(answerListener);
        btnOption3.setOnClickListener(answerListener);
        btnOption4.setOnClickListener(answerListener);
    }

    private void generateQuestion() {
        Random random = new Random();
        int correctAnswerIndex = random.nextInt(countries.length);

        // Устанавливаем флаг и вопрос
        tvQuestion.setText("Угадайте флаг этой страны:");

        // Устанавливаем изображение флага
        ivFlag.setImageResource(flags[correctAnswerIndex]);

        correctAnswer = countries[correctAnswerIndex];

        // Создаем варианты ответов
        String[] options = new String[4];
        options[0] = countries[correctAnswerIndex]; // Правильный ответ

        // Заполняем остальные варианты случайными странами
        for (int i = 1; i < 4; i++) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(countries.length);
            } while (randomIndex == correctAnswerIndex || contains(options, countries[randomIndex]));
            options[i] = countries[randomIndex];
        }

        // Перемешиваем варианты
        shuffleArray(options);

        // Устанавливаем текст кнопок
        btnOption1.setText(options[0]);
        btnOption2.setText(options[1]);
        btnOption3.setText(options[2]);
        btnOption4.setText(options[3]);

        // Сбрасываем цвета кнопок
        resetButtonColors();
    }

    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item != null && item.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void shuffleArray(String[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void checkAnswer(String selectedAnswer) {
        boolean isCorrect = selectedAnswer.equals(correctAnswer);

        // Подсвечиваем кнопки
        highlightAnswer(selectedAnswer, isCorrect);

        if (isCorrect) {
            currentScore += 10;
            questionsAnswered++;

            // Повышаем уровень каждые 5 правильных ответов
            if (questionsAnswered % 5 == 0) {
                currentLevel++;
                updateLevelDisplay();
                Toast.makeText(this, "Поздравляем! Новый уровень: " + currentLevel, Toast.LENGTH_SHORT).show();
            }

            updateTotalScore();
            Toast.makeText(this, "Правильно! +10 очков", Toast.LENGTH_SHORT).show();

            // Задержка перед следующим вопросом
            btnOption1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    generateQuestion();
                }
            }, 1000);
        } else {
            Toast.makeText(this, "Неправильно! Правильный ответ: " + correctAnswer, Toast.LENGTH_SHORT).show();

            // Задержка перед следующим вопросом
            btnOption1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    generateQuestion();
                }
            }, 2000);
        }

        updateScoreDisplay();
    }

    private void highlightAnswer(String selectedAnswer, boolean isCorrect) {
        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};

        for (Button button : buttons) {
            if (button.getText().toString().equals(selectedAnswer)) {
                if (isCorrect) {
                    button.setBackgroundResource(R.drawable.button_answer_correct);
                } else {
                    button.setBackgroundResource(R.drawable.button_answer_wrong);
                }
            } else if (button.getText().toString().equals(correctAnswer)) {
                button.setBackgroundResource(R.drawable.button_answer_correct);
            }
        }
    }

    private void resetButtonColors() {
        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (Button button : buttons) {
            button.setBackgroundResource(R.drawable.button_answer_normal);
        }
    }

    private void showHint() {
        // Простая подсказка - показываем первую букву страны
        if (correctAnswer != null && correctAnswer.length() > 0) {
            String hint = "Первая буква: " + correctAnswer.charAt(0);
            Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
        }
    }

    protected void updateScoreDisplay() {
        tvScore.setText(String.valueOf(currentScore));
    }

    protected void updateLevelDisplay() {
        tvLevel.setText(String.valueOf(currentLevel));
    }

    protected void updateTotalScore() {
        if (currentUserId != -1) {
            try {
                userRepository.updateUserScore(currentUserId, 10);

                // Обновляем SharedPreferences
                int totalScore = sharedPreferences.getInt("user_score", 0) + 10;
                sharedPreferences.edit().putInt("user_score", totalScore).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void saveProgress() {
        // Сохраняем прогресс при выходе из игры
        if (currentUserId != -1 && currentScore > 0) {
            try {
                userRepository.updateUserScore(currentUserId, currentScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveProgress();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        saveProgress();
        super.onDestroy();
    }
}
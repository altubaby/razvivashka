package com.example.razvivashka.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.razvivashka.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LogicGameActivity extends BaseGameActivity {

    private TextView tvQuestion, tvScore, tvLevel;
    private Button[] optionButtons = new Button[4];
    private Button btnHint, btnBack;

    private int currentLevel = 1;
    private String correctAnswer;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic_game);

        initViews();
        generatePuzzle();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);

        optionButtons[0] = findViewById(R.id.btnOption1);
        optionButtons[1] = findViewById(R.id.btnOption2);
        optionButtons[2] = findViewById(R.id.btnOption3);
        optionButtons[3] = findViewById(R.id.btnOption4);

        btnHint = findViewById(R.id.btnHint);
        btnBack = findViewById(R.id.btnBack);

        for (Button btn : optionButtons) {
            btn.setOnClickListener(v -> checkAnswer(((Button) v).getText().toString()));
        }

        btnHint.setOnClickListener(v -> showHint());
        btnBack.setOnClickListener(v -> finish());

        updateScoreDisplay();
        updateLevelDisplay();
    }

    private void generatePuzzle() {
        int puzzleType = random.nextInt(3); // 3 типа головоломок

        switch (puzzleType) {
            case 0:
                generateNumberSequence();
                break;
            case 1:
                generatePattern();
                break;
            case 2:
                generateAnalogy();
                break;
        }
    }

    private void generateNumberSequence() {
        String[] sequences = {
                "2, 4, 6, 8, ?", "1, 4, 9, 16, ?", "5, 10, 15, 20, ?",
                "1, 1, 2, 3, 5, ?", "3, 6, 12, 24, ?", "10, 8, 6, 4, ?"
        };
        int[] answers = {10, 25, 25, 8, 48, 2};

        int index = random.nextInt(sequences.length);
        tvQuestion.setText("Продолжите последовательность:\n" + sequences[index]);
        correctAnswer = String.valueOf(answers[index]);

        generateOptions(answers[index]);
    }

    private void generatePattern() {
        String[][] patterns = {
                {"▲ ▼ ▲ ▼ ?", "▲"},
                {"● ● ○ ○ ● ● ○ ○ ?", "●"},
                {"♠ ♥ ♦ ♣ ♠ ♥ ♦ ?", "♣"},
                {"1A 2B 3C 4D ?", "5E"},
                {"XX O XX O ?", "XX"}
        };

        int index = random.nextInt(patterns.length);
        tvQuestion.setText("Найдите закономерность:\n" + patterns[index][0]);
        correctAnswer = patterns[index][1];

        generateOptionsForPattern(patterns[index][1], new String[]{"▲", "▼", "●", "○", "♠", "♥", "♦", "♣", "XX", "O", "5E", "4E"});
    }

    private void generateAnalogy() {
        String[][] analogies = {
                {"Яблоко → Фрукт", "Морковь → ?", "Овощ"},
                {"Солнце → Желтый", "Трава → ?", "Зеленый"},
                {"Птица → Гнездо", "Собака → ?", "Будка"},
                {"Утро → Завтрак", "Вечер → ?", "Ужин"},
                {"Холодно → Шуба", "Дождь → ?", "Зонт"}
        };

        int index = random.nextInt(analogies.length);
        tvQuestion.setText("Найдите аналогию:\n" + analogies[index][0] + "\n" + analogies[index][1]);
        correctAnswer = analogies[index][2];

        generateOptionsForPattern(analogies[index][2], new String[]{"Овощ", "Фрукт", "Зеленый", "Красный", "Будка", "Дом", "Ужин", "Обед", "Зонт", "Пальто"});
    }

    private void generateOptions(int correctAnswer) {
        List<String> options = new ArrayList<>();
        options.add(String.valueOf(correctAnswer));

        // Добавляем неправильные варианты
        while (options.size() < 4) {
            int wrongAnswer = correctAnswer + random.nextInt(10) - 5;
            if (wrongAnswer != correctAnswer && !options.contains(String.valueOf(wrongAnswer))) {
                options.add(String.valueOf(wrongAnswer));
            }
        }

        Collections.shuffle(options);
        setOptions(options);
    }

    private void generateOptionsForPattern(String correct, String[] allOptions) {
        List<String> options = new ArrayList<>();
        options.add(correct);

        // Добавляем неправильные варианты
        List<String> wrongOptions = new ArrayList<>(Arrays.asList(allOptions));
        wrongOptions.remove(correct);
        Collections.shuffle(wrongOptions);

        for (int i = 0; i < 3; i++) {
            options.add(wrongOptions.get(i));
        }

        Collections.shuffle(options);
        setOptions(options);
    }

    private void setOptions(List<String> options) {
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options.get(i));
        }
    }

    private void checkAnswer(String selectedAnswer) {
        if (selectedAnswer.equals(correctAnswer)) {
            // ИСПРАВЛЕНО: используем addScore вместо updateTotalScore
            addScore(15);

            if (gameScore % 75 == 0) {
                currentLevel++;
                updateLevelDisplay();
                Toast.makeText(this, "Новый уровень логики!", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(this, "Верно! +15 очков", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неверно! Попробуйте еще", Toast.LENGTH_SHORT).show();
        }

        updateScoreDisplay();
        generatePuzzle();
    }

    private void showHint() {
        String hint = "";
        if (correctAnswer.length() == 1) {
            hint = "Первая буква: " + correctAnswer.charAt(0);
        } else {
            hint = "Подсказка: подумайте о закономерности";
        }
        Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
    }

    // ИСПРАВЛЕНО: изменен модификатор доступа с private на protected
    @Override
    protected void updateScoreDisplay() {
        tvScore.setText("Очки: " + gameScore);
    }

    private void updateLevelDisplay() {
        tvLevel.setText("Ур: " + currentLevel);
    }

    @Override
    protected void onDestroy() {
        saveGameProgress();
        super.onDestroy();
    }
}
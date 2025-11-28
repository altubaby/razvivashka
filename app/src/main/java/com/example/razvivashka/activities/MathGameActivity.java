package com.example.razvivashka.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.razvivashka.R;

import java.util.Random;

public class MathGameActivity extends BaseGameActivity {

    private TextView tvQuestion, tvScore, tvLevel;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private int correctAnswer;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game);

        initViews();
        generateQuestion();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        View.OnClickListener answerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                checkAnswer(Integer.parseInt(btn.getText().toString()));
            }
        };

        btnOption1.setOnClickListener(answerListener);
        btnOption2.setOnClickListener(answerListener);
        btnOption3.setOnClickListener(answerListener);
        btnOption4.setOnClickListener(answerListener);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void generateQuestion() {
        Random random = new Random();
        int a, b;
        String operator;
        int difficulty = Math.min(currentLevel, 5); // Ограничиваем сложность

        switch (difficulty) {
            case 1: // Сложение
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                correctAnswer = a + b;
                operator = "+";
                break;
            case 2: // Вычитание
                a = random.nextInt(20) + 1;
                b = random.nextInt(a) + 1;
                correctAnswer = a - b;
                operator = "-";
                break;
            case 3: // Умножение
                a = random.nextInt(10) + 1;
                b = random.nextInt(10) + 1;
                correctAnswer = a * b;
                operator = "×";
                break;
            case 4: // Деление
                b = random.nextInt(5) + 2;
                correctAnswer = random.nextInt(5) + 1;
                a = b * correctAnswer;
                operator = "÷";
                break;
            default: // Смешанные операции
                int op = random.nextInt(4);
                switch (op) {
                    case 0:
                        a = random.nextInt(20) + 1;
                        b = random.nextInt(20) + 1;
                        correctAnswer = a + b;
                        operator = "+";
                        break;
                    case 1:
                        a = random.nextInt(30) + 1;
                        b = random.nextInt(a) + 1;
                        correctAnswer = a - b;
                        operator = "-";
                        break;
                    case 2:
                        a = random.nextInt(12) + 1;
                        b = random.nextInt(12) + 1;
                        correctAnswer = a * b;
                        operator = "×";
                        break;
                    default:
                        b = random.nextInt(6) + 2;
                        correctAnswer = random.nextInt(6) + 1;
                        a = b * correctAnswer;
                        operator = "÷";
                        break;
                }
        }

        tvQuestion.setText(a + " " + operator + " " + b + " = ?");

        // Создаем варианты ответов
        int[] options = new int[4];
        options[0] = correctAnswer;

        for (int i = 1; i < 4; i++) {
            int wrongAnswer;
            do {
                wrongAnswer = correctAnswer + random.nextInt(10) - 5;
            } while (wrongAnswer == correctAnswer || contains(options, wrongAnswer) || wrongAnswer < 0);
            options[i] = wrongAnswer;
        }

        // Перемешиваем
        shuffleArray(options);

        btnOption1.setText(String.valueOf(options[0]));
        btnOption2.setText(String.valueOf(options[1]));
        btnOption3.setText(String.valueOf(options[2]));
        btnOption4.setText(String.valueOf(options[3]));

        updateScoreDisplay();
    }

    private boolean contains(int[] array, int value) {
        for (int item : array) {
            if (item == value) return true;
        }
        return false;
    }

    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void checkAnswer(int selectedAnswer) {
        if (selectedAnswer == correctAnswer) {
            gameScore += 10;
            updateTotalScore(10);
            Toast.makeText(this, "Правильно! +10 очков", Toast.LENGTH_SHORT).show();

            // Каждые 5 правильных ответов повышаем уровень
            if (gameScore % 50 == 0) {
                currentLevel++;
                tvLevel.setText("Ур: " + currentLevel);
                Toast.makeText(this, "Новый уровень! Сложность увеличена", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Неправильно! Ответ: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        generateQuestion();
    }

    protected void updateScoreDisplay() {
        tvScore.setText("Очки: " + gameScore);
    }

    @Override
    protected void onDestroy() {
        saveGameProgress();
        super.onDestroy();
    }
}
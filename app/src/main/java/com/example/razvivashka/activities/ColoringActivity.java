package com.example.razvivashka.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.razvivashka.R;

public class ColoringActivity extends BaseGameActivity {

    private ImageView coloringArea;
    private LinearLayout colorPalette;
    private Button btnClear, btnNewImage, btnBack;

    private Bitmap originalBitmap;
    private Bitmap mutableBitmap;
    private Canvas drawCanvas;
    private Paint drawPaint;
    private Path drawPath;
    private int currentColor = Color.RED;
    private float brushSize = 40f; // Увеличим размер кисти

    // Простые раскраски
    private int[] coloringImages = {
            R.drawable.coloring_cat,
            R.drawable.coloring_house,
            R.drawable.coloring_tree,
            R.drawable.coloring_car
    };
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        initViews();
        setupDrawing();
        loadColoringImage();
    }

    private void initViews() {
        coloringArea = findViewById(R.id.coloringArea);
        colorPalette = findViewById(R.id.colorPalette);
        btnClear = findViewById(R.id.btnClear);
        btnNewImage = findViewById(R.id.btnNewImage);
        btnBack = findViewById(R.id.btnBack);

        // Цветовая палитра
        setupColorPalette();

        btnClear.setOnClickListener(v -> clearCanvas());
        btnNewImage.setOnClickListener(v -> loadNextImage());
        btnBack.setOnClickListener(v -> finish());

        // Обработчик касаний для ImageView
        coloringArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouch(event);
            }
        });
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(currentColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void setupColorPalette() {
        int[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.MAGENTA, Color.CYAN, Color.BLACK, Color.GRAY
        };

        for (int color : colors) {
            View colorView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
            params.setMargins(8, 0, 8, 0);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(color);
            colorView.setOnClickListener(v -> {
                currentColor = color;
                drawPaint.setColor(currentColor);
                highlightSelectedColor(colorView);
            });
            colorPalette.addView(colorView);
        }
    }

    private void highlightSelectedColor(View selectedView) {
        for (int i = 0; i < colorPalette.getChildCount(); i++) {
            View child = colorPalette.getChildAt(i);
            child.setAlpha(child == selectedView ? 1.0f : 0.6f);
        }
    }

    private void loadColoringImage() {
        if (currentImageIndex < coloringImages.length) {
            // Загружаем оригинальное изображение
            originalBitmap = BitmapFactory.decodeResource(getResources(), coloringImages[currentImageIndex]);

            // Создаем mutable bitmap для рисования
            mutableBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(mutableBitmap);

            // Рисуем оригинальное изображение как фон
            drawCanvas.drawBitmap(originalBitmap, 0, 0, null);

            // Устанавливаем bitmap в ImageView
            coloringArea.setImageBitmap(mutableBitmap);
            coloringArea.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    private void loadNextImage() {
        currentImageIndex = (currentImageIndex + 1) % coloringImages.length;
        loadColoringImage();
        // ИСПРАВЛЕНО: используем addScore вместо updateTotalScore
        addScore(5);
        Toast.makeText(this, "Новая раскраска! +5 очков", Toast.LENGTH_SHORT).show();
    }

    private void clearCanvas() {
        if (mutableBitmap != null && originalBitmap != null) {
            mutableBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(mutableBitmap);
            drawCanvas.drawBitmap(originalBitmap, 0, 0, null);
            coloringArea.setImageBitmap(mutableBitmap);
        }
    }

    private boolean handleTouch(MotionEvent event) {
        if (mutableBitmap == null) return false;

        // Получаем координаты касания относительно ImageView
        float touchX = event.getX();
        float touchY = event.getY();

        // Рассчитываем масштаб и смещение изображения
        ImageView imageView = (ImageView) coloringArea;
        float[] point = getBitmapCoordinatesFromTouch(imageView, touchX, touchY);

        if (point == null) return false;

        float bitmapX = point[0];
        float bitmapY = point[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(bitmapX, bitmapY);
                return true;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(bitmapX, bitmapY);
                drawCanvas.drawPath(drawPath, drawPaint);
                coloringArea.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();

                // Начисляем очки за рисование
                // ИСПРАВЛЕНО: используем addScore
                addScore(1);
                return true;
            default:
                return false;
        }
    }

    // Метод для преобразования координат касания в координаты bitmap
    private float[] getBitmapCoordinatesFromTouch(ImageView imageView, float touchX, float touchY) {
        if (mutableBitmap == null) return null;

        // Получаем границы bitmap внутри ImageView
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];
        float transX = f[Matrix.MTRANS_X];
        float transY = f[Matrix.MTRANS_Y];

        // Рассчитываем координаты в bitmap
        float bitmapX = (touchX - transX) / scaleX;
        float bitmapY = (touchY - transY) / scaleY;

        // Проверяем, что координаты внутри bitmap
        if (bitmapX >= 0 && bitmapX <= mutableBitmap.getWidth() &&
                bitmapY >= 0 && bitmapY <= mutableBitmap.getHeight()) {
            return new float[]{bitmapX, bitmapY};
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        saveGameProgress();
        super.onDestroy();
    }
}
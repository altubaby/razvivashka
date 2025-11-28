package com.example.razvivashka.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.razvivashka.database.dao.UserDao;
import com.example.razvivashka.model.User;

@Database(entities = {User.class}, version = 2, exportSchema = false) // Увеличили версию с 1 до 2
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "razvivashka_database"
                            )
                            .fallbackToDestructiveMigration() // Это удалит старую базу и создаст новую
                            .allowMainThreadQueries() // Для тестирования
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
package com.example.razvivashka.repository;

import android.content.Context;

import com.example.razvivashka.database.AppDatabase;
import com.example.razvivashka.database.dao.UserDao;
import com.example.razvivashka.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
    }

    public long registerUser(User user) {
        return userDao.insertUser(user);
    }

    public User loginUser(String email, String password) {
        return userDao.loginUser(email, password);
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    // ОБНОВЛЯЕМ метод - теперь он ДОБАВЛЯЕТ очки к существующим
    public void updateUserScore(int userId, int points) {
        executor.execute(() -> {
            try {
                userDao.updateUserScore(userId, points);
                System.out.println("Добавлено " + points + " очков пользователю " + userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Новый метод для получения текущего счета
    public int getUserScore(int userId) {
        try {
            return userDao.getUserScore(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<User> getTopUsers() {
        try {
            return userDao.getTopUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUser(User user) {
        executor.execute(() -> {
            try {
                userDao.updateUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
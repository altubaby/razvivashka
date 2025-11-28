package com.example.razvivashka.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.razvivashka.model.User;

import java.util.List;

// UserDao.java
@Dao
public interface UserDao {

    @Insert
    long insertUser(User user); // у вас уже есть этот метод

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User loginUser(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    // ИСПРАВЛЯЕМ метод обновления счета - ДОБАВЛЯЕМ очки
    @Query("UPDATE users SET total_score = total_score + :points WHERE id = :userId")
    void updateUserScore(int userId, int points);

    @Query("SELECT * FROM users ORDER BY total_score DESC LIMIT 10")
    List<User> getTopUsers();

    @Update
    void updateUser(User user);

    // Добавляем метод для получения текущего счета
    @Query("SELECT total_score FROM users WHERE id = :userId")
    int getUserScore(int userId);
}
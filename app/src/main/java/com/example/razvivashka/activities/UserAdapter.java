package com.example.razvivashka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.razvivashka.R;
import com.example.razvivashka.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private String currentUserName;
    private Context context;

    public UserAdapter(List<User> userList, String currentUserName) {
        this.userList = userList;
        this.currentUserName = currentUserName;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_rating, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, position + 1);

        // Подсвечиваем текущего пользователя
        if (user.getUsername().equals(currentUserName)) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.purple_200)
            );
        } else {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.transparent)
            );
        }

        // Разные цвета для топ-3 мест
        int rank = position + 1;
        int rankColor;
        if (rank == 1) {
            rankColor = R.color.warning_yellow; // золото
        } else if (rank == 2) {
            rankColor = R.color.text_muted; // серебро
        } else if (rank == 3) {
            rankColor = R.color.teal_700; // бронза
        } else {
            rankColor = R.color.purple_500; // остальные
        }

        holder.rankText.setBackgroundResource(rankColor);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView rankText, nameText, scoreText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rank_text);
            nameText = itemView.findViewById(R.id.name_text);
            scoreText = itemView.findViewById(R.id.score_text);
        }

        public void bind(User user, int rank) {
            rankText.setText(String.valueOf(rank));
            nameText.setText(user.getUsername());
            scoreText.setText(user.getTotalScore() + " очков");
        }
    }
}
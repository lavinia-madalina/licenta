package com.example.licenta;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public TaskAdapter(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.textViewTitle.setText(task.getName());
        holder.textViewDescription.setText(task.getDescription());

        // Convertim Timestamp-ul în string formatat
        if (task.getDate() != null) {
            Date taskDate = task.getDate().toDate();
            holder.textViewDate.setText(dateFormat.format(taskDate));
        } else {
            holder.textViewDate.setText("Fără dată");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewDate = itemView.findViewById(R.id.textViewTaskDate);
        }
    }
}

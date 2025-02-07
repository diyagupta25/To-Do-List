package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button add;
    AlertDialog dialog;
    LinearLayout layout;
    ScrollView scrollView;
    SharedPreferences sharedPreferences;
    ArrayList<String> taskList;

    private static final String PREFS_NAME = "ToDoPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        layout = findViewById(R.id.container);
        scrollView = findViewById(R.id.scrollView);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        taskList = loadTasks();  // Load saved tasks

        buildDialog();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // Add previously saved tasks to the layout
        for (String task : taskList) {
            addCard(task, false);
        }
    }

    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Enter your Task")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskText = name.getText().toString();
                        if (!taskText.isEmpty()) {
                            taskList.add(taskText);
                            saveTasks();
                            addCard(taskText, true);
                        }
                        name.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name.setText("");
                    }
                });
        dialog = builder.create();
    }

    private void addCard(final String name, boolean scrollToBottom) {
        final View view = getLayoutInflater().inflate(R.layout.card, null);

        TextView nameView = view.findViewById(R.id.name);
        Button delete = view.findViewById(R.id.done);
        nameView.setText(name);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);
                taskList.remove(name);
                saveTasks();
            }
        });

        layout.addView(view);

        // Scroll to bottom if a new task is added
        if (scrollToBottom) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(taskList);
        editor.putString(TASKS_KEY, jsonArray.toString());
        editor.apply();
    }

    private ArrayList<String> loadTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        String tasksJson = sharedPreferences.getString(TASKS_KEY, "[]");

        try {
            JSONArray jsonArray = new JSONArray(tasksJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                tasks.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}

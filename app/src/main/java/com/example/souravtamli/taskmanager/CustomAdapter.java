package com.example.souravtamli.taskmanager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private  final ArrayList<String> todoTask;
    private  final ArrayList<String> memberEmail;



    public CustomAdapter(Activity context, ArrayList<String> todoTask, ArrayList<String> memberEmail) {
        super(context, R.layout.todo_list, todoTask);
        this.context = context;
        this.todoTask = todoTask;
        this.memberEmail = memberEmail;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.todo_list, null, true);
        TextView todo = rowView.findViewById(R.id.todo);
        TextView membersEmail = rowView.findViewById(R.id.memberEmail);
        todo.setText(todoTask.get(position));
        membersEmail.setText(memberEmail.get(position));

        return rowView;
    }
}

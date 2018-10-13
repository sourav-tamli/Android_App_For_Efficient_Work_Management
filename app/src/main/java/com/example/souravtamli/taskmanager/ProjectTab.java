package com.example.souravtamli.taskmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectTab extends AppCompatActivity {

    String currentUserUid, currentProjectWindow, currentUserEmail, currentProjectRefUri ;
    ArrayList<String> projectTodos = new ArrayList<String>();
    ArrayList<String> email = new ArrayList<String>();
    ImageButton addTodo;
    ListView memberTodos;
    TextView memberEmail;
    //dataBase refferences
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference userRef = rootRef.child("Users");
    DatabaseReference projectRef;
    DatabaseReference currentProjectRef;
    DatabaseReference todosRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_tab);
        Intent intent = getIntent();
        addTodo = (ImageButton) findViewById(R.id.addTodo);
        memberTodos = (ListView) findViewById(R.id.memberTodos);
        //final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.todo_list, R.id.todo, projectTodos);
       final CustomAdapter customAdapter = new CustomAdapter(ProjectTab.this, projectTodos, email);
        memberTodos.setAdapter(customAdapter);
        memberEmail = (TextView) findViewById(R.id.memberEmail);
        //memberTodos.setAdapter(mAdapter);
        currentUserUid = intent.getStringExtra("currentUserUid");
        currentProjectWindow = intent.getStringExtra("tappedProjectName");
        currentUserEmail = intent.getStringExtra("currentUserEmail");
        currentProjectRefUri = intent.getStringExtra("obj");
        projectRef = userRef.child(currentUserUid).child("Project").child(currentProjectWindow);
        currentProjectRef = database.getReferenceFromUrl(currentProjectRefUri);
        todosRef = currentProjectRef.child("ToDo");


        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getApplicationContext());
                //String todo = editText.getText().toString();
                AlertDialog dialog = new AlertDialog.Builder(ProjectTab.this)
                        .setTitle("Your ToDo")
                        .setMessage("Enter your new ToDo")
                        .setView(editText)
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String todo = editText.getText().toString();
                                currentProjectRef.child("ToDo").child(todo).setValue(currentUserEmail);
                                customAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }

        });

        todosRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String currentTodos = dataSnapshot.getKey().toString();
                String members_email = dataSnapshot.getValue().toString();
                projectTodos.add(currentTodos);
                email.add(members_email);
                customAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        currentProjectRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue().toString();
                Log.i("MMM", value);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMembers:
                final EditText editText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add a new project Member")
                    .setMessage("Enter the registered mail-id of new project member")
                    .setView(editText)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String newMemberEmail = String.valueOf(editText.getText());
                            final Query query = userRef.orderByChild("Email").equalTo(newMemberEmail);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        //Toast.makeText(getApplicationContext(), "EXITS", Toast.LENGTH_SHORT).show();
                                        String value = dataSnapshot.getValue().toString();
                                        try {
                                            JSONObject object = new JSONObject(value);
                                            JSONArray jarray = object.names();
                                            String memberUid = jarray.getString(0);
                                            userRef.child(memberUid).child("Project").child(currentProjectWindow).setValue(currentProjectWindow);
                                            userRef.child(memberUid).child("Project").child(currentProjectWindow).child("Admins").setValue(currentUserEmail);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
            return true;

            case R.id.chatRoom:
                Intent chattingWindow = new Intent(getApplicationContext(), ChattingWindow.class);
                chattingWindow.putExtra("currentUserEmail", currentUserEmail);
                chattingWindow.putExtra("currentUserUid", currentUserUid);
                chattingWindow.putExtra("projectRef", String.valueOf(currentProjectRef));
                startActivity(chattingWindow);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }



    }





}

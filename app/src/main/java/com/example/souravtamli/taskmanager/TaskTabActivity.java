package com.example.souravtamli.taskmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Iterator;
import java.util.Map;

public class TaskTabActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String task;
    String currentUserEmail, currentUserUid;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> arrayList = new ArrayList<String>();
    private DrawerLayout mDrawerlayout;
    ListView projectList;
    ImageButton addProject;
    Toolbar toolbar;
    //Firebase Database refferences
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference userRef = rootRef.child("Users");
    DatabaseReference projectRef;
    DatabaseReference currentProjectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_tab);
        projectList = (ListView) findViewById(R.id.memberTodos);
        addProject = (ImageButton) findViewById(R.id.addProject);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        // Receiving data from the previous Intent
        Intent intent = getIntent();
        currentUserEmail = intent.getStringExtra("email");
        currentUserUid = intent.getStringExtra("uid");
        projectRef = userRef.child(currentUserUid).child("Project");

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeader = navigationView.getHeaderView(0);
        TextView userEmail = (TextView) navigationHeader.findViewById(R.id.userEmail);
        TextView userUid = (TextView) navigationHeader.findViewById(R.id.userUid);
        userEmail.setText(currentUserEmail);
        userUid.setText(currentUserUid);

        projectRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getKey().toString();
                //Log.i("MMM",value);  for debugging purpose only, programmer can change the "tag" according to choice
                arrayList.add(value);
                loadProjectList();
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

        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getApplicationContext());
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(TaskTabActivity.this, R.style.myDialog))
                        .setTitle("Add new project")
                        .setMessage("What is the name of your project")
                        .setView(editText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String projectName = String.valueOf(editText.getText());
                                projectRef.child(projectName).setValue(projectName);
                                projectRef.child(projectName).child("Admins").setValue(currentUserEmail);

                                arrayList.add(projectName);
                                //mAdapter.notifyDataSetChanged();
                                loadProjectList();

                            }
                        })
                        .setNegativeButton("Cancel", null )
                        .create();
                dialog.show();
            }
        });


    }

    @Override
    public void setSupportActionBar(@Nullable android.support.v7.widget.Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    //add icon to menu bar

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    public void enter(View view){
        int index = projectList.getPositionForView(view);
        task = mAdapter.getItem(index);
        currentProjectRef = userRef.child(currentUserUid).child("Project").child(task);
        currentProjectRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String adminEmailId = dataSnapshot.getValue().toString();
                //Log.i("MMM",adminEmailId);   for debugging purpose only, programmer can change the "tag" according to choice
                if (adminEmailId.equals(currentUserEmail)) {
                    //Log.i("MMM", "ENTER OWN");  for debugging purpose only, programmer can change the "tag" according to choice
                    currentProjectRef = userRef.child(currentUserUid).child("Project").child(task);
                    Intent intent = new Intent(TaskTabActivity.this, ProjectTab.class);
                    intent.putExtra("tappedProjectName", task);
                    intent.putExtra("currentUserUid", currentUserUid);
                    intent.putExtra("currentUserEmail", currentUserEmail);
                    intent.putExtra("obj", String.valueOf(currentProjectRef));
                    startActivity(intent);
                } else {
                    final Query query = userRef.orderByChild("Email").equalTo(adminEmailId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String adminUid = null;
                            if (dataSnapshot.exists()) {
                                String value = dataSnapshot.getValue().toString();
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                    adminUid = childDataSnapshot.getKey();
                                }

                                currentProjectRef = userRef.child(adminUid).child("Project").child(task);
                                Log.i("MMM", String.valueOf(currentProjectRef));
                                Intent intent = new Intent(TaskTabActivity.this, ProjectTab.class);
                                intent.putExtra("tappedProjectName", task);
                                intent.putExtra("currentUserUid", currentUserUid);
                                intent.putExtra("currentUserEmail", currentUserEmail);
                                intent.putExtra("obj", String.valueOf(currentProjectRef));
                                startActivity(intent);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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

        /*Intent intent = new Intent(TaskTabActivity.this, ProjectTab.class);
        intent.putExtra("tappedProjectName", task);
        intent.putExtra("currentUserUid", currentUserUid);
        intent.putExtra("currentUserEmail", currentUserEmail);
        intent.putExtra("obj", String.valueOf(currentProjectRef));
        startActivity(intent);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerlayout.openDrawer(GravityCompat.START);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadProjectList() {
        // TODO: 03-10-2018
        if (mAdapter == null) {
        mAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.projectName, arrayList);
        projectList.setAdapter(mAdapter);
        Log.i("TAG", "EMPTY");
        } else{
            //mAdapter.clear();
            //mAdapter.addAll(arrayList);
            for (int i = 0 ; i < arrayList.size(); i++){
                Log.i("TAG", "MSG " +arrayList.get(i));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_info:

                break;
            case R.id.nav_share:

                break;
            case R.id.nav_update:

                break;
            case R.id.nav_signout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(TaskTabActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Log out successful", Toast.LENGTH_SHORT).show();
                break;
        }
        mDrawerlayout.closeDrawer(GravityCompat.START);

        return true;
    }



   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addProject :
                final EditText editText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add new project")
                        .setMessage("What is the name of your project")
                        .setView(editText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String projectName = String.valueOf(editText.getText());
                                projectRef.child(projectName).setValue(projectName);
                                projectRef.child(projectName).child("Admins").setValue(currentUserEmail);

                                arrayList.add(projectName);
                                //mAdapter.notifyDataSetChanged();
                                loadProjectList();

                            }
                        })
                        .setNegativeButton("Cancel", null )
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


    // TODO: 06-10-2018
    // Log.i("MMM",  value);

    /* JSONObject jsonObject = new JSONObject(value);
       Log.i("MMM", "ENTER OWN");
       JSONArray jsonArray = jsonObject.names();
       adminUid = jsonArray.getString(0);*/


}

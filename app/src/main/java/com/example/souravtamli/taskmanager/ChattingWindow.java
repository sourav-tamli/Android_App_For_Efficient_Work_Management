package com.example.souravtamli.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChattingWindow extends AppCompatActivity {
    String currentUserEmail, currentUserUid, projectRefUri;
    ListView chatList;
    ImageButton sendButton;
    EditText enterMessage;
    ArrayList<String> sendMsgList = new ArrayList<String>();
    ArrayList<String> senderEmail = new ArrayList<String>();
//    ArrayList<String> receiveMsgList = new ArrayList<String>();
//    ArrayList<String> receivedMsgEmail = new ArrayList<String>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference userRef = rootRef.child("Users");
    DatabaseReference projectRef, chattingNodeRef;
    CustomAdapterForChattingWindow customAdapterForChattingWindow;
    CustomAdapterForReceivedMessages customAdapterForReceivedMessages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_window);
        chatList = (ListView) findViewById(R.id.chatList);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        enterMessage = (EditText) findViewById(R.id.enterMessage);
        ScrollView scrollView;

       // customAdapterForReceivedMessages = new CustomAdapterForReceivedMessages(ChattingWindow.this, receiveMsgList, receivedMsgEmail);
        Intent intent = getIntent();
        currentUserEmail = intent.getStringExtra("currentUserEmail");
        currentUserUid = intent.getStringExtra("currentUserUid");
        projectRefUri = intent.getStringExtra("projectRef");
        projectRef = database.getReferenceFromUrl(projectRefUri);
        chattingNodeRef = projectRef.child("Messages");
        customAdapterForChattingWindow = new CustomAdapterForChattingWindow(ChattingWindow.this, sendMsgList, senderEmail, currentUserEmail);
        chatList.setAdapter(customAdapterForChattingWindow);

        //setting a "childEventListner" for "chattingNodeRef"
        chattingNodeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    String email = (String) ((DataSnapshot)iterator.next()).getValue();       // To retrieve the email for the senders
                    String msg = (String) ((DataSnapshot)iterator.next()).getValue();        //  To retrieve the actual messages
                    sendMsgList.add(msg);
                    senderEmail.add(email);
                    customAdapterForChattingWindow.notifyDataSetChanged();
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

        // setting a onClick lister for the ImageButton
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMessage = enterMessage.getText().toString();
                if (textMessage == null) {
                    Toast.makeText(getApplicationContext(), "Please enter your message", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String temp_key = chattingNodeRef.push().getKey();
                    chattingNodeRef.updateChildren(map);

                    DatabaseReference root = chattingNodeRef.child(temp_key);
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("MSG", textMessage);
                    map1.put("Email", currentUserEmail);
                    root.updateChildren(map1);

                }
            }
        });



    }
































}





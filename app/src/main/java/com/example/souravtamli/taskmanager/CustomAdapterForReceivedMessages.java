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

public class CustomAdapterForReceivedMessages extends ArrayAdapter<String> {

    private final Activity context;
    private  final ArrayList<String> receivedMessages;
    private  final ArrayList<String> receivedMessagesEmail;

    public  CustomAdapterForReceivedMessages(Activity context, ArrayList<String> receivedMessages, ArrayList<String> receivedMessagesEmail){

        super(context, R.layout.message_receive, receivedMessages);
        this.context = context;
        this.receivedMessages = receivedMessages;
        this.receivedMessagesEmail = receivedMessagesEmail;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.message_receive, null, true);
        TextView receiverMessages = rowView.findViewById(R.id.receivedMessage);
        TextView messagesMailAddress = rowView.findViewById(R.id.receiveMassageMailAddress);
        receiverMessages.setText(receivedMessages.get(position));
        messagesMailAddress.setText(receivedMessagesEmail.get(position));
        return rowView;
    }
}

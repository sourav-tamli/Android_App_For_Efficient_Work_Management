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

public class CustomAdapterForChattingWindow extends ArrayAdapter<String> {

    private final Activity context;
    private  final ArrayList<String> sendMessages;
    private  final ArrayList<String> sendersEmail;
    private final String email;

    public CustomAdapterForChattingWindow(Activity context, ArrayList<String> sendMessages, ArrayList<String> sendersEmail, String email) {
        super(context, R.layout.message_send, sendMessages);
        this.context = context;
        this.sendMessages = sendMessages;
        this.sendersEmail = sendersEmail;
        this.email = email;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = null;
            LayoutInflater inflater = context.getLayoutInflater();
            if ((sendersEmail.get(position)).equals(email)) {
                rowView = inflater.inflate(R.layout.message_send, null, true);
                TextView sendersMessage = rowView.findViewById(R.id.sendersMessage);
                TextView sendersMailAddress = rowView.findViewById(R.id.sendersMailAddress);
                sendersMessage.setText(sendMessages.get(position));
                sendersMailAddress.setText(sendersEmail.get(position));

            } else {
                rowView = inflater.inflate(R.layout.message_receive, null, true);
                TextView receiverMessages = rowView.findViewById(R.id.receivedMessage);
                TextView messagesMailAddress = rowView.findViewById(R.id.receiveMassageMailAddress);
                receiverMessages.setText(sendMessages.get(position));
                messagesMailAddress.setText(sendersEmail.get(position));
            }

        return rowView;
    }
}

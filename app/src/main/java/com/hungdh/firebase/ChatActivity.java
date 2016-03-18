package com.hungdh.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references
    private EditText mMessageView;
    private Button mBtnSend;

    private RecyclerView mListMessages;
    private FirebaseRecyclerAdapter<Chat, ChatHolder> mRecycleViewAdapter;


    // Firebase
    public Firebase firebase;
    private Firebase firebaseChatbox;
    private AuthData auth;

    private static final String TAG = ChatActivity.class.getSimpleName();

    // static
    public static final String NAME = "name";
    public static final String UID = "uid";
    public static final String EMAIL = "email";

    private String mEmail;
    private String mName;
    private String mUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle("Chatbox - Firebase");

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://hungdh-demo.firebaseio.com");
        firebaseChatbox = firebase.child("chatbox");

        auth = firebase.getAuth();

        getWidgetForm();

    }

    private void getWidgetForm() {
        mMessageView = (EditText) findViewById(R.id.message_input);
        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(this);

        // Setup list converstation
        mListMessages = (RecyclerView) findViewById(R.id.messagesList);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListMessages.setHasFixedSize(true);
        mListMessages.setLayoutManager(layout);

        mRecycleViewAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>
                (Chat.class, R.layout.mesage_item, ChatHolder.class, firebaseChatbox) {
            @Override
            protected void populateViewHolder(ChatHolder chatHolder, Chat chat, int i) {

                chatHolder.setText(chat.getText());

                chatHolder.setName(chat.getName());

                if (chat.getUid().equals(auth.getUid())) {
                    chatHolder.setIsSender(true);
                } else {
                    chatHolder.setIsSender(false);
                }

            }
        };

        mListMessages.setAdapter(mRecycleViewAdapter);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSend) {
            if (auth == null) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
                return;
            }
            sendMessage();
        }
    }

    private void sendMessage() {
        switch (auth.getProvider()) {
            case "password":
                mName = (String) firebase.getAuth().getProviderData().get("email");
                break;
            default:
                mName = (String) firebase.getAuth().getProviderData().get("displayName");
                break;
        }
        String message = mMessageView.getText().toString().trim();
        Chat chat = new Chat(mName, auth.getUid(), message);

        firebaseChatbox.push().setValue(chat, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(getApplicationContext(), "Send a message failed!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, firebaseError.toString());
                    return;
                }
                Toast.makeText(getApplicationContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        mMessageView.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebase.unauth();
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_about) {
            new MaterialDialog.Builder(this)
                    .title(R.string.about_title)
                    .content(R.string.about_content)
                    .positiveText(R.string.about_ok)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

}

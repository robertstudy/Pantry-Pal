package com.example.pantrypal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.java_websocket.handshake.ServerHandshake;

/**
 * The Activity that utilizes websockets to help users chat with admins to solve their problems.
 * The users can chat one-on-one with an admin and ask any questions and admin can respond in real time.
 */
public class AdminChatActivity extends AppCompatActivity implements WebSocketListener{

    private Button sendBtn;
    private FloatingActionButton backButton;
    private EditText msgEtx;
    private TextView msgTv;
    private boolean detailedView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       //Need to change link to non-local version if fully implemented
        String serverUrl = "ws://coms-3090-007.class.las.iastate.edu:8080/chat/" + UserInfo.username;

        // Establish WebSocket connection and set listener
        WebSocketManager.getInstance().connectWebSocket(serverUrl);


        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.Send_chat_button);
        msgEtx = (EditText) findViewById(R.id.message_text);
        msgTv = (TextView) findViewById(R.id.chat_display);
        backButton = (FloatingActionButton) findViewById(R.id.back_button);

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(AdminChatActivity.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                // send message
                WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                msgEtx.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminChatActivity.this, UserAccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        msgTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detailedView) {
                    msgTv.setTranslationX(0);
                }else {
                    msgTv.setTranslationX(80 * getResources().getDisplayMetrics().density);
                }
                detailedView = !detailedView;
                return false;
            }
        });

    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            msgTv.setMovementMethod(new ScrollingMovementMethod());
            String message1 = message;
            if (message.toLowerCase().contains("welcome to the chat server,"))
            {
                message1 = "                 " + message;
                String s = msgTv.getText().toString();
                msgTv.setText(s + "\n\n"+message1);
            }else {
                String s = msgTv.getText().toString();
                msgTv.setText(s + "\n\n" + message);
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
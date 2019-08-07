package cn.moecity.coursework;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private int userRole;
    private SharedPreferences userData;
    private String tutId, stuId, userName;
    private Intent intent = getIntent();
    private Button chatBtn;
    private EditText chatText;
    private SqlDAO sqlDAO;
    private List<SavedChat> savedChatList;
    private List<Chat> chatList;
    private Boolean isRunned = false;
    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private String stuName, tutName;
    private Boolean isRun=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        userRole = userData.getInt("userRole", 0);
        chatBtn = (Button) findViewById(R.id.chatSend);
        chatText = (EditText) findViewById(R.id.chatText);
        listView = (ListView) findViewById(R.id.chatListView);
        chatText.setText("");
        chatBtn.setOnClickListener(this);
        sqlDAO = new SqlDAO(this);
        Thread t1 = null;
        t1 = new Thread(new WeakThread());
        t1.start();
        switch (userRole) {
            case TUTOR:
                tutChat();
                break;
            case STUDENT:
                stuChat();
                break;
        }

    }

    private void stuChat() {
        stuId = userData.getString("objectId", "fffffffff");
        intent = getIntent();
        tutName = intent.getStringExtra("NName");
        tutId = intent.getStringExtra("Id");
        stuName = "I said:";
        try{
        readChatData();}
        catch (Exception e){}
    }

    private void tutChat() {
        tutId = userData.getString("objectId", "fffffffff");
        intent = getIntent();
        stuName = intent.getStringExtra("NName");
        tutName = "I said:";
        stuId = intent.getStringExtra("Id");
        try{
            readChatData();}
        catch (Exception e){}

    }

    private void readChatData() {
        //sqlDAO=new SqlDAO(this);
        sqlDAO.deleteChat();
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from Chat";
        new BmobQuery<Chat>().doSQLQuery(bql, new SQLQueryListener<Chat>() {

            @Override
            public void done(BmobQueryResult<Chat> result, BmobException e) {
                if (e == null) {
                    List<Chat> list = (List<Chat>) result.getResults();
                    if (list != null && list.size() > 0) {
                        chatList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", chatList.toString());
                        for (int i = 0; i < chatList.size(); i++) {
                            String objId = chatList.get(i).getObjectId();
                            SavedChat savedChat = new SavedChat(objId, chatList.get(i));
                            Log.e("chatList", chatList.toString());
                            sqlDAO.insertChat(savedChat);
                        }
                        savedChatList = sqlDAO.getStuTutChat(stuId, tutId);
                        Log.e("chat", savedChatList.toString());
                        updateLocalChat();
                    } else {
                        Log.e("Empty", "No Data");
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());

                }
            }
        });
    }

    private int getSizeOfList(List<?> list) {
        int size = 0;
        try {
            return list.size();
        } catch (Exception e) {
            return size;
        }

    }

    private void updateLocalChat() {
        savedChatList = sqlDAO.getStuTutChat(stuId, tutId);
        Log.e("msg", savedChatList.toString());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < getSizeOfList(savedChatList); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (savedChatList.get(i).isFromTo())
                map.put("itemName", stuName);
            else
                map.put("itemName", tutName);
            map.put("content", savedChatList.get(i).getTimestamp()+"\n"+savedChatList.get(i).getContent());
            list.add(map);
        }
        listViewAdapter = new ListViewAdapter(list, getLayoutInflater(), getApplicationContext());
        listView.setAdapter(listViewAdapter);

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    readChatData();

            }
            super.handleMessage(msg);
        }
    };


    public class WeakThread implements Runnable {

        @Override
        public void run() {
            while (isRun) {

                try {

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addChat() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        Chat chat = new Chat();
        chat.setStuId(stuId);
        chat.setTutId(tutId);
        if (userRole == TUTOR) {
            chat.setFromTo(false);

        } else chat.setFromTo(true);
        chat.setContent(chatText.getText().toString());
        chat.setTimestamp(new Date());
        chat.setRead(false);
        chat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                readChatData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        isRun=false;
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatSend:
                isRun=false;
                addChat();
                chatText.setText("");
                switch (userRole) {
                    case TUTOR:
                        tutChat();
                        break;
                    case STUDENT:
                        stuChat();
                        break;
                }
                isRun=true;
                break;
        }
    }
}

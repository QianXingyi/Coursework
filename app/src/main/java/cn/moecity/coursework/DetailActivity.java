package cn.moecity.coursework;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String itemId, itemRoleStr;
    private int itemRole;
    private Boolean isTaken = false;
    private SharedPreferences userData;
    private String objectId;
    private int userRole;
    private ListView detailView;
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private Student stuUser;
    private Tutor tutUser;
    private Admin admUser;
    private List<Student> studentList;
    private List<Tutor> tutorList;
    private List<Admin> adminList;
    private List<Subject> subjectList;
    private List<Chat> chatList;
    private List<StuTut> stuTutList, savedStuTutList;
    private List<TutSub> tutSubList, savedTutSubList;
    private List<SavedChat> savedChats;
    private List<SavedStudent> savedStudentList;
    private List<SavedTutor> savedTutorList;
    private List<SavedSubject> savedSubjectList;
    private SqlDAO sqlDAO;
    private Button backBtn, firstBtn, secondBtn;
    private TextView text1, text2, text3;
    private EditText firstEdit, lastEdit, emailAddressEdit, passchange;
    private ListViewAdapter listViewAdapter;
    private int isItem;
    private String listItemId;
    private Boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        if (intent != null) {
            itemId = intent.getStringExtra("itemId");
            itemRoleStr = intent.getStringExtra("itemRole");
            itemRole = Integer.parseInt(itemRoleStr);

            Log.e("intent", itemId + "," + itemRole);

        }
        backBtn = (Button) findViewById(R.id.goToBtn);
        firstBtn = (Button) findViewById(R.id.button4);
        secondBtn = (Button) findViewById(R.id.button3);
        text1 = (TextView) findViewById(R.id.textA);
        text2 = (TextView) findViewById(R.id.textB);
        text3 = (TextView) findViewById(R.id.textC);
        firstEdit = (EditText) findViewById(R.id.firstEdit);
        lastEdit = (EditText) findViewById(R.id.lastEdit);
        emailAddressEdit = (EditText) findViewById(R.id.emailAddressEdit);
        passchange = (EditText) findViewById(R.id.passchange);
        passchange.setText("");
        detailView = (ListView) findViewById(R.id.detailView);
        backBtn = (Button) findViewById(R.id.goToBtn);
        backBtn.setOnClickListener(this);
        firstBtn = (Button) findViewById(R.id.button4);
        firstBtn.setOnClickListener(this);
        //detailView.setAdapter(new ListViewAdapter(null,getLayoutInflater(),getApplicationContext()));
        firstBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        objectId = userData.getString("objectId", "ffffffffff");
        userRole = userData.getInt("userRole", 0);
        try {
            sqlDAO = new SqlDAO(this);
            savedStudentList = sqlDAO.getAllStudent();
            savedTutorList = sqlDAO.getAllTutor();
            savedSubjectList = sqlDAO.getAllSubject();
            savedTutSubList = sqlDAO.getAllTutSub();
            savedStuTutList = sqlDAO.getAllStuTut();
            Log.e("stutut", savedStuTutList.toString());
            detailView.setOnItemClickListener(this);
            setUI();
            getObject();
        } catch (Exception e) {

        }

    }

    private int getSizeOfList(List<?> list) {
        int size = 0;
        try {
            return list.size();
        } catch (Exception e) {
            return size;
        }

    }

    private void updateEdit() {
        if (isActive) {
            firstEdit.setEnabled(true);
            lastEdit.setEnabled(true);
            emailAddressEdit.setEnabled(true);
            passchange.setEnabled(true);
            firstBtn.setText("Keep");
        } else {
            firstEdit.setEnabled(false);
            lastEdit.setEnabled(false);
            emailAddressEdit.setEnabled(false);
            passchange.setEnabled(false);
            firstBtn.setText("Eidt");
        }


    }

    private void layoutUsers() {
        lastEdit.setVisibility(View.VISIBLE);
        detailView.setVisibility(View.VISIBLE);
        passchange.setVisibility(View.GONE);
        firstBtn.setVisibility(View.GONE);
        secondBtn.setVisibility(View.GONE);
        text1.setText("Name:");
        text2.setText("Email:");
        if (userRole == ADMIN) {
            passchange.setVisibility(View.VISIBLE);
            firstBtn.setVisibility(View.VISIBLE);
            secondBtn.setVisibility(View.VISIBLE);
            firstBtn.setText("Edit");
            secondBtn.setText("Save");
        }

    }

    private void getObject() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        switch (itemRole) {
            case 0:
                lastEdit.setVisibility(View.GONE);
                text1.setText("Subject:");
                text2.setText("Subject No:");
                switch (userRole) {
                    case TUTOR:
                        for (int i = 0; i < getSizeOfList(savedSubjectList); i++) {
                            if (savedSubjectList.get(i).getObjectId().equals(itemId)) {
                                firstEdit.setText(savedSubjectList.get(i).getName());
                                emailAddressEdit.setText(savedSubjectList.get(i).getSubNo() + "");
                            }
                        }
                        for (int i = 0; i < getSizeOfList(savedTutSubList); i++) {
                            if (savedTutSubList.get(i).getTutId().equals(objectId) && savedTutSubList.get(i).getSubId().equals(itemId)) {
                                emailAddressEdit.setText(emailAddressEdit.getText().toString());
                                passchange.setText("You have registered to this subject");
                                isTaken = true;
                                firstBtn.setText("Cancel the registration");

                            }
                        }
                        if (!isTaken) {
                            emailAddressEdit.setText(emailAddressEdit.getText().toString());
                            passchange.setText("You haven't registered to this subject");
                            isTaken = false;
                            firstBtn.setText("Register");
                        }

                    case STUDENT:


                    case ADMIN:

                        break;
                }
                break;
            case STUDENT:
                layoutUsers();
                switch (userRole) {
                    case TUTOR:
                        for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                            if (savedStudentList.get(i).getObjectId().equals(itemId)) {
                                firstEdit.setText(savedStudentList.get(i).getFirstName());
                                lastEdit.setText(savedStudentList.get(i).getLastName());
                                emailAddressEdit.setText(savedStudentList.get(i).getEmail());
                            }
                        }


                        listViewAdapter = new ListViewAdapter(list, getLayoutInflater(), getApplicationContext());
                        detailView.setAdapter(listViewAdapter);
                        putSubjects(list);
                    case ADMIN:
                        for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                            if (savedStudentList.get(i).getObjectId().equals(itemId)) {
                                firstEdit.setText(savedStudentList.get(i).getFirstName());
                                lastEdit.setText(savedStudentList.get(i).getLastName());
                                emailAddressEdit.setText(savedStudentList.get(i).getEmail());
                            }
                        }


                }
                break;
            case TUTOR:
                layoutUsers();
                switch (userRole) {
                    case STUDENT:
                        for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                            if (savedTutorList.get(i).getObjectId().equals(itemId)) {
                                firstEdit.setText(savedTutorList.get(i).getFirstName());
                                lastEdit.setText(savedTutorList.get(i).getLastName());
                                emailAddressEdit.setText(savedTutorList.get(i).getEmail());
                            }
                        }

                        listViewAdapter = new ListViewAdapter(list, getLayoutInflater(), getApplicationContext());
                        detailView.setAdapter(listViewAdapter);
                        putSubjects(list);

                        break;
                    case ADMIN:
                        for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                            if (savedTutorList.get(i).getObjectId().equals(itemId)) {
                                firstEdit.setText(savedTutorList.get(i).getFirstName());
                                lastEdit.setText(savedTutorList.get(i).getLastName());
                                emailAddressEdit.setText(savedTutorList.get(i).getEmail());
                            }

                        }
                        listViewAdapter = new ListViewAdapter(list, getLayoutInflater(), getApplicationContext());
                        detailView.setAdapter(listViewAdapter);
                        putSubjects(list);
                        break;
                }
                break;
            case ADMIN:
                //layoutUsers();
                break;

        }

    }

    private void setUI() {
        switch (userRole) {
            case STUDENT:

            case TUTOR: {
                firstEdit.setEnabled(false);
                lastEdit.setEnabled(false);
                emailAddressEdit.setEnabled(false);
                passchange.setEnabled(false);
                text3.setVisibility(View.GONE);
                firstBtn.setText("Register");
                secondBtn.setVisibility(View.GONE);
                break;
            }
            case ADMIN:
                firstEdit.setEnabled(false);
                lastEdit.setEnabled(false);
                emailAddressEdit.setEnabled(false);
                passchange.setEnabled(false);
                firstBtn.setVisibility(View.VISIBLE);
                secondBtn.setVisibility(View.VISIBLE);
                if (itemRole == 0) {
                    passchange.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                }
                break;
        }

    }

    private void updateData() {
        savedTutSubList = sqlDAO.getAllTutSub();
        savedStuTutList = sqlDAO.getAllStuTut();
        savedStudentList = sqlDAO.getAllStudent();
        savedTutSubList = sqlDAO.getAllTutSub();
        savedTutorList = sqlDAO.getAllTutor();

    }

    private void putSubjects(List<Map<String, Object>> list) {
        String stuId = "";
        String tutId = "";
        if (userRole != ADMIN) {
            if (userRole == TUTOR && itemRole == STUDENT) {
                stuId = itemId;
                tutId = objectId;
            } else if (userRole == STUDENT && itemRole == TUTOR) {
                stuId = objectId;
                tutId = itemId;
            }
            String content = "Cannot Take.";
            for (int i = 0; i < getSizeOfList(savedSubjectList); i++) {
                Map<String, Object> map = new HashMap<String, Object>();

                map.put("itemName", savedSubjectList.get(i).getName());
                //Log.e("tutor",savedSubjectList.toString());

                for (int j = 0; j < getSizeOfList(savedTutSubList); j++) {
                    if (savedTutSubList.get(j).getTutId().equals(tutId) &&
                            savedTutSubList.get(j).getSubId().equals(savedSubjectList.get(i).getObjectId())
                    ) {
                        //Log.e("taken",savedSubjectList.get(j).toString());
                        content = "Can Take";
                    }
                }
                for (int j = 0; j < getSizeOfList(savedStuTutList); j++) {
                    if (savedStuTutList.get(j).getTutId().equals(tutId) &&
                            savedStuTutList.get(j).getSubId().equals(savedSubjectList.get(i).getObjectId()) &&
                            savedStuTutList.get(j).getStuId().equals(stuId)
                    ) {
                        //Log.e("taken",savedSubjectList.get(j).toString());
                        content = "Taken with me.";
                    }
                }


                map.put("content", content);
                content = "Cannot Take.";
                map.put("objectId", savedSubjectList.get(i).getObjectId());
                map.put("role", "0");
                list.add(map);
            }
        } else {
            if (itemRole == TUTOR) {
                tutId = itemId;
                String content = "Not Take.";
                for (int i = 0; i < getSizeOfList(savedSubjectList); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();

                    map.put("itemName", savedSubjectList.get(i).getName());
                    //Log.e("tutor",savedSubjectList.toString());

                    for (int j = 0; j < getSizeOfList(savedTutSubList); j++) {
                        if (savedTutSubList.get(j).getTutId().equals(tutId) &&
                                savedTutSubList.get(j).getSubId().equals(savedSubjectList.get(i).getObjectId())
                        ) {
                            //Log.e("taken",savedSubjectList.get(j).toString());
                            content = "Taken";
                        }
                    }
                    map.put("content", content);
                    content = "Not Taken.";
                    map.put("objectId", savedSubjectList.get(i).getObjectId());
                    map.put("role", "0");
                    list.add(map);
                }
            }
        }
        if (list.size() == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemName", "No Data");
            map.put("objectId", "No Data");
            map.put("role", "No Data");
            map.put("content", "No Data");
            list.add(map);
        }
    }

    private void insertTutorStuSub(StuTut stuTut) {
        sqlDAO.insertStuTut(stuTut);

        savedStuTutList = sqlDAO.getAllStuTut();

        stuTut.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.e("newId", s);

                } else {
                    Log.e("msg", e.toString());
                }
            }
        });

    }

    private void deleteTutorStuSub(StuTut stuTut) {
        sqlDAO.deleteStuTutByStuTutSubId(stuTut.getStuId(), stuTut.getTutId(), stuTut.getSubId());
        savedStuTutList = sqlDAO.getAllStuTut();
        String bql = "select * from StuTut where stuId='"
                + stuTut.getStuId() + "'and tutId='"
                + stuTut.getTutId() + "'and subId='"
                + stuTut.getSubId() + "'";
        new BmobQuery<StuTut>().doSQLQuery(bql, new SQLQueryListener<StuTut>() {

            @Override
            public void done(BmobQueryResult<StuTut> result, BmobException e) {
                if (e == null) {
                    List<StuTut> list = (List<StuTut>) result.getResults();
                    if (list != null && list.size() > 0) {
                        list.get(0).delete(list.get(0).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });
                    } else {
                        Log.e("smile", "no data");
                    }
                } else {
                    Log.e("smile", "error：" + e.getErrorCode() + "，error code：" + e.getMessage());
                }
            }
        });

    }

    private void deleteTutorSub(TutSub tutSub) {
        sqlDAO.deleteTutSubByTutSub(tutSub.getTutId(), tutSub.getSubId());
        String bql = "select * from StuTut where tutId='"
                + tutSub.getTutId() + "'and subId='"
                + tutSub.getSubId() + "'";
        new BmobQuery<StuTut>().doSQLQuery(bql, new SQLQueryListener<StuTut>() {

            @Override
            public void done(BmobQueryResult<StuTut> result, BmobException e) {
                if (e == null) {
                    final List<StuTut> list = (List<StuTut>) result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            sqlDAO.deleteStuTutByStuTutSubId(list.get(i).getStuId()
                                    , list.get(i).getTutId(), list.get(i).getSubId());
                            list.get(i).delete(list.get(i).getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                }

                            });
                        }
                    } else {
                        Log.e("smile", "no data");
                    }
                } else {
                    Log.e("smile", "error：" + e.getErrorCode() + "，error code：" + e.getMessage());
                }
            }
        });

        String bql1 = "select * from TutSub where tutId='"
                + tutSub.getTutId() + "'and subId='"
                + tutSub.getSubId() + "'";
        new BmobQuery<TutSub>().doSQLQuery(bql1, new SQLQueryListener<TutSub>() {

            @Override
            public void done(BmobQueryResult<TutSub> result, BmobException e) {
                if (e == null) {
                    final List<TutSub> list = (List<TutSub>) result.getResults();
                    if (list != null && list.size() > 0) {
                        list.get(0).delete(list.get(0).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });

                    } else {
                        Log.e("smile", "no data");
                    }
                } else {
                    Log.e("smile", "error：" + e.getErrorCode() + "，error code：" + e.getMessage());
                }
            }
        });

    }

    private void updateTut(SavedTutor tutor) {
        Tutor tutor1 = new Tutor();
        tutor1.setFirstName(tutor.getFirstName());
        tutor1.setLastName(tutor.getLastName());
        tutor1.setEmail(tutor.getEmail());
        tutor1.setPassword(tutor.getPassword());
        tutor1.setAndroidId(tutor.getAndroidId());
        tutor1.setFacebookId(tutor.getFacebookId());
        tutor1.setLogged(tutor.getLogged());
        tutor1.setObjectId(tutor.getObjectId());
        try {
            sqlDAO.updateTutor(tutor);
        } catch (Exception e) {
            Log.e("tutorError", e.toString());
        }
        Log.e("student", sqlDAO.getAllTutor().toString());
        tutor1.update(tutor.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {

                finish();
            }
        });

    }

    private void updateStu(SavedStudent student) {
        Student s1 = new Student();
        s1.setFirstName(student.getFirstName());
        s1.setLastName(student.getLastName());
        s1.setEmail(student.getEmail());
        s1.setPassword(student.getPassword());
        s1.setAndroidId(student.getAndroidId());
        s1.setFacebookId(student.getFacebookId());
        s1.setObjectId(student.getObjectId());
        try {
            sqlDAO.updateStudent(student);
        } catch (Exception e) {
            Log.e("student", e.toString());
        }
        Log.e("student", sqlDAO.getAllStudent().toString());
        Log.e("s1", student.toString());
        s1.update(itemId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Log.e("error", e.toString());
                finish();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (userRole) {
            case TUTOR:
            case STUDENT: {
                HashMap<String, Object> map = (HashMap<String, Object>) listViewAdapter.getItem(position);
                listItemId = map.get("objectId").toString();
                String itemContent = map.get("content").toString();
                String subName = map.get("itemName").toString();
                if (itemContent.contains("Cannot Take.") || itemContent.contains("No Data")) {
                    isItem = 0;
                } else if (itemContent.contains("Can Take"))
                    isItem = 1;
                else
                    isItem = 2;


                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Alert");
                if (itemRole == STUDENT) {

                    if (isItem == 1)
                        builder.setMessage("Are you going to take this student in " + subName + "?");
                    else if (isItem == 2)
                        builder.setMessage("Are you going to delete this student in " + subName + "?");
                    else
                        builder.setMessage("You are not in " + subName + ".");
                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StuTut stuTut = new StuTut();
                            stuTut.setStuId(itemId);
                            stuTut.setTutId(objectId);
                            stuTut.setSubId(listItemId);
                            Bmob.initialize(DetailActivity.this, "8f00c8d01878b845809a7343475d1fc1");
                            if (isItem == 1) {
                                insertTutorStuSub(stuTut);

                            } else if (isItem == 2) {
                                deleteTutorStuSub(stuTut);
                            }
                            updateData();
                            setUI();
                            getObject();
                        }

                    });
                } else if (itemRole == TUTOR) {
                    if (isItem == 1)
                        builder.setMessage("Are you going to take " + subName + " with this tutor?");
                    else if (isItem == 2)
                        builder.setMessage("Are you going to give up learning " + subName + " with this tutor?");
                    else
                        builder.setMessage("The tutor is not in " + subName + ".");
                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            StuTut stuTut = new StuTut();
                            stuTut.setStuId(objectId);
                            stuTut.setTutId(itemId);
                            stuTut.setSubId(listItemId);
                            Bmob.initialize(DetailActivity.this, "8f00c8d01878b845809a7343475d1fc1");
                            if (isItem == 1) {
                                insertTutorStuSub(stuTut);

                            } else if (isItem == 2) {
                                deleteTutorStuSub(stuTut);
                            }
                            updateData();
                            setUI();
                            getObject();

                        }

                    });
                }
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case ADMIN:
                HashMap<String, Object> map = (HashMap<String, Object>) listViewAdapter.getItem(position);
                listItemId = map.get("objectId").toString();
                String itemContent = map.get("content").toString();
                String subName = map.get("itemName").toString();
                if (itemContent.contains("Not"))
                    isItem = 1;
                else isItem = 2;

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Alert");
                if (isItem == 1)
                    builder.setMessage("Are you going make this tutor to take " + subName + "?");
                else if (isItem == 2)
                    builder.setMessage("Are you going to delete " + subName + " from this tutor?");
                builder.setCancelable(false);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TutSub tutSub = new TutSub();
                        tutSub.setTutId(itemId);
                        tutSub.setSubId(listItemId);
                        if (isItem == 1) {
                            sqlDAO.insertTutSub(tutSub);
                            tutSubList = sqlDAO.getAllTutSub();
                            tutSub.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {

                                }
                            });
                        } else if (isItem == 2) {
                            deleteTutorSub(tutSub);
                        }
                        updateData();
                        setUI();
                        getObject();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goToBtn:
                finish();
                break;
            case R.id.button4:
                if (userRole != ADMIN) {
                    if (isTaken) {
                        TutSub tutSub = new TutSub();
                        tutSub.setSubId(itemId);
                        tutSub.setTutId(objectId);
                        deleteTutorSub(tutSub);
                        firstBtn.setText("Register");
                        isTaken = false;
                        updateData();
                        setUI();
                        getObject();
                    } else {
                        TutSub tutSub = new TutSub();
                        tutSub.setSubId(itemId);
                        tutSub.setTutId(objectId);
                        sqlDAO.insertTutSub(tutSub);
                        tutSub.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {

                            }
                        });
                        isTaken = true;
                        updateData();
                        setUI();
                        getObject();
                    }
                } else {
                    if (isActive)
                        isActive = false;
                    else
                        isActive = true;
                    updateEdit();

                }
                break;
            case R.id.button3: {
                if (userRole == ADMIN) {
                    switch (itemRole) {
                        case STUDENT:
                            for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                                if (savedStudentList.get(i).getObjectId().equals(itemId)) {
                                    SavedStudent student = savedStudentList.get(i);
                                    student.setEmail(emailAddressEdit.getText().toString());
                                    if (!(passchange.getText().toString().length() == 0)) {
                                        student.setPassword(MD5.generatePassword(passchange.getText().toString()));
                                        student.setFirstName(firstEdit.getText().toString());
                                        student.setLastName(lastEdit.getText().toString());
                                        updateStu(student);
                                        Log.e("msg", passchange.getText().toString());
                                    } else finish();
                                }

                            }
                            break;
                        case TUTOR:
                            for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                                if (savedTutorList.get(i).getObjectId().equals(itemId)) {
                                    SavedTutor tutor = savedTutorList.get(i);
                                    tutor.setEmail(emailAddressEdit.getText().toString());
                                    if (!(passchange.getText().toString().length() == 0)) {
                                        tutor.setPassword(MD5.generatePassword(passchange.getText().toString()));
                                        Log.e("pass", passchange.getText().toString());
                                        tutor.setFirstName(firstEdit.getText().toString());
                                        tutor.setLastName(lastEdit.getText().toString());
                                        updateTut(tutor);
                                        Log.e("msg", tutor.toString());
                                    } else finish();
                                }

                            }
                            break;

                    }

                }
                break;
            }

        }

    }
}

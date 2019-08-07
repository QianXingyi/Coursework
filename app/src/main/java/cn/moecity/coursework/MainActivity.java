package cn.moecity.coursework;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String ANDROID_ID;
    private String password, objectId;
    private String userId;
    private SharedPreferences userData;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment messageFragment, meFragment, itemFragment;
    private boolean isRunned = false;
    private int userRole;
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
    private LocationManager locationManager;

    private List<SavedSubject> savedSubjectList;

    private SqlDAO sqlDAO;
    private Button homeBtn, messageBtn, aboutBtn;
    private Drawable[] drawable = new Drawable[3];
    private Drawable[] drawableClicked = new Drawable[3];
    private Context thisContext;
    private String locationProvider;
    private Double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlDAO = new SqlDAO(this);
        sqlDAO.deleteAll();
        homeBtn = (Button) findViewById(R.id.homeBtn);
        messageBtn = (Button) findViewById(R.id.messageBtn);
        aboutBtn = (Button) findViewById(R.id.aboutBtn);
        meFragment = new MeFragment();
        messageFragment = new MessageFragment();
        itemFragment = new ItemFragment();
        homeBtn.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);

        drawable[0] = getDrawable(R.drawable.ic_home_black_24dp);
        drawable[1] = getDrawable(R.drawable.ic_chat_bubble_outline_black_24dp);
        drawable[2] = getDrawable(R.drawable.ic_account_circle_black_24dp);
        drawableClicked[0] = getDrawable(R.drawable.ic_home_white_24dp);
        drawableClicked[1] = getDrawable(R.drawable.ic_chat_bubble_outline_white_24dp);
        drawableClicked[2] = getDrawable(R.drawable.ic_account_circle_white_24dp);
        for (int i = 0; i < 3; i++) {
            drawableClicked[i].setBounds(0, 0, drawableClicked[i].getMinimumWidth(), drawableClicked[i].getMinimumHeight());
            drawable[i].setBounds(0, 0, drawable[i].getMinimumWidth(), drawable[i].getMinimumHeight());
        }
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_content, new BlankFragment());
        transaction.commit();
        sqlDAO.deleteAll();
        thisContext = this;
        try {


            String[] permissions;
            permissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,

                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            LocationManager man = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = man.getProviders(true);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                Toast.makeText(this, "The application need permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        1);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            man.requestLocationUpdates(locationProvider, 5000, 50, new LocationListener() {


                @Override
                public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

                }

                @Override
                public void onProviderEnabled(String arg0) {


                }

                @Override
                public void onProviderDisabled(String arg0) {
                }

                @Override
                public void onLocationChanged(Location arg0) {
                    // TODO Auto-generated method stub
                    lat = arg0.getLatitude();
                    lon = arg0.getLongitude();
                    Log.e("location", lat + "," + lon);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putFloat("lat",lat.floatValue());
                    editor.putFloat("lon",lon.floatValue());

                    editor.commit();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
        LoadingAsyncTask loadingAsyncTask = new LoadingAsyncTask();
        loadingAsyncTask.execute(5000);


//        homeClick();
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        ANDROID_ID = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        objectId = userData.getString("objectId", "ffffffffff");
        userRole = userData.getInt("userRole", 0);

        Log.e("msg", objectId + "," + userRole);
        switch (userRole) {
            case ADMIN:

                break;
            case STUDENT:

                break;
            case TUTOR:

                break;

        }

//        String [] findId={"49c1ea71ad"};
//        BmobQuery<Student> studentBmobQuery=new BmobQuery<>();
//        studentBmobQuery.addWhereContainedIn("objectId", Arrays.asList(findId));
//        studentBmobQuery.findObjects(new FindListener<Student>() {
//            @Override
//            public void done(List<Student> list, BmobException e) {
//                if(e==null){
//                    Log.e("msg",list.toString()+objectId+","+userRole);
//                }else {
//
//                    Log.e("msg",e.toString());
//                }
//            }
//        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.e("Now:", simpleDateFormat.format(date));
        Log.e("ANDROID_ID", ANDROID_ID);
        sqlDAO = new SqlDAO(this);

    }

    private void readChatData() {
        //sqlDAO=new SqlDAO(this);
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
                        savedChats = sqlDAO.getAllChat();
                        Log.e("chat", savedChats.toString());
                        isRunned = false;

                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });
    }


    private void readStudentData() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from Student";
        new BmobQuery<Student>().doSQLQuery(bql, new SQLQueryListener<Student>() {

            @Override
            public void done(BmobQueryResult<Student> result, BmobException e) {
                if (e == null) {
                    List<Student> list = (List<Student>) result.getResults();
                    if (list != null && list.size() > 0) {
                        studentList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", studentList.toString());
                        for (int i = 0; i < studentList.size(); i++) {
                            String objId = studentList.get(i).getObjectId();
                            SavedStudent savedStudent = new SavedStudent(objId, studentList.get(i));
                            Log.e("studentList", studentList.toString());
                            sqlDAO.insertStudent(savedStudent);
                        }
                        savedStudentList = sqlDAO.getAllStudent();
                        Log.e("students", savedStudentList.toString());
                        isRunned = false;
                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });

    }


    private void readTutorData() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from Tutor";
        new BmobQuery<Tutor>().doSQLQuery(bql, new SQLQueryListener<Tutor>() {

            @Override
            public void done(BmobQueryResult<Tutor> result, BmobException e) {
                if (e == null) {
                    List<Tutor> list = (List<Tutor>) result.getResults();
                    if (list != null && list.size() > 0) {
                        tutorList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", tutorList.toString());
                        for (int i = 0; i < tutorList.size(); i++) {
                            String objId = tutorList.get(i).getObjectId();
                            SavedTutor savedTutor = new SavedTutor(objId, tutorList.get(i));
                            Log.e("studentList", tutorList.toString());
                            sqlDAO.insertTutor(savedTutor);
                        }
                        savedTutorList = sqlDAO.getAllTutor();
                        Log.e("students", savedTutorList.toString());
                        isRunned = false;
                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });

    }

    private void readSubjectData() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from Subject";
        new BmobQuery<Subject>().doSQLQuery(bql, new SQLQueryListener<Subject>() {

            @Override
            public void done(BmobQueryResult<Subject> result, BmobException e) {
                if (e == null) {
                    List<Subject> list = (List<Subject>) result.getResults();
                    if (list != null && list.size() > 0) {
                        subjectList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", subjectList.toString());
                        for (int i = 0; i < subjectList.size(); i++) {
                            String objId = subjectList.get(i).getObjectId();
                            SavedSubject savedSubject = new SavedSubject(objId, subjectList.get(i));
                            Log.e("subjects", subjectList.toString());
                            sqlDAO.insertSub(savedSubject);
                        }
                        savedSubjectList = sqlDAO.getAllSubject();
                        Log.e("subjects", savedSubjectList.toString());
                        isRunned = false;
                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });

    }

    private void readStuTutData() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from StuTut";
        new BmobQuery<StuTut>().doSQLQuery(bql, new SQLQueryListener<StuTut>() {

            @Override
            public void done(BmobQueryResult<StuTut> result, BmobException e) {
                if (e == null) {
                    List<StuTut> list = (List<StuTut>) result.getResults();
                    if (list != null && list.size() > 0) {
                        stuTutList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", stuTutList.toString());
                        for (int i = 0; i < stuTutList.size(); i++) {

                            Log.e("subjects", stuTutList.toString());
                            sqlDAO.insertStuTut(stuTutList.get(i));
                        }
                        savedStuTutList = sqlDAO.getAllStuTut();
                        Log.e("savedStuTutList", savedStuTutList.toString());
                        isRunned = false;
                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });

    }

    private void readTutSubData() {
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        String bql = "select * from TutSub";
        new BmobQuery<TutSub>().doSQLQuery(bql, new SQLQueryListener<TutSub>() {

            @Override
            public void done(BmobQueryResult<TutSub> result, BmobException e) {
                if (e == null) {
                    List<TutSub> list = (List<TutSub>) result.getResults();
                    if (list != null && list.size() > 0) {
                        tutSubList = list;
                        Log.e("hello", list.get(0).getObjectId());
                        Log.e("smile", tutSubList.toString());
                        for (int i = 0; i < tutSubList.size(); i++) {

                            Log.e("subjects", tutSubList.toString());
                            sqlDAO.insertTutSub(tutSubList.get(i));
                        }
                        savedTutSubList = sqlDAO.getAllTutSub();
                        Log.e("savedTutSubList", savedTutSubList.toString());
                        isRunned = false;
                    } else {
                        Log.e("Empty", "No Data");
                        isRunned = false;
                    }
                } else {
                    Log.e("error", "error code：" + e.getErrorCode() + "，error：" + e.getMessage());
                    isRunned = false;
                }
            }
        });

    }

    private void makeAdminUserData() {

        BmobQuery<Admin> adminBmobQuery = new BmobQuery<>();
        adminBmobQuery.addWhereEqualTo("objectId", objectId);
        adminBmobQuery.findObjects(new FindListener<Admin>() {
            @Override
            public void done(List<Admin> list, BmobException e) {
                if (e == null) {

                } else {
                    Log.e("msg", e.toString());
                }
            }
        });
    }

    private void aboutClick() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_content, new MeFragment());
        transaction.commit();
        aboutBtn.setCompoundDrawables(null, drawableClicked[2], null, null);
        messageBtn.setCompoundDrawables(null, drawable[1], null, null);
        homeBtn.setCompoundDrawables(null, drawable[0], null, null);
        aboutBtn.setTextColor(Color.WHITE);
        messageBtn.setTextColor(Color.BLACK);
        homeBtn.setTextColor(Color.BLACK);

    }

    private void messageClick() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_content, new MessageFragment());
        transaction.commit();
        aboutBtn.setCompoundDrawables(null, drawable[2], null, null);
        messageBtn.setCompoundDrawables(null, drawableClicked[1], null, null);
        homeBtn.setCompoundDrawables(null, drawable[0], null, null);
        aboutBtn.setTextColor(Color.BLACK);
        messageBtn.setTextColor(Color.WHITE);
        homeBtn.setTextColor(Color.BLACK);
    }

    private void homeClick() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_content, itemFragment);
        transaction.commit();
        aboutBtn.setCompoundDrawables(null, drawable[2], null, null);
        messageBtn.setCompoundDrawables(null, drawable[1], null, null);
        homeBtn.setCompoundDrawables(null, drawableClicked[0], null, null);
        aboutBtn.setTextColor(Color.BLACK);
        messageBtn.setTextColor(Color.BLACK);
        homeBtn.setTextColor(Color.WHITE);
    }

    private void checkState() {
        if (isRunned) {
            try {
                Thread.sleep(10);
                // Log.e("msg","block");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkState();
        }

    }

    private class LoadingAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Bmob.initialize(MainActivity.this, "8f00c8d01878b845809a7343475d1fc1");
            readStudentData();
            isRunned = true;
            checkState();

            readTutorData();
            isRunned = true;
            checkState();

            readSubjectData();
            isRunned = true;
            checkState();

            readStuTutData();
            isRunned = true;
            checkState();

            readTutSubData();
            isRunned = true;
            checkState();

            readChatData();
            isRunned = true;
            checkState();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            homeClick();
        }
    }

    private void readUpdate() {
        readStudentData();
        readTutorData();
        readSubjectData();
        readStuTutData();
        readTutSubData();
        readChatData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutBtn:
                aboutClick();
                break;

            case R.id.messageBtn:
                messageClick();
                break;

            case R.id.homeBtn:
                homeClick();
                break;
        }
    }

}

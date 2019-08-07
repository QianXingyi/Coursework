package cn.moecity.coursework;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import q.rorbin.badgeview.QBadgeView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private SharedPreferences userData;
    private LinearLayout waitLayout;
    private String ANDROID_ID;
    private Button logBtn, newBtn;
    private EditText emailEditText, passwordEditText;
    private TextView errorTextView;
    private String email, password, onlinePassword, errorMsg;
    private boolean isPass = false;
    private int userRole = 0;
    private Student student = new Student();
    private Admin admin = new Admin();
    private Tutor tutor = new Tutor();
    private String objectId = "";
    private boolean isRunned = false;
    private QBadgeView badgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ANDROID_ID = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        logBtn = (Button) findViewById(R.id.login_btn);
        newBtn = (Button) findViewById(R.id.new_btn);
        emailEditText = (EditText) findViewById(R.id.emailText);
        passwordEditText = (EditText) findViewById(R.id.passText);
        errorTextView = (TextView) findViewById(R.id.errorView);
        waitLayout = (LinearLayout) findViewById(R.id.waitlayout);
        logBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
//        badgeView=new QBadgeView(this);
//        badgeView.bindTarget(logBtn);
//        badgeView.setBadgeNumber(10);
//        badgeView.setBadgeTextSize(20,true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
//              badgeView.setBadgeNumber(0);
                isPass = false;
                email = emailEditText.getText().toString();
                errorTextView.setText("");
                Log.e("click", email);

                password = passwordEditText.getText().toString();
                if (email.equals("")) {
                    errorTextView.setText("Email can't be empty!");
                } else if (!email.contains("@") || !email.contains(".")) {
                    errorTextView.setText("Wrong email format!");
                } else if (password.equals("")) {
                    errorTextView.setText("Password can't be blank!");
                } else {
                    errorTextView.setText("");
                    waitLayout.setVisibility(View.VISIBLE);
                    logBtn.setVisibility(View.GONE);
                    passwordEditText.setEnabled(false);
                    emailEditText.setEnabled(false);
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.execute(5000);

                }
                break;
            case R.id.new_btn:
                Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void checkAdminEmail(final String localEmail) {
        onlinePassword = "";
        BmobQuery<Admin> adminBmobQuery = new BmobQuery<>();
        adminBmobQuery.addWhereEqualTo("email", localEmail);
        //Log.e("userrole",userRole+localEmail);
        adminBmobQuery.findObjects(new FindListener<Admin>() {
            @Override
            public void done(List<Admin> list, BmobException e) {
                if (e == null) {
                    onlinePassword = list.get(0).getPassword();

                    if (MD5.generatePassword(password).equals(onlinePassword)) {

                        isPass = true;
                        userRole = ADMIN;
                        admin = list.get(0);
                        objectId = list.get(0).getObjectId();
//                        Log.e("adm",onlinePassword);
//                        Log.e("objID",list.get(0).getObjectId());
//                        Log.e("adm",MD5.generatePassword(password)+"");
//                        Log.e("adm",userRole+","+MD5.generatePassword(password).equals(onlinePassword)+"");
                        isRunned = false;
                    } else {
                        isRunned = false;
                    }
                } else {
                    errorMsg = e.toString();
                    //Log.e("Login_Admin",e.toString());
                }
            }
        });

        if (!isPass) {
            checkTutorEmail(localEmail);
        }

    }

    private void checkTutorEmail(final String localEmail) {
        onlinePassword = "";
        BmobQuery<Tutor> tutorBmobQuery = new BmobQuery<>();
        tutorBmobQuery.addWhereEqualTo("email", localEmail);
        tutorBmobQuery.findObjects(new FindListener<Tutor>() {
            @Override
            public void done(List<Tutor> list, BmobException e) {
                if (e == null) {
                    onlinePassword = list.get(0).getPassword();

                    if (MD5.generatePassword(password).equals(onlinePassword)) {
                        isPass = true;
                        userRole = TUTOR;
                        tutor = list.get(0);
                        objectId = list.get(0).getObjectId();
//                        Log.e("tut",onlinePassword);
//                        Log.e("tut",MD5.generatePassword(password)+"");
//                        Log.e("tut",userRole+","+MD5.generatePassword(password).equals(onlinePassword)+"");
                        isRunned = false;
                    } else {
                        isRunned = false;
                    }
                } else {
                    errorMsg = e.toString();
//                    Log.e("Login_Tutor",e.toString());

                }
            }
        });
        if (!isPass) {
            checkStudentEmail(localEmail);
        }

    }

    private void checkStudentEmail(String localEmail) {
        onlinePassword = "";
        BmobQuery<Student> studentBmobQuery = new BmobQuery<>();
        studentBmobQuery.addWhereEqualTo("email", localEmail);

        studentBmobQuery.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    onlinePassword = list.get(0).getPassword();
                    if (MD5.generatePassword(password).equals(onlinePassword)) {

                        isPass = true;
                        userRole = STUDENT;
                        student = list.get(0);
                        objectId = list.get(0).getObjectId();
//                        Log.e("stu",onlinePassword);
//                        Log.e("stu",onlinePassword);
//                        Log.e("stu",MD5.generatePassword(password)+"");
//                        Log.e("stu",userRole+","+MD5.generatePassword(password).equals(onlinePassword)+"");
                        isRunned = false;
                    } else {
                        isRunned = false;
                    }
                } else {
                    errorMsg = e.toString();
                    //Log.e("Login_Stu",e.toString());
                    isRunned = false;
                }
            }

        });
        if (!isPass) {
            userRole = 0;
            isRunned = false;
        }
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

    private class LoginAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Bmob.initialize(LoginActivity.this, "8f00c8d01878b845809a7343475d1fc1");
            checkAdminEmail(email);
            isRunned = true;
            checkState();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (userRole == 0) {
                errorTextView.setText("Please make sure your email and password are correct!");
                waitLayout.setVisibility(View.GONE);
                logBtn.setVisibility(View.VISIBLE);
                passwordEditText.setEnabled(true);
                emailEditText.setEnabled(true);
            } else {
                String userId;
                String welcomeToast = "Welcome, ";
                if (userRole == ADMIN) {
                    welcomeToast += admin.getFirstName() + "!";
                    Admin admin = new Admin();
                    admin.setAndroidId(ANDROID_ID);
                    admin.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e("Update", "Success");
                            } else {
                                Log.e("Update", "Failed");
                            }
                        }
                    });


                } else if (userRole == STUDENT) {
                    welcomeToast += student.getFirstName() + "!";
                    Student student = new Student();
                    student.setAndroidId(ANDROID_ID);
                    student.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e("Update", "Success");
                            } else {
                                Log.e("Update", "Failed");
                            }
                        }
                    });

                } else if (userRole == TUTOR) {
                    welcomeToast += tutor.getFirstName() + "!";
                    Tutor tutor = new Tutor();
                    tutor.setAndroidId(ANDROID_ID);
                    tutor.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e("Update", "Success");
                            } else {
                                Log.e("Update", "Failed");
                            }
                        }
                    });
                }
                Toast.makeText(getApplicationContext(), welcomeToast,
                        Toast.LENGTH_SHORT).show();

                welcomeToast = "Welcome, ";
                userData = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("objectId", objectId);
                editor.putInt("userRole", userRole);
                editor.putBoolean("isIn", true);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("newLogin", true);
                startActivity(intent);
                finish();
            }
        }
    }
}

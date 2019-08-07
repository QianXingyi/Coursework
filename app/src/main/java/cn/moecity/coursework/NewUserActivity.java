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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backBtn, signUpButton;
    private LinearLayout waitLayout;
    private String ANDROID_ID;
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;
    private EditText passwordEditText, confirmEditText, emailEditText, firstNameEditText, lastNameEditText;
    private TextView alertText;
    private String newPassword, confirmPassword;
    private String firstName, lastName;
    private String newEmail;
    private int userRole = 0;
    private boolean isEmailUsable = false;
    private boolean isEmailUsableAdm = false;
    private boolean isEmailUsableStu = false;
    private boolean isEmailUsableTut = false;
    private String objectId = "fff";
    private Tutor tutor = new Tutor();
    private Student student = new Student();
    private SharedPreferences userData;
    private boolean isRunned = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ANDROID_ID = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Bmob.initialize(NewUserActivity.this, "8f00c8d01878b845809a7343475d1fc1");
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        signUpButton = findViewById(R.id.signUp_btn);
        signUpButton.setOnClickListener(this);
        backBtn = findViewById(R.id.backLogBtn);
        backBtn.setOnClickListener(this);
        passwordEditText = findViewById(R.id.newPassText);
        confirmEditText = findViewById(R.id.repeatPassText);
        emailEditText = findViewById(R.id.newEmailText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        alertText = findViewById(R.id.alertText);
        waitLayout = findViewById(R.id.waitlayout1);
        alertText.setText("");
    }

    private int roleSelect() {
        int i = 0;
        if (radioButton1.isChecked()) {
            return STUDENT;
        } else if (radioButton2.isChecked()) {
            return TUTOR;
        } else return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUp_btn:
                newEmail = emailEditText.getText().toString();
                newPassword = passwordEditText.getText().toString();
                confirmPassword = confirmEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();
                firstName = firstNameEditText.getText().toString();

                if (newEmail.equals("")) {
                    alertText.setText("Email can't be empty!");
                } else if (!newEmail.contains("@") || !newEmail.contains(".")) {
                    alertText.setText("Wrong email format!");
                } else if (firstName.equals("") || lastName.equals("")) {
                    alertText.setText("Please enter your name!");
                } else if (newPassword.equals("") || confirmPassword.equals("")) {
                    alertText.setText("Password can't be blank!");
                } else if (!confirmPassword.equals(newPassword)) {
                    alertText.setText("Enter the same password twice!");
                } else {
                    alertText.setText("");
                    userRole = roleSelect();
                    waitLayout.setVisibility(View.VISIBLE);
                    signUpButton.setVisibility(View.GONE);
                    passwordEditText.setEnabled(false);
                    confirmEditText.setEnabled(false);
                    emailEditText.setEnabled(false);
                    if (userRole == TUTOR) {
                        insertTutor();


                    } else if (userRole == STUDENT) {
                        insertStudent();
                    }
                }
                break;
            case R.id.backLogBtn:
                Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }

    private void checkState() {
        if (isRunned) {
            try {
                Thread.sleep(10);
                //Log.e("msg","block");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkState();
        }

    }

    private void checkAdminUsed() {
        BmobQuery<Admin> adminBmobQuery = new BmobQuery<>();
        adminBmobQuery.addWhereEqualTo("email", newEmail);
        adminBmobQuery.findObjects(new FindListener<Admin>() {
            @Override
            public void done(List<Admin> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        Toast.makeText(getApplicationContext(), "This email has been used!",
                                Toast.LENGTH_SHORT).show();
                        isEmailUsableAdm = false;
                        waitLayout.setVisibility(View.GONE);
                        signUpButton.setVisibility(View.VISIBLE);
                        passwordEditText.setEnabled(true);
                        confirmEditText.setEnabled(true);
                        emailEditText.setEnabled(true);
                    } else {
                        isEmailUsableAdm = true;
                    }
                    isRunned = false;

                } else {
                    isEmailUsableAdm = true;

                    Log.e("msg", e.toString());
                    isRunned = false;
                }

            }
        });

    }

    private void checkTutorUsed() {
        BmobQuery<Tutor> tutorBmobQuery = new BmobQuery<>();
        tutorBmobQuery.addWhereEqualTo("email", newEmail);
        tutorBmobQuery.findObjects(new FindListener<Tutor>() {
            @Override
            public void done(List<Tutor> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        Toast.makeText(getApplicationContext(), "This email has been used!",
                                Toast.LENGTH_SHORT).show();
                        isEmailUsableTut = false;
                        waitLayout.setVisibility(View.GONE);
                        signUpButton.setVisibility(View.VISIBLE);
                        passwordEditText.setEnabled(true);
                        confirmEditText.setEnabled(true);
                        emailEditText.setEnabled(true);
                    } else {
                        isEmailUsableTut = true;
                    }
                    isRunned = false;

                } else {
                    isEmailUsableTut = true;
                    Log.e("msg", e.toString());
                    isRunned = false;

                }
            }
        });
    }

    private void checkStudentUsed() {

        BmobQuery<Student> studentBmobQuery = new BmobQuery<>();
        studentBmobQuery.addWhereEqualTo("email", newEmail);
        studentBmobQuery.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        Toast.makeText(getApplicationContext(), "This email has been used!",
                                Toast.LENGTH_SHORT).show();
                        isEmailUsableStu = false;
                        waitLayout.setVisibility(View.GONE);
                        signUpButton.setVisibility(View.VISIBLE);
                        passwordEditText.setEnabled(true);
                        confirmEditText.setEnabled(true);
                        emailEditText.setEnabled(true);
                    } else {
                        isEmailUsableStu = true;
                    }
                    isRunned = false;

                } else {
                    isEmailUsableStu = true;
                    Log.e("msg", e.toString());
                    isRunned = false;
                }
            }
        });


    }

    private void insertTutor() {
        TutorAsyncTask tutorAsyncTask = new TutorAsyncTask();
        tutorAsyncTask.execute(5000);
    }

    private void insertStudent() {
        StudentAsyncTask studentAsyncTask = new StudentAsyncTask();
        studentAsyncTask.execute(5000);
    }

    private void nextPage() {
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("objectId", objectId);
        Log.e("msg", "new" + objectId + "," + userRole);
        editor.putInt("userRole", userRole);
        editor.commit();
        String userId;
        String welcomeToast = "Welcome, ";
        if (userRole == STUDENT) {
            welcomeToast += student.getFirstName() + "!";

        } else if (userRole == TUTOR) {
            welcomeToast += tutor.getFirstName() + "!";
        }
        Toast.makeText(getApplicationContext(), welcomeToast,
                Toast.LENGTH_SHORT).show();
        welcomeToast = "Welcome, ";
        Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
        intent.putExtra("newLogin", true);
        startActivity(intent);
        finish();

    }

    private void checkUsed() {
        checkAdminUsed();
        isRunned = true;
        checkState();

        checkTutorUsed();
        isRunned = true;
        checkState();

        checkStudentUsed();
        isRunned = true;
        checkState();
    }

    private class TutorAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            checkUsed();
            if (isEmailUsableAdm && isEmailUsableStu && isEmailUsableTut)
                isEmailUsable = true;
            if (isEmailUsable) {
                tutor.setLogged(false);
                tutor.setEmail(newEmail);
                tutor.setFirstName(firstName);
                tutor.setLastName(lastName);
                tutor.setAndroidId(ANDROID_ID);
                tutor.setFacebookId("0");
                tutor.setPassword(MD5.generatePassword(newPassword));
                tutor.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            objectId = s;
                            Log.e("objectId", objectId);

                        } else {
                            Log.e("msg", e.toString());
                        }
                    }
                });


            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (isEmailUsable) {

                waitLayout.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);
                passwordEditText.setEnabled(true);
                confirmEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                nextPage();
            }
        }
    }

    private class StudentAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            checkUsed();

            if (isEmailUsableAdm && isEmailUsableStu && isEmailUsableTut)
                isEmailUsable = true;
            if (isEmailUsable) {
                student.setEmail(newEmail);
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setAndroidId(ANDROID_ID);
                student.setFacebookId("0");
                student.setPassword(MD5.generatePassword(newPassword));
                student.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            objectId = s;
                            Log.e("objectId", objectId);
                        } else {
                            Log.e("msg", e.toString());
                        }
                    }
                });


            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (isEmailUsable) {

                waitLayout.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);
                passwordEditText.setEnabled(true);
                confirmEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                nextPage();
            }
        }
    }
}

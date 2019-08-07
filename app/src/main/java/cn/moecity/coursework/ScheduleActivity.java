package cn.moecity.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private SqlDAO sqlDAO;
    private List<SavedTutor> savedTutorList;
    private List<SavedStudent> savedStudentList;
    private String tutId;
    private String studentName;
    private TextView showSchedule;
    private String stuId, time, tutorName;
    Button btnDatePicker, btnTimePicker, submit_time;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private int userRole;
    private SharedPreferences userData;

    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        submit_time = (Button) findViewById(R.id.submit_time);
        txtDate = (EditText) findViewById(R.id.in_date);
        txtTime = (EditText) findViewById(R.id.in_time);
        showSchedule = (TextView) findViewById(R.id.showSchedule);
        Calendar c = Calendar.getInstance();
        txtDate.setText("01/01/2019");
        txtTime.setText("00:00");
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        userRole = userData.getInt("userRole", 0);
        switch (userRole) {
            case ADMIN:
                break;
            case STUDENT:
                schStu();
                break;
            case TUTOR:
                schTut();
                break;
        }


    }


    ///////////////////////student/////////////////////////
    private void schStu() {
        System.out.println("student");
        //get student id and name from sharedpreference
        SharedPreferences pref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        stuId = pref.getString("objectId", "empty");
        System.out.println("stuid=" + stuId);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        //get tutorname from intent

        intent = getIntent();
        tutorName = intent.getStringExtra("NName");
        tutId=intent.getStringExtra("Id");
        sqlDAO = new SqlDAO(getApplicationContext());
        savedTutorList = sqlDAO.getAllTutor();
        savedStudentList = sqlDAO.getAllStudent();

        //get tutId by tutor name
        System.out.println("tutid=" + tutId);

        //get studentName by studentId
        for (int i = 0; i < savedStudentList.size(); i++) {
            if (savedStudentList.get(i).getObjectId().equals(stuId)) {
                studentName = savedStudentList.get(i).getFirstName() + savedStudentList.get(i).getLastName();
            }
        }

        //show the schedule time
        BmobQuery<Schedule> eq1 = new BmobQuery<Schedule>();
        eq1.addWhereEqualTo("stuId", stuId);
        BmobQuery<Schedule> eq2 = new BmobQuery<Schedule>();
        eq2.addWhereEqualTo("tutId", tutId);
        List<BmobQuery<Schedule>> andQuerys = new ArrayList<BmobQuery<Schedule>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Schedule> query = new BmobQuery<Schedule>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> object, BmobException e) {
                if (e == null) {
                    String t = object.get(0).getSchedule_Time();
                    showSchedule.setText("Date: " + t.split(";")[0] + "\nTime: " + t.split(";")[1]);
                } else {
                    Log.i("bmob", "error：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

        submit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = txtDate.getText().toString() + ";" + txtTime.getText().toString();

                //judge if the data exist
                BmobQuery<Schedule> eq1 = new BmobQuery<Schedule>();
                eq1.addWhereEqualTo("stuId", stuId);
                BmobQuery<Schedule> eq2 = new BmobQuery<Schedule>();
                eq2.addWhereEqualTo("tutId", tutId);

                List<BmobQuery<Schedule>> andQuerys = new ArrayList<BmobQuery<Schedule>>();
                andQuerys.add(eq1);
                andQuerys.add(eq2);
                BmobQuery<Schedule> query = new BmobQuery<Schedule>();
                query.and(andQuerys);
                query.findObjects(new FindListener<Schedule>() {
                    @Override
                    public void done(List<Schedule> object, BmobException e) {
                        if (e == null) {
                            if (object.size() != 0) {
                                //The data existing, update the dataset
                                Schedule p2 = new Schedule();
                                p2.setSchedule_Time(time);
                                p2.update("" + object.get(0).getObjectId(), new UpdateListener() {

                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                        }
                                    }

                                });

                            } else {
                                //add time into dataset
                                Schedule p2 = new Schedule();
                                p2.setSchedule_Time(time);
                                p2.setStuId(stuId);
                                p2.setStudent_Name(studentName);
                                p2.setTutor_Name(tutorName);
                                p2.setTutId(tutId);
                                p2.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("bmob", "ERROR：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });


            }
        });

    }

    /////////////////tutor//////////////////
    private void schTut() {
        System.out.println("tutor");
        //get student id and name from sharedpreference
        SharedPreferences pref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        tutId = pref.getString("objectId", "empty");
        System.out.println("tutid=" + tutId);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        //get tutorname from intent

        intent = getIntent();
        studentName = intent.getStringExtra("NName");
        stuId=intent.getStringExtra("id");
        sqlDAO = new SqlDAO(getApplicationContext());
        savedTutorList = sqlDAO.getAllTutor();
        savedStudentList = sqlDAO.getAllStudent();

        //get stuId by student name

        //get tutorName by tutorId
        for (int i = 0; i < savedTutorList.size(); i++) {
            if (savedTutorList.get(i).getObjectId().equals(tutId)) {
                studentName = savedTutorList.get(i).getFirstName() + savedTutorList.get(i).getLastName();
            }
        }

        //show the schedule time
        BmobQuery<Schedule> eq1 = new BmobQuery<Schedule>();
        eq1.addWhereEqualTo("stuId", stuId);
        BmobQuery<Schedule> eq2 = new BmobQuery<Schedule>();
        eq2.addWhereEqualTo("tutId", tutId);
        List<BmobQuery<Schedule>> andQuerys = new ArrayList<BmobQuery<Schedule>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Schedule> query = new BmobQuery<Schedule>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> object, BmobException e) {
                if (e == null) {
                    String t = object.get(0).getSchedule_Time();
                    showSchedule.setText("Date: " + t.split(";")[0] + "\nTime: " + t.split(";")[1]);
                } else {
                    Log.i("bmob", "ERROR：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

        submit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = txtDate.getText().toString() + ";" + txtTime.getText().toString();

                //judge if the data exist
                BmobQuery<Schedule> eq1 = new BmobQuery<Schedule>();
                eq1.addWhereEqualTo("stuId", stuId);
                BmobQuery<Schedule> eq2 = new BmobQuery<Schedule>();
                eq2.addWhereEqualTo("tutId", tutId);

                List<BmobQuery<Schedule>> andQuerys = new ArrayList<BmobQuery<Schedule>>();
                andQuerys.add(eq1);
                andQuerys.add(eq2);
                BmobQuery<Schedule> query = new BmobQuery<Schedule>();
                query.and(andQuerys);
                query.findObjects(new FindListener<Schedule>() {
                    @Override
                    public void done(List<Schedule> object, BmobException e) {
                        if (e == null) {
                            if (object.size() != 0) {
                                //The data existing, update the dataset
                                Schedule p2 = new Schedule();
                                p2.setSchedule_Time(time);
                                p2.update("" + object.get(0).getObjectId(), new UpdateListener() {

                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                        }
                                    }

                                });

                            } else {
                                //add time into dataset
                                Schedule p2 = new Schedule();
                                p2.setSchedule_Time(time);
                                p2.setStuId(stuId);
                                p2.setStudent_Name(studentName);
                                p2.setTutor_Name(tutorName);
                                p2.setTutId(tutId);
                                p2.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.i("bmob", "ERROR：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            String dd = dayOfMonth + "";
                            String mm = (monthOfYear + 1) + "";
                            if (dayOfMonth < 10)
                                dd = "0" + dayOfMonth;
                            else dd = dayOfMonth + "";
                            if ((monthOfYear + 1) < 10)
                                mm = "0" + (monthOfYear + 1);
                            else mm = (monthOfYear + 1) + "";
                            txtDate.setText(dd + "/" + mm + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String h = hourOfDay + "";
                            String m = minute + "";
                            if (hourOfDay < 10)
                                h = "0" + hourOfDay;
                            else h = hourOfDay + "";
                            if (minute < 10)
                                m = "0" + minute;
                            else m = minute + "";

                            txtTime.setText(h + ":" + m);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
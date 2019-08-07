package cn.moecity.coursework;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class WelcomeActivity extends AppCompatActivity {
    private String ANDROID_ID, online_android_id;
    private SharedPreferences userData;
    private String objectId;
    private int userRole;
    private Boolean isIn = false;
    private boolean isRunned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ANDROID_ID = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        setContentView(R.layout.activity_welcome);
        Bmob.initialize(this, "8f00c8d01878b845809a7343475d1fc1");
        userData = getSharedPreferences("userData", MODE_PRIVATE);
        objectId = userData.getString("objectId", "ffffffffff");
        userRole = userData.getInt("userRole", 0);
        isIn = userData.getBoolean("isIn", false);
        if (isIn) {
            WelcomAsyncTask welcomAsyncTask = new WelcomAsyncTask();
            welcomAsyncTask.execute(5000);
        } else {
            DelayAsyncTask delayAsyncTask = new DelayAsyncTask();
            delayAsyncTask.execute(5000);
        }
    }

    private void checkAndroidId() {
        if (online_android_id.equals(ANDROID_ID)) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            userData = getSharedPreferences("userData", MODE_PRIVATE);
            SharedPreferences.Editor editor = userData.edit();
            editor.putString("objectId", "ffffffffff");
            editor.putInt("userRole", 0);
            editor.putBoolean("isIn", false);
            editor.commit();
            AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
            builder.setTitle("Waring");
            builder.setMessage("Your account is already logged in on another device.\nPlease don't share your password with anyone!");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private class DelayAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(1000);
                //Log.e("msg","block");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class WelcomAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Bmob.initialize(WelcomeActivity.this, "8f00c8d01878b845809a7343475d1fc1");
            BmobQuery<Admin> bmobQuery = new BmobQuery<Admin>();
            bmobQuery.getObject(objectId, new QueryListener<Admin>() {
                @Override
                public void done(Admin admin, BmobException e) {
                    if (e == null) {
                        online_android_id = admin.getAndroidId();
                        Log.e("id", online_android_id);
                        checkAndroidId();
                    } else {
                        BmobQuery<Tutor> bmobQuery1 = new BmobQuery<Tutor>();
                        bmobQuery1.getObject(objectId, new QueryListener<Tutor>() {
                            @Override
                            public void done(Tutor tutor, BmobException e) {
                                if (e == null) {

                                    online_android_id = tutor.getAndroidId();

                                    Log.e("id", online_android_id);
                                    checkAndroidId();
                                } else {
                                    BmobQuery<Student> bmobQuery2 = new BmobQuery<Student>();
                                    bmobQuery2.getObject(objectId, new QueryListener<Student>() {
                                        @Override
                                        public void done(Student student, BmobException e) {
                                            if (e == null) {
                                                online_android_id = student.getAndroidId();

                                                Log.e("id", online_android_id);
                                                checkAndroidId();

                                            } else {
                                                Toast.makeText(WelcomeActivity.this,
                                                        "Please check the Internet", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();


                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

}

package cn.moecity.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences userData;
    private SqlDAO sqlDAO;
    private Button logOutBtn, aBtn, bBtn;
    private String objectId;
    private int userRole;
    private Boolean isClicked = false;
    private EditText myFirst, myLast, myEmail, myPass;
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private List<Student> studentList;
    private List<Tutor> tutorList;
    private List<Admin> adminList;
    private Admin savedAdmin;
    private List<Subject> subjectList;
    private List<Chat> chatList;
    private List<StuTut> stuTutList, savedStuTutList;
    private List<TutSub> tutSubList, savedTutSubList;
    private List<SavedChat> savedChats;
    private List<SavedStudent> savedStudentList;
    private List<SavedTutor> savedTutorList;
    private List<SavedSubject> savedSubjectList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        logOutBtn = (Button) view.findViewById(R.id.logOut);
        userData = this.getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        Bmob.initialize(getContext(), "8f00c8d01878b845809a7343475d1fc1");
        objectId = userData.getString("objectId", "ffffffffff");
        userRole = userData.getInt("userRole", 0);
        myFirst = (EditText) view.findViewById(R.id.myFirst);
        myLast = (EditText) view.findViewById(R.id.myLast);
        myEmail = (EditText) view.findViewById(R.id.myEmail);
        myPass = (EditText) view.findViewById(R.id.myPass);
        aBtn = (Button) view.findViewById(R.id.buttonA);
        bBtn = (Button) view.findViewById(R.id.buttonB);
        aBtn.setOnClickListener(this);
        bBtn.setOnClickListener(this);
        sqlDAO = new SqlDAO(getContext());
        updateData();
        setView();
        if (userRole == ADMIN) {
            BmobQuery<Admin> bmobQuery = new BmobQuery<Admin>();
            bmobQuery.getObject(objectId, new QueryListener<Admin>() {
                @Override
                public void done(Admin admin, BmobException e) {
                    if (e == null) {
                        savedAdmin = admin;
                        myFirst.setText(savedAdmin.getFirstName());
                        myLast.setText(savedAdmin.getLastName());
                        myEmail.setText(savedAdmin.getEmail());
                    } else {
                        Log.e("BMOB", e.toString());

                    }
                }
            });
        } else if (userRole == STUDENT) {
            for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                if (savedStudentList.get(i).getObjectId().equals(objectId)) {
                    SavedStudent student = savedStudentList.get(i);
                    myFirst.setText(student.getFirstName());
                    myLast.setText(student.getLastName());
                    myEmail.setText(student.getEmail());
                }
            }

        } else if (userRole == TUTOR) {
            for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                if (savedTutorList.get(i).getObjectId().equals(objectId)) {
                    SavedTutor tutor = savedTutorList.get(i);
                    myFirst.setText(tutor.getFirstName());
                    myLast.setText(tutor.getLastName());
                    myEmail.setText(tutor.getEmail());
                }
            }
        }

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("objectId", "ffffff");
                editor.putInt("userRole", 0);
                editor.putBoolean("isIn", false);
                editor.commit();
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;

    }

    private void updateData() {
        savedTutSubList = sqlDAO.getAllTutSub();
        savedStuTutList = sqlDAO.getAllStuTut();
        savedStudentList = sqlDAO.getAllStudent();
        savedTutSubList = sqlDAO.getAllTutSub();
        savedTutorList = sqlDAO.getAllTutor();

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
                Toast.makeText(getContext(), "Password has been changed!", Toast.LENGTH_SHORT).show();
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
        }
        Log.e("student", sqlDAO.getAllStudent().toString());
        Log.e("s1", student.toString());
        s1.update(student.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Toast.makeText(getContext(), "Password has been changed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdmin(Admin admin) {
        admin.update(admin.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Toast.makeText(getContext(), "Password has been changed!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("objectId", "ffffff");
                editor.putInt("userRole", 0);
                editor.putBoolean("isIn", false);
                editor.commit();
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


    private void setView() {
        if (isClicked) {
            myPass.setEnabled(true);
            myEmail.setEnabled(true);
            myFirst.setEnabled(true);
            myLast.setEnabled(true);
            aBtn.setText("Keep");
            bBtn.setText("Submit");

        } else {
            myPass.setEnabled(false);
            myEmail.setEnabled(false);
            myFirst.setEnabled(false);
            myLast.setEnabled(false);
            aBtn.setText("Edit");
            bBtn.setText("Submit");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonA:
                isClicked = !isClicked;
                setView();
                break;
            case R.id.buttonB:
                switch (userRole) {
                    case STUDENT:
                        for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                            if (savedStudentList.get(i).getObjectId().equals(objectId)) {
                                SavedStudent student = savedStudentList.get(i);
                                student.setEmail(myEmail.getText().toString());
                                if (!(myPass.getText().toString().length() == 0)) {
                                    student.setPassword(MD5.generatePassword(myPass.getText().toString()));
                                    student.setFirstName(myFirst.getText().toString());
                                    student.setLastName(myLast.getText().toString());
                                    updateStu(student);
                                    Log.e("msg", myPass.getText().toString());
                                } else {
                                    Toast.makeText(getContext(), "The new password cannot be empty!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                        break;
                    case TUTOR:
                        for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                            if (savedTutorList.get(i).getObjectId().equals(objectId)) {
                                SavedTutor tutor = savedTutorList.get(i);
                                tutor.setEmail(myEmail.getText().toString());
                                if (!(myPass.getText().toString().length() == 0)) {
                                    tutor.setPassword(MD5.generatePassword(myPass.getText().toString()));
                                    Log.e("pass", myPass.getText().toString());
                                    tutor.setFirstName(myFirst.getText().toString());
                                    tutor.setLastName(myLast.getText().toString());
                                    updateTut(tutor);
                                    Log.e("msg", tutor.toString());
                                } else {
                                    Toast.makeText(getContext(), "The new password cannot be empty!", Toast.LENGTH_SHORT).show();
                                }
                                ;
                            }

                        }
                        break;
                    case ADMIN:
                        if (savedAdmin.getObjectId().equals(objectId)) {
                            Admin admin = savedAdmin;
                            admin.setEmail(myEmail.getText().toString());
                            if (!(myPass.getText().toString().length() == 0)) {
                                admin.setPassword(MD5.generatePassword(myPass.getText().toString()));
                                Log.e("pass", myPass.getText().toString());
                                admin.setFirstName(myFirst.getText().toString());
                                admin.setLastName(myLast.getText().toString());
                                updateAdmin(admin);
                                Log.e("msg", admin.toString());
                            } else {
                                Toast.makeText(getContext(), "The new password cannot be empty!", Toast.LENGTH_SHORT).show();
                            }
                            ;
                        }

                        break;

                }
                break;
        }

    }


}

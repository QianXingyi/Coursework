package cn.moecity.coursework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.Context.MODE_PRIVATE;

public class ItemFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listView;
    private Boolean isUsed = false;
    private SqlDAO sqlDAO;
    private Button button1, button2;
    private List<SavedStudent> savedStudentList;
    private List<SavedTutor> savedTutorList;
    private List<SavedSubject> savedSubjectList;
    private List<TutSub> savedTutSubList;
    private List<StuTut> savedStuTutList;
    private SavedTutor savedTutor;
    private SavedStudent savedStudent;
    private SharedPreferences userData;
    private SavedTutor updatedTutor;
    private String objectId;
    private int userRole;
    private Spinner subjectSpinner;
    private List<String> stringList = new ArrayList<String>();
    ;
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private ArrayAdapter<String> arrayAdapter;
    private ListViewAdapter listViewAdapter;
    private LayoutInflater thisInflater;
    private Boolean isAll = true;
    private int temposition;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        isAll = true;
        thisInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_item_base, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        button1 = (Button) view.findViewById(R.id.button);
        button2 = (Button) view.findViewById(R.id.button2);
        button1.setBackgroundColor(getResources().getColor(R.color.colorFront));
        button1.setTextColor(getResources().getColor(R.color.colorBack));
        button2.setBackgroundColor(getResources().getColor(R.color.colorBack));
        button2.setTextColor(getResources().getColor(R.color.colorFront));
        Bmob.initialize(getContext(), "8f00c8d01878b845809a7343475d1fc1");
        subjectSpinner = (Spinner) view.findViewById(R.id.spinnerView);
        userData = this.getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        objectId = userData.getString("objectId", "ffffffffff");
        userRole = userData.getInt("userRole", 0);
        if (userRole == STUDENT) {
            button1.setText("View Tutors");
            button2.setText("View My Tutors");
        } else if (userRole == TUTOR) {
            button1.setText("View Subjects");
            button2.setText("View Students");
        } else if (userRole == ADMIN) {
            button1.setText("View Users");
            button2.setText("View Subjects");

        }
        Context context = getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        try {
            List<Map<String, Object>> list = getData();
            //Log.e("size", list.size() + "");
            listViewAdapter = new ListViewAdapter(list, inflater, context);
            listView.setAdapter(listViewAdapter);
            listView.setOnItemClickListener(this);
            setSpinner();
            if (userRole == STUDENT) {
                for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
                    if (savedStudentList.get(i).getObjectId().equals(objectId)) {
                        savedStudent = savedStudentList.get(i);
                    }

                }
            } else if (userRole == TUTOR) {
                for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                    if (savedTutorList.get(i).getObjectId().equals(objectId)) {
                        savedTutor = savedTutorList.get(i);
                    }

                }
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void putTutors(List<Map<String, Object>> list) {
        for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
            String content = "";
            Map<String, Object> map = new HashMap<String, Object>();
            String con = "";
            map.put("itemName", savedTutorList.get(i).getFirstName() + " " + savedTutorList.get(i).getLastName());
            content += savedTutorList.get(i).getEmail() + "\n";
            con += savedTutorList.get(i).getEmail() + "\n";
            for (int j = 0; j < getSizeOfList(savedTutSubList); j++) {
                if (savedTutSubList.get(j).getTutId().equals(savedTutorList.get(i).getObjectId())) {
                    for (int k = 0; k < getSizeOfList(savedSubjectList); k++) {
                        if (savedSubjectList.get(k).getObjectId().equals(savedTutSubList.get(j).getSubId())) {
                            content += savedSubjectList.get(k).getName() + "| ";
                        }
                    }
                }
            }

            map.put("content", content);
            map.put("objectId", savedTutorList.get(i).getObjectId());
            map.put("role", TUTOR + "");
            if (isAll && userRole == STUDENT)
                list.add(map);
            else if (userRole == STUDENT) {
                Set<String> hashSet = new HashSet<String>();
                for (int j = 0; j < getSizeOfList(savedStuTutList); j++) {
                    if (map.get("objectId").toString().equals(savedStuTutList.get(j).getTutId())
                            && savedStuTutList.get(j).getStuId().equals(objectId)) {

                        for (int k = 0; k < getSizeOfList(savedSubjectList); k++) {
                            if (savedSubjectList.get(k).getObjectId().equals(savedStuTutList.get(j).getSubId())) {
                                con += savedSubjectList.get(k).getName() + "| ";
                            }
                        }

                        map.put("content", con);
                        if (!hashSet.contains(savedStuTutList.get(j).getTutId())) {
                            list.add(map);
                            hashSet.add(savedStuTutList.get(j).getTutId());
                        }

                    }
                }
            } else if (userRole == ADMIN) {
                if (savedTutorList.get(i).getLogged()) {
                    content += "\nLogged Teacher";
                } else {
                    content += "\nNot logged Teacher";
                }
                map.put("content", content);
                list.add(map);

            }
        }
        if (getSizeOfList(list) == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemName", "No Data");
            map.put("objectId", "No Data");
            map.put("role", "No Data");
            map.put("content", "No Data");
            list.add(map);
        }

    }

    private void putSubjects(List<Map<String, Object>> list) {
        String content = "Not Taken";

        for (int i = 0; i < getSizeOfList(savedSubjectList); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (userRole == ADMIN) {
                content = savedSubjectList.get(i).getSubNo() + "";
            }
            map.put("itemName", savedSubjectList.get(i).getName());
            //Log.e("tutor",savedSubjectList.toString());

            for (int j = 0; j < getSizeOfList(savedTutSubList); j++) {
                if (savedTutSubList.get(j).getTutId().equals(objectId) &&
                        savedTutSubList.get(j).getSubId().equals(savedSubjectList.get(i).getObjectId())
                ) {
                    //Log.e("taken",savedSubjectList.get(j).toString());
                    content = "Taken";
                }
            }
            map.put("content", content);
            if (userRole != ADMIN)
                content = "Not Taken";
            map.put("objectId", savedSubjectList.get(i).getObjectId());
            map.put("role", "0");
            list.add(map);
        }
        if (getSizeOfList(list) == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemName", "No Data");
            map.put("objectId", "No Data");
            map.put("role", "No Data");
            map.put("content", "No Data");
            list.add(map);
        }
    }

    private void putStudents(List<Map<String, Object>> list) {
        for (int i = 0; i < getSizeOfList(savedStudentList); i++) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("itemName", savedStudentList.get(i).getFirstName() + " " + savedStudentList.get(i).getLastName());

            map.put("objectId", savedStudentList.get(i).getObjectId());
            map.put("role", STUDENT + "");
            if (userRole == TUTOR) {
                Set<String> hashSet = new HashSet<String>();
                String content = savedStudentList.get(i).getEmail() + "\n";
                Log.e("content", savedStuTutList.toString() + savedSubjectList.toString());
                for (int j = 0; j < getSizeOfList(savedStuTutList); j++) {
                    if (savedStuTutList.get(j).getTutId().equals(objectId)
                            && savedStuTutList.get(j).getStuId().equals(savedStudentList.get(i).getObjectId())) {
                        for (int k = 0; k < getSizeOfList(savedSubjectList); k++) {
                            if (savedStuTutList.get(j).getSubId().equals(savedSubjectList.get(k).getObjectId())) {
                                content += savedSubjectList.get(k).getName() + "| ";

                            }

                        }
                        map.put("content", content);
                        if (!hashSet.contains(savedStuTutList.get(j).getStuId())) {
                            list.add(map);
                        }
                        hashSet.add(savedStudentList.get(i).getObjectId());

                        Log.e("saved", hashSet.toString());
                    }
                }

            } else if (userRole == ADMIN) {
                map.put("content", savedStudentList.get(i).getEmail() + "\nStudent");
                list.add(map);
            } else {
                map.put("content", savedStudentList.get(i).getEmail());
                list.add(map);

            }

        }
        if (getSizeOfList(list) == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemName", "No Data");
            map.put("objectId", "No Data");
            map.put("role", "No Data");
            map.put("content", "No Data");
            list.add(map);
        }
    }

    private void setSpinner() {
        stringList.add("All Subjects");
        if (stringList.size() > 0) stringList.clear();
        stringList.add("All Subjects");
        //Log.e("list",savedSubjectList.toString());
        for (int i = 0; i < getSizeOfList(savedSubjectList); i++) {
            stringList.add(savedSubjectList.get(i).getName());
        }
        //Log.e("list",savedSubjectList.toString());
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stringList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        subjectSpinner.setAdapter(arrayAdapter);
        subjectSpinner.setSelection(0);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                     @Override
                                                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                         if (userRole == STUDENT) {
                                                             List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                                                             List<Map<String, Object>> newlist = new ArrayList<Map<String, Object>>();
                                                             if (!stringList.get(position).equals("All Subjects")) {
                                                                 list = new ArrayList<Map<String, Object>>();
                                                                 putTutors(list);
                                                                 for (int i = 0; i < getSizeOfList(list); i++) {
                                                                     //Log.e("contents", i + "," + list.get(i).get("content").toString() + "\nsize" + list.size());

                                                                     if (list.get(i).get("content").toString().contains(stringList.get(position))) {
                                                                         newlist.add(list.get(i));
                                                                         // Log.e("list", list.toString() + "size" + list.size());

                                                                     }

                                                                 }
                                                             } else {
                                                                 list = new ArrayList<Map<String, Object>>();
                                                                 putTutors(list);
                                                                 newlist = list;
                                                                 // Log.e("contents", newlist.toString());

                                                             }
                                                             if (getSizeOfList(newlist) == 0) {
                                                                 Map<String, Object> map = new HashMap<String, Object>();
                                                                 map.put("itemName", "No Data");
                                                                 map.put("objectId", "No Data");
                                                                 map.put("role", "No Data");
                                                                 map.put("content", "No Data");
                                                                 newlist.add(map);

                                                             }
                                                             listViewAdapter = new ListViewAdapter(newlist, thisInflater, getContext());
                                                             listView.setAdapter(listViewAdapter);
                                                         }
                                                     }

                                                     @Override
                                                     public void onNothingSelected(AdapterView<?> parent) {

                                                     }
                                                 }

        );

    }

    private int getSizeOfList(List<?> list) {
        int size = 0;
        try {
            return list.size();
        } catch (Exception e) {
            return size;
        }

    }

    private void updateData() {

        savedTutSubList = sqlDAO.getAllTutSub();
        savedStuTutList = sqlDAO.getAllStuTut();
        savedStudentList = sqlDAO.getAllStudent();
        savedTutSubList = sqlDAO.getAllTutSub();
        savedTutorList = sqlDAO.getAllTutor();

    }

    private void goDetail(int position) {
        HashMap<String, Object> map = (HashMap<String, Object>) listViewAdapter.getItem(position);
        String oid = map.get("objectId").toString();
        String orole = map.get("role").toString();
        Log.e("msg", oid + "," + orole);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("itemId", oid);
        intent.putExtra("itemRole", orole);
        startActivity(intent);
    }

    private List<Map<String, Object>> getData() {
        sqlDAO = new SqlDAO(getContext());
        savedStudentList = sqlDAO.getAllStudent();
        savedTutorList = sqlDAO.getAllTutor();
        savedSubjectList = sqlDAO.getAllSubject();
        savedStuTutList = sqlDAO.getAllStuTut();
        //Log.e("stutut",savedStuTutList.toString());
        savedTutSubList = sqlDAO.getAllTutSub();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> secondlist = new ArrayList<Map<String, Object>>();

        if (userRole == STUDENT) {
            putTutors(list);
        } else if (userRole == TUTOR) {
            //Log.e("this is a tutor","this is a tutor");
            if (isAll) {
                //Log.e("this is a tutor","this is a isall");
                putSubjects(list);
                //Log.e("print list", list.toString());
            } else {
                putStudents(list);
            }

        } else if (userRole == ADMIN) {
            if (isAll) {
                putStudents(list);
                putTutors(secondlist);
                list.addAll(secondlist);
            } else putSubjects(list);

        }

        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                button1.setBackgroundColor(getResources().getColor(R.color.colorFront));
                button1.setTextColor(getResources().getColor(R.color.colorBack));
                button2.setBackgroundColor(getResources().getColor(R.color.colorBack));
                button2.setTextColor(getResources().getColor(R.color.colorFront));
                subjectSpinner.setSelection(0);
                isAll = true;
                List<Map<String, Object>> list = getData();
                //Log.e("size", list.size() + "");
                listViewAdapter = new ListViewAdapter(list, thisInflater, getContext());
                listView.setAdapter(listViewAdapter);
                break;
            case R.id.button2:
                button1.setBackgroundColor(getResources().getColor(R.color.colorBack));
                button1.setTextColor(getResources().getColor(R.color.colorFront));
                button2.setBackgroundColor(getResources().getColor(R.color.colorFront));
                button2.setTextColor(getResources().getColor(R.color.colorBack));
                subjectSpinner.setSelection(0);
                isAll = false;
                List<Map<String, Object>> mylist = getData();
                //Log.e("size", mylist.size() + "");
                listViewAdapter = new ListViewAdapter(mylist, thisInflater, getContext());
                listView.setAdapter(listViewAdapter);
                break;

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (userRole) {
            case TUTOR: {
                if (!savedTutor.getLogged()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Waring");
                    builder.setMessage("You are not authorized to use the system yet. \n" +
                            "Please wait for the approval result from the administrator.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {

                    goDetail(position);

                }
                updateData();
                break;
            }
            case STUDENT:
                goDetail(position);
                updateData();
                break;
            case ADMIN:
                HashMap<String, Object> map = (HashMap<String, Object>) listViewAdapter.getItem(position);
                temposition = position;
                String oid = map.get("objectId").toString();
                String orole = map.get("role").toString();
                if (Integer.parseInt(orole) == TUTOR) {
                    for (int i = 0; i < getSizeOfList(savedTutorList); i++) {
                        if (!savedTutorList.get(i).getLogged() && savedTutorList.get(i).getObjectId().equals(oid)) {
                            updatedTutor = savedTutorList.get(i);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Alert");
                            builder.setMessage("Are you going to active this tutor?");
                            builder.setCancelable(false);
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updatedTutor.setLogged(true);
                                    sqlDAO.updateTutor(updatedTutor);
                                    savedTutorList = sqlDAO.getAllTutor();
                                    Tutor tutor = new Tutor();
                                    tutor.setObjectId(updatedTutor.getObjectId());
                                    tutor.setLogged(true);
                                    tutor.update(tutor.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            goDetail(temposition);
                                        }
                                    });
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (savedTutorList.get(i).getLogged() && savedTutorList.get(i).getObjectId().equals(oid))
                            goDetail(position);
                    }
                } else if (Integer.parseInt(orole) != 0)
                    goDetail(position);
                break;

        }
    }
}

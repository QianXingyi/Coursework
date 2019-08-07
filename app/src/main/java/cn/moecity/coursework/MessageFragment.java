package cn.moecity.coursework;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.Context.MODE_PRIVATE;

public class MessageFragment extends Fragment {
    private ListView listView;
    private SqlDAO sqlDAO;
    private List<SavedTutor> savedTutorList;
    private List<SavedStudent> savedStudentList;
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int STUDENT = 3;
    private int userRole;
    private SharedPreferences userData;
    private String objectId;
    private Button loc;
    private LocationManager locationManager;
    private Double lon,lat;

    private List<StuTut> st;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.message_single_list, null);
        listView = (ListView) view.findViewById(R.id.listview);

//        List<Map<String, Object>> list=getData();

        //get student id from sharePreference
        SharedPreferences pref = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        objectId = pref.getString("objectId", "empty");
        System.out.println("objectid=" + objectId);
        userData = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        userRole = userData.getInt("userRole", 0);

        loc = (Button) view.findViewById(R.id.locSend);
        switch (userRole) {
            case ADMIN:
                break;
            case STUDENT:
                loc.setVisibility(View.GONE);
                mesStu();
                break;
            case TUTOR:
                loc.setVisibility(View.VISIBLE);
                mesTut();
                break;
        }
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoc();
            }
        });


        return view;

    }

    private void getLoc() {

        SharedPreferences pref = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        lon= Double.valueOf(pref.getFloat("lon",0));
        lat= Double.valueOf(pref.getFloat("lat",0));
        BmobQuery<Location> eq1 = new BmobQuery<Location>();
        eq1.addWhereEqualTo("tutId", objectId);
        List<BmobQuery<Location>> andQuerys = new ArrayList<BmobQuery<Location>>();
        andQuerys.add(eq1);
        BmobQuery<Location> query = new BmobQuery<Location>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Location>() {
            @Override
            public void done(List<Location> object, BmobException e) {
                if (e == null) {
                    Location location = object.get(0);
                    location.setLat(lat.toString());
                    location.setLon(lon.toString());
                    location.setTimestamp(new Date());
                    location.update(location.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });

                } else {
                    Location location=new Location();
                    location.setLat(lat.toString());
                    location.setLon(lon.toString());
                    location.setTimestamp(new Date());
                    location.setTutId(objectId);
                    location.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {

                        }
                    });
                }
            }
        });





    }

    private void mesStu() {
        //get tutor id by student id from dataset
        BmobQuery<StuTut> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("stuId", objectId);
        categoryBmobQuery.findObjects(new FindListener<StuTut>() {
            @Override
            public void done(List<StuTut> object, BmobException e) {
                if (e == null) {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
                    sqlDAO = new SqlDAO(getContext());
                    savedTutorList = sqlDAO.getAllTutor();

                    for (int i = 0; i < savedTutorList.size(); i++) {
                        for (int t = 0; t < object.size(); t++) {
                            System.out.println("save=" + savedTutorList.get(i).getObjectId());
                            System.out.println("objectid=" + object.get(t).getTutId());
                            if (savedTutorList.get(i).getObjectId().equals(object.get(t).getTutId())) {
                                String name = savedTutorList.get(i).getFirstName() + " " + savedTutorList.get(i).getLastName();
                                Map<String, Object> map = new HashMap<String, Object>();
                                System.out.println("finalname=" + name);
                                map.put("title", name);
                                map.put("info", savedTutorList.get(i).getObjectId());
                                set.add(map);
                            }
                        }
                    }
                    for (Map str : set) {
                        list.add(str);
                    }

                    listView.setAdapter(new MessageAdapter(getActivity(), list));
                } else {
                    Log.e("BMOB", e.toString());
                }
            }
        });

    }

    private void mesTut() {
        BmobQuery<StuTut> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("tutId", objectId);
        categoryBmobQuery.findObjects(new FindListener<StuTut>() {
            @Override
            public void done(List<StuTut> object, BmobException e) {
                if (e == null) {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    Set<Map<String, Object>> set = new HashSet<Map<String, Object>>();
                    sqlDAO = new SqlDAO(getContext());
//                    savedTutorList=sqlDAO.getAllTutor();
                    savedStudentList = sqlDAO.getAllStudent();

                    for (int i = 0; i < savedStudentList.size(); i++) {
                        for (int t = 0; t < object.size(); t++) {
                            if (savedStudentList.get(i).getObjectId().equals(object.get(t).getStuId())) {
                                String name = savedStudentList.get(i).getFirstName() + " " + savedStudentList.get(i).getLastName();
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("title", name);
                                map.put("info", savedStudentList.get(i).getObjectId());
                                set.add(map);
                            }
                        }
                    }
                    for (Map str : set) {
                        list.add(str);
                    }

                    listView.setAdapter(new MessageAdapter(getActivity(), list));
                } else {
                    Log.e("BMOB", e.toString());
                }
            }
        });

    }
}
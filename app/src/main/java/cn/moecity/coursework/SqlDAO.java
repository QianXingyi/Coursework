package cn.moecity.coursework;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SqlDAO extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";
    public static final int DB_VRESION = 1;

    private static final String TABLE_NAME = "chat";
    private static final String STU_NAME = "student";
    private static final String TUT_NAME = "tutor";
    private static final String STUTU_NAME = "stutut";
    private static final String SUB_NAME = "subject";
    private static final String TUTSUB_NAME = "tutsub";

    private static final String CHAT_CREATE_TABLE = "create table " + TABLE_NAME +
            " ( objectId text not null, " +
            "stuId text not null, " +
            "tutId text not null, " +
            "timestamp text," +
            "fromTo text not null," +
            "isRead text not null," +
            "content text not null);";
    private static final String STUDENT_CREATE_TABLE = "create table " + STU_NAME +
            " ( objectId text not null," +
            " firstName text not null, " +
            "lastName text not null, " +
            "email text," +
            "password text not null," +
            "androidId text not null," +
            "facebookId text not null);";
    private static final String TUTOR_CREATE_TABLE = "create table " + TUT_NAME +
            " ( objectId text not null," +
            " firstName text not null, " +
            "lastName text not null, " +
            "email text," +
            "password text not null," +
            "androidId text not null," +
            "facebookId text not null," +
            "isLogged text not null);";
    private static final String STUTUT_CREATE_TABLE = "create table " + STUTU_NAME +
            " ( stuId text not null, " +
            "tutId text not null, " +
            "subId text not null);";
    private static final String SUBJECT_CREATE_TABLE = "create table " + SUB_NAME +
            " ( objectId text not null, " +
            "name text not null, " +
            "subNo text not null);";

    private static final String TUTSUB_CREATE_TABLE = "create table " + TUTSUB_NAME +
            " (tutId text not null, " +
            "subId text not null);";

    public SqlDAO(Context context) {
        this(context, DB_NAME, null, DB_VRESION);
    }

    public SqlDAO(Context context, String name, SQLiteDatabase.CursorFactory factory,
                  int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CHAT_CREATE_TABLE);
        db.execSQL(STUDENT_CREATE_TABLE);
        db.execSQL(TUTOR_CREATE_TABLE);
        db.execSQL(STUTUT_CREATE_TABLE);
        db.execSQL(SUBJECT_CREATE_TABLE);
        db.execSQL(TUTSUB_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertChat(SavedChat entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[7];
        args[0] = entity.getObjectId();
        args[1] = entity.getStuId();
        args[2] = entity.getTutId();
        args[3] = entity.getTimestamp();
        if (entity.isFromTo()) {
            args[4] = "1";
        } else args[4] = "0";

        if (entity.isRead()) {
            args[5] = "1";
        } else args[5] = "0";
        args[6] = entity.getContent();
        db.execSQL("INSERT INTO " + TABLE_NAME + " (objectId,stuId,tutId,timestamp,fromTo,isRead,content) VALUES (?,?,?,?,?,?,?)", args);
        db.close();

    }


    public void insertStudent(SavedStudent entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[7];
        args[0] = entity.getObjectId();
        args[1] = entity.getFirstName();
        args[2] = entity.getLastName();
        args[3] = entity.getEmail();
        args[4] = entity.getPassword();
        args[5] = entity.getAndroidId();
        args[6] = entity.getFacebookId();
        db.execSQL("INSERT INTO " + STU_NAME +
                " (objectId," +
                "firstName" +
                ",lastName," +
                "email," +
                "password," +
                "androidId," +
                "facebookId)" +
                " VALUES (?,?,?,?,?,?,?)", args);
        db.close();

    }

    public void insertTutor(SavedTutor entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[8];
        args[0] = entity.getObjectId();
        args[1] = entity.getFirstName();
        args[2] = entity.getLastName();
        args[3] = entity.getEmail();
        args[4] = entity.getPassword();
        args[5] = entity.getAndroidId();
        args[6] = entity.getFacebookId();
        if (entity.getLogged()) {
            args[7] = "1";
        } else args[7] = "0";
        db.execSQL("INSERT INTO " + TUT_NAME +
                " (objectId," +
                "firstName" +
                ",lastName," +
                "email," +
                "password," +
                "androidId," +
                "facebookId," +
                "isLogged)" +
                " VALUES (?,?,?,?,?,?,?,?)", args);
        db.close();

    }

    public void insertStuTut(StuTut entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[3];
        args[0] = entity.getStuId();
        args[1] = entity.getTutId();
        args[2] = entity.getSubId();

        db.execSQL("INSERT INTO " + STUTU_NAME +
                " (stuId," +
                "tutId," +
                "subId)" +
                " VALUES (?,?,?)", args);
        db.close();

    }

    public void insertSub(SavedSubject entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[3];
        args[0] = entity.getObjectId();
        args[1] = entity.getName();
        args[2] = entity.getSubNo() + "";

        db.execSQL("INSERT INTO " + SUB_NAME +
                " (objectId," +
                "name," +
                "subNo)" +
                " VALUES (?,?,?)", args);
        db.close();

    }

    public void insertTutSub(TutSub entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[2];
        args[0] = entity.getTutId();
        args[1] = entity.getSubId();

        db.execSQL("INSERT INTO " + TUTSUB_NAME +
                " (tutId," +
                "subId)" +
                " VALUES (?,?)", args);
        db.close();

    }


    public void deleteChat() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    public void deleteStudent() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + STU_NAME);
        db.close();
    }

    public void deleteTutor() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TUT_NAME);
        db.close();
    }

    public void deleteStuTut() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + STUTU_NAME);
        db.close();
    }

    public void deleteSubject() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + SUB_NAME);
        db.close();
    }

    public void deleteTutSub() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TUTSUB_NAME);
        db.close();
    }

    public void deleteChatByTutorId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + TABLE_NAME + " where tutId=?", args);
        db.close();
    }

    public void deleteChatByStuId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + TABLE_NAME + " where stuId=?", args);
        db.close();
    }

    public void deleteChatByStuTutId(String stuId, String tutId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[2];
        args[0] = stuId;
        args[1] = tutId;
        db.execSQL("delete from " + TABLE_NAME + " where subId=? and tutId=?", args);
        db.close();
    }

    public void deleteStudentById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + STU_NAME + " where stuId=?", args);
        db.close();
    }

    public void deleteTutorById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TUT_NAME);
        db.close();
    }

    public void deleteStuTutByStuId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + STUTU_NAME + " where stuId=?", args);
        db.close();
    }

    public void deleteStuTutByTutId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + STUTU_NAME + " where tutId=?", args);
        db.close();
    }

    public void deleteStuTutByStuTutId(String stuId, String tutId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[2];
        args[0] = stuId;
        args[1] = tutId;
        db.execSQL("delete from " + STUTU_NAME + " where stuId=? and tutId=?", args);
        db.close();
    }

    public void deleteStuTutByStuTutSubId(String stuId, String tutId, String subId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[3];
        args[0] = stuId;
        args[1] = tutId;
        args[2] = subId;
        db.execSQL("delete from " + STUTU_NAME + " where stuId=? and tutId=? and subId=?", args);
        db.close();
    }

    public void deleteStuTutBySubId(String subId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = subId;
        db.execSQL("delete from " + STUTU_NAME + " where subId=?", args);
        db.close();
    }

    public void deleteStuTutByTutSubId(String tutId, String subId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[2];
        args[0] = tutId;
        args[1] = subId;
        db.execSQL("delete from " + STUTU_NAME + " where tutId=? and subId=?", args);
        db.close();
    }

    public void deleteSubjectById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + SUB_NAME + " where objectId=?", args);
        db.close();
    }

    public void deleteTutSubByTut(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + TUTSUB_NAME + " where tutId=?", args);
        db.close();
    }

    public void deleteTutSubBySub(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[1];
        args[0] = id;
        db.execSQL("delete from " + TUTSUB_NAME + " where subId=?", args);
        db.close();
    }

    public void deleteTutSubByTutSub(String tutId, String subId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = new String[2];
        args[0] = tutId;
        args[1] = subId;
        db.execSQL("delete from " + TUTSUB_NAME + " where tutId=? and subId=?", args);
        db.close();
    }

    public void deleteAll() {
        deleteChat();
        deleteStudent();
        deleteStuTut();
        deleteTutor();
        deleteSubject();
        deleteTutSub();
    }

    public void updateChat(SavedChat entity) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[2];
        args[1] = entity.getObjectId();
        if (entity.isRead())
            args[0] = "1";
        else args[0] = "0";
        db.execSQL("update " + TABLE_NAME + " set isRead=? where objectId=?", args);
        db.close();
    }

    public void updateTutor(SavedTutor entity) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[8];
        args[7] = entity.getObjectId();
        args[0] = entity.getFirstName();
        args[1] = entity.getLastName();
        args[2] = entity.getEmail();
        args[3] = entity.getPassword();
        args[4] = entity.getAndroidId();
        args[5] = entity.getFacebookId();
        if (entity.getLogged()) {
            args[6] = "1";
        } else args[6] = "0";
        db.execSQL("update " + TUT_NAME + " set " +
                "firstName=?" +
                ",lastName=?," +
                "email=?," +
                "password=?," +
                "androidId=?," +
                "facebookId=?," +
                "isLogged=?" +
                " where objectId=?", args);
        db.close();
    }

    public void updateStudent(SavedStudent entity) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = new String[7];
        args[6] = entity.getObjectId();
        args[0] = entity.getFirstName();
        args[1] = entity.getLastName();
        args[2] = entity.getEmail();
        args[3] = entity.getPassword();
        args[4] = entity.getAndroidId();
        args[5] = entity.getFacebookId();

        db.execSQL("update " + TUT_NAME + " set " +
                "firstName=?" +
                ",lastName=?," +
                "email=?," +
                "password=?," +
                "androidId=?," +
                "facebookId=?" +
                " where objectId=?", args);
        db.close();
    }

    public List<SavedChat> getAllChat() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(TABLE_NAME, null, null, null, null, null, null);
        return ConvertToEntityChat(cr);
    }
    public List<SavedChat> getStuTutChat(String stuId,String tutId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection="stuId=? and tutId=?";
        String []selectionArgs={stuId,tutId};
        Cursor cr;
        cr = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        return ConvertToEntityChat(cr);
    }

    public List<SavedStudent> getAllStudent() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(STU_NAME, null, null, null, null, null, null);
        return ConvertToEntityStudent(cr);
    }

    public List<SavedTutor> getAllTutor() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(TUT_NAME, null, null, null, null, null, null);
        return ConvertToEntityTutor(cr);
    }

    public List<SavedSubject> getAllSubject() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(SUB_NAME, null, null, null, null, null, null);
        return ConvertToEntitySubject(cr);
    }

    public List<StuTut> getAllStuTut() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(STUTU_NAME, null, null, null, null, null, null);
        return ConvertToEntityStutut(cr);
    }

    public List<TutSub> getAllTutSub() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr;
        cr = db.query(TUTSUB_NAME, null, null, null, null, null, null);
        return ConvertToEntityTutSub(cr);
    }

    private List<SavedChat> ConvertToEntityChat(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<SavedChat> chatList = new ArrayList<SavedChat>();
        for (int i = 0; i < resultCounts; i++) {
            SavedChat member = new SavedChat();
            member.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            member.setStuId(cursor.getString(cursor.getColumnIndex("stuId")));
            member.setTutId(cursor.getString(cursor.getColumnIndex("tutId")));
            member.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            member.setContent(cursor.getString(cursor.getColumnIndex("content")));

            if (cursor.getString(cursor.getColumnIndex("isRead")).equals("1")) {
                member.setRead(true);

            } else member.setRead(false);
            if (cursor.getString(cursor.getColumnIndex("fromTo")).equals("1")) {
                member.setFromTo(true);

            } else member.setFromTo(false);
            chatList.add(member);
            cursor.moveToNext();
        }
        return chatList;
    }

    private List<SavedStudent> ConvertToEntityStudent(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<SavedStudent> studentList = new ArrayList<SavedStudent>();
        for (int i = 0; i < resultCounts; i++) {
            SavedStudent member = new SavedStudent();
            member.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            member.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
            member.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
            member.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            member.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            member.setAndroidId(cursor.getString(cursor.getColumnIndex("androidId")));
            member.setFacebookId(cursor.getString(cursor.getColumnIndex("facebookId")));

            studentList.add(member);
            cursor.moveToNext();
        }
        return studentList;
    }

    private List<SavedTutor> ConvertToEntityTutor(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<SavedTutor> tutorList = new ArrayList<SavedTutor>();
        for (int i = 0; i < resultCounts; i++) {
            SavedTutor member = new SavedTutor();
            member.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            member.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
            member.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
            member.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            member.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            member.setAndroidId(cursor.getString(cursor.getColumnIndex("androidId")));
            member.setFacebookId(cursor.getString(cursor.getColumnIndex("facebookId")));
            if (cursor.getString(cursor.getColumnIndex("isLogged")).equals("1")) {
                member.setLogged(true);

            } else member.setLogged(false);
            tutorList.add(member);
            cursor.moveToNext();
        }
        return tutorList;
    }


    private List<SavedSubject> ConvertToEntitySubject(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<SavedSubject> subjectList = new ArrayList<SavedSubject>();
        for (int i = 0; i < resultCounts; i++) {
            SavedSubject member = new SavedSubject();
            member.setObjectId(cursor.getString(cursor.getColumnIndex("objectId")));
            member.setName(cursor.getString(cursor.getColumnIndex("name")));
            member.setSubNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex("subNo"))));
            subjectList.add(member);
            cursor.moveToNext();
        }
        return subjectList;
    }

    private List<StuTut> ConvertToEntityStutut(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<StuTut> stuTutList = new ArrayList<StuTut>();
        for (int i = 0; i < resultCounts; i++) {
            StuTut member = new StuTut();
            member.setStuId(cursor.getString(cursor.getColumnIndex("stuId")));
            member.setTutId(cursor.getString(cursor.getColumnIndex("tutId")));
            member.setSubId(cursor.getString(cursor.getColumnIndex("subId")));
            stuTutList.add(member);
            cursor.moveToNext();
        }
        return stuTutList;
    }

    private List<TutSub> ConvertToEntityTutSub(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<TutSub> tutSubList = new ArrayList<TutSub>();
        for (int i = 0; i < resultCounts; i++) {
            TutSub member = new TutSub();
            member.setTutId(cursor.getString(cursor.getColumnIndex("tutId")));
            member.setSubId(cursor.getString(cursor.getColumnIndex("subId")));
            tutSubList.add(member);
            cursor.moveToNext();
        }
        return tutSubList;
    }


}

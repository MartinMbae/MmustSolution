
package studios.luxurious.mmustsolution.attendance.Student.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import studios.luxurious.mmustsolution.attendance.Utils;

public class DBAdapter {
    private static final String DB_NAME = "student_attendance";
    private static final int DB_VER = 1;


    private static final String TABLE_LESSONS = "lessons";
    private static final String LESSON_ID = "lesson_id";
    private static final String UNIT_ID = "unit_id";
    private static final String TEACHER_CODE = "teacher_code";
    private static final String START_TIME = "start_time";
    private static final String SEM_NAME = "sem_name";
    private static final String YEAR_NAME = "year_name";
    private static final String SEM_ID = "sem_id";
    private static final String YEAR_ID = "year_id";
    private static final String UNIT_NAME = "unit_name";
    private static final String COURSE_NAME = "course_name";

    private static final String DB_CREATE_TABLE_LESSON = "CREATE TABLE IF NOT EXISTS "
            + TABLE_LESSONS + "(" + LESSON_ID + " text, "
            + UNIT_ID + " text, "
            + TEACHER_CODE + " text , "
            + START_TIME + " text , "
            + SEM_NAME + " text , "
            + YEAR_NAME + " text, "
            + SEM_ID + " text , "
            + YEAR_ID + " text , "
            + UNIT_NAME + " text , "
            + COURSE_NAME + " text ); ";


    public static final String TABLE_UNITS = "teacher_units";
    public static final String STUDENT_UNIT_ID = "unit_id";
    public static final String STUDEN_UNIT_CODE = "unit_code";
    public static final String STUDENT_UNIT_NAME = "unit_name";


    public static final String TABLE_UNSAVED = "unsaved_table";
    public static final String UNSAVED_ID = "id";
    public static final String UNSAVED_UNIT_ID = "unit_id";
    public static final String UNSAVED_START_TIME = "start_time";
    public static final String UNSAVED_MY_ATTENDANCE_TIME = "attendance_time";
    public static final String UNSAVED_STUDENT_REGNO = "regno";
    public static final String UNSAVED_UNIT_NAME = "unit_name";
    public static final String UNSAVED_UNIT_CODE = "unit_code";

    private static final String DB_CREATE_TABLE_UNSAVED_LESSON = "CREATE TABLE IF NOT EXISTS "
            + TABLE_UNSAVED + "(" + UNSAVED_UNIT_ID + " text, "
            + UNSAVED_ID + " integer primary key autoincrement, "
            + UNSAVED_START_TIME + " text , "
            + UNSAVED_MY_ATTENDANCE_TIME + " text , "
            + UNSAVED_UNIT_NAME + " text , "
            + UNSAVED_UNIT_CODE + " text , "
            + UNSAVED_STUDENT_REGNO + " text ); ";


    private static final String DB_CREATE_TABLE_TEACHER_UNITS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_UNITS + "(" + STUDENT_UNIT_ID + " text not null, "
            + STUDEN_UNIT_CODE + " text not null, "
            + STUDENT_UNIT_NAME + " text not null); ";



    public static final String TABLE_STUDENT_LESSONS = "student_lessons";
    public static final String LESSON_UNIT_NAME = "unit_name";
    public static final String LESSON_TEACHER_NAME = "teacher_name";
    public static final String LESSON_START_TIME = "start_time";
    public static final String LESSON_MY_ATTENDANCE_TIME = "my_attendance_time";
    public static final String LESSON_STATUS = "status";
    public static final String LESSON_LESSON_ID = "lesson_id";
    public static final String LESSON_UNIT_CODE = "unit_code";
    public static final String LESSON_SEM_NAME = "sem_name";
    public static final String LESSON_YEAR_NAME = "year_name";
    public static final String LESSON_TOTAL_STUDENTS = "total_students";
    public static final String LESSON_TOTAL_ATTENDANCE = "total_attendance";
    public static final String LESSON_WEEK_NUMBER = "week";


    private static final String DB_CREATE_TABLE_STUDENTS_LESSONS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STUDENT_LESSONS + "(" + LESSON_UNIT_NAME + " text not null, "
            + LESSON_TEACHER_NAME + " text not null, "
            + LESSON_START_TIME + " text not null, "
            + LESSON_MY_ATTENDANCE_TIME + " text not null, "
            + LESSON_STATUS + " text not null, "
            + LESSON_LESSON_ID + " text not null, "
            + LESSON_UNIT_CODE + " text not null, "
            + LESSON_SEM_NAME + " text not null, "
            + LESSON_YEAR_NAME + " text not null, "
            + LESSON_TOTAL_STUDENTS + " text not null, "
            + LESSON_WEEK_NUMBER + " interger not null, "
            + LESSON_TOTAL_ATTENDANCE + " text not null); ";


    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private final Context context;

    public DBAdapter(Context context) {
        this.context = context;
        try {
            if (!db.isOpen()) {
                open();
            }
        }catch (Exception ignored){}
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context, DB_NAME, DB_VER);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }





    public boolean addUnsavedLesson(String unit_id, String start_time, String attendance_time, String regno,String unit_code,String unit_name) {
        ContentValues values = new ContentValues();
        values.put(UNSAVED_UNIT_ID, unit_id);
        values.put(UNSAVED_START_TIME, start_time);
        values.put(UNSAVED_MY_ATTENDANCE_TIME, attendance_time);
        values.put(UNSAVED_STUDENT_REGNO, regno);
        values.put(UNSAVED_UNIT_CODE, unit_code);
        values.put(UNSAVED_UNIT_NAME, unit_name);

        db.execSQL(DB_CREATE_TABLE_UNSAVED_LESSON);

        return db.insert(TABLE_UNSAVED, null, values) > 0;

    }

    public ArrayList<ArrayList<Object>> getAllUnsavedLessons() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_UNSAVED, new String[]{UNSAVED_ID, UNSAVED_UNIT_ID, UNSAVED_START_TIME, UNSAVED_MY_ATTENDANCE_TIME, UNSAVED_STUDENT_REGNO,UNSAVED_UNIT_CODE,UNSAVED_UNIT_NAME},
                    null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
        } catch (SQLException e) {

            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }


    public int getNumber_of_Unsaved_Lessons() {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_UNSAVED, new String[]{UNSAVED_UNIT_ID}, null, null, null, null, null);

            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return lessons;
    }


    public boolean deleteUnsavedLesson(int id) {
        return db.delete(TABLE_UNSAVED, UNSAVED_ID + "=" + id, null) > 0;
    }


    public boolean addLesson(String unit_name, String teacher_name, String start_time, String my_attendance_time, String status, String lesson_id, String unit_code, String sem_name, String year_name, String total_students, String total_attendance,int weekNumber) {
        ContentValues values = new ContentValues();
        values.put(LESSON_UNIT_NAME, unit_name);
        values.put(LESSON_TEACHER_NAME, teacher_name);
        values.put(LESSON_START_TIME, start_time);
        values.put(LESSON_MY_ATTENDANCE_TIME, my_attendance_time);
        values.put(LESSON_STATUS, status);
        values.put(LESSON_LESSON_ID, lesson_id);
        values.put(LESSON_UNIT_CODE, unit_code);
        values.put(LESSON_SEM_NAME, sem_name);
        values.put(LESSON_YEAR_NAME, year_name);
        values.put(LESSON_TOTAL_STUDENTS, total_students);
        values.put(LESSON_TOTAL_ATTENDANCE, total_attendance);
        values.put(LESSON_WEEK_NUMBER, weekNumber);

        db.execSQL(DB_CREATE_TABLE_STUDENTS_LESSONS);

        return db.insert(TABLE_STUDENT_LESSONS, null, values) > 0;

    }


    public ArrayList<ArrayList<Object>> getAllLessons() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_UNIT_NAME, LESSON_TEACHER_NAME, LESSON_START_TIME, LESSON_MY_ATTENDANCE_TIME, LESSON_STATUS, LESSON_LESSON_ID, LESSON_UNIT_CODE, LESSON_SEM_NAME, LESSON_YEAR_NAME, LESSON_TOTAL_STUDENTS,LESSON_TOTAL_ATTENDANCE,LESSON_WEEK_NUMBER},
                    null, null, null, null, "LESSON_ID DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataList.add(cursor.getString(9));
                    dataList.add(cursor.getString(10));
                    dataList.add(cursor.getInt(11));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }


    public List<Integer> getDistinctStudentWeeks() {
        List<Integer> weeks = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_WEEK_NUMBER},
                    null, null, null, null, "WEEK DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {

                    int week = Integer.parseInt(cursor.getString(0));

                    if (!weeks.contains(week)) {
                        weeks.add(week);
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return weeks;
    }



    public ArrayList<ArrayList<Object>> getAllLessonsWhereWeek(int weekNumber) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_UNIT_NAME, LESSON_TEACHER_NAME, LESSON_START_TIME, LESSON_MY_ATTENDANCE_TIME, LESSON_STATUS, LESSON_LESSON_ID, LESSON_UNIT_CODE, LESSON_SEM_NAME, LESSON_YEAR_NAME, LESSON_TOTAL_STUDENTS,LESSON_TOTAL_ATTENDANCE},
                    LESSON_WEEK_NUMBER + "='" + weekNumber+"'", null, null, null, "LESSON_ID DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataList.add(cursor.getString(9));
                    dataList.add(cursor.getString(10));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }




    public ArrayList<ArrayList<Object>> getAllLessonsWhereUnitCode(String unit_code) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_UNIT_NAME, LESSON_TEACHER_NAME, LESSON_START_TIME, LESSON_MY_ATTENDANCE_TIME, LESSON_STATUS, LESSON_LESSON_ID, LESSON_UNIT_CODE, LESSON_SEM_NAME, LESSON_YEAR_NAME, LESSON_TOTAL_STUDENTS,LESSON_TOTAL_ATTENDANCE},
                    LESSON_UNIT_CODE + "='" + unit_code+"'", null, null, null, "LESSON_ID DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataList.add(cursor.getString(9));
                    dataList.add(cursor.getString(10));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }


    public int getNumber_of_Lessons() {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_LESSON_ID}, null, null, null, null, null);
            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return lessons;
    }

    public int getNumber_of_LessonsWhereUnitCode(String unit_code) {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENT_LESSONS, new String[]{LESSON_LESSON_ID}, LESSON_UNIT_CODE + "='" + unit_code+"'", null, null, null, null);
            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return lessons;
    }


    public boolean deleteLessons() {

        return !tableExists(TABLE_STUDENT_LESSONS) || db.delete(TABLE_STUDENT_LESSONS, null, null) > 0;
    }


    public boolean insertUnit(String unit_id, String unit_code, String unit_name) {

        ContentValues cv = new ContentValues();
        cv.put(STUDENT_UNIT_ID, unit_id);
        cv.put(STUDEN_UNIT_CODE, unit_code);
        cv.put(STUDENT_UNIT_NAME, unit_name);


        db.execSQL(DB_CREATE_TABLE_TEACHER_UNITS);
        return db.insert(TABLE_UNITS, null, cv) > 0;
    }

    public boolean deleteUnits() {
        return db.delete(TABLE_UNITS, null, null) > 0;
    }

    public ArrayList<ArrayList<Object>> getSudentUnits() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();

        String column_units[] = {STUDENT_UNIT_ID, STUDEN_UNIT_CODE, STUDENT_UNIT_NAME};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_UNITS, column_units, null, null, null, null,
                    STUDENT_UNIT_ID + " DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public ArrayList<String> getUnitCodeandName(String unit_id) {

        ArrayList<String> details = new ArrayList<>();

        String column_units[] = {STUDEN_UNIT_CODE, STUDENT_UNIT_NAME};
        Cursor cursor;
        try {
            cursor = db.query(TABLE_UNITS, column_units, STUDENT_UNIT_ID + "=" + unit_id, null, null, null,
                    STUDENT_UNIT_ID + " DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {

                    details.add(cursor.getString(0));
                    details.add(cursor.getString(1));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return details;
    }

    public int getNumber_of_StudentUnits() {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_UNITS, new String[]{STUDENT_UNIT_ID}, null, null, null, null, null);
            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return lessons;
    }


//    public Cursor getDataById(int id) {
//        Cursor cursor = db.query(TABEL_OCR, column_Ocr, IDOCR + "="
//                + id, null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        return cursor;
//    }
//


//
//    public boolean updateData(Catatan catatan, int id) {
//        ContentValues cv = new ContentValues();
//        cv.put(TITLE_OCR, catatan.getTitle());
//        cv.put(MESSAGE_OCR, catatan.getMessage());
//
//        return db.update(TABEL_OCR, cv, IDOCR + "=" + id, null) > 0;
//    }

    private String dateDataSaved() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, int version) {
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE_TEACHER_UNITS);
            db.execSQL(DB_CREATE_TABLE_LESSON);
            db.execSQL(DB_CREATE_TABLE_STUDENTS_LESSONS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNITS);
            onCreate(db);
        }

    }

    private boolean tableExists(String table_name){
        open();
        boolean tableExists = false;
        try
        {
            db.query(table_name, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception ignored) {
        }

        return tableExists;
    }

    public void resetDB(){

        try {
            if (db.isOpen()){
                db.close();
            }
        }catch (Exception ignored){}


        context.deleteDatabase(DB_NAME);

        Utils utils = new Utils(context);
        utils.restartApp();
    }

}

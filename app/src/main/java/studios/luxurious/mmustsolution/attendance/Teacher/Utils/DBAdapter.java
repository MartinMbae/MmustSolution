
package studios.luxurious.mmustsolution.attendance.Teacher.Utils;


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

import studios.luxurious.mmustsolution.attendance.Utils;

public class DBAdapter {
    private static final String DB_NAME = "teacher_attendance";
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
    private static final String WEEK = "week";

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
            + WEEK + " interger , "
            + COURSE_NAME + " text ); ";


    public static final String TABEL_UNITS = "teacher_units";
    public static final String TEACHER_UNIT_ID = "unit_id";
    public static final String TEACHER_UNIT_CODE = "unit_code";
    public static final String TEACHER_UNIT_NAME = "unit_name";

    public static final String TABEL_UNSAVED = "unsaved_table";
    public static final String UNSAVED_ID = "id";
    public static final String UNSAVED_UNIT_ID = "unit_id";
    public static final String UNSAVED_TEACHER_CODE = "teacher_code";
    public static final String UNSAVED_START_TIME = "start_time";
    public static final String UNSAVED_SEM_ID = "sem_id";
    public static final String UNSAVED_YEAR_ID = "year_id";


    public static final String TABEL_STUDENT_LESSON = "student_lesson";
    public static final String STUDENT_LESSON_ID = "id";
    public static final String STUDENT_LESSON_LESSON_ID = "lesson_id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_REGNO = "regno";
    public static final String STUDENT_TIME = "time";
    public static final String STUDENT_GENDER = "gender";
    public static final String STUDENT_PHONE = "phone";
    public static final String STUDENT_STATUS = "status";


    private static final String DB_CREATE_TABEL_STUDENT_LESSON = "CREATE TABLE IF NOT EXISTS "
            + TABEL_STUDENT_LESSON + "(" + STUDENT_LESSON_ID + " integer primary key autoincrement, "
            + STUDENT_LESSON_LESSON_ID + " text , "
            + STUDENT_REGNO + " text , "
            + STUDENT_NAME + " text , "
            + STUDENT_TIME + " text , "
            + STUDENT_GENDER + " text , "
            + STUDENT_STATUS + " text , "
            + STUDENT_PHONE + " text ); ";


    private static final String DB_CREATE_TABLE_UNSAVED_LESSON = "CREATE TABLE IF NOT EXISTS "
            + TABEL_UNSAVED + "(" + UNSAVED_UNIT_ID + " text, "
            + UNSAVED_TEACHER_CODE + " text, "
            + UNSAVED_ID + " integer primary key autoincrement, "
            + UNSAVED_START_TIME + " text , "
            + UNSAVED_SEM_ID + " text , "
            + UNSAVED_YEAR_ID + " text ); ";


    private static final String DB_CREATE_TABLE_TEACHER_UNITS = "CREATE TABLE IF NOT EXISTS "
            + TABEL_UNITS + "(" + TEACHER_UNIT_ID + " text not null, "
            + TEACHER_UNIT_CODE + " text not null, "
            + TEACHER_UNIT_NAME + " text not null); ";


    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private final Context context;

    public DBAdapter(Context context) {
        this.context = context;
        try {
            open();
        } catch (Exception ignored) {
        }

    }

    public DBAdapter open() {
        dbHelper = new DatabaseHelper(context, DB_NAME, DB_VER);

        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception ignored) {
        }
        return this;
    }


    public boolean addStudentLesson(String lesson_id, String name, String time, String gender, String phone, String regno, String status) {
        ContentValues values = new ContentValues();
        values.put(STUDENT_LESSON_LESSON_ID, lesson_id);
        values.put(STUDENT_NAME, name);
        values.put(STUDENT_TIME, time);
        values.put(STUDENT_GENDER, gender);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_REGNO, regno);
        values.put(STUDENT_STATUS, status);

        db.execSQL(DB_CREATE_TABEL_STUDENT_LESSON);

        return db.insert(TABEL_STUDENT_LESSON, null, values) > 0;

    }

    public void close() {
        dbHelper.close();
    }


    public ArrayList<ArrayList<Object>> getAllStudentLessons(String lesson_id) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABEL_STUDENT_LESSON, new String[]{STUDENT_LESSON_ID, STUDENT_LESSON_LESSON_ID, STUDENT_NAME, STUDENT_TIME, STUDENT_GENDER, STUDENT_PHONE, STUDENT_REGNO, STUDENT_STATUS},
                    STUDENT_LESSON_LESSON_ID + "= '" + lesson_id + "'", null, null, null, null);
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

    public boolean deleteStudentLesson(String lesson_id, String regno) {

        db.execSQL(DB_CREATE_TABEL_STUDENT_LESSON);


        return db.delete(TABEL_STUDENT_LESSON, STUDENT_LESSON_LESSON_ID + "= '" + lesson_id + "' and " + STUDENT_REGNO + "='" + regno + "'", null) > 0;
    }

    public int getNumber_of_StudentLessons(String lesson_id) {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABEL_STUDENT_LESSON, new String[]{STUDENT_LESSON_LESSON_ID}, STUDENT_LESSON_LESSON_ID + "= '" + lesson_id + "'", null, null, null, null);

            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return lessons;
    }


    public boolean addUnsavedLesson(String unit_ids, String teacher_code, String start_time, String sem_id, String year_id) {
        ContentValues values = new ContentValues();
        values.put(UNSAVED_UNIT_ID, unit_ids);
        values.put(UNSAVED_TEACHER_CODE, teacher_code);
        values.put(UNSAVED_START_TIME, start_time);
        values.put(UNSAVED_SEM_ID, sem_id);
        values.put(UNSAVED_YEAR_ID, year_id);

        db.execSQL(DB_CREATE_TABLE_UNSAVED_LESSON);

        return db.insert(TABEL_UNSAVED, null, values) > 0;

    }

    public ArrayList<ArrayList<Object>> getAllUnsavedLessons() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABEL_UNSAVED, new String[]{UNSAVED_UNIT_ID, UNSAVED_TEACHER_CODE, UNSAVED_START_TIME, UNSAVED_SEM_ID, UNSAVED_YEAR_ID,  UNSAVED_ID},
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
            cursor = db.query(TABEL_UNSAVED, new String[]{UNSAVED_UNIT_ID}, null, null, null, null, null);

            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return lessons;
    }


    public boolean deleteUnsavedUnit(int id) {
        return db.delete(TABEL_UNSAVED, UNSAVED_ID + "=" + id, null) > 0;
    }


    public boolean addLesson(String lesson_id, String unit_id, String teacher_code, String start_time, String sem_name, String year_name, String sem_id, String year_id, String unit_name, String course_name, String week) {
        ContentValues values = new ContentValues();

        int wk = Integer.parseInt(week);

        values.put(LESSON_ID, lesson_id);
        values.put(UNIT_ID, unit_id);
        values.put(TEACHER_CODE, teacher_code);
        values.put(START_TIME, start_time);
        values.put(SEM_NAME, sem_name);
        values.put(YEAR_NAME, year_name);
        values.put(SEM_ID, sem_id);
        values.put(YEAR_ID, year_id);
        values.put(UNIT_NAME, unit_name);
        values.put(COURSE_NAME, course_name);
        values.put(WEEK, wk);

        db.execSQL(DB_CREATE_TABLE_LESSON);

        return db.insert(TABLE_LESSONS, null, values) > 0;

    }


    public ArrayList<ArrayList<Object>> getAllLessons() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LESSONS, new String[]{LESSON_ID, UNIT_ID, TEACHER_CODE, START_TIME, SEM_NAME, YEAR_NAME, SEM_ID, YEAR_ID, UNIT_NAME, COURSE_NAME},
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

    public ArrayList<String> getAllDistinctWeeks() {
        ArrayList<String> dataArrays = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LESSONS, new String[]{WEEK},
                    null, null, null, null, "WEEK DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {

                    if (!dataArrays.contains(cursor.getString(0))) {
                        dataArrays.add(cursor.getString(0));
                    }
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


    public ArrayList<ArrayList<Object>> getAllLessonsWhereUnitID(String id) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LESSONS, new String[]{LESSON_ID, UNIT_ID, TEACHER_CODE, START_TIME, SEM_NAME, YEAR_NAME, SEM_ID, YEAR_ID, UNIT_NAME, COURSE_NAME},
                    UNIT_ID + "='" + id + "'", null, null, null, "LESSON_ID DESC");
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


    public ArrayList<ArrayList<Object>> getAllLessonsWhereWeek(String week) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LESSONS, new String[]{LESSON_ID, UNIT_ID, TEACHER_CODE, START_TIME, SEM_NAME, YEAR_NAME, SEM_ID, YEAR_ID, UNIT_NAME, COURSE_NAME},
                    WEEK + "='" + week + "'", null, null, null, "WEEK DESC");
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
            cursor = db.query(TABLE_LESSONS, new String[]{LESSON_ID}, null, null, null, null, null);

            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return lessons;
    }

    public int getNumber_of_Lessons_WhereUnitID(String id) {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LESSONS, new String[]{LESSON_ID}, UNIT_ID + "='" + id + "'", null, null, null, null);

            lessons = cursor.getCount();
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return lessons;
    }

    public boolean insertUnit(String unit_id, String unit_code, String unit_name) {

        ContentValues cv = new ContentValues();
        cv.put(TEACHER_UNIT_ID, unit_id);
        cv.put(TEACHER_UNIT_CODE, unit_code);
        cv.put(TEACHER_UNIT_NAME, unit_name);


        db.execSQL(DB_CREATE_TABLE_TEACHER_UNITS);
        return db.insert(TABEL_UNITS, null, cv) > 0;
    }

    public boolean deleteLessons() {
        return db.delete(TABLE_LESSONS, null, null) > 0;
    }


    public boolean deleteUnits() {
        return db.delete(TABEL_UNITS, null, null) > 0;
    }

    public ArrayList<String> getUnitCodeandName(String unit_id) {

        ArrayList<String> details = new ArrayList<>();

        String column_units[] = {TEACHER_UNIT_CODE, TEACHER_UNIT_NAME};
        Cursor cursor;
        try {
            cursor = db.query(TABEL_UNITS, column_units, TEACHER_UNIT_ID + "=" + unit_id, null, null, null,
                    TEACHER_UNIT_ID + " DESC");
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


    public ArrayList<ArrayList<Object>> getTeacherUnits() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();

        String column_units[] = {TEACHER_UNIT_ID, TEACHER_UNIT_CODE, TEACHER_UNIT_NAME};
        Cursor cursor = null;
        try {
            cursor = db.query(TABEL_UNITS, column_units, null, null, null, null,
                    TEACHER_UNIT_ID + " DESC");
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


    public int getNumber_of_TeacherUnits() {
        int lessons = 0;
        Cursor cursor = null;
        try {
            cursor = db.query(TABEL_UNITS, new String[]{TEACHER_UNIT_ID}, null, null, null, null, null);
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
            db.execSQL(DB_CREATE_TABEL_STUDENT_LESSON);
            db.execSQL(DB_CREATE_TABLE_UNSAVED_LESSON);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABEL_UNITS);
            onCreate(db);
        }

    }


    public void resetDB() {
        try {
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception ignored) {
        }


        context.deleteDatabase(DB_NAME);

        Utils utils = new Utils(context);
        utils.restartApp();
    }

}

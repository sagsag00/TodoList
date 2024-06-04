package com.tsafrir.sagi.schoolproject.projectpop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProjectDBHelper extends  SQLiteOpenHelper {
    private static final String DATABASE_NAME = "projects_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PROJECTS = "projects";
    private static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_PROJECT_TITLE = "project_title";
    private static final String COLUMN_TASKS = "tasks";
    private static final String COLUMN_IMPORTANCE_LEVEL = "importance_level";
    private static final String COLUMN_DUE_DATE_TOGGLED = "due_date_toggled";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_REMINDERS = "reminders";
    private static final String COLUMN_REMINDERS_START_BEFORE = "reminders_start_before";
    private static final String COLUMN_REMINDERS_EACH_TIME = "reminders_each_time";
    private static final String COLUMN_COLOR = "color";

    public ProjectDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProjectsTableQuery = "CREATE TABLE " + TABLE_PROJECTS + " ("
                + COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PROJECT_TITLE + " TEXT, "
                + COLUMN_TASKS + " TEXT, "
                + COLUMN_IMPORTANCE_LEVEL + " INTEGER, "
                + COLUMN_DUE_DATE_TOGGLED + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT, "
                + COLUMN_REMINDERS + " TEXT, "
                + COLUMN_REMINDERS_START_BEFORE + " TEXT, "
                + COLUMN_REMINDERS_EACH_TIME + " TEXT, "
                + COLUMN_COLOR + " INTEGER);";

        db.execSQL(createProjectsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public long insertProject(Project project) {
        // Inserts the project.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, project.getProjectTitle());
        values.put(COLUMN_TASKS, joinList(project.getTasks()));
        values.put(COLUMN_IMPORTANCE_LEVEL, project.getImportanceLevel());
        values.put(COLUMN_DUE_DATE_TOGGLED, String.valueOf(project.isDueDateToggled()));
        values.put(COLUMN_DUE_DATE, project.getDueDate());
        values.put(COLUMN_REMINDERS, project.getReminders().toString());
        values.put(COLUMN_REMINDERS_START_BEFORE, project.getRemindersStartXBefore().toString());
        values.put(COLUMN_REMINDERS_EACH_TIME, project.getRemindersEachTime().toString());
        values.put(COLUMN_COLOR, project.getColor());

        long id = db.insert(TABLE_PROJECTS, null, values);
        db.close();
        return id;
    }

    public long insertProject(Project project, long id) {
        // Inserts the project.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_TITLE, project.getProjectTitle());
        values.put(COLUMN_TASKS, joinList(project.getTasks()));
        values.put(COLUMN_IMPORTANCE_LEVEL, project.getImportanceLevel());
        values.put(COLUMN_DUE_DATE_TOGGLED, String.valueOf(project.isDueDateToggled()));
        values.put(COLUMN_DUE_DATE, project.getDueDate());
        values.put(COLUMN_REMINDERS, project.getReminders().toString());
        values.put(COLUMN_REMINDERS_START_BEFORE, project.getRemindersStartXBefore().toString());
        values.put(COLUMN_REMINDERS_EACH_TIME, project.getRemindersEachTime().toString());
        values.put(COLUMN_COLOR, project.getColor());

        long returnId = db.update(TABLE_PROJECTS, values, "project_id=?", new String[]{String.valueOf(id)});

        db.close();
        return returnId;
    }

    public void removeProject(long id){
        // Removes the project.
        SQLiteDatabase db = this.getReadableDatabase();

        int i =  db.delete(TABLE_PROJECTS, "project_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Project> getAllProjects() {
        // Returns all of the projects.
        List<Project> projectsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROJECTS, null);

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project();
                int idColumnIndex = cursor.getColumnIndex(COLUMN_PROJECT_ID);
                if (idColumnIndex != -1) {
                    project.setProjectId(cursor.getLong(idColumnIndex));
                }

                int titleColumnIndex = cursor.getColumnIndex(COLUMN_PROJECT_TITLE);
                if (titleColumnIndex != -1) {
                    project.setProjectTitle(cursor.getString(titleColumnIndex));
                }

                int taskColumnIndex = cursor.getColumnIndex(COLUMN_TASKS);
                if(taskColumnIndex != -1){
                    String tasksString = cursor.getString(taskColumnIndex);
                    List<String> tasks = splitList(tasksString, String::valueOf);
                    project.setTasks(tasks);
                }

                int importanceLevelColumnIndex = cursor.getColumnIndex(COLUMN_IMPORTANCE_LEVEL);
                if(importanceLevelColumnIndex != -1){
                    project.setImportanceLevel((byte) cursor.getInt(importanceLevelColumnIndex));
                }

                int dueDateToggledColumnIndex = cursor.getColumnIndex(COLUMN_DUE_DATE_TOGGLED);
                if(dueDateToggledColumnIndex != -1){
                    project.setDueDateToggled(Boolean.parseBoolean(cursor.getString(dueDateToggledColumnIndex)));
                }

                int dueDateColumnIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
                if(dueDateColumnIndex != -1){
                    project.setDueDate(cursor.getString(dueDateColumnIndex));
                }

                int remindersColumnIndex = cursor.getColumnIndex(COLUMN_REMINDERS);
                if(remindersColumnIndex != -1){
                    String remindersString  = cursor.getString(remindersColumnIndex);
                    Reminder reminders = Reminder.valueOf(remindersString);
                    project.setReminders(reminders);
                }

                int remindersStartBeforeColumnIndex = cursor.getColumnIndex(COLUMN_REMINDERS_START_BEFORE);
                if(remindersStartBeforeColumnIndex != -1){
                    String startBeforeString = cursor.getString(remindersStartBeforeColumnIndex);
                    RemindersStartXBefore remindersStartXBefore = RemindersStartXBefore.valueOf(startBeforeString);
                    project.setRemindersStartXBefore(remindersStartXBefore);
                }

                int remindersEachTimeColumnIndex = cursor.getColumnIndex(COLUMN_REMINDERS_EACH_TIME);
                if(remindersEachTimeColumnIndex != -1){
                    String remindersEachTimeString = cursor.getString(remindersEachTimeColumnIndex);
                    RemindersEachXTime remindersEachXTime = RemindersEachXTime.valueOf(remindersEachTimeString);
                    project.setRemindersEachTime(remindersEachXTime);
                }

                int colorColumnIndex = cursor.getColumnIndex(COLUMN_COLOR);
                if(colorColumnIndex != -1){
                    project.setColor(cursor.getInt(colorColumnIndex));
                }

                projectsList.add(project);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return projectsList;
    }



    public <T> String joinList(List<T> list) {
        // Makes a string from a list.
        if (list == null || list.isEmpty()) {
            return "";
        }
        return TextUtils.join(",", list);
    }

    // Example for call: "splitList("1,2,3", Integer::parseInt);".
    public static <T> List<T> splitList(String input, Function<String, T> converter) {
        // Splits a string to a list.
        String[] splitArray = input.split(",");
        List<T> resultList = new ArrayList<>(splitArray.length);
        for (String obj : splitArray) {
            resultList.add(converter.apply(obj.trim())); // Convert String to T
        }
        return resultList;
    }

}

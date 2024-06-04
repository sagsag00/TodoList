package com.tsafrir.sagi.schoolproject.projectpop.db;

import android.app.Application;

public class ProjectDatabase extends Application {

    private static ProjectDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new ProjectDBHelper(this);
    }

    public static ProjectDBHelper getDBHelper() {
        return dbHelper;
    }
}

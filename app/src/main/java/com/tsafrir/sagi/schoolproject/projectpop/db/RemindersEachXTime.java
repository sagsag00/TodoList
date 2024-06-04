package com.tsafrir.sagi.schoolproject.projectpop.db;

import androidx.annotation.NonNull;

public class RemindersEachXTime extends Object{
    private int day; // Times per day.
    private int week; // Times per week.
    private int month; // Times per month.

    public RemindersEachXTime() {
    }
    public RemindersEachXTime(int day, int week, int month) {
        this.day = day;
        this.week = week;
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getSelectedTime(){
        if(this.getDay() != 0){
            return "day:" + this.getDay();
        }
        if(this.getWeek() != 0){
            return "week:" + this.getWeek();
        }
        return "month:" + this.getMonth();
    }

    public static RemindersEachXTime valueOf(String str){
        // valueOf function.
        String[] parts = str.split(",");
        int day = Integer.parseInt(parts[0].split(":")[1]);
        int week = Integer.parseInt(parts[1].split(":")[1]);
        int month = Integer.parseInt(parts[2].split(":")[1]);
        return new RemindersEachXTime(day, week, month);
    };

    @NonNull
    public String toString() {
        return "day:" + this.getDay() + "," +
                "week:" + this.getWeek() + "," +
                "month:" + this.getMonth();
    }
}

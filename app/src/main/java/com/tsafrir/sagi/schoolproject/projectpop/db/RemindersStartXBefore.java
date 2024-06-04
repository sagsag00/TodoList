package com.tsafrir.sagi.schoolproject.projectpop.db;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

public class RemindersStartXBefore extends Object{
    private int minutes; // Minutes before deadline.
    private int hours; // Hours before deadline.
    private int days; // Days before deadline.
    private int weeks; // Weeks before deadline.

    public RemindersStartXBefore() {
    }

    public RemindersStartXBefore(int minutes, int hours, int days, int weeks) {
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
        this.weeks = weeks;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public String getSelectedTime(){
        if(this.getMinutes() != 0){
            return "minutes:" + this.getMinutes();
        }
        if(this.getHours() != 0){
            return "hours:" + this.getHours();
        }
        if(this.getDays() != 0){
            return "days:" + this.getDays();
        }
        return "weeks:" + this.getWeeks();
    }

    public long getTimeInMillis(){
        Calendar currTime = Calendar.getInstance();
        currTime.set(Calendar.SECOND, 0);
        currTime.set(Calendar.MILLISECOND, 0);
        Calendar calendar = (Calendar) currTime.clone();

        if(this.getMinutes() != 0){
            return 1000L * 60 * this.getMinutes();
        }
        if(this.getHours() != 0){
            return 1000L * 60 * 60 * this.getHours();
        }
        if(this.getDays() != 0){
            return 1000L * 60 * 60 * 24 * this.getDays();
        }
        // do with current time and one week advance
        calendar.add(Calendar.WEEK_OF_YEAR, 1);

        return 1000L * 60 * 60 * 24 * 7 * this.getWeeks();
    }

    public static RemindersStartXBefore valueOf(String str){
        // valueOf function.
        String[] parts = str.split(",");
        int minutes = Integer.parseInt(parts[0].split(":")[1]);
        int hours = Integer.parseInt(parts[1].split(":")[1]);
        int days = Integer.parseInt(parts[2].split(":")[1]);
        int weeks = Integer.parseInt(parts[3].split(":")[1]);

        return new RemindersStartXBefore(minutes, hours, days, weeks);
    };

    @NonNull
    public String toString() {
        return "minutes:" + this.getMinutes() + "," +
                "hours:" + this.getHours() + "," +
                "days:" + this.getDays() + "," +
                "weeks:" + this.getWeeks();
    }

}

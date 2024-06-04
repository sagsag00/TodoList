package com.tsafrir.sagi.schoolproject.projectpop.db;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Reminder extends Object{
    private List<Integer> dayOfWeek; // At which day of the week the reminder is.
    private List<Integer> dayOfMonth; // At which day of the month the reminder is.
    private List<String> times; // The different times of alarms for each reminder. For example: [04:00 PM, 05:00 PM, 06:00 PM].
    private String date; // The date of the reminder.

    public Reminder() {
    }

    public Reminder(List<Integer> dayOfWeek, List<String> times) {
        this.dayOfWeek = dayOfWeek;
        this.times = times;
    }

    public Reminder(List<String> times, String date) {
        this.times = times;
        this.date = date;
    }

    public Reminder(List<Integer> dayOfWeek, List<Integer> dayOfMonth, List<String> times) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.times = times;
    }

    public Reminder(List<Integer> dayOfWeek, List<Integer> dayOfMonth, List<String> times, String date) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.times = times;
        this.date = date;
    }

    public List<Integer> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<Integer> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Integer> getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(List<Integer> dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Reminder valueOf(String str){
        // valueOf function.
        String[] parts = str.split(":");
        List<Integer> dayOfWeek = keepOnlyIntegers(parseList(parts[1].split(",d")[0], String::valueOf));
        List<Integer> dayOfMonth = keepOnlyIntegers(parseList(parts[2].split(",t")[0], String::valueOf));

        for(int i = 4; i < parts.length - 2; i++){
            parts[3] += ":";
            parts[3] += parts[i].split(",")[0];
            parts[3] += ",";
            parts[3] += parts[i].split(",")[1].trim();
        }
        parts[3] += ":";
        parts[3] += parts[parts.length-2].split(",")[0];
        List<String> times = parseList(parts[3], String::valueOf);

        String date = parts[parts.length-1].equals("null") ? null : parts[parts.length-1].replace("]", "");

        return new Reminder(dayOfWeek, dayOfMonth, times, date);
    }

    @NonNull
    public String toString() {
        return "[dayOfWeek:" + this.getDayOfWeek() + "," +
                "dayOfMonth:" + this.getDayOfMonth() + "," +
                "times:" + this.getTimes() + "," +
                "date:" + this.getDate() + "]";
    }
}

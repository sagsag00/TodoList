package com.tsafrir.sagi.schoolproject.projectpop.db;

import androidx.annotation.NonNull;

import java.util.List;

public class Project extends Object{
    private long projectId; // The id of the project. -- AUTO
    private String projectTitle; // The title of the project.
    private List<String> tasks; // A list of all of the tasks.
    private byte importanceLevel; // The importance level.
    private boolean isDueDateToggled; // Is there a due date?
    private String dueDate; // The due date (if exists).
    private Reminder reminders; //
    private RemindersStartXBefore remindersStartXBefore; // Start X time before deadline.
    private RemindersEachXTime remindersEachTime; // Remind each X time (no deadline).
    private int color;

    public Project(){}

    public Project(String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, RemindersStartXBefore remindersStartXBefore, RemindersEachXTime remindersEachTime, int color) {
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.remindersStartXBefore = remindersStartXBefore;
        this.remindersEachTime = remindersEachTime;
        this.color = color;
    }

    public Project(long projectId, String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, RemindersStartXBefore remindersStartXBefore, RemindersEachXTime remindersEachTime, int color) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.remindersStartXBefore = remindersStartXBefore;
        this.remindersEachTime = remindersEachTime;
        this.color = color;
    }

    public Project(String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, int color) {
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.color = color;
    }

    public Project(String projectTitle, byte importanceLevel, boolean isDueDateToggled, Reminder reminders, RemindersEachXTime remindersEachTime, int color) {
        this.projectTitle = projectTitle;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.reminders = reminders;
        this.remindersEachTime = remindersEachTime;
        this.color = color;
    }

    public Project(String projectTitle, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, RemindersStartXBefore remindersStartXBefore, int color) {
        this.projectTitle = projectTitle;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.remindersStartXBefore = remindersStartXBefore;
        this.color = color;
    }

    public Project(String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, Reminder reminders, RemindersEachXTime remindersEachTime, int color) {
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.reminders = reminders;
        this.remindersEachTime = remindersEachTime;
        this.color = color;
    }

    public Project(String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, RemindersStartXBefore remindersStartXBefore, int color) {
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.remindersStartXBefore = remindersStartXBefore;
        this.color = color;
    }

    public Project(long projectId, String projectTitle, List<String> tasks, byte importanceLevel, boolean isDueDateToggled, String dueDate, Reminder reminders, RemindersStartXBefore remindersStartXBefore, int color) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.tasks = tasks;
        this.importanceLevel = importanceLevel;
        this.isDueDateToggled = isDueDateToggled;
        this.dueDate = dueDate;
        this.reminders = reminders;
        this.remindersStartXBefore = remindersStartXBefore;
        this.color = color;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public byte getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(byte importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public boolean isDueDateToggled() {
        return isDueDateToggled;
    }

    public void setDueDateToggled(boolean dueDateToggled) {
        isDueDateToggled = dueDateToggled;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Reminder getReminders() {
        return this.reminders;
    }

    public void setReminders(Reminder reminders) {
        this.reminders = reminders;
    }

    public RemindersStartXBefore getRemindersStartXBefore() {
        return remindersStartXBefore;
    }

    public void setRemindersStartXBefore(RemindersStartXBefore remindersStartXBefore) {
        this.remindersStartXBefore = remindersStartXBefore;
    }

    public RemindersEachXTime getRemindersEachTime() {
        return remindersEachTime;
    }

    public void setRemindersEachTime(RemindersEachXTime remindersEachTime) {
        this.remindersEachTime = remindersEachTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @NonNull
    public static Project valueOf(@NonNull String str){
        // valueOf converter.
        String[] parts = str.split("\\{")[1].split("=");

        long projectId = Integer.parseInt(parts[0].split(",")[0]);

        String projectTitle = parts[1].split(",")[0];

        List<String> tasks = parseList(parts[2].replace("[", "")
                .replace("]", "").split(", ")[0], String::valueOf);

        byte importanceLevel = Byte.parseByte(parts[3].split(",")[0]);

        boolean isDueDateToggled = Boolean.parseBoolean(parts[4].split(",")[0]);

        String dueDate = parts[5].split(",")[0];

        Reminder reminder = Reminder.valueOf(parts[6].replace("[d", "d")
                .replace("],", ",").split(",")[0]);

        RemindersStartXBefore reminderStartXBefore = RemindersStartXBefore
                .valueOf(parts[7].split(", ")[0]);

        RemindersEachXTime remindersEachXTime = RemindersEachXTime
                .valueOf(parts[8].split(", ")[0]);

        int color = Integer.parseInt(parts[9]);

        return new Project(
                projectId,
                projectTitle,
                tasks,
                importanceLevel,
                isDueDateToggled,
                dueDate, reminder,
                reminderStartXBefore,
                remindersEachXTime,
                color);
    };

    @NonNull
    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectTitle='" + projectTitle + '\'' +
                ", tasks=" + tasks +
                ", importanceLevel=" + importanceLevel +
                ", isDueDateToggled=" + isDueDateToggled +
                ", dueDate='" + dueDate + '\'' +
                ", reminders=" + reminders +
                ", remindersStartXBefore=" + remindersStartXBefore +
                ", remindersEachTime=" + remindersEachTime +
                ", color=" + color +
                '}';
    }
}
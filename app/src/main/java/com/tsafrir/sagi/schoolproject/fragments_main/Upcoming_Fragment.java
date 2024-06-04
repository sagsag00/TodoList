package com.tsafrir.sagi.schoolproject.fragments_main;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.tsafrir.sagi.schoolproject.R;
import com.tsafrir.sagi.schoolproject.SplashActivity;
import com.tsafrir.sagi.schoolproject.notifications.NotificationReceiver;
import com.tsafrir.sagi.schoolproject.projectpop.ProjectCreatePop;
import com.tsafrir.sagi.schoolproject.projectpop.ProjectManagePop;
import com.tsafrir.sagi.schoolproject.projectpop.db.Project;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDBHelper;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDatabase;
import com.tsafrir.sagi.schoolproject.projectpop.db.Reminder;
import com.tsafrir.sagi.schoolproject.projectpop.db.RemindersStartXBefore;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Upcoming_Fragment extends Fragment {

    private Context context; // The context.
    private Button newProjectButton; // The button that lets you add a project.
    private Button newProjectButton2; // The button that lets you add a project.
    private ConstraintLayout constLayoutContainer; // The layout where all of the projects reside.
    private RelativeLayout mainLayout; // The fragments layout.

    private View upcomingFragmentView; // The current view.
    private Set<Integer> usedRequestCodes = new HashSet<>();
    private int projectId = -1;

    private int index; // The index of the current project.

    private static final int REQUEST_CODE_PROJECT_CREATE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        upcomingFragmentView = inflater.inflate(R.layout.upcoming_fragment, container, false);

        init();

        newProjectButton.setOnClickListener(this::onClickNewProjectButton);
        newProjectButton2.setOnClickListener(this::onClickNewProjectButton);

        return upcomingFragmentView;
    }

    private int dpToPx(int dp) {
        // Converts dp to px.
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    private void init(){
        // Initialization.
        context = getActivity();

        newProjectButton = (Button) upcomingFragmentView.findViewById(R.id.newProjectButton);
        newProjectButton2 = (Button) upcomingFragmentView.findViewById(R.id.newProjectButton2);
        mainLayout = (RelativeLayout) upcomingFragmentView.findViewById(R.id.MainLayout);
        constLayoutContainer = (ConstraintLayout) upcomingFragmentView.findViewById(R.id.homeFragmentConstLayout);

        ProjectDBHelper dbHelper = ProjectDatabase.getDBHelper();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        loadProjects(dbHelper);
    }

    private void onClickNewProjectButton(View view) {
        // The onClickListener for the new project buttons (the plus buttons).
        if (Objects.equals(newProjectButton.getTag(), "newProjectButtonL")){

            createBackgroundView(context);

            Intent intent = new Intent(context, ProjectCreatePop.class);
            startActivityForResult(intent, REQUEST_CODE_PROJECT_CREATE);

        }
    }

    private void onClickProjectButton(View view){
        // The on click listener for the normal projects.
        createBackgroundView(context);

        Intent intent = new Intent(context, ProjectManagePop.class);
        intent.putExtra("clickedProject", Integer.parseInt((String) view.getContentDescription()));
        startActivityForResult(intent, REQUEST_CODE_PROJECT_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When the results returns, this will be called.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PROJECT_CREATE) {

            removeBackgroundView();

            if (resultCode == RESULT_OK) {
                createProjects();
            } else if (resultCode == RESULT_CANCELED) {

            }

        }
    }

    private void createProjects(){
        // Creates the project button.

        ProjectDBHelper dbHelper = ProjectDatabase.getDBHelper();
        List<Project> projects = dbHelper.getAllProjects();
        Project lastProject = projects.get(projects.size() - 1);
        if(lastProject.getDueDate().trim().equals("")){
            return;
        }

        createProjectView(lastProject, index++);

        projectId = (int) lastProject.getProjectId();
        createNotifications(lastProject);
    }

    private void createNotifications(Project project){
        // Creates a notification for the latest project.
        // This one is mainly for filtering. Find which notification should do what.
        // For example: what interval should the notification have, or how many notifications to create.

        Reminder reminder = project.getReminders();
        String firstTime = reminder.getTimes().get(0);
        Date date = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
        try {
            date = inputFormat.parse(firstTime);
        } catch (ParseException e) {
            Log.e("createNotifications", e.toString());
        }
        firstTime = outputFormat.format(Objects.requireNonNull(date));

        if(reminder.getDayOfWeek().size() != 0){
            List<Integer> daysOfWeek = reminder.getDayOfWeek();
            createNotification(firstTime, daysOfWeek, "week", project.getRemindersStartXBefore(), project.getDueDate());
            return;
        }
        if(reminder.getDayOfMonth().size() != 0){
            List<Integer> daysOfMonth = reminder.getDayOfMonth();
            createNotification(firstTime, daysOfMonth, "month", project.getRemindersStartXBefore(), project.getDueDate());
            return;
        }

        for(String time : reminder.getTimes()) {
            List<Integer> day = new ArrayList<>();
            day.add(-1);
            try {
                date = inputFormat.parse(time);
            } catch (ParseException e) {
                Log.e("createNotifications", e.toString());
            }
            time = outputFormat.format(Objects.requireNonNull(date));
            createNotification(time, day, "day", project.getRemindersStartXBefore(), project.getDueDate());
        }
    }

    private void createNotification(String time, List<Integer> days, String interval, RemindersStartXBefore startXBefore, String date){
        // Creates a notification based on a time, day(s), interval, how much time before it should start and date.

        if (interval == null || !interval.equals("week") && !interval.equals("month") && !interval.equals("day")){
            Log.e("createNotification", "Unknown interval");
            return;
        }
        long dueDate = 0;

        if(date != null && !date.isEmpty()) {

            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date due = null;
            try {
                due = inputFormat.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            dueDate = Timestamp.valueOf(outputFormat.format(due) + " 00:00:00").getTime();
        }


        Calendar calendar;
        Calendar currentTimeCalendar = Calendar.getInstance();
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        Collections.sort(days);
        Collections.reverse(days);

        for (int day : days) {
            calendar = (Calendar) currentTimeCalendar.clone();
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long currentTime = calendar.getTimeInMillis();

            if(dueDate != 0){
                calendar.setTimeInMillis(dueDate);
            }
            if(interval.equals("week")){
                calendar.set(Calendar.DAY_OF_WEEK, day);
            } else if(interval.equals("month")){
                calendar.set(Calendar.DAY_OF_MONTH, day);
            } // Else - day. Meaning you don't need to set the day: It is either today or tomorrow.
            calendar.set(Calendar.HOUR_OF_DAY, hour); // hour is wrong
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Finding the time in millis for the set time (the time of the due date in millis).
            // Then removing the added time in millis (startXBefore. For example if startXBefore is 2 weeks, it would remove 2 weeks.).
            long setTime = calendar.getTimeInMillis();
            long addedTime = startXBefore.getTimeInMillis(); // Remind X time before.

            setTime -= addedTime;

            calendar.setTimeInMillis(setTime);

            // Change of clock.
            int timeDifference = hour - calendar.get(Calendar.HOUR_OF_DAY);
            if(timeDifference < 0){
                calendar.add(Calendar.HOUR_OF_DAY, -timeDifference);
            } else {
                calendar.add(Calendar.HOUR_OF_DAY, timeDifference);
            }
            setTime = calendar.getTimeInMillis();

            Timestamp setTimestamp = new Timestamp(setTime);
            Timestamp currentTimestamp = new Timestamp(currentTime);

            // Checks whether the time of the notification is before the current time.
            // If it is before, adds time based on the interval until it is not before.
            if(setTimestamp.before(currentTimestamp)){
                calendar.setTimeInMillis(setTime);
            }
            while (setTimestamp.before(currentTimestamp)){
                Log.d("createNotifications", "setTimestamp: " + setTimestamp + " is before currentTimestamp: " + currentTimestamp);


                if(interval.equals("week")){
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    Log.d("createNotifications", "added 1 week");
                } else if(interval.equals("month")){
                    calendar.add(Calendar.MONTH, 1);
                    Log.d("createNotifications", "added 1 month");
                } else {
                    calendar.add(Calendar.DATE, 1);
                    Log.d("createNotifications", "added 1 day");
                }
                setTimestamp.setTime(calendar.getTimeInMillis());

                Log.d("createNotifications", "newTimeStamp: " + setTimestamp);
            }
            setNotification(setTimestamp.getTime(), interval);

            Log.i("createNotifications", "Notification set for " + interval);
        }

    }


    private void setNotification(long timeInMillis, String interval) {
        // Sets a notification to run on the given timeInMillis.
        // When the time of the notification reaches, the NotificationReciever.class will be called.
        // Putting the interval and projectId in the intent so that the NotificationReciever.class can use them.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notificationInterval", interval);
        intent.putExtra("clickedProject", projectId+1);

        // Finds a request code that is not used so that notifications won't override each other.
        int requestCode = projectId * 10;
        do {
            requestCode++;
        } while (isRequestCodeTaken(requestCode));
        intent.putExtra("requestCode", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        usedRequestCodes.add(requestCode);

        // If the user didn't accept SecheduleExactAlarms permission, the app will make an AlertDialog that asks them to enable it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_SchoolProject_RoundedDialogTheme)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent mIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            context.startActivity(mIntent);
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .setTitle("In order to get exact notifications you need to allow it in settings.");

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.purple_light_mode));
                    negativeButton.setTextColor(getResources().getColor(R.color.purple_light_mode));
                }
            });
            alertDialog.show();

        }


        try{
            // The best way to create an exact notification.
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
        catch (SecurityException e){
            Log.e("setNotification", e.toString());
            try{
                // Second best way to create an exact notification, this may be less accurate.
                // This will be called if the CanScheduleExactAlarms permission wasn't granted.
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
            catch (SecurityException ex){
                Log.e("setNotification", ex.toString());
                // Worst way to create an exact notification, may send a notification with a delay of 10 minutes.
                // This will be called if no permission was granted.
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }


    public void loadProjects(ProjectDBHelper dbHelper){
        // Loads all of the projects from the database.
        List<Project> projects = dbHelper.getAllProjects();

        Log.e("loadProjects", projects.toString());
        index = 0;

        for(Project project : projects){
            if(project.getDueDate().trim().equals("")){
                continue;
            }
            createProjectView(project, index++);

        }
    }

    private void createProjectView(Project project, int index){
        // Create a project with constraintLayout as the base of it.
        int width = 0;
        int height = dpToPx(190);


        ConstraintLayout projectButton = new ConstraintLayout(context);
        projectButton.setTag("projectLeft");
        projectButton.setId(View.generateViewId());
        projectButton.setBackgroundColor(project.getColor());
        projectButton.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
        projectButton.setClickable(true);
        projectButton.setVisibility(View.VISIBLE);
        projectButton.setContentDescription(String.valueOf(index));

        // The Project Title.
        TextView projectTitle = new TextView(context);
        projectTitle.setText(project.getProjectTitle());
        projectTitle.setId(View.generateViewId());
        projectTitle.setTextColor(Color.parseColor("#000000"));
        projectTitle.setGravity(Gravity.CENTER);
        projectTitle.setTextSize(19);
        projectTitle.setPaddingRelative(10,10,10,10);
        projectTitle.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        projectButton.addView(projectTitle);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(projectButton);

        constraintSet.connect(projectTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10);
        constraintSet.connect(projectTitle.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 10);
        constraintSet.connect(projectTitle.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 10);

        // The Tasks.
        for(int i = 0; i < project.getTasks().size() && i <= 3; i++){
            String task = project.getTasks().get(i);
            constraintSet = new ConstraintSet();
            constraintSet.clone(projectButton);

            TextView projectTask = createTaskComment(task);

            projectButton.addView(projectTask);
            constraintSet.connect(projectTask.getId(), ConstraintSet.TOP, projectTitle.getId(), ConstraintSet.BOTTOM, 80*i + 10);
            constraintSet.connect(projectTask.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(projectTask.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(10));
            constraintSet.connect(projectTask.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(10));
            constraintSet.applyTo(projectButton);
        }

        constLayoutContainer.addView(projectButton);

        constraintSet = new ConstraintSet();
        constraintSet.clone(constLayoutContainer);

        constraintSet.connect(projectButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(10));
        constraintSet.connect(projectButton.getId(), ConstraintSet.END, newProjectButton2.getId(), ConstraintSet.START, dpToPx(20));
        constraintSet.connect(projectButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(1));

        constraintSet.applyTo(constLayoutContainer);


        if(index % 2 != 0) {
            constraintSet = new ConstraintSet();
            constraintSet.clone(constLayoutContainer);

            if(index != 1){
                ConstraintLayout aboveProject = constLayoutContainer.findViewWithTag("projectRight");
                constraintSet.clear(projectButton.getId(), ConstraintSet.TOP);
                constraintSet.connect(projectButton.getId(), ConstraintSet.TOP, aboveProject.getId(), ConstraintSet.BOTTOM, dpToPx(10));
                aboveProject.setTag("projectRightUsed");
            }



            projectButton.setTag("projectRight");
            // Fixing constraints of the last one because a new one appeared...
            ConstraintLayout lastProject = constLayoutContainer.findViewWithTag("projectLeft");
            lastProject.setTag("projectLeftUsed");
            if(lastProject == null){
                Intent intent = new Intent(context, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            try {
                constraintSet.clear(lastProject.getId(), ConstraintSet.END);
            } catch (Exception e){
                Log.e("loadProjects", e.toString());
                lastProject.setId(View.generateViewId());
                constraintSet.clear(lastProject.getId(), ConstraintSet.END);
            }
            constraintSet.connect(lastProject.getId(), ConstraintSet.END, projectButton.getId(), ConstraintSet.START, dpToPx(10));

            // Changing a bit the right one too...
            constraintSet.clear(projectButton.getId(), ConstraintSet.START);
            constraintSet.connect(projectButton.getId(), ConstraintSet.START, lastProject.getId(), ConstraintSet.END, dpToPx(10));
            constraintSet.clear(projectButton.getId(), ConstraintSet.END);
            constraintSet.connect(projectButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dpToPx(10));

            new Handler().postDelayed((Runnable) () -> {
                projectButton.setY(newProjectButton.getY() - dpToPx(200));
                newProjectButton2.setY(newProjectButton.getY());

            }, 10);

            constraintSet.connect(newProjectButton2.getId(), ConstraintSet.TOP, projectButton.getId(), ConstraintSet.BOTTOM, dpToPx(10));
            constraintSet.applyTo(constLayoutContainer);
            newProjectButton.setVisibility(View.VISIBLE);
            newProjectButton2.setVisibility(View.INVISIBLE);

        }
        else{
            new Handler().postDelayed((Runnable) () -> {
                projectButton.setY(newProjectButton.getY());
                newProjectButton.setY(newProjectButton.getY() + dpToPx(200));
            }, 10);

            newProjectButton.setVisibility(View.INVISIBLE);
            newProjectButton2.setVisibility(View.VISIBLE);

        }

        projectButton.setOnClickListener(this::onClickProjectButton);
    }

    private TextView createTaskComment(String comment){
        // Create a TextView with the comment provided.

        TextView projectTask = new TextView(context);
        projectTask.setText(comment);
        if(comment.contains("\u200E")){
            projectTask.setPaintFlags(projectTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        projectTask.setTextColor(Color.parseColor("#000000"));
        projectTask.setGravity(Gravity.START);
        projectTask.setTextSize(12);
        projectTask.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        projectTask.setId(View.generateViewId());

        return projectTask;
    }


    private boolean isRequestCodeTaken(int requestCode){
        // Checks if the request code is taken.
        return usedRequestCodes.contains(requestCode);
    }

    public void createBackgroundView(Context context) {
        // Creates the dark background for dialogs and pop menues.
        new Handler().postDelayed(() -> {
            View transparentGreyView = new View(context);
            transparentGreyView.setBackgroundColor(Color.parseColor("#80808080"));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            transparentGreyView.setLayoutParams(layoutParams);
            transparentGreyView.setTag("mainBackground");

            if (mainLayout != null) {
                mainLayout.addView(transparentGreyView);
                Log.i("createBackgroundView", "Background created successfully");
            } else {
                Log.e("createBackgroundView", "mainLayout is null");
            }
        }, 100);

    }

    public void removeBackgroundView(){
        // Removes the dark background from dialogs and pop menues.
        new Handler().postDelayed(() -> {
            View removeView = mainLayout.findViewWithTag("mainBackground");

            if (removeView != null) {
                mainLayout.removeView(removeView);
            }

        }, 100);
    }
}

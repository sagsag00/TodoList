package com.tsafrir.sagi.schoolproject.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.VolumeShaper;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.tsafrir.sagi.schoolproject.MainActivity;
import com.tsafrir.sagi.schoolproject.R;
import com.tsafrir.sagi.schoolproject.fragments_main.Home_Fragment;
import com.tsafrir.sagi.schoolproject.projectpop.ProjectManagePop;
import com.tsafrir.sagi.schoolproject.projectpop.db.Project;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDBHelper;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationReceiver", "Sending notification");

        int projectId = intent.getIntExtra("clickedProject", -1) - 2;
        RemoteViews notificationViews = null;

        // If not null.
        if(projectId != -1){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Creating a custom notification.

                ProjectDBHelper dbHelper = ProjectDatabase.getDBHelper();
                List<Project> projects = dbHelper.getAllProjects();
                Project project = projects.get(projectId);
                List<String> tasks = project.getTasks();

                notificationViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);

                int textColor = Color.parseColor("#ffffff");
                int drawable = R.drawable.circle_notification_task_white;
                if((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                        != Configuration.UI_MODE_NIGHT_YES){
                    textColor = Color.parseColor("#000000");
                    drawable = R.drawable.circle_notification_task_black;
                }

                notificationViews.setTextViewText(R.id.projectNameNotification, project.getProjectTitle());
                notificationViews.setTextColor(R.id.projectNameNotification, project.getColor());

                // Showing the first 3 tasks.
                try {
                    notificationViews.setTextViewText(R.id.task1Notification, tasks.get(0));
                    notificationViews.setTextViewCompoundDrawables(R.id.task1Notification,
                            drawable, 0, 0, 0);
                    notificationViews.setTextColor(R.id.task1Notification, textColor);
                    notificationViews.setTextViewText(R.id.task2Notification, tasks.get(1));
                    notificationViews.setTextViewCompoundDrawables(R.id.task2Notification,
                            drawable, 0, 0, 0);
                    notificationViews.setTextColor(R.id.task2Notification, textColor);
                    notificationViews.setTextViewText(R.id.task3Notification, tasks.get(2));
                    notificationViews.setTextViewCompoundDrawables(R.id.task3Notification,
                            drawable, 0, 0, 0);
                    notificationViews.setTextColor(R.id.task3Notification, textColor);
                } catch (Exception e){
                    Log.e("onReceive", e.toString());
                }
                String[] colors = context.getResources().getStringArray(R.array.important_color);
                String[] colorsDate = context.getResources().getStringArray(R.array.notification_date_color);
                try {
                    notificationViews.setTextViewText(R.id.dueDateNotification, project.getDueDate());

                    SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = inputFormat.parse(project.getDueDate());

                    long dueDate = Timestamp.valueOf(outputFormat.format(date) + " 00:00:00").getTime();
                    long currentDate = Calendar.getInstance().getTimeInMillis();
                    long difference = dueDate - currentDate;
                    int color;
                    // If the due date is 2 days or less, sets the color differently.
                    if(difference < 1000 * 60 * 60 * 24 * 2) // At max two days until due date.
                    {
                        color = Color.parseColor(colorsDate[2]);
                        // If the due date is 1 week or less (but more than 2 days), sets the color differently.
                    } else if (difference < 1000 * 60 * 60 * 24 * 7) // At max seven days until due date.
                    {
                        color = Color.parseColor(colorsDate[1]);
                        // Else sets it differently.
                    } else {
                        color = Color.parseColor(colorsDate[0]);
                    }
                    notificationViews.setTextColor(R.id.dueDateNotification, color);

                } catch (Exception e){
                    Log.e("onReceive", e.toString());
                }
                // Sets the importance and its color.
                switch (project.getImportanceLevel()){
                    case 0:
                        notificationViews.setTextViewText(R.id.importanceLevelNotification, "Not Important");
                        notificationViews.setTextColor(R.id.importanceLevelNotification,
                                Color.parseColor(colors[0]));
                        break;
                    case 1:
                        notificationViews.setTextViewText(R.id.importanceLevelNotification, "Not Very Important");
                        notificationViews.setTextColor(R.id.importanceLevelNotification,
                                Color.parseColor(colors[1]));
                        break;
                    case 2:
                        notificationViews.setTextViewText(R.id.importanceLevelNotification, "Important");
                        notificationViews.setTextColor(R.id.importanceLevelNotification,
                                Color.parseColor(colors[2]));
                        break;
                    case 3:
                        notificationViews.setTextViewText(R.id.importanceLevelNotification, "Very Important");
                        notificationViews.setTextColor(R.id.importanceLevelNotification,
                                Color.parseColor(colors[3]));
                        break;
                    case 4:
                        notificationViews.setTextViewText(R.id.importanceLevelNotification, "Cannot Miss");
                        notificationViews.setTextColor(R.id.importanceLevelNotification,
                                Color.parseColor(colors[4]));
                        break;

                }

                Log.i("onReceive", "RemoteViews created");

            }
        }

        createNotificationChannel(context);

        // Creates the notification intent for when it is clicked.
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivityIntent.putExtra("notificationMainActivity", 1);

        Intent popUpActivityIntent = new Intent(context, ProjectManagePop.class);
        popUpActivityIntent.putExtra("clickedProject", projectId);
        popUpActivityIntent.putExtra("notificationManagePop", 2); // To let the ProjectManagePop know that you got sent from the notification.

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(mainActivityIntent);
        stackBuilder.addNextIntent(popUpActivityIntent);


        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ProjectNotification")
                .setContentTitle("You need to work on your project!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notifications)
                .setCustomBigContentView(notificationViews)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

        rescheduleNotification(context, intent);


    }

    private void createNotificationChannel(Context context) {
        // Creates the notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ProjectNotification",
                    "Project Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for project notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void rescheduleNotification(Context context, Intent mIntent){
        // Rescheduling the notification.
        String interval = mIntent.getStringExtra("notificationInterval");

        if (interval == null || !interval.equals("week") && !interval.equals("month") && !interval.equals("day")){
            Log.e("createNotification", "Unknown interval");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);

        // Finds the interval.
        if(interval.equals("day")){
            calendar.add(Calendar.DATE, 1);
        } else if (interval.equals("week")){
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }
        long timeInMillis = calendar.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notificationInterval", interval);

        int requestCode = mIntent.getIntExtra("requestCode", -1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        try{
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
        catch (SecurityException e){
            Log.e("setNotification", e.toString());
            try{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
            catch (SecurityException ex){
                Log.e("setNotification", ex.toString());
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }

        Log.e("test", String.valueOf(timeInMillis));
        Log.i("createNotifications", "Notification set for " + interval);
    }


}


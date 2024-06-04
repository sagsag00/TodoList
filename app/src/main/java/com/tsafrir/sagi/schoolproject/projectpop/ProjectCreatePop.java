package com.tsafrir.sagi.schoolproject.projectpop;

import static android.view.View.INVISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import com.tsafrir.sagi.schoolproject.R;
import com.tsafrir.sagi.schoolproject.projectpop.db.Project;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDBHelper;
import com.tsafrir.sagi.schoolproject.projectpop.db.ProjectDatabase;
import com.tsafrir.sagi.schoolproject.projectpop.db.Reminder;
import com.tsafrir.sagi.schoolproject.projectpop.db.RemindersEachXTime;
import com.tsafrir.sagi.schoolproject.projectpop.db.RemindersStartXBefore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class ProjectCreatePop extends FragmentActivity implements SetTimeBefore.OnInputListener, SetTimesRemind.OnInputListener {

    private ProjectDBHelper dbHelper;
    private SQLiteDatabase database;
    private EditText projectTitle;
    private TextView projectPopDone;
    private EditText newTask1; // The button that is named New Task.
    private EditText newTask2; // The button that is named New Task.
    private EditText setDueDateButton; // The button to change the date.
    private EditText setTimeBefore; // The button to configure how much time before you need to be reminded.
    private EditText setHowMuchToRemind; // Set every X per days/weeks/months to be reminded. For example: 7 days: will remind you 7 times a day, 2 weeks: 2 times  a week.

    private AnimationDrawable trashCanAnimation;
    private ImageView trashCan; // The trash can that shows up when holding on the tasks.
    private MaterialDayPicker dayPickerForWeek; // Lets the user pick the days of the week they want to be reminded at. //! I DID NOT MAKE THIS SPECIFIC CLASS.
    private EditText setTimeOfReminder; // The button to configure the time to be reminded everyday.

    private Button createMoreTasks; // The big plus button that creates more tasks.

    private View stopLine4; //  The line that separates the time set buttons from the rest of the screen.
    private View stopLine5; // Line like 4 but it is for when the user is wanting more than 1 per day.
    private View stopLine6; // Line like 5 but it is for when the user is wanting more than 1 per day.

    private TextView selectImportance; // A button that let's you select the importance of the project.

    private char lastWordDay; // The last word of the setHowMuchToRemind
    private Context context; // The context of this.

    Drawable circleImportant; // The circle next to the selectImportance text.
    Drawable customDialogStyle; // Custom style for the Dialog.

    private String[] importanceLevels; // All of the levels of importance.
    private String[] importanceColors; // All of the colors corresponding to the levels.
    private byte numberOfTimes = 2; // A variable that stores the number of notifications per time.
    private byte saveNumberOfTimes = 2; // Saves the numberOfTimes variable.

    MaterialDayPicker.Weekday[] weekDays = {MaterialDayPicker.Weekday.valueOf("SUNDAY"),
            MaterialDayPicker.Weekday.valueOf("MONDAY"), MaterialDayPicker.Weekday.valueOf("TUESDAY"), MaterialDayPicker.Weekday.valueOf("WEDNESDAY"),
            MaterialDayPicker.Weekday.valueOf("THURSDAY"), MaterialDayPicker.Weekday.valueOf("FRIDAY"), MaterialDayPicker.Weekday.valueOf("SATURDAY")};
    private String timeToNotify; // The time to notify the user.
    private int countNewTasks = 0;

    private CheckBox dueDateToggle; // Lets you toggle if your project has a due date or not.
    private boolean isDueDateToggled = true; // Is the button dueDate is toggled (default yes).

    char saveStrLastWordDay = 'k';
    private RadioGroup multipleOptionsForMoreThan1PerDay; // The radio group that has 2 radio buttons to choose when you want more than 1 notification per day.
    private RadioButton optionAutomaticBetweenSetHours; // If you have more than 1 notification per day, you can choose this so that all of the notifications' times will be automatic.
    private RadioButton optionSetEachAlarmManually; // If you have more than 1 notification per day, you can choose this so that you can set all of the notifications manually.

    private EditText fromThisTime; // If the optionAutomaticBetweenSetHours is selected, you will be presented with this edit text to choose start time.
    private EditText toThisTime; // If the optionAutomaticBetweenSetHours is selected, you will be presented with this view. Does nothing.
    private TextView fromThisTimeTotoThisTime; // If the optionAutomaticBetweenSetHours is selected, you will be presented with this edit text to choose end time.

    // If the optionSetEachAlarmManually is selected, you will be presented with 2-6 of these, depends on how much times per day.
    private final EditText[] manualSelectTimesDay = new EditText[6]; // Array with all of the manualSelectTimeDay. //TODO Could maybe merge all three into one to make code cleaner.

    private int currId = -1;

    private List<Integer> newTaskIds = new ArrayList<>();

    private ConstraintLayout popConstLayout; // The main layout.

    private ConstraintLayout constLayoutGoDown1; // The layout for everything that is under the plus button - so it all will go down easily.

    Date dueDate; // The due date.

    private int intImportanceColor; // The int of the importance color.

    static byte intChosenImportanceLevel = 2; // The default importance is in place 2 (important).


    //TODO FIX THE CONSTRAINTS.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_create_pop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ProjectCreatePop.this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProjectCreatePop.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        context = this;

        // Gets the importance levels and corresponding colors.
        importanceLevels = getResources().getStringArray(R.array.importance_levels);
        importanceColors = getResources().getStringArray(R.array.important_color);

        // Creates the ProjectCreatePop as a pop up.

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Sets the height of the popup to 85% of the screen.
        getWindow().setLayout(width, (int) (height * 0.85));

        init();

        Drawable drawableWithColor = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle_importance_color));
        DrawableCompat.setTint(drawableWithColor, Color.parseColor("#808080"));

        newTaskIds.add(newTask1.getId());
        newTaskIds.add(newTask2.getId());

        newTask1.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
        newTask1.setCompoundDrawablePadding(dpToPx(10));

        newTask1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = newTask1.getText().toString();
                Drawable drawableWithColor = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle_importance_color));

                if (inputText.isEmpty()) {
                    DrawableCompat.setTint(drawableWithColor, Color.parseColor("#808080"));
                } else {

                    DrawableCompat.setTint(drawableWithColor, Color.parseColor("#000000"));
                }
                DrawableCompat.setTintMode(drawableWithColor, PorterDuff.Mode.SRC_IN);
                newTask1.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        newTask1.setOnLongClickListener(this::onLongClickTask);
        newTask2.setOnLongClickListener(this::onLongClickTask);

        trashCan.setOnDragListener(this::onDragTrashCan);

        newTask2.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
        newTask2.setCompoundDrawablePadding(dpToPx(10));

        newTask2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = newTask2.getText().toString();
                Drawable drawableWithColor = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle_importance_color));

                if (inputText.isEmpty()) {
                    DrawableCompat.setTint(drawableWithColor, Color.parseColor("#808080"));
                } else {

                    DrawableCompat.setTint(drawableWithColor, Color.parseColor("#000000"));
                }
                DrawableCompat.setTintMode(drawableWithColor, PorterDuff.Mode.SRC_IN);
                newTask2.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        drawableWithColor = DrawableCompat.wrap(circleImportant);
        DrawableCompat.setTint(drawableWithColor, intImportanceColor);
        DrawableCompat.setTintMode(drawableWithColor, PorterDuff.Mode.SRC_IN);


        // Listening if the user wants to create more tasks (if he presses the big plus button)

        createMoreTasks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("createMoreTasks", "Clicked...");
                if(newTask2 != null && newTask2.getVisibility() == View.INVISIBLE){
                    newTask2.setVisibility(View.VISIBLE);
                    return;
                }

                if(countNewTasks > 7){
                    Toast.makeText(context, "Maximum tasks reached.", Toast.LENGTH_LONG).show();
                    return;
                }

                Drawable drawableWithColor = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle_importance_color));
                DrawableCompat.setTint(drawableWithColor, Color.parseColor("#808080"));


                // Creating a new EditText that wll be the new task.
                EditText editText1 = createTask(newTask1.getWidth(), newTask1.getHeight(), drawableWithColor);

                editText1.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String inputText = editText1.getText().toString();
                        Drawable drawableWithColor = DrawableCompat.wrap(getResources().getDrawable(R.drawable.circle_importance_color));

                        if (inputText.isEmpty()) {
                            DrawableCompat.setTint(drawableWithColor, Color.parseColor("#808080"));
                        } else {

                            DrawableCompat.setTint(drawableWithColor, Color.parseColor("#000000"));
                        }
                        DrawableCompat.setTintMode(drawableWithColor, PorterDuff.Mode.SRC_IN);
                        editText1.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }

                });
                editText1.setOnLongClickListener(ProjectCreatePop.this::onLongClickTask);


                setTaskConstraints(editText1);
                countNewTasks++;
                popConstLayout.requestLayout();
            }
        });

        // Setting up a onClickListener for when the user wants to choose the importance.
        selectImportance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("selectImportance", "Clicked...");

                // Creates a dialog that lets the user pick between importance.
                AlertDialog.Builder builder = new AlertDialog.Builder(context,
                        R.style.Theme_SchoolProject_RoundedDialogTheme)
                        .setItems(importanceLevels, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                intImportanceColor = Color.parseColor(importanceColors[i]); // Setting the color to the new chosen importance level.
                                Drawable drawableWithColor = DrawableCompat.wrap(circleImportant);
                                DrawableCompat.setTint(drawableWithColor, intImportanceColor);
                                DrawableCompat.setTintMode(drawableWithColor, PorterDuff.Mode.SRC_IN);

                                selectImportance.setHint(importanceLevels[i]); // Setting the text to the new chosen importance level.
                                dialogInterface.dismiss();

                                intChosenImportanceLevel = (byte) Arrays.asList(importanceLevels).indexOf(selectImportance.getHint());

                            }
                        });
                AlertDialog alertDialog = builder.create();


                // Creates a new onShowListener for when the dialog is shown.
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        ListView listView = alertDialog.getListView(); // Gets the list of items (importance levels).
                        if (listView != null) {
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                            if (adapter != null) {
                                int textColor = ContextCompat.getColor(context, android.R.color.black);
                                // Makes a new adapter using the importanceLevels as the ArrayList.
                                listView.setAdapter(new ArrayAdapter<String>(
                                        context,
                                        android.R.layout.simple_list_item_1, // Which layout from android studio to use.
                                        android.R.id.text1, // Where would the importance levels will be. (TextView)
                                        importanceLevels
                                ) {
                                    @NonNull
                                    @Override
                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        // Sets the color of the text.
                                        View view = super.getView(position, convertView, parent);
                                        TextView textView = view.findViewById(android.R.id.text1);
                                        textView.setTextColor(textColor);
                                        return view;
                                    }
                                });
                            }
                        }
                    }
                });

                alertDialog.show();
            }
        });

        // Set the drawable to the left of "selectImportance".
        selectImportance.setCompoundDrawablesWithIntrinsicBounds(drawableWithColor, null, null, null);
        selectImportance.setCompoundDrawablePadding(dpToPx(10));

        // Getting the date of tomorrow. (To set as the default date.)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dueDate = calendar.getTime();

        // Formatting the date. Example: "Sat, Oct 21, 2023".
        SimpleDateFormat outFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        String goal = outFormat.format(dueDate);
        setDueDateButton.setHint(goal); // The default date is tomorrow.

        Calendar correctTimeCalendar = Calendar.getInstance();

        // Setting an onClickListener for when the user decides to pick a due date for their project.
        setDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("setDueDateButton", "Clicked...");

                // Creating the grey background.
                View transparentGreyView = new View(context);
                transparentGreyView.setBackgroundColor(Color.parseColor("#80808080"));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                );
                transparentGreyView.setLayoutParams(layoutParams);
                transparentGreyView.setTag("greyBackground");
                popConstLayout.addView(transparentGreyView);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Log.i("datePickerDialog", "Date set...");

                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(year, month, day);
                                if (calendar1.getTime().before(correctTimeCalendar.getTime())) {
                                    Toast.makeText(context, "Date already passed", Toast.LENGTH_SHORT).show();
                                } else {
                                    setDueDateButton.setHint(outFormat.format(calendar1.getTime()));
                                }
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ConstraintLayout constraintLayout = findViewById(R.id.PopConstLayout);
                        View removeView = constraintLayout.findViewWithTag("greyBackground");

                        if (removeView != null) {
                            constraintLayout.removeView(removeView);
                        }
                    }
                });

                datePickerDialog.show();

            }
        });

        // Default time.
        setTimeBefore.setHint("3 Weeks before");

        // Setting an onClickListener for when the user wants to change the time to be reminded before the due date.
        setTimeBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SetTimeBefore", "Clicked...");

                // Creating the grey background.

                View transparentGreyView = new View(context);
                transparentGreyView.setBackgroundColor(Color.parseColor("#80808080"));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                );
                transparentGreyView.setLayoutParams(layoutParams);
                transparentGreyView.setTag("greyBackground");
                popConstLayout.addView(transparentGreyView);


                SetTimeBefore stbCustomDialog = new SetTimeBefore(intChosenImportanceLevel);
                stbCustomDialog.setOnCustomDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.i("stbCustomDialog", "Dismissing...");

                        // Removing the grey background on dismiss.
                        View removeView = popConstLayout.findViewWithTag("greyBackground");

                        if (removeView != null) {
                            popConstLayout.removeView(removeView);
                        }
                    }
                });
                stbCustomDialog.show(getSupportFragmentManager().beginTransaction(), "SetTimeBefore");
            }
        });

        // The default time to be reminded.
        setTimeOfReminder.setHint("04:00 PM");

        // Setting a onClickListener for when the user wants to change the default time of the day of the notification.
        setTimeOfReminder.setOnClickListener(this::onClickTimeDialog);

        // Checks if the user has toggled the dueDateToggle button.
        dueDateToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isToggledOn) {
                Log.i("dueDateToggle", "Checked changed...");

                isDueDateToggled = isToggledOn;
                if (isToggledOn) {
                    setDueDateButton.setVisibility(View.VISIBLE);
                    setTimeBefore.setVisibility(View.VISIBLE);
                    setHowMuchToRemind.setVisibility(View.INVISIBLE);
                    stopLine5.setVisibility(INVISIBLE);

                    if (multipleOptionsForMoreThan1PerDay.getVisibility() == View.VISIBLE || dayPickerForWeek.getVisibility() == View.VISIBLE)
                        ProjectCreatePop.this.destroyAllButtonsForMoreThan1PerX();
                } else {
                    setDueDateButton.setVisibility(View.INVISIBLE);
                    setTimeBefore.setVisibility(View.INVISIBLE);
                    setHowMuchToRemind.setVisibility(View.VISIBLE);

                    Log.d("dueDateToggleOnClickListener", String.valueOf(setHowMuchToRemind.getHint()));

                    char lastChar = ProjectCreatePop.this.setHowMuchToRemind.getHint().toString().charAt(setHowMuchToRemind.getHint().length() - 1);

                    if (ProjectCreatePop.this.setHowMuchToRemind.getHint().toString().charAt(0) >= 1) {
                        if (lastChar == 'y') {
                            ProjectCreatePop.this.buttonsForMoreThan1PerDay();
                        } else if (lastChar == 'k') {
                            ProjectCreatePop.this.buttonsForMoreThan1PerWeek();
                        } else if (lastChar == 'h') {
                            //buttonsForMoreThan1PerMonth();
                        }

                    }

                }
            }
        });

        setHowMuchToRemind.setHint("2 times per Week");
        dayPickerForWeek.setSelectedDays(MaterialDayPicker.Weekday.valueOf("SUNDAY"), MaterialDayPicker.Weekday.valueOf("THURSDAY"));

        setHowMuchToRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("setHowMuchToRemind", "Clicked...");

                // Creating the grey background.
                View transparentGreyView = new View(context);
                transparentGreyView.setBackgroundColor(Color.parseColor("#80808080"));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                );
                transparentGreyView.setLayoutParams(layoutParams);
                transparentGreyView.setTag("greyBackground");
                popConstLayout.addView(transparentGreyView);


                SetTimesRemind strCustomDialog = new SetTimesRemind(intChosenImportanceLevel, importanceLevels);
                strCustomDialog.setOnCustomDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog1) {
                        Log.i("strCustomDialog", "Dismissed...");
                        // Removing the grey background on dismiss.
                        View removeView = popConstLayout.findViewWithTag("greyBackground");

                        if (removeView != null) {
                            popConstLayout.removeView(removeView);
                        }

                    }
                });
                strCustomDialog.show(ProjectCreatePop.this.getSupportFragmentManager().beginTransaction(), "SetTimesRemind");

            }
        });

        multipleOptionsForMoreThan1PerDay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                Log.i("multipleOptionsForMoreThan1PerDay", "Check changed...");

                if (optionAutomaticBetweenSetHours.getId() == index) {
                    ProjectCreatePop.this.setupAutomaticallyBetweenButtonForMoreThan1PerDay();
                } else {
                    String textOfHowMuchToRemind = Objects.requireNonNull(String.valueOf(setHowMuchToRemind.getHint()));
                    if (textOfHowMuchToRemind.length() > 3) {
                        if ((ProjectCreatePop.this.numberOfTimes > 1)
                                && lastWordDay == 'y') {
                            ProjectCreatePop.this.setupManuallyButtonForMoreThan1PerDay();
                        }
                    }
                }
            }

        });

        projectPopDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inserts the project to the database.
                dbHelper = ProjectDatabase.getDBHelper();
                database = dbHelper.getWritableDatabase();

                List<String> tasks = new ArrayList<>();

                Iterator<Integer> iterator = newTaskIds.iterator();
                while (iterator.hasNext()) {
                    Integer taskId = iterator.next();
                    EditText editText = (EditText) findViewById(taskId);
                    if(editText != null){
                        tasks.add(editText.getText().toString());
                    }
                }

                String dueDate = "";
                RemindersStartXBefore remindersStartXBefore = new RemindersStartXBefore();
                RemindersEachXTime remindersEachXTime = new RemindersEachXTime();
                StringBuilder amountOfAlarms = new StringBuilder();

                List<String> times = getCurrentSetTimes();
                Reminder reminders = new Reminder();
                reminders.setTimes(times);
                if(isDueDateToggled){
                    dueDate = setDueDateButton.getHint().toString();
                    String[] setTimeBeforeString = setTimeBefore.getHint().toString().split(" ");
                    for(String word : setTimeBeforeString){
                        switch (word){
                            case "Minutes":
                                remindersStartXBefore.setMinutes(Integer.parseInt(amountOfAlarms.toString()));
                                break;
                            case "Hours":
                                remindersStartXBefore.setHours(Integer.parseInt(amountOfAlarms.toString()));
                                break;
                            case "Days":
                                remindersStartXBefore.setDays(Integer.parseInt(amountOfAlarms.toString()));
                                break;
                            case "Weeks":
                                remindersStartXBefore.setWeeks(Integer.parseInt(amountOfAlarms.toString()));
                                break;
                            case "before":
                                break;
                            default: // Numbers
                                amountOfAlarms.append(word);
                                break;
                        }

                    }
                } else{
                    String[] setAmountAlarmsString = setHowMuchToRemind.getHint().toString().split(" ");
                    constructReminderXAndReminder(setAmountAlarmsString, amountOfAlarms, remindersEachXTime, reminders);
                }
                if(reminders.getDayOfWeek() == null){
                    reminders.setDayOfWeek(new ArrayList<>());
                }
                if(reminders.getDayOfMonth() == null){
                    reminders.setDayOfMonth(new ArrayList<>());
                }

                Random random = new Random();

                int red = random.nextInt(128) + 128;
                int green = random.nextInt(128) + 128;
                int blue = random.nextInt(128) + 128;

                Project thisProject = new Project(
                        projectTitle.getText().toString(),
                        tasks,
                        intChosenImportanceLevel,
                        isDueDateToggled,
                        dueDate,
                        reminders,
                        remindersStartXBefore,
                        remindersEachXTime,
                        Color.rgb(red, green, blue)
                );
                long rowId = dbHelper.insertProject(thisProject);

                if (rowId != -1) {
                    // Successful insertion
                    setResult(RESULT_OK);
                } else {
                    // Failed insertion
                    setResult(RESULT_CANCELED);
                }

                finish();

            }
        });
    }

    public int dpToPx(int dp) {
        // Converts dp to px.
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    public int pxToDp(int px) {
        // Converts px to dp.
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, getResources().getDisplayMetrics()));
    }

    public int pxToDp(float px) {
        // Converts px to dp.
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, getResources().getDisplayMetrics()));
    }

    public String findLongestString(String[] checkString) {
        // Finds the longest string.
        if (checkString == null || checkString.length == 0) {
            return null;
        }

        String longestString = checkString[0];
        for (String str : checkString) {
            if (str.length() > longestString.length()) {
                longestString = str;
            }
        }
        return longestString;
    }

    public void init() {
        Log.i("init", "Initializing...");

        // Finding each View by its id.
        popConstLayout = (ConstraintLayout) findViewById(R.id.PopConstLayout);
        constLayoutGoDown1 = (ConstraintLayout) findViewById(R.id.constLayoutForGoDown1);
//        constLayoutGoDown2 = (ConstraintLayout) findViewById(R.id.constLayoutForGoDown2);
        projectTitle = (EditText) findViewById(R.id.addProjectTitle);
        createMoreTasks = (Button) findViewById(R.id.createMoreTasks);
        newTask1 = (EditText) findViewById(R.id.newTask1);
        newTask2 = (EditText) findViewById(R.id.newTask2);
        stopLine4 = (View) findViewById(R.id.stopLine4);
        stopLine5 = (View) findViewById(R.id.stopLine5);
        stopLine6 = (View) findViewById(R.id.stopLine6);
        selectImportance = (TextView) findViewById(R.id.selectImportance);
        circleImportant = getResources().getDrawable(R.drawable.circle_importance_color);
        setDueDateButton = (EditText) findViewById(R.id.setDueDateButton);
        setTimeOfReminder = (EditText) findViewById(R.id.setTimeOfReminder);
        setTimeBefore = (EditText) findViewById(R.id.setTimeBefore);
        dueDateToggle = (CheckBox) findViewById(R.id.dueDateToggle);
        setHowMuchToRemind = (EditText) findViewById(R.id.setHowMuchToRemind);
        multipleOptionsForMoreThan1PerDay = (RadioGroup) findViewById(R.id.radioGroupOptionsForMoreThan1PerDay);
        optionAutomaticBetweenSetHours = (RadioButton) findViewById(R.id.optionAutomaticBetweenSetHours);
        optionSetEachAlarmManually = (RadioButton) findViewById(R.id.optionSetEachAlarmManually);
        fromThisTime = (EditText) findViewById(R.id.fromThisTime);
        toThisTime = (EditText) findViewById(R.id.toThisTime);
        fromThisTimeTotoThisTime = (TextView) findViewById(R.id.fromThisTimeTotoThisTimeLinker);
        dayPickerForWeek = (MaterialDayPicker) findViewById(R.id.dayPickerForWeek);
        trashCan = (ImageView) findViewById(R.id.trashCan);
        trashCan.setImageResource(R.drawable.animation_trash_can_start);
        trashCanAnimation = (AnimationDrawable) trashCan.getDrawable();
        projectPopDone = (TextView) findViewById(R.id.projectPopDone);

        // Sets the default importance, its color, and wrapping its drawable.
        selectImportance.setHint(importanceLevels[importanceLevels.length / 2]);
        intImportanceColor = Color.parseColor(importanceColors[importanceColors.length / 2]);

        // Getting the custom dialog style and setting its bounds.
        customDialogStyle = getResources().getDrawable(R.drawable.rounded_dialog_bg);
        customDialogStyle.setBounds(0, 0, pxToDp(findLongestString(importanceLevels).length()), customDialogStyle.getIntrinsicHeight());

    }

    public List<Integer> getSelectedDaysOfWeek(){
        // Gets the selected days of dayPickerForWeek.
        List<Integer> selectedDaysOfWeek = new ArrayList<>();
        MaterialDayPicker dayPickerForWeek = findViewById(R.id.dayPickerForWeek);
        List<MaterialDayPicker.Weekday> days = dayPickerForWeek.getSelectedDays();
        for(MaterialDayPicker.Weekday day : days){
            switch (day.toString()){
                case "SUNDAY":
                    selectedDaysOfWeek.add(1);
                    break;
                case "MONDAY":
                    selectedDaysOfWeek.add(2);
                    break;
                case "TUESDAY":
                    selectedDaysOfWeek.add(3);
                    break;
                case "WEDNESDAY":
                    selectedDaysOfWeek.add(4);
                    break;
                case "THURSDAY":
                    selectedDaysOfWeek.add(5);
                    break;
                case "FRIDAY":
                    selectedDaysOfWeek.add(6);
                    break;
                case "SATURDAY":
                    selectedDaysOfWeek.add(7);
                    break;
            }
        }
        return selectedDaysOfWeek;
    }

    public void constructReminderXAndReminder(String[] setAmountAlarmsString, StringBuilder amountOfAlarms, RemindersEachXTime remindersEachXTime, Reminder reminders){
        // Constructing the ReminderEachXTime and Reminder classes.
        for(String word : setAmountAlarmsString){
            int amount = 0;
            if(!amountOfAlarms.toString().isEmpty()){
                amount = Integer.parseInt(amountOfAlarms.toString());
            }

            switch (word){
                case "Day":
                    remindersEachXTime.setDay(amount);
                    break;
                case "Week":
                    remindersEachXTime.setWeek(amount);
                    List<Integer> selectedDaysOfWeek = getSelectedDaysOfWeek();
                    reminders.setDayOfWeek(selectedDaysOfWeek);
                    break;
                case "Month":
                    remindersEachXTime.setMonth(amount);

                    List<Integer> selectedDaysOfMonth = new ArrayList<>();
                    int steps = (int) Math.floor(28.0 / amount); // 28 -> The least amount of days in a month.
                    int addAmount;
                    for(int i = 1; i <= amount; i++){
                        addAmount = i * steps + 1;
                        if(addAmount > 28){
                            addAmount = 1;
                        }
                        selectedDaysOfMonth.add(addAmount);
                    }

                    reminders.setDayOfMonth(selectedDaysOfMonth);
                    break;
                case "times":
                case "per":
                    break;
                default: // Numbers
                    amountOfAlarms.append(word);
                    break;
            }
        }
    }

    public void setupAutomaticallyBetweenButtonForMoreThan1PerDay() {
        // Sets up the buttons of automatically option when you select more than 1 per day (without a due date).
        // Steps to reach: Create Project -> Untick Due Date button -> Change the time interval of each reminder to a time more than 1 per day.
        Log.i("setupAutomaticallyBetweenButtonForMoreThan1PerDay", "Creating buttons...");

        if (manualSelectTimesDay[0] != null) {
            destroyButtonsForMoreThan1PerDay();
        }

        this.multipleOptionsForMoreThan1PerDay.setVisibility(View.VISIBLE);
        this.fromThisTime.setVisibility(View.VISIBLE);
        this.toThisTime.setVisibility(View.VISIBLE);
        this.fromThisTimeTotoThisTime.setVisibility(View.VISIBLE);
        this.stopLine5.setVisibility(View.VISIBLE);
        this.stopLine6.setVisibility(View.INVISIBLE);

        // End of showing everything needed.

        // Setting up the onClickListeners:

        if (String.valueOf(this.fromThisTime.getHint()).equals("Time -> ring")) {
            this.fromThisTime.setHint("04:00 PM");
        }
        if (String.valueOf(this.toThisTime.getHint()).equals("Time -> ring")) {
            this.toThisTime.setHint("06:00 PM");
        }

        this.fromThisTime.setOnClickListener(this::onClickTimeDialog);

        this.toThisTime.setOnClickListener(this::onClickTimeDialog);
    }

    public void setupManuallyButtonForMoreThan1PerDay() {
        // Sets up the manual option for more than 1 per day.
        // Steps to reach: Create Project -> Untick Due Date button -> Change the time interval of each reminder to a time more than 1 per day
        // -> Manual option.

        Log.i("setupManuallyButtonForMoreThan1PerDay", "Creating buttons...");

        if (this.manualSelectTimesDay[0] != null) {
            for (byte i = 0; i < this.saveNumberOfTimes; i++) {
                this.manualSelectTimesDay[i].setVisibility(View.VISIBLE);
            }
        }

        if(this.manualSelectTimesDay[0] == null){
            for (byte i = 1; i <= this.manualSelectTimesDay.length; i++) {
                Log.v("setupManuallyButtonForMoreThan1PerDay", "Setting manualSelectTimesDay...");
                int resourceId = getResources().getIdentifier("customTime" + i, "id", getPackageName());
                manualSelectTimesDay[i-1] = findViewById(resourceId);
                manualSelectTimesDay[i-1].setHint("0" + (i+3) + ":00 PM");
                manualSelectTimesDay[i-1].setOnClickListener(this::onClickTimeDialog);
            }
        }

        if (this.numberOfTimes < this.saveNumberOfTimes) {
            for (byte i = this.numberOfTimes; i < this.saveNumberOfTimes; i++) {
                if(this.manualSelectTimesDay[i] != null) {
                    this.manualSelectTimesDay[i].setVisibility(View.INVISIBLE);
                }
            }
        } else {
            for (byte i = this.saveNumberOfTimes; i < this.numberOfTimes; i++) {
                if(this.manualSelectTimesDay[i] != null) {
                    this.manualSelectTimesDay[i].setVisibility(View.VISIBLE);
                }
            }
        }

        if(numberOfTimes > 3){
            stopLine5.setVisibility(View.INVISIBLE);
            stopLine6.setVisibility(View.VISIBLE);
        }
        else{
            stopLine5.setVisibility(View.VISIBLE);
            stopLine6.setVisibility(INVISIBLE);
        }

        if (this.fromThisTime.getVisibility() == View.VISIBLE) {
            destroyButtonsForMoreThan1PerDay();
        }
    }

    public List<String> getCurrentSetTimes(){
        // Gets all of the times for notifications. (For example: 4:00 PM, 5:00 PM).
        List<String> times = new ArrayList<>();
        if(lastWordDay == 0){ // lastWordDay = null.
            lastWordDay = 'k'; // The default is week so 'k' is from the last character of week.
        }
        if(isDueDateToggled){
            times.add(setTimeOfReminder.getHint().toString()); // The time in the bottom right. Default is 04:00 PM.
            return times;
        }
        if (lastWordDay == 'y') // Meaning if the word is "Day" because the y is from the last character.
        {
            if(numberOfTimes == 1) {
                times.add(setTimeOfReminder.getHint().toString()); // The time in the bottom right. Default is 04:00 PM.
                return times;
            }
            // If the user wants manual time for each alarm.
            if(multipleOptionsForMoreThan1PerDay.getCheckedRadioButtonId() == optionSetEachAlarmManually.getId()){
                for(EditText timeEditText : manualSelectTimesDay){
                    if(timeEditText.getVisibility() == INVISIBLE){
                        break;
                    }
                    times.add(timeEditText.getHint().toString()); // Adds the times.
                }
                return times;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Auto alarms.
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a"); // Formats it as "04:00 PM", for example.
                LocalTime startTime = LocalTime.parse(fromThisTime.getHint().toString(), formatter); // Format the start time.
                LocalTime endTime = LocalTime.parse(toThisTime.getHint().toString(), formatter); // Format the end time.


                long timeBetweenMinutes = startTime.until(endTime, ChronoUnit.MINUTES); // Get the amount of minutes between the start and the end.
                long timeOffsetMinutes = timeBetweenMinutes / (numberOfTimes-1); // Get the offset.

                for(byte i = 0; i < numberOfTimes; i++){
                    times.add(startTime.format(formatter));
                    startTime = startTime.plus(timeOffsetMinutes, ChronoUnit.MINUTES); // Add the offset to the start time.
                }
            }
            
            return times;
        }
        times.add(setTimeOfReminder.getHint().toString());


        return times;
    }

    public void buttonsForMoreThan1PerDay() {
        // Sets up the two options: manual and automatic for the choice of more than 1 notification per day.
        Log.i("buttonsForMoreThan1PerDay", "Creating buttons...");

        if (this.multipleOptionsForMoreThan1PerDay.getVisibility() == View.INVISIBLE) {

            stopLine4.setVisibility(View.INVISIBLE);


            this.setTimeOfReminder.setVisibility(View.INVISIBLE);
            this.multipleOptionsForMoreThan1PerDay.setVisibility(View.VISIBLE);
        }

        int checkedId = this.multipleOptionsForMoreThan1PerDay.getCheckedRadioButtonId();

        if (checkedId == this.optionAutomaticBetweenSetHours.getId()) {
            setupAutomaticallyBetweenButtonForMoreThan1PerDay();
        } else if (checkedId == optionSetEachAlarmManually.getId()) {
            setupManuallyButtonForMoreThan1PerDay();
        }
    }

    public void destroyButtonsForMoreThan1PerDay() {
        // Destroys the buttons for the choice; more than 1 notification per day.
        Log.i("destroyButtonsForMoreThan1PerDay", "Destroying...");
        // Pushing back to location --> if more than 1 reminder per day:
        if (this.lastWordDay != 'y') {

            multipleOptionsForMoreThan1PerDay.setVisibility(View.INVISIBLE);
            stopLine4.setVisibility(View.VISIBLE);
            stopLine5.setVisibility(View.INVISIBLE);
            stopLine6.setVisibility(View.INVISIBLE);

            fromThisTime.setVisibility(View.INVISIBLE);
            fromThisTimeTotoThisTime.setVisibility(View.INVISIBLE);
            toThisTime.setVisibility(View.INVISIBLE);

            setTimeOfReminder.setVisibility(View.VISIBLE);

            for (EditText manualSelectTime : manualSelectTimesDay) {
                if (manualSelectTime != null) {
                    manualSelectTime.setVisibility(View.INVISIBLE);
                }
            }
        } else if (this.manualSelectTimesDay[0] == null || this.isDueDateToggled) {
            Log.v("destroyButtonsForMoreThan1PerDay", "Second if...");

            // Setting everything that shouldn't be invisible.

            stopLine4.setVisibility(View.VISIBLE);
            setTimeOfReminder.setVisibility(View.VISIBLE);
            stopLine6.setVisibility(View.INVISIBLE);

            multipleOptionsForMoreThan1PerDay.setVisibility(View.INVISIBLE);
            fromThisTime.setVisibility(View.INVISIBLE);
            toThisTime.setVisibility(View.INVISIBLE);
            fromThisTimeTotoThisTime.setVisibility(View.INVISIBLE);

            for (EditText manualSelectTime : manualSelectTimesDay) {
                if (manualSelectTime != null) {
                    manualSelectTime.setVisibility(View.INVISIBLE);
                }
            }

            // End of setting invisibility.
        }
        // Showing the manually select buttons screen.
        else if (this.fromThisTime.getVisibility() == View.VISIBLE
                && this.multipleOptionsForMoreThan1PerDay.getCheckedRadioButtonId() != this.optionAutomaticBetweenSetHours.getId()) {
            Log.d("destroyButtonsForMoreThan1PerDay", "The first option is still visible");
            fromThisTime.setVisibility(View.INVISIBLE);
            toThisTime.setVisibility(View.INVISIBLE);
            fromThisTimeTotoThisTime.setVisibility(View.INVISIBLE);
            for (byte i = 0; i < this.numberOfTimes; i++) {
                if (manualSelectTimesDay[i] != null) {
                    manualSelectTimesDay[i].setVisibility(View.VISIBLE);
                }
            }
        }
        // Showing the automatic select buttons screen.
        else {
            Log.d("destroyButtonsForMoreThan1PerDay", "The second option is still visible.");
            for (EditText manualSelectTime : manualSelectTimesDay) {
                if (manualSelectTime != null) {
                    manualSelectTime.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void buttonsForMoreThan1PerWeek() {
        // Sets up the views for weekly interval notifications.
        Log.i("buttonsForMoreThan1PerWeek", "Creating buttons...");

        this.dayPickerForWeek.setVisibility(View.VISIBLE);

        String hintOfButton = setHowMuchToRemind.getHint().toString();

        this.dayPickerForWeek.setDaySelectionChangedListener(new MaterialDayPicker.DaySelectionChangedListener() {
            @Override
            public void onDaySelectionChanged(@NonNull List<MaterialDayPicker.Weekday> list) {
                numberOfTimes = (byte) list.size();
                setHowMuchToRemind.setHint(hintOfButton.replace(hintOfButton.charAt(0), String.valueOf(list.size()).charAt(0)));

                if (list.size() == 1) {
                    numberOfTimes = (byte) list.size();
                    for (MaterialDayPicker.Weekday day : weekDays) {
                        if (list.contains(day)) {
                            dayPickerForWeek.setDayEnabled(day, false);
                        }

                    }

                } else {
                    for (MaterialDayPicker.Weekday day : weekDays) {
                        if (list.contains(day)) {
                            dayPickerForWeek.setDayEnabled(day, true);
                        }
                    }
                }

            }
        });
    }

    public void destroyButtonsForMoreThan1PerWeek() {
        // Destroys the views of weekly interval option.
        Log.i("destroyButtonsForMoreThan1PerWeek", "Destroying...");

        this.dayPickerForWeek.setVisibility(View.INVISIBLE);
    }

    public void destroyAllButtonsForMoreThan1PerX() {
        // Destroys all of the views of all of the options.
        Log.i("destroyAllButtonsForMoreThan1PerX", "Destroying all...");
        try {
            destroyButtonsForMoreThan1PerDay();
        } catch (Exception e) {
            Log.e("destroyAllButtonsForMoreThan1PerX", "Couldn't destroy the buttons for more than 1 per day..." + "\n" + e);
        }

        try {
            destroyButtonsForMoreThan1PerWeek();
        } catch (Exception e) {
            Log.e("destroyAllButtonsForMoreThan1PerX", "Couldn't destroy the buttons for more than 1 per week..." + "\n" + e);
        }
    }

    private void onClickTimeDialog(View view) {
        // onClickListener for the time picker dialog.

        EditText editText = (EditText) view;

        Calendar calendar1 = Calendar.getInstance();

        int hour = calendar1.get(Calendar.HOUR);
        int minute = calendar1.get(Calendar.MINUTE);

        // Creating the grey background.
        View transparentGreyView = new View(context);
        transparentGreyView.setBackgroundColor(Color.parseColor("#80808080"));
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        transparentGreyView.setLayoutParams(layoutParams);
        transparentGreyView.setTag("greyBackground");
        popConstLayout.addView(transparentGreyView);


        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view12, int hourOfDay, int minute12) {
                        if (hourOfDay != 12) {
                            // Formatting.
                            String timeShort = hourOfDay + ":" + minute12 + " AM";
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                            try {
                                editText.setHint(sdf.format(Objects.requireNonNull(sdf.parse(timeShort))));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            // Formatting.
                            String timeShort = hourOfDay + ":" + minute12 + " PM";
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                            try {
                                editText.setHint(sdf.format(Objects.requireNonNull(sdf.parse(timeShort))));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // Removing the grey background on dismiss.
                View removeView = popConstLayout.findViewWithTag("greyBackground");

                if (removeView != null) {
                    popConstLayout.removeView(removeView);
                }
            }
        });
        timePickerDialog.show();

    }

    private EditText createTask(int width, int height, Drawable drawable){
        // Creates a new task.
        EditText editText1 = new EditText(context);
        editText1.setId(View.generateViewId());
        editText1.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
        editText1.setBackgroundColor(Color.TRANSPARENT);
        editText1.setTextColor(ProjectCreatePop.this.getResources().getColor(R.color.black));
        editText1.setHint("New Task");
        editText1.setHintTextColor(ProjectCreatePop.this.getResources().getColor(R.color.black));
        editText1.setInputType(InputType.TYPE_CLASS_TEXT);
        byte maxLength = 20;
        editText1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        if(drawable != null){
            editText1.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            editText1.setCompoundDrawablePadding(dpToPx(10));
        }
        editText1.setTag("left");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editText1.setTextCursorDrawable(null);
        }
        editText1.setHighlightColor(getResources().getColor(R.color.highlight_grey));

        Log.i("createTask", "Task created successfully");

        return editText1;
    }

    private void setTaskConstraints(View view){
        // Sets the constraints of the task.
        ConstraintSet constraintSet = new ConstraintSet();
        popConstLayout.addView(view);

        // If the task is on the right.
        if (newTaskIds.size() % 2 != 0) {
            view.setTag("right");
            EditText lastEditText = findViewById(newTaskIds.get(newTaskIds.size() - 1));

            constraintSet.clone(popConstLayout);
            constraintSet.connect(view.getId(), ConstraintSet.START, lastEditText.getId(), ConstraintSet.END, dpToPx(15));
            constraintSet.applyTo(popConstLayout);

            view.setY(lastEditText.getY());

            newTaskIds.add(view.getId());

        } else {
            // If the task is on the left.
            // Adding the new EditText buttons to the view.
            constraintSet.clone(popConstLayout);
            constraintSet.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(12));

            constraintSet.applyTo(popConstLayout);

            view.setY(createMoreTasks.getY());

            int goDown = ProjectCreatePop.this.pxToDp(145);
            createMoreTasks.setY(createMoreTasks.getY() + goDown);

            constLayoutGoDown1.setY(constLayoutGoDown1.getY() + goDown);

            newTaskIds.add(view.getId());
        }
    }

    private boolean onDragTrashCan(View view, DragEvent dragEvent){
        // The trash can onDragListener.
        final int action = dragEvent.getAction();

        int currId = ProjectCreatePop.this.currId;

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                view.setVisibility(View.VISIBLE);

            case DragEvent.ACTION_DRAG_ENTERED:
                view.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // If the task is physically on the trash can.
                view.setScaleX(2.10f);
                view.setScaleY(2.10f);

                trashCanAnimation.start();
                trashCan.setImageResource(R.drawable.animation_trash_can_stop);
                trashCanAnimation = (AnimationDrawable) trashCan.getDrawable();

                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                // If the task is not on the trash can.
                view.setScaleX(2);
                view.setScaleY(2);

                trashCanAnimation.stop();
                trashCan.setImageResource(R.drawable.animation_trash_can_start);
                trashCanAnimation = (AnimationDrawable) trashCan.getDrawable();

                view.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                // If the user dropped the task on the trash can.
                Log.d(TAG, "dropping...");
                trashCanAnimation.stop();
                trashCan.setImageResource(R.drawable.animation_monster);
                trashCanAnimation = (AnimationDrawable) trashCan.getDrawable();
                trashCanAnimation.start();


                if(newTaskIds.indexOf(currId) + 1 == newTaskIds.size() && findViewById(currId).getTag().equals("left")){
                    int goDown = ProjectCreatePop.this.pxToDp(145);
                    createMoreTasks.setY(createMoreTasks.getY() - goDown);

                    constLayoutGoDown1.setY(constLayoutGoDown1.getY() - goDown);

                    Log.i("trashCan", "The EditText doesn't have another EditText after");
                }

                for(int i = newTaskIds.indexOf(currId) + 1; i < newTaskIds.size(); i++){
                    moveEditTextAfterDeletion(i);
                }

                popConstLayout.removeView(popConstLayout.getViewById(currId));
                ProjectCreatePop.this.newTaskIds.remove((Integer) currId);

                // Delay for the animation.
                view.postDelayed(() -> {
                    trashCanAnimation.stop();
                    trashCan.setImageResource(R.drawable.animation_trash_can_start);
                    trashCanAnimation = (AnimationDrawable) trashCan.getDrawable();
                }, 1330);

                countNewTasks--;
                view.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                // When the drag ended.
                view.postDelayed(() -> {
                    view.setVisibility(View.INVISIBLE);
                }, 1300);

                view.setScaleX(2);
                view.setScaleY(2);
                view.invalidate();

                if(!dragEvent.getResult()) {
                    view.post(() -> findViewById(currId).setVisibility(View.VISIBLE));
                }
                return  false;

            default:
                return false;
        }
    }

    private void moveEditTextAfterDeletion(int index){
        // After the user deletes a task, move all of the tasks after it forward.
        ConstraintSet constraintSet = new ConstraintSet();

        EditText editText = findViewById(newTaskIds.get(index));
        if(editText.getTag().equals("left")){
            if(index + 1 == newTaskIds.size()){
                int goDown = ProjectCreatePop.this.pxToDp(145);
                Log.i("trashCan", createMoreTasks.getY() + " ");
                createMoreTasks.setY(createMoreTasks.getY() - goDown);
                Log.i("trashCan", createMoreTasks.getY() + " ");

                constLayoutGoDown1.setY(constLayoutGoDown1.getY() - goDown);

                Log.i("trashCan", "The EditText doesn't have another one after");
            }

            constraintSet.clone(popConstLayout);

            try {
                constraintSet.clear(newTaskIds.get(index + 1), ConstraintSet.START);
            } catch (Exception e){
                Log.e("trashCan", "Couldn't clear the constraint of index: " + (index+1) + "\n" + e.getMessage());
            }
            if(index - 1 == newTaskIds.indexOf(currId)){
                try {
                    constraintSet.connect(newTaskIds.get(index), ConstraintSet.START, newTaskIds.get(index - 2), ConstraintSet.END, ProjectCreatePop.this.pxToDp(50));
                } catch (Exception e){
                    Log.e("trashCan", "Couldn't set the constraint of index: " + (index+1) + "\n" + e.getMessage());
                    constraintSet.connect(newTaskIds.get(index), ConstraintSet.START, newTaskIds.get(index-1), ConstraintSet.END, ProjectCreatePop.this.pxToDp(50));
                }
            } else{
                constraintSet.connect(newTaskIds.get(index), ConstraintSet.START, newTaskIds.get(index-1), ConstraintSet.END, ProjectCreatePop.this.pxToDp(50));
            }


            constraintSet.applyTo(popConstLayout);

            editText.setTag("right");

            try {
                editText.setY(findViewById(newTaskIds.get(index - 1)).getY());
            } catch (Exception e){
                Log.e("trashCan", "Couldn't set the Y of index: " + (index-1) + "\n" + e.getMessage());
            }

            Log.d(TAG, "The index " + (index) + " was left");

        } else if(editText.getTag().equals("right")){
            constraintSet.clone(popConstLayout);

            constraintSet.connect(newTaskIds.get(index), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(16));
            constraintSet.clear(newTaskIds.get(index), ConstraintSet.END);

            constraintSet.applyTo(popConstLayout);

            editText.setTag("left");

            Log.d(TAG, "The index " + (index) + " was right");
        }
    }

    public boolean onLongClickTask(View view) {
        // The onLongClickListener for each task.
        // Starts the dragAndDrop event.
        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
        ProjectCreatePop.this.currId = view.getId();

        view.startDragAndDrop(null, dragShadowBuilder, null, 0);
        view.setVisibility(View.INVISIBLE);
        trashCan.setVisibility(View.VISIBLE);


        return true;
    }

    @Override
    protected void onDestroy() {
        // When the activity is destroyed.
        try {
            database.close();
        } catch (Exception e){
            Log.w("onDestroy", e.getMessage());
        }

        super.onDestroy();
    }

    @Override
    public void sendInputTimeBefore(String input) {
        // Receives input from SetTimeBefore dialog.
        this.setTimeBefore.setHint(input);
    }

    @Override
    public void sendInputTimesRemind(String input) {
        // Receives input from SetTimesRemind dialog.
        this.setHowMuchToRemind.setHint(input);

        String textOfHowMuchToRemind = Objects.requireNonNull(String.valueOf(this.setHowMuchToRemind.getHint()));
        if (textOfHowMuchToRemind.length() > 3) {
            if (lastWordDay != 0) {
                this.saveStrLastWordDay = this.lastWordDay;
            }
            this.lastWordDay = textOfHowMuchToRemind.charAt(textOfHowMuchToRemind.length() - 1);
            if (this.saveStrLastWordDay != 0 && this.saveStrLastWordDay != lastWordDay) {
                this.saveNumberOfTimes = 0;
            } else {
                this.saveNumberOfTimes = numberOfTimes;
            }
            this.numberOfTimes = Byte.parseByte(String.valueOf(textOfHowMuchToRemind.charAt(0)));
            // If the user wants more than 1 notification per day, then we are showing them the options of notifications
            if (!this.isDueDateToggled) {

                if ((this.numberOfTimes > 1)) {
                    if (this.lastWordDay == 'y') { // Day.
                        destroyButtonsForMoreThan1PerWeek();
                        buttonsForMoreThan1PerDay();
                    } else if (this.lastWordDay == 'k') { // Week.
                        dayPickerLogic();
                        buttonsForMoreThan1PerWeek();
                        destroyButtonsForMoreThan1PerDay();
                    } else if (this.lastWordDay == 'h') { // Month.
                        destroyButtonsForMoreThan1PerDay();
                        destroyButtonsForMoreThan1PerWeek();
                    }
                } else {
                    if (this.lastWordDay == 'h' || this.lastWordDay == 'y') { // Month or Day.
                        destroyAllButtonsForMoreThan1PerX();
                    } else if (this.lastWordDay == 'k') { // Week.
                        dayPickerForWeek.setVisibility(View.VISIBLE);
                        dayPickerForWeek.setSelectedDays(dayPickerForWeek.getSelectedDays().get(0));
                    }

                    setTimeOfReminder.setVisibility(View.VISIBLE);

                    multipleOptionsForMoreThan1PerDay.setVisibility(View.INVISIBLE);
                    fromThisTime.setVisibility(View.INVISIBLE);
                    toThisTime.setVisibility(View.INVISIBLE);
                    fromThisTimeTotoThisTime.setVisibility(View.INVISIBLE);

                    for (EditText manualSelectTime : manualSelectTimesDay) {
                        if (manualSelectTime != null) {
                            manualSelectTime.setVisibility(View.INVISIBLE);
                        }
                    }

                    // End of setting invisibility.

                    multipleOptionsForMoreThan1PerDay.setVisibility(View.INVISIBLE);
                    multipleOptionsForMoreThan1PerDay.setVisibility(View.INVISIBLE);
                    destroyButtonsForMoreThan1PerDay();

                }
            } else {
                destroyAllButtonsForMoreThan1PerX();
            }
        } else {
            destroyAllButtonsForMoreThan1PerX();
        }
    }

    private void dayPickerLogic(){
        // Makes the logic of the day picker.
        // If the last number of times (amount of notifications per week) is more than the current one.
        if (this.saveNumberOfTimes >= this.numberOfTimes) {
            List<MaterialDayPicker.Weekday> select = new ArrayList<>(7);
            for (byte i = 0; i < this.numberOfTimes; i++) {
                // Adds only the first X days of week to the array (because there is less selected days than last time, meaning we are removing some days).
                select.add(dayPickerForWeek.getSelectedDays().get(i));
            }
            // Setting the selected days as the new one, meaning we removed some days.
            dayPickerForWeek.setSelectedDays(select);
        } else {
            // If the last number of times (amount of notifications per week) is less than the current one.
            byte tempNumberOfTimes = this.numberOfTimes;
            List<MaterialDayPicker.Weekday> selected = dayPickerForWeek.getSelectedDays();
            for (MaterialDayPicker.Weekday day : weekDays) {
                if (!selected.contains(day)) {
                    // Adds days to the dayPickerForWeek. Meaning we select more days.
                    dayPickerForWeek.selectDay(day);
                }
                if (dayPickerForWeek.getSelectedDays().size() == tempNumberOfTimes) {
                    // If the selected days size is the same as the number of notifications per week.
                    break;
                }
            }
        }
    }
}

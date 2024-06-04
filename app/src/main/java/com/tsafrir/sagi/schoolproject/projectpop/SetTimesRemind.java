package com.tsafrir.sagi.schoolproject.projectpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.tsafrir.sagi.schoolproject.R;

import java.util.Objects;

public class SetTimesRemind extends DialogFragment {
    public interface OnInputListener{
        void sendInputTimesRemind(String input);
    }
    public OnInputListener mOnInputListener; // Sending between classes.

    private EditText etInputWhenRemind; // The place where the user can write how frequently they want to be reminded.
    private RadioButton choseDays; // Lets the user pick if they want to be reminded every day.
    private RadioButton choseWeeks; // Lets the user pick if they want to be reminded every week.
    private RadioButton choseMonths; // Lets the user pick if they want to be reminded every month.
    private RadioButton chosenTime; // The time chosen by the user.
    private final RadioButton[] radioButtons = new RadioButton[3]; // Saves all of the time buttons (chooseDays, chooseWeeks, chooseMonths).
    private final int defaultNotificationNumber; // Helper for knowing what is the default number of notifications per day/week/month
    private final int timeSpan; // 0 = Day, 1 = Week, 2 = Month.

    private final String[] timeSpans = {"day", "week", "month"}; // The possible time spans.

    // Structure Function: int level, String[] levels.
    public SetTimesRemind(int defaultItem, String[] items){
        if (Objects.equals(items[defaultItem], items[4])){
            this.defaultNotificationNumber = 1; // 1 time per day.
            this.timeSpan = 0;
        } else {
            this.defaultNotificationNumber = defaultItem + 1;
            this.timeSpan = 1;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_times_remind, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        // Setting up all of the buttons.
        etInputWhenRemind = view.findViewById(R.id.inputWhenRemind);

        RadioGroup chooseWhenRemind = view.findViewById(R.id.chooseWhenRemind); // A radio group of all of the buttons.
        TextView actionDone = view.findViewById(R.id.actionDone); // A finish button.

        choseDays = view.findViewById(R.id.chooseDays);
        choseWeeks = view.findViewById(R.id.chooseWeeks);
        choseMonths = view.findViewById(R.id.chooseMonths);

        // Setting the radio buttons in an array.
        radioButtons[0] = choseDays; radioButtons[1] = choseWeeks; radioButtons[2] = choseMonths;

        etInputWhenRemind.setText(String.valueOf(defaultNotificationNumber));
        chosenTime = radioButtons[timeSpan]; // Setting the chosen time as the default.
        chosenTime.setChecked(true); // Setting the default time span as checked.

        /* Changing the text of inputWhenRemind when unfocused.
           When inputWhenRemind is not focused without any text in it, it returns an error.
           When closing you set the text of inputWhenRemind to 0. */
        etInputWhenRemind.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                etInputWhenRemind.getWindowVisibleDisplayFrame(r);
                int screenHeight = etInputWhenRemind.getRootView().getHeight();


                boolean keyboardClosed = (screenHeight - r.bottom) > screenHeight * 0.15;

                if (keyboardClosed) {
                    etInputWhenRemind.setText("0");
                    Log.w("inputWhenRemind", "Not focused...");
                }
            }
        });

        // Setting a textChangedListener for when the user types a new number.
        etInputWhenRemind.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Changes the text if it is empty or if it more than the maximum for each category.
                String inputText = etInputWhenRemind.getText().toString();
                if (inputText.isEmpty()) {
                    if(!etInputWhenRemind.isFocused()){
                        etInputWhenRemind.setText("0");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                }
                else {
                    int i = Integer.parseInt(inputText);
                    // Maxing the possible number of times per day/week/month respectively.
                    if (choseDays.isChecked() && i > 6) {
                        etInputWhenRemind.setText("6");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                    else if (choseWeeks.isChecked() && i > 7) {
                        etInputWhenRemind.setText("7");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                    else if (choseMonths.isChecked() && i > 14) {
                        etInputWhenRemind.setText("14");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No need.
            }
        });

        // Setting a new onCheckedChangeListener on the RadioGroup chooseWhenRemind, to know when the user has chosen a new time span.
        chooseWhenRemind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                // Changes the text if it is empty or if it more than the maximum for each category.
                chosenTime = radioGroup.findViewById(id);

                String inputText = etInputWhenRemind.getText().toString();
                if (inputText.isEmpty()) {
                    etInputWhenRemind.setText("0");
                    etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                }
                else {
                    if (choseDays.isChecked() && Integer.parseInt(String.valueOf(etInputWhenRemind.getText())) > 6) {
                        etInputWhenRemind.setText("6");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                    else if (choseWeeks.isChecked() && Integer.parseInt(String.valueOf(etInputWhenRemind.getText())) > 7) {
                        etInputWhenRemind.setText("7");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                    else if (choseMonths.isChecked() && Integer.parseInt(String.valueOf(etInputWhenRemind.getText())) > 14) {
                        etInputWhenRemind.setText("14");
                        etInputWhenRemind.setSelection(etInputWhenRemind.getText().length());
                    }
                }
            }
        });

        // Sending back the data when actionDone is clicked.
        actionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputReturnTime;

                if (Integer.parseInt(etInputWhenRemind.getText().toString()) == 1){
                    inputReturnTime = etInputWhenRemind.getText().toString() + " time per " + chosenTime.getText();
                }
                else {
                    inputReturnTime = etInputWhenRemind.getText().toString() + " times per " + chosenTime.getText();
                }

                // Sends back the text via implementation.
                mOnInputListener.sendInputTimesRemind(inputReturnTime);
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }
        catch (ClassCastException e){
            Log.e("onAttach", "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    private DialogInterface.OnDismissListener customDismissListener;

    public void setOnCustomDismissListener(DialogInterface.OnDismissListener listener) {
        this.customDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);


        // Removes the gray background.
        ConstraintLayout constraintLayout = getActivity().findViewById(R.id.PopConstLayout);
        View removeView = constraintLayout.findViewWithTag("greyBackground");

        if (removeView != null) {
            constraintLayout.removeView(removeView);
        }
    }
}
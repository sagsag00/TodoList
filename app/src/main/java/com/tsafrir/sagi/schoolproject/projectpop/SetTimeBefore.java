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

public class SetTimeBefore extends DialogFragment {
    public interface OnInputListener{
        void sendInputTimeBefore(String input);
    }
    public OnInputListener mOnInputListener;

    private EditText inputXBefore;

    private RadioGroup chooseTimeBefore;
    private RadioButton chosenTime;

    private RadioButton choseMinute;
    private RadioButton choseHours;
    private RadioButton choseDays;
    private RadioButton rbChooseWeeks;
    private TextView actionDone;

    private int saveInt;

    public SetTimeBefore(int levelIndex){
        this.saveInt = levelIndex+1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_time_before, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        inputXBefore = (EditText) view.findViewById(R.id.inputXBefore);
        chooseTimeBefore = (RadioGroup) view.findViewById(R.id.chooseTimeBefore);
        actionDone = (TextView) view.findViewById(R.id.actionDone);
        chosenTime = (RadioButton) view.findViewById(R.id.chooseWeeks);

        choseMinute = (RadioButton) view.findViewById(R.id.chooseMinutes);
        choseHours = (RadioButton) view.findViewById(R.id.chooseHours);
        choseDays = (RadioButton) view.findViewById(R.id.chooseDays);
        rbChooseWeeks = (RadioButton) view.findViewById(R.id.chooseWeeks);

        inputXBefore.setText(String.valueOf(saveInt));

        inputXBefore.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                inputXBefore.getWindowVisibleDisplayFrame(r);
                int screenHeight = inputXBefore.getRootView().getHeight();


                boolean keyboardClosed = (screenHeight - r.bottom) > screenHeight * 0.15;

                if (keyboardClosed) {
                    inputXBefore.setText("0");
                    Log.w("inputXBefore", "Not focused...");
                }
            }
        });

        inputXBefore.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Changes the text if it is empty or if it more than the maximum for each category.
                String inputText = inputXBefore.getText().toString();
                if (inputText.isEmpty()) {
                    if(!inputXBefore.isFocused()){
                        inputXBefore.setText("0");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                }
                else {
                    int i = Integer.parseInt(inputText);
                    // Maxing the possible number of times per minute/hour/day/week respectively.
                    if (choseMinute.isChecked() && i > 600) {
                        inputXBefore.setText("600");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (choseHours.isChecked() && i > 120) {
                        inputXBefore.setText("120");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (choseDays.isChecked() && i > 35) {
                        inputXBefore.setText("35");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (rbChooseWeeks.isChecked() && i > 5) {
                        inputXBefore.setText("5");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is not relevant for my use case.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is not relevant for my use case.
            }
        });

        // Setting a new onCheckedChangeListener on the RadioGroup chooseTimeBefore, to know when the user has chosen a new time span.
        chooseTimeBefore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // Changes the text if it is empty or if it more than the maximum for each category.
                chosenTime.setText(String.valueOf(chosenTime.getText()).replace(" before", ""));

                int id = radioGroup.getCheckedRadioButtonId();
                chosenTime = (RadioButton) view.findViewById(id);

                String inputText = inputXBefore.getText().toString();
                if (inputText.isEmpty()) {
                    inputXBefore.setText("0");
                    inputXBefore.setSelection(inputXBefore.getText().length());
                }
                else {
                    int j = Integer.parseInt(inputText);
                    if (choseMinute.isChecked() && j > 600) {
                        inputXBefore.setText("600");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (choseHours.isChecked() && j > 120) {
                        inputXBefore.setText("120");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (choseDays.isChecked() && j > 35) {
                        inputXBefore.setText("35");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                    else if (rbChooseWeeks.isChecked() && j > 5) {
                        inputXBefore.setText("5");
                        inputXBefore.setSelection(inputXBefore.getText().length());
                    }
                }

                chosenTime.setText(chosenTime.getText() + " before");
            }
        });

        actionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputXBeforeNumber = inputXBefore.getText().toString() + " " + chosenTime.getText();

                // Sends back the text via implementation.
                mOnInputListener.sendInputTimeBefore(inputXBeforeNumber);
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
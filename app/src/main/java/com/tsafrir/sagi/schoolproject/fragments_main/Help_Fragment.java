package com.tsafrir.sagi.schoolproject.fragments_main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.tsafrir.sagi.schoolproject.R;

public class Help_Fragment extends Fragment {

    View helpFragmentView; // The main fragment view.
    View helpCircle; // The circle that helps the user.
    RelativeLayout helpDescription; // The help message container.
    TextView helpText; // The help message itself.
    TextView helpNext; // The button to go to the next help.
    TextView helpBack; // The button to go to the previous help.
    String[] descriptions; // The descriptions for each situation.
    String[] ids; // The corresponding ids for each description.
    ScrollView projectContainer; // The projects container
    ScrollView mainLayoutProjectPop; // The project pop page.
    int index = 0; // The index of the description. Default: 0.


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        helpFragmentView = inflater.inflate(R.layout.help_fragment, container, false);

        init();

        // The "next" button.
        helpNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index < descriptions.length - 1){
                    if(index == 1){
                        // To show a specific view on the description.
                        mainLayoutProjectPop.setVisibility(View.VISIBLE);
                    } else if (index == descriptions.length - 3) {
                        // Remove specific views on a location.
                        mainLayoutProjectPop.setVisibility(View.GONE);
                        helpCircle.setVisibility(View.GONE);
                    }

                    // Goes to the next description.
                    helpText.setText(descriptions[++index]);
                    int viewId = getResources().getIdentifier(ids[index], "id", getActivity().getPackageName());
                    View helpNeededView = helpFragmentView.findViewById(viewId);

                    if(helpNeededView == null){
                        return;
                    }

                    // Sets the red circle on the corresponding needed location.
                    int[] helpNeededViewLocation = new int[2];
                    helpNeededView.getLocationOnScreen(helpNeededViewLocation);
                    int helpNeededViewWidth = helpNeededView.getWidth();
                    int helpNeededViewHeight = helpNeededView.getHeight();

                    helpCircle.getLayoutParams().width = helpNeededViewWidth;
                    helpCircle.getLayoutParams().height = helpNeededViewHeight;

                    helpCircle.setX(helpNeededViewLocation[0] - 50);
                    helpCircle.setY(helpNeededViewLocation[1] - 145);
                }
            }
        });

        // The "back" button.
        helpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the back button is pressed.
                if(index > 0){
                    // Because we are going back it should be > 0.
                    if(index == descriptions.length - 3){
                        // We are starting from the end. When the index is length - 3, the pop screen should be visible.
                        mainLayoutProjectPop.setVisibility(View.VISIBLE);
                        helpCircle.setVisibility(View.VISIBLE);
                    } else if (index == 2) {
                        // Here the pop screen should be gone.
                        mainLayoutProjectPop.setVisibility(View.GONE);
                    }
                    // Changing the description.
                    helpText.setText(descriptions[--index]);
                    if(ids[index].equals("parent")){
                        helpCircle.setX(10);
                        helpCircle.setY(0);
                        helpCircle.getLayoutParams().width = dpToPx(45);
                        helpCircle.getLayoutParams().height = dpToPx(45);
                        return;
                    }
                    // Finding the id with its' name.
                    int viewId = getResources().getIdentifier(ids[index], "id", getActivity().getPackageName());
                    View helpNeededView = helpFragmentView.findViewById(viewId);

                    if(helpNeededView == null){
                        return;
                    }

                    // Setting the location and width to match the helpNeededView.
                    int[] helpNeededViewLocation = new int[2];
                    helpNeededView.getLocationOnScreen(helpNeededViewLocation);
                    int helpNeededViewWidth = helpNeededView.getWidth();
                    int helpNeededViewHeight = helpNeededView.getHeight();

                    helpCircle.getLayoutParams().width = helpNeededViewWidth;
                    helpCircle.getLayoutParams().height = helpNeededViewHeight;

                    helpCircle.setX(helpNeededViewLocation[0] - 50);
                    helpCircle.setY(helpNeededViewLocation[1] - 145);

                }
            }
        });

        return helpFragmentView;
    }


    public int dpToPx(int dp) {
        // Convert dp to px.
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
    private void init(){
        // Initialization.
        helpCircle = helpFragmentView.findViewById(R.id.helpCircle);
        helpDescription = helpFragmentView.findViewById(R.id.helpDescription);
        helpText = helpFragmentView.findViewById(R.id.helpText);
        helpNext = helpFragmentView.findViewById(R.id.helpNext);
        helpBack = helpFragmentView.findViewById(R.id.helpBack);
        descriptions = getResources().getStringArray(R.array.descriptions);
        ids = getResources().getStringArray(R.array.descriptions_corresponding_ids);
        projectContainer = helpFragmentView.findViewById(R.id.projectContainer);
        mainLayoutProjectPop = helpFragmentView.findViewById(R.id.mainLayoutProjectPop);

    }
}

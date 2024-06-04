package com.tsafrir.sagi.schoolproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.tsafrir.sagi.schoolproject.fragments_main.Home_Fragment;
import com.tsafrir.sagi.schoolproject.fragments_main.Upcoming_Fragment;
import com.tsafrir.sagi.schoolproject.fragments_main.Help_Fragment;

public class MainActivity extends AppCompatActivity {

    String[] menuTitles; // Titles of all of the menu items.
    TypedArray menuIcons; // Icons of all the menu items.

    private CharSequence drawerTitle; // Gets the title of the current window. Used to preserve the title.
    private  CharSequence title; // Gets the title of the current window. Used to dynamically change the title.
    private DrawerLayout drawerLayout; // The main layout. Used to implement the navigation drawer functionality.
    private ListView sliderList; // The navigation drawer.
    private ActionBarDrawerToggle drawerToggle; // Connects the navigation drawer to the action bar.

    private List<SideListItem> rowItems; // A list that holds all of the slideListItem items for the navigation drawer.
    private CustomAdapter adapter; // Adds each of the rowItems items to the view (sliderList).
    private FrameLayout frameLayout;

    private Toolbar toolbar; // Replaces the default action bar.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            new Handler().postDelayed(() -> {
                this.recreate();
            }, 100);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check and request POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        rowItems = new ArrayList<SideListItem>();

        // Creates the items for the side list.
        for (int i = 0; i < menuTitles.length && i < menuIcons.length(); i++) {
            int resourceId = menuIcons.getResourceId(i, -1);

            if (resourceId != -1) {
                SideListItem items = new SideListItem(menuTitles[i], resourceId);
                rowItems.add(items);
            }
        }

        menuIcons.recycle();

        adapter = new CustomAdapter(this, rowItems);

        sliderList.setAdapter(adapter);
        sliderList.setOnItemClickListener(new SlideItemListener());

        toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close) {
            public void onDrawerClosed(View view){
                Objects.requireNonNull(getSupportActionBar()).setTitle(title); // Used to dynamically change the title.
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView){
                Objects.requireNonNull(getSupportActionBar()).setTitle(drawerTitle); // Used to preserve the original title.
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        if(savedInstanceState == null) {
            // On start the savedInstanceState will be null.
            // Set the fragment in that case to the first fragment. (Home_Fragment).
            updateDisplay(0);
        }
    }

    private void init(){
        sliderList = (ListView) findViewById(R.id.sliderList);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        title = drawerTitle = getTitle();
        menuTitles = getResources().getStringArray(R.array.titles);
        menuIcons = getResources().obtainTypedArray(R.array.icons);

    }

    class SlideItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int clicked, long id){
            // When an item is clicked, change the fragment to that item.
            updateDisplay(clicked);
        }
    }

    private void updateDisplay(int clicked){
        // Changes the current fragment to the clicked one.
        Fragment fragment = null;
        switch(clicked){
            case 0:
                fragment = new Home_Fragment();
                break;
            case 1:
                fragment = new Upcoming_Fragment();
                break;
            case 2:
                fragment = new Help_Fragment();
                break;
            default:
                break;
        }

        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, "homeFragment").commit();

            Intent intent = getIntent();
            if(fragment instanceof Home_Fragment && intent != null) {
                // If you clicked on the notification, it will be 1.
                if (intent.getIntExtra("notificationMainActivity", -1) == 1) {
                    // Creates a background for the ProjectManagePop.
                    ((Home_Fragment) fragment).createBackgroundView(this);
                    intent.putExtra("notificationMainActivity", -1);
                    // If you clicked on the notification and finished managing the project, it will be 2.
                } else if (intent.getIntExtra("managePopDone", -1) == 2){
                    // Removes the background for the ProjectManagePop.
                    // Called when you finish editing the project.
                    ((Home_Fragment) fragment).removeBackgroundView();
                }
            }

            drawerLayout.closeDrawer(sliderList);

            Log.i("updateDisplay", "Fragment created successfully");
        }
        else{
            Log.e("updateDisplay", "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

}
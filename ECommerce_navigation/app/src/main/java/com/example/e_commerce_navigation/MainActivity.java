package com.example.e_commerce_navigation;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * This class is the first class the user will access
 *
 * @author Seontaek Oh
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Initialize preferences variable
     * Make menu items invisible related to a singed user and a supplier
     * Show default fragment
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("MyCustomSharedPreferences", 0);
        preferences.edit().clear().commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu nav_Menu = navigationView.getMenu();

        //Make menu items invisible related to a singed user and a supplier
        nav_Menu.findItem(R.id.logoutMenu).setVisible(false);
        nav_Menu.findItem(R.id.addProductMenu).setVisible(false);

        //Show default fragment
        displaySelectedScreen(R.id.searchProductMenu);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Calling the method displayselectedscreen and passing the id of selected menu
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    /**
     * Move according to the selected menu item.
     *
     * @param itemId
     */
    public void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.searchProductMenu:
                fragment = new SearchProductMenu();
                break;
            case R.id.addProductMenu:
                fragment = new AddProductMenu();
                break;
            case R.id.loginMenu:
                fragment = new LoginMenu();
                break;
            case R.id.logoutMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        //make preferences variable initialized.
                        SharedPreferences preferences = getSharedPreferences("MyCustomSharedPreferences", 0);
                        preferences.edit().clear().commit();

                        //make menu items initialized
                        initializeMenu();
                        Toast.makeText(getApplicationContext(), "You successfully logged out", Toast.LENGTH_LONG).show();
                        // Do nothing, but close the dialog
                        dialog.dismiss();

                        //Move to SearchProductMenu()
                        Fragment fragment = new SearchProductMenu();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                break;
            case R.id.registerMenu:
                fragment = new RegisterMenu();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * make menu items adjusted according to user type
     *
     * @param userType
     */
    public void adjustMenu(String userType) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.loginMenu).setVisible(false);
        nav_Menu.findItem(R.id.logoutMenu).setVisible(true);

        if (userType.equals("S")) {
            nav_Menu.findItem(R.id.addProductMenu).setVisible(true);
        }
    }

    /**
     * make menu items initialized
     */
    public void initializeMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.loginMenu).setVisible(true);
        nav_Menu.findItem(R.id.logoutMenu).setVisible(false);
        nav_Menu.findItem(R.id.addProductMenu).setVisible(false);
    }

}

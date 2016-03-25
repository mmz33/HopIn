package aub.hopin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.*;
import android.widget.AdapterView;
import android.app.Fragment;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class SwipeLeft extends AppCompatActivity {

    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listSliding = new ArrayList<>();

        //Add item for sliding list
        //String firstName = UserSession.getActiveSession().getUserInfo().firstName.toString();
        //String lastName = UserSession.getActiveSession().getUserInfo().lastName.toString();
        //String userName = firstName + " " + lastName;
        //listSliding.add(new ItemSlideMenu(0, userName));
        listSliding.add(new ItemSlideMenu(R.drawable.ic_action_settings, "Settings"));
        adapter = new SlidingMenuAdapter(this, listSliding);

        listViewSliding.setAdapter(adapter);

        //Display icon to open/ close sliding list
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle(listSliding.get(0).getTitle());
        listViewSliding.setItemChecked(0, true);
        drawerLayout.closeDrawer(listViewSliding);

        //Display Map Fragment
        replaceFragment(1);

        listViewSliding.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setTitle(listSliding.get(position).getTitle());
                        listViewSliding.setItemChecked(position, true);
                        replaceFragment(position);
                        drawerLayout.closeDrawer(listViewSliding);
                    }
                }
        );

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    public void replaceFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new SettingsFragment();
                break;
        }
        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.map, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}

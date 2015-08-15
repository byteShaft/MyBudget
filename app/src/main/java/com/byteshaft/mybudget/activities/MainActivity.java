package com.byteshaft.mybudget.activities;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.byteshaft.mybudget.Fragments.ContactFragment;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.ui.BudgetDialogFragment;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Fragments.GoalsFragment;

public class MainActivity extends AppCompatActivity implements BudgetDialogFragment.BudgetDialogListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private Fragment mFragment;
    private String[] mListTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, new HomeFragment());
        tx.commit();
        setContentView(R.layout.mainactivity);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = getActionBarDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.list_drawer);
        mListTitles = new String[] {
                getString(R.string.title_section1),
                getString(R.string.title_section2),
                getString(R.string.title_section3),
        };
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private ActionBarDrawerToggle getActionBarDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

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
    }

    void newFragment(int position) {
        switch (position) {
            case 0:
                mFragment = new HomeFragment();
                break;
            case 1:
                mFragment = new GoalsFragment();
                break;
            case 2:
                mFragment = new ContactFragment();
                break;
            default:
                return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        fragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        setTitle(mListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            newFragment(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}

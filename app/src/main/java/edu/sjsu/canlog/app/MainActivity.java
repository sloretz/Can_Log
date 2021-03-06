package edu.sjsu.canlog.app;

import java.util.Arrays;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import android.widget.Toast;

import edu.sjsu.canlog.app.frontend.*;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static int REQUEST_ENABLE_BT = 3;
    public static String HAVE_REQUESTED_BT = "HaveRequestedBluetooth";
    private boolean haveRequestedBluetooth = false;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putBoolean(HAVE_REQUESTED_BT, haveRequestedBluetooth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //restore state
        if (savedInstanceState != null) {
            haveRequestedBluetooth = savedInstanceState.getBoolean(HAVE_REQUESTED_BT);
        }

        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private int currentPosition = -1;
            @Override
            public void onPageSelected(int position) {
                //Hide the old
                if (currentPosition >= 0) {
                    Fragment invisibleFragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, currentPosition);
                    if (invisibleFragment instanceof HandleVisibilityChange) {
                        ((HandleVisibilityChange) invisibleFragment).onBecomesInvisible();
                    }
                }
                //Bring in the new
                actionBar.setSelectedNavigationItem(position);
                Fragment visibleFragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, position);
                if (visibleFragment instanceof HandleVisibilityChange)
                {
                    ((HandleVisibilityChange) visibleFragment).onBecomesVisible();
                }
                currentPosition = position;
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if ( bluetoothAdapter == null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {

            //Request bluetooth, but only do it once
            if (!haveRequestedBluetooth && !bluetoothAdapter.isEnabled())
            {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
                haveRequestedBluetooth = true;
            }

            //Assume the user has enabled bluetooth

        }
    }

    @Override
    public void onBackPressed()
    {
        int selectedItem = mViewPager.getCurrentItem();
        //Weird call, this is how we get the actual fragment instance from the
        //view pager's internal cache
        Fragment visibleFragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, selectedItem);
        if (visibleFragment instanceof HandleBack)
        {
            ((HandleBack) visibleFragment).onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth Enabled", Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth could not be enabled", Toast.LENGTH_LONG);
                toast.show();
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) //DTC page
            {
                DTCPage fragment = new DTCPage();
                return fragment;
            }
            else if (position == 1)
            {
                /*
                //Live data
                ArrayList<Number> xVals = new ArrayList<Number>();
                xVals.add(1);
                xVals.add(2);
                xVals.add(3);
                xVals.add(4);
                ArrayList<Number> yVals = new ArrayList<Number>();
                yVals.add(4);
                yVals.add(3);
                yVals.add(1);
                yVals.add(4);
                StaticXYPlotFragment fragment = StaticXYPlotFragment.newInstance("Garbage",xVals,yVals);
                */
                LiveDataPage fragment = new LiveDataPage();
                return fragment;
            }
            else
            {
                AboutCarPage fragment = new AboutCarPage();
                return fragment;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}

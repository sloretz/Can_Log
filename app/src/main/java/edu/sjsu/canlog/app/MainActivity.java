package edu.sjsu.canlog.app;

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
import android.widget.Toast;
import edu.sjsu.canlog.app.frontend.*;
import edu.sjsu.canlog.app.backend.Backend;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static int REQUEST_ENABLE_BT = 3;
    public static String HAVE_REQUESTED_BT = "HaveRequestedBluetooth";
    public static String DO_HAVE_BT_CONN = "doHaveBTConnection";
    public static String CURRENT_PAGE_DISPLAYED = "currentPageDisplayed";
    protected int currentPage = 0;
    private boolean haveRequestedBluetooth = false;
    private boolean haveBTConn = false;
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
        state.putBoolean(DO_HAVE_BT_CONN, haveBTConn);
    }

    @Override
    public void onStop(){
        super.onStop();
        Backend backend = Backend.getInstance();
        backend.stop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onResume();
        Fragment visibleFragment = getVisiblePage();
        if (visibleFragment instanceof HandleVisibilityChange)
        {
            if(hasFocus){
                ((HandleVisibilityChange) visibleFragment).onBecomesVisible();
            }
            else {
                ((HandleVisibilityChange) visibleFragment).onBecomesInvisible();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //restore state
        if (savedInstanceState != null) {
            haveRequestedBluetooth = savedInstanceState.getBoolean(HAVE_REQUESTED_BT);
            haveBTConn = savedInstanceState.getBoolean(DO_HAVE_BT_CONN);
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
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i("visible", "onPageSelected " + Integer.toString(position));
                //Hide the old
                if (currentPage >= 0) {
                    Fragment invisibleFragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, currentPage);
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
                currentPage = position;
            }
        };
        mViewPager.setOnPageChangeListener( pageChangeListener );

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
            Toast toast = Toast.makeText(MainActivity.this, "Bluetooth not supported", Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }
            //Request bluetooth, but only do it once
            if (!haveBTConn)//!haveRequestedBluetooth)
            {
                //Initialize backend first
                Backend backend = new Backend(getApplicationContext());
                backend.start();
                backend.setBoardTime(null);

                final ConnectingDialog blockingDialog = new ConnectingDialog();
                blockingDialog.setCancelable(false);
                blockingDialog.show(getSupportFragmentManager(), "ConnectDeviceDialog");

                backend.waitForConnection(new Backend.ResultHandler() {
                    public void gotResult(Bundle result) {
                        Log.d("PairDeviceDialog", "We must be connected, dismissing dialog");
                        haveBTConn = true;
                        blockingDialog.dismiss();
                    }
                });

                //Create dialog that will block until a device is paired
                final PairDeviceDialog pairDeviceDialog = new PairDeviceDialog();
                pairDeviceDialog.setCancelable(false);
                pairDeviceDialog.show(getSupportFragmentManager(), "PairDeviceDialog");
            }

        }
    }

    private Fragment getVisiblePage()
    {
        int selectedItem = mViewPager.getCurrentItem();
        //Weird call, this is how we get the actual fragment instance from the
        //view pager's internal cache
        return (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, selectedItem);
    }


    @Override
    public void onBackPressed()
    {
        Fragment visibleFragment = getVisiblePage();
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
                Toast toast = Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(MainActivity.this, "Bluetooth could not be enabled", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment visibleFragment = getVisiblePage();
        if (visibleFragment instanceof LoggedDataPage)
        {
            if (id == R.id.start_date)
                ((LoggedDataPage)visibleFragment).promptForStartDate();
            else if (id == R.id.end_date)
                ((LoggedDataPage)visibleFragment).promptForEndDate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        //if page is the Logged data page, make a menu with
        //startDate and endDate
        Fragment visibleFragment = getVisiblePage();
        if (visibleFragment instanceof LoggedDataPage)
        {
            getMenuInflater().inflate(R.menu.history, menu);
        }

        return super.onPrepareOptionsMenu(menu);
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
            //about
            //live data
            //dtc
            //history
            if (position == 0) //DTC page
            {
                //AboutCarPage fragment = new AboutCarPage();
                return new AboutCarPage();
            }
            else if (position == 1)
            {
                //LiveDataPage fragment = new LiveDataPage();
                return new LiveDataPage();
            }
            else if (position == 2)
            {
                //DTCPage fragment = new DTCPage();
                return new DTCPage();
            }
            else
            {
                //LoggedDataPage fragment = new LoggedDataPage();
                return new LoggedDataPage();
            }


        }

        @Override
        public int getCount() {
            return 4;
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
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }
}

package com.example.cool1024.android_example;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String SAVE_DATA_TAG = "ACTIVE_FRAGMENT";

    private Bundle mSavedInstanceState;

    private FragmentManager mFragmentManager;

    private BottomNavigationView mNavigationView;

    private Fragment mActiveFragment;

    private String mActiveFragmentTag;

    private FragmentTransaction mFragmentTransaction;

    private HomeFragment mHomeFragment;

    private DashboardFragment mDashboardFragment;

    private CenterFragment mCenterFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);
        findViewComponent();
        if(savedInstanceState !=null){
            loadSaveFragment(savedInstanceState);
        }
        initViewEvent();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_DATA_TAG,mActiveFragmentTag);
    }

    /**
     * Find all ui components from view
     */
    private void findViewComponent(){
        mFragmentManager = (FragmentManager) getSupportFragmentManager();
        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    /**
     * Init view event
     */
    private void initViewEvent(){
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment oldFragment = mActiveFragment;
            mActiveFragmentTag = HomeFragment.TAG;
            getSupportActionBar().show();
            switch (item.getItemId()){
                case R.id.navigation_home:
                    if(mHomeFragment == null){
                        mHomeFragment = new HomeFragment();
                    }
                    mActiveFragment = mHomeFragment;
                    mActiveFragmentTag = HomeFragment.TAG;
                    getSupportActionBar().setTitle(R.string.title_home);
                    Log.d(TAG,"HOME_FRAGMENT_SELECTED");
                    break;
                case R.id.navigation_dashboard:
                    if(mDashboardFragment == null){
                        mDashboardFragment = new DashboardFragment();
                    }
                    mActiveFragment = mDashboardFragment;
                    mActiveFragmentTag = DashboardFragment.TAG;
                    getSupportActionBar().setTitle(R.string.title_dashboard);
                    Log.d(TAG,"DASHBOARD_FRAGMENT_SELECTED");
                    break;
                case R.id.navigation_center:
                    if(mCenterFragment == null){
                        mCenterFragment = new CenterFragment();
                    }
                    Log.d(TAG,"CENTER_FRAGMENT_SELECTED");
                    mActiveFragmentTag = CenterFragment.TAG;
                    mActiveFragment = mCenterFragment;
                    getSupportActionBar().hide();
                    break;
            }
            mFragmentTransaction = mFragmentManager.beginTransaction();
            if(oldFragment != null){
                mFragmentTransaction.hide(oldFragment);
            }
            if(!mActiveFragment.isAdded()){
                mFragmentTransaction.add(R.id.frame_layout,mActiveFragment,mActiveFragmentTag);
            }else{
                mFragmentTransaction.show(mActiveFragment);
            }
            mFragmentTransaction.commit();
            return true;
            }
        });
        if(mSavedInstanceState == null){
            mHomeFragment = new HomeFragment();
            mActiveFragment = mHomeFragment;
            mActiveFragmentTag = HomeFragment.TAG;
            getSupportActionBar().setTitle(R.string.title_home);
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.frame_layout,mActiveFragment,HomeFragment.TAG).commit();
        }
    }

    /**
     * Recover fragment from save handle
     * @param  savedInstanceState
     */
    private void loadSaveFragment(Bundle savedInstanceState){
        mActiveFragmentTag = savedInstanceState.getString(SAVE_DATA_TAG);
        mActiveFragment = mFragmentManager.findFragmentByTag(mActiveFragmentTag);
        Fragment home = mFragmentManager.findFragmentByTag(HomeFragment.TAG);
        Fragment dashboard = mFragmentManager.findFragmentByTag(DashboardFragment.TAG);
        Fragment center = mFragmentManager.findFragmentByTag(CenterFragment.TAG);
        if(mActiveFragmentTag.equals(HomeFragment.TAG)){
            getSupportActionBar().setTitle(R.string.title_home);
        }else if(mActiveFragmentTag.equals(DashboardFragment.TAG)){
            getSupportActionBar().setTitle(R.string.title_dashboard);
        }else if(mActiveFragmentTag.equals(CenterFragment.TAG)){
            getSupportActionBar().hide();
        }
        if(home != null){
            mHomeFragment = (HomeFragment)home;
        }
        if(dashboard != null){
            mDashboardFragment = (DashboardFragment)dashboard;
        }
        if(center != null){
            mCenterFragment = (CenterFragment)center;
        }
    }
}

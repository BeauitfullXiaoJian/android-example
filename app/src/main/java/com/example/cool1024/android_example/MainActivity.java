package com.example.cool1024.android_example;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.cool1024.android_example.fragments.CenterFragment;
import com.example.cool1024.android_example.fragments.DashboardFragment;
import com.example.cool1024.android_example.fragments.HomeFragment;
import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;

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

    private Toolbar mToolBar;

    private AppBarLayout mAppBarLayout;

    private Menu mMenu;

    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);
        findViewComponent();
        if(savedInstanceState != null){
            loadSaveFragment(savedInstanceState);
        }
        initViewEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean result = super.onCreateOptionsMenu(menu);
        mMenu = menu;
        if(mActiveFragmentTag.equals(CenterFragment.TAG)){
            setCenterMode();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.center_menu_item_settings){
            startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
        }
        return super.onOptionsItemSelected(item);
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
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        mFragmentManager = (FragmentManager) getSupportFragmentManager();
        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_bar_layout);
        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);
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
            setDefaultMode();
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
                    mActiveFragmentTag = CenterFragment.TAG;
                    mActiveFragment = mCenterFragment;
                    getSupportActionBar().setTitle(R.string.title_center);
                    setCenterMode();
                    Log.d(TAG,"CENTER_FRAGMENT_SELECTED");
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
     * @param  savedInstanceState 保存的数据
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
            getSupportActionBar().setTitle(R.string.title_center);
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

    /**
     * 显示设为个人中心模式
     */
    private void setCenterMode(){
        mToolBar.getBackground().setAlpha(20);
        ViewGroup.MarginLayoutParams params =  (ViewGroup.MarginLayoutParams)mFrameLayout.getLayoutParams();
        params.topMargin = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           mAppBarLayout.setElevation(0);
        }
        getMenuInflater().inflate(R.menu.center,mMenu);
    }

    /**
     * 还原默认模式
     */
    private void setDefaultMode(){
        mToolBar.getBackground().setAlpha(255);
        ViewGroup.MarginLayoutParams params =  (ViewGroup.MarginLayoutParams)mFrameLayout.getLayoutParams();
        params.topMargin = mToolBar.getHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBarLayout.setElevation(12);
        }
        mMenu.clear();
    }

    /**
     * 初始化海康SDK
     */
    private void initSDK(){

        MCRSDK.init();
        // 初始视频流客户端
        RtspClient.initLib();
        MCRSDK.setPrint(1,null);
        // 初始化语音对讲
        TalkClientSDK.initLib();
        // SDK初始化
        VMSNetSDK.init(getApplication());
        VMSNetSDK.getInstance().Login("http://192.168.1.107","admin","anasit123456789+","");
    }
}

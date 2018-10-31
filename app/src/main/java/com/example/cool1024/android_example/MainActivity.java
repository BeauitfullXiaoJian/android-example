package com.example.cool1024.android_example;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cool1024.android_example.fragments.BaseTabFragment;
import com.example.cool1024.android_example.fragments.CenterFragment;
import com.example.cool1024.android_example.fragments.DashboardFragment;
import com.example.cool1024.android_example.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String SAVE_DATA_TAG = "ACTIVE_FRAGMENT";

    private Bundle mSavedInstanceState;

    private FragmentManager mFragmentManager;

    private BottomNavigationView mNavigationView;

    private FragmentTransaction mFragmentTransaction;

    private BaseTabFragment mActiveFragment;

    private BaseTabFragment mHomeFragment;

    private BaseTabFragment mDashboardFragment;

    private BaseTabFragment mCenterFragment;

    private long mExitClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewComponent();
        initViewEvent();
        recoverState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.SAVE_DATA_TAG,
                mActiveFragment == null ? BaseTabFragment.EMPTY_TAG
                        : mActiveFragment.getFragmentTag());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment oldFragment = mActiveFragment;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                }
                mActiveFragment = mHomeFragment;
                Log.d(TAG, "HOME_FRAGMENT_SELECTED");
                break;
            case R.id.navigation_dashboard:
                if (mDashboardFragment == null) {
                    mDashboardFragment = new DashboardFragment();
                }
                mActiveFragment = mDashboardFragment;
                Log.d(TAG, "DASHBOARD_FRAGMENT_SELECTED");
                break;
            case R.id.navigation_center:
                if (mCenterFragment == null) {
                    mCenterFragment = new CenterFragment();
                }
                mActiveFragment = mCenterFragment;
                Log.d(TAG, "CENTER_FRAGMENT_SELECTED");
                break;
        }
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (oldFragment != null) {
            mFragmentTransaction.hide(oldFragment);
        }
        if (!mActiveFragment.isAdded()) {
            mFragmentTransaction.add(R.id.frame_layout, mActiveFragment
                    , mActiveFragment.getFragmentTag());
        } else {
            mFragmentTransaction.show(mActiveFragment);
        }
        mFragmentTransaction.commit();
        return true;
    }

    /**
     * 按2次退出应用
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitClickTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT)
                    .show();
            mExitClickTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 获取到所有相关的视图组件
     */
    private void findViewComponent() {
        mFragmentManager = getSupportFragmentManager();
        mNavigationView = findViewById(R.id.navigation);
    }

    /**
     * 初始化相关视图组件事件
     */
    private void initViewEvent() {
        mNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
    }

    /**
     * 从之前保存的数据中尝试恢复页面状态
     */
    private void recoverState() {
        if (mSavedInstanceState == null) {
            mHomeFragment = new HomeFragment();
            mActiveFragment = mHomeFragment;
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.frame_layout, mActiveFragment, HomeFragment.TAG).commit();
        } else {
            String activeFragmentTag = mSavedInstanceState.getString(SAVE_DATA_TAG,
                    BaseTabFragment.EMPTY_TAG);
            mActiveFragment = (BaseTabFragment) mFragmentManager.findFragmentByTag(activeFragmentTag);
            mHomeFragment = (BaseTabFragment) mFragmentManager.findFragmentByTag(HomeFragment.TAG);
            mDashboardFragment = (BaseTabFragment) mFragmentManager.findFragmentByTag(DashboardFragment.TAG);
            mCenterFragment = (BaseTabFragment) mFragmentManager.findFragmentByTag(CenterFragment.TAG);
        }
    }
}

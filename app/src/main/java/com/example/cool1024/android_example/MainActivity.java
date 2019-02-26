package com.example.cool1024.android_example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cool1024.android_example.fragments.AnimationFragment;
import com.example.cool1024.android_example.fragments.BannerFragment;
import com.example.cool1024.android_example.fragments.BaseFragment;
import com.example.cool1024.android_example.fragments.CenterFragment;
import com.example.cool1024.android_example.fragments.HomeFragment;
import com.example.cool1024.android_example.fragments.WebViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String SAVE_DATA_TAG = "ACTIVE_FRAGMENT";

    private Bundle mSavedInstanceState;

    private FragmentManager mFragmentManager;
    private BottomNavigationView mNavigationView;
    private NavigationView mNavigationSideView;
    private DrawerLayout mDrawer;

    private FragmentTransaction mFragmentTransaction;

    private BaseFragment mActiveFragment;
    private BaseFragment mHomeFragment;
    private BaseFragment mDashboardFragment;
    private BaseFragment mCenterFragment;

    private long mExitClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewComponents();
        initViewEvent();
        recoverState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem item = mNavigationSideView.getMenu().findItem(R.id.menu_home);
        item.setCheckable(true);
        item.setChecked(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.SAVE_DATA_TAG,
                mActiveFragment == null ? BaseFragment.EMPTY_TAG
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
                    mDashboardFragment = new WebViewFragment();
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
            Log.d(TAG, "之前的不为空");
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
    private void findViewComponents() {
        mFragmentManager = getSupportFragmentManager();
        mNavigationView = findViewById(R.id.navigation);
        mNavigationSideView = findViewById(R.id.navigation_side);
        mNavigationSideView.setItemIconTintList(null);
        mDrawer = findViewById(R.id.drawer);
    }

    /**
     * 初始化相关视图组件事件
     */
    private void initViewEvent() {
        mNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
        mNavigationSideView.setNavigationItemSelectedListener((item) -> {
            item.setCheckable(true);
            item.setChecked(true);
            mDrawer.closeDrawers();
            switch (item.getItemId()) {
                case R.id.menu_banner: {
                    Intent intent = new Intent(MainActivity.this, DevActivity.class);
                    intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, BannerFragment.TAG);
                    startActivity(intent);
                    break;
                }
            }
            return true;
        });
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
                    BaseFragment.EMPTY_TAG);
            Log.d(TAG, "TAG:" + activeFragmentTag);
            mActiveFragment = (BaseFragment) mFragmentManager.findFragmentByTag(activeFragmentTag);
            mHomeFragment = (BaseFragment) mFragmentManager.findFragmentByTag(HomeFragment.TAG);
            mDashboardFragment = (BaseFragment) mFragmentManager.findFragmentByTag(WebViewFragment.TAG);
            mCenterFragment = (BaseFragment) mFragmentManager.findFragmentByTag(CenterFragment.TAG);
        }
    }
}

package com.example.cool1024.android_example;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cool1024.android_example.classes.FlvDetail;
import com.example.cool1024.android_example.fragments.AnimationFragment;
import com.example.cool1024.android_example.fragments.BannerFragment;
import com.example.cool1024.android_example.fragments.BaseFragment;
import com.example.cool1024.android_example.fragments.CameraFragment;
import com.example.cool1024.android_example.fragments.DrawFragment;
import com.example.cool1024.android_example.fragments.FlvFragments.FlvFragment;
import com.example.cool1024.android_example.fragments.ImageScaleFragment;

public class DevActivity extends AppCompatActivity {

    public static final String TAG = "DevActivityLog";
    public static final String FRAGMENT_NAME_PARAM = "fragment_name";
    private BaseFragment mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        setActiveFragment();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "按下返回键");
        if (mActiveFragment == null || mActiveFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * 根据传递的参数设置某个Fragment为积极状态
     */
    private void setActiveFragment() {
        String activeFragmentName = getIntent().getStringExtra(FRAGMENT_NAME_PARAM);
        Log.d(TAG, String.format("打开的碎片为:%s", activeFragmentName));
        switch (activeFragmentName) {
            case ImageScaleFragment.TAG: {
                showFragment(ImageScaleFragment.newInstance());
                break;
            }
            case AnimationFragment.TAG: {
                int animationName = getIntent().getIntExtra(AnimationFragment.ANIMATION_NAME_PARAM,
                        AnimationFragment.AnimationNames.Fling);
                showFragment(AnimationFragment.newInstance(animationName));
                break;
            }
            case BannerFragment.TAG: {
                showFragment(BannerFragment.newInstance(new String[]{
                        "https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310312505b88b23288693.jpg",
                        "https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310313105b88b246cb80c.jpg",
                        "https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310313295b88b2595bfb5.jpg"
                }));
                break;
            }
            case DrawFragment.TAG: {
                showFragment(DrawFragment.newInstance());
                break;
            }
            case CameraFragment.TAG: {
                showFragment(CameraFragment.newInstance());
                break;
            }
            case FlvFragment.TAG: {
                FlvDetail flvDetail = new FlvDetail();
                flvDetail.setFlvUrl("https://www.cool1024.com:8000/flv?video=1.flv");
                // flvDetail.setFlvUrl("http://192.168.0.116:8000/flv?video=1.flv");
                showFragment(FlvFragment.newInstance(flvDetail));
                break;
            }
        }
    }

    /**
     * 显示指定的视图碎片
     *
     * @param fragment 要显示的碎片
     */
    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.dev_fragment_layout, fragment)
                .commitAllowingStateLoss();
        mActiveFragment = (BaseFragment) fragment;
    }
}

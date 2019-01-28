package com.example.cool1024.android_example.classes;

import java.io.InputStream;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;

public class DMManager implements DrawHandler.Callback {

    // 弹幕控件的Context
    private DanmakuContext mContext;

    // 弹幕控件
    private IDanmakuView mView;

    public DMManager(IDanmakuView view) {
        mView = view;
        mView.setCallback(DMManager.this);
    }

    /**
     * 创建一个弹幕数据解析器
     *
     * @param stream 弹幕文件输入流
     * @return 弹幕解析器
     */
    public BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    /**
     * 获取弹幕控件的Context
     *
     * @return 弹幕控件的Context
     */
    public DanmakuContext getDMContext() {
        if (mContext == null) {
            mContext = DanmakuContext.create();
        }
        return mContext;
    }

    public void prepared() {
        mView.start();
    }

    public void updateTimer(DanmakuTimer timer) {
    }

    public void danmakuShown(BaseDanmaku dm) {
    }

    public void drawingFinished() {
    }
}

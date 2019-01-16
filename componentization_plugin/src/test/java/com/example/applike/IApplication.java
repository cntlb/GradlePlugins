package com.example.applike;

import android.content.Context;

public interface IApplication {
    int MIN = 1, NORMAL = 50, MAX = 100;

    /**
     * 组件初始化执行
     */
    void onCreate(Context context);

    /**
     * 组件执行生命周期方法的优先级, [1, 100]之间, 可以使用
     * {@link #MIN}, {@link #NORMAL}, {@link #MAX}
     */
    default int priority(){
        return NORMAL;
    }
}

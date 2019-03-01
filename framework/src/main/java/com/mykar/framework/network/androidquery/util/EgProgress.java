package com.mykar.framework.network.androidquery.util;

/**
 * Created by linquandong on 16/8/6.
 * 自定义进度条
 */
public abstract class EgProgress extends Progress {
    public EgProgress() {
        super();
    }

    public EgProgress(Object p) {
        super(p);
    }

    @Override
    public void setBytes(int bytes) {
        super.setBytes(bytes);
        startLoading(getMax());
    }

    @Override
    public void increment(int delta) {
        if (unknown) {
            current++;
        } else {
            current += delta;
        }
        loadingProgress(bytes, current, delta);

    }

    public abstract void loadingProgress(int max, int currtent, int delta);
    public abstract void startLoading(int max);

    public int getMax() {
        return bytes;
    }

}

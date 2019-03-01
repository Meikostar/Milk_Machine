package com.canplay.milk.base;

import android.support.annotation.NonNull;

public interface BasePresenter<View extends BaseView> {

    void attachView(@NonNull View view);

    void detachView();
}

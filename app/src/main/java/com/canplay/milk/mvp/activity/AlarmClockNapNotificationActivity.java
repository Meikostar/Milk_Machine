/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.canplay.milk.mvp.activity;

import android.os.Bundle;


import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.util.MyUtil;
import com.google.zxing.client.android.activity.BaseActivitySimple;
import com.google.zxing.client.android.decode.WeacConstants;


/**
 * 闹钟小睡通知Activity
 *
 * @author 咖枯
 * @version 1.0 2015
 */
public class AlarmClockNapNotificationActivity extends BaseActivitySimple {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmClock alarmClock = getIntent().getParcelableExtra(
                WeacConstants.ALARM_CLOCK);
        // 关闭小睡
        MyUtil.cancelAlarmClock(this, -Integer.valueOf(alarmClock.getId()));
        finish();
    }

}

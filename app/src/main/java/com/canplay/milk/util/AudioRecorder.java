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
package com.canplay.milk.util;

import android.content.Context;
import android.media.MediaRecorder;


/**
 * 录音器
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class AudioRecorder {

    /**
     * 录音
     */
    public MediaRecorder mRecorder;

    /**
     * 共通录音器实例
     */
    private static AudioRecorder sAudioRecorder;

    private static Context sContext;

    /**
     * 录音开启成功
     */
    public boolean mIsStarted = false;

    private AudioRecorder(Context appContext) {
        sContext = appContext;
    }

    /**
     * 取得录音器实例
     *
     * @return 录音器实例
     */
    public static AudioRecorder getInstance(Context context) {
        if (sAudioRecorder == null) {
            sAudioRecorder = new AudioRecorder(context.getApplicationContext());
        }
        return sAudioRecorder;
    }

    /**
     * 停止录音
     */
    public void stop() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

    }



}

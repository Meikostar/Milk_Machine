package com.canplay.milk.mvp.LogModel;

import com.mykar.framework.commonlogic.model.BaseDelegate;

import java.util.ArrayList;

import socket.laserprotocol.entry.LaserLog;

/**
 * Created by xzh on 15/9/22.
 */
public interface ILogsDelegate extends BaseDelegate {
  void getLogsSuccess(ArrayList<LaserLog> params);
}

package com.canplay.milk.fragment;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseFragment;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.bean.RemindMilk;
import com.canplay.milk.mvp.adapter.RemindMeasureAdapter;
import com.canplay.milk.mvp.component.AlarmClockDeleteEvent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.AlarmClockOperate;
import com.canplay.milk.util.MyUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 测量提醒
 */
public class RemindFragment extends BaseFragment implements BaseContract.View {

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.rl_menu)
    ListView rlMenu;
    Unbinder unbinder;
    private RemindMeasureAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remind_medical, null);
//        DaggerBaseComponent.builder().appComponent(((BaseApplication) getActivity().getApplication()).getAppComponent()).build().inject(this);
//
//        presenter.attachView(this);

//        presenter.MeasureRemindList();
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<AlarmClock> mAlarmClockList;
    private Subscription mSubscription;

    private void initView() {
        adapter = new RemindMeasureAdapter(getActivity());
        rlMenu.setAdapter(adapter);
        mAlarmClockList = new ArrayList<>();
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.MESURE == bean.type) {
                    setData();
                }


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        RxBus.getInstance().addSubscription(mSubscription);
        updateList();
        adapter.setListener(new RemindMeasureAdapter.selectItemListener() {
            @Override
            public void delete(AlarmClock medicine, int type, int poistion) {


            }
        });
        setData();
    }
    public void setData(){
        List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
        adapter.setData(allAlarm,0);
    }
    private String name = "";
    private String time = "";
    //    private Mesure mesure=new Mesure();
    private List<String> times = new ArrayList<>();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    private void checkIsEmpty(List<AlarmClock> list) {
        if (list.size() != 0) {
            rlMenu.setVisibility(View.VISIBLE);
//            mEmptyView.setVisibility(View.GONE);
        } else {
            rlMenu.setVisibility(View.GONE);
//            mEmptyView.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

    }

    private void addList(AlarmClock ac) {
        mAlarmClockList.clear();

        String id = ac.getId();
        int count = 0;
        int position = 0;
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            if (id .equals(alarmClock.getId()) ) {
                position = count;
                if (alarmClock.isOnOff()) {
                    MyUtil.startAlarmClock(getActivity(), alarmClock);
                }
            }
            count++;
        }
        adapter.setData(mAlarmClockList,0);
        checkIsEmpty(list);


    }

    private void deleteList(AlarmClockDeleteEvent event) {
        mAlarmClockList.clear();

        int position = event.getPosition();
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);
        }
        // 列表为空时不显示删除，完成按钮


        checkIsEmpty(list);

        adapter.setData(mAlarmClockList,0);
//        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
    }

    private void updateList() {
        mAlarmClockList.clear();

        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            // 当闹钟为开时刷新开启闹钟
            if (alarmClock.isOnOff()) {
                MyUtil.startAlarmClock(getActivity(), alarmClock);
            }
        }
        adapter.setData(mAlarmClockList,0);
        checkIsEmpty(list);


    }


    private int i = 0;

    @Override
    public <T> void toEntity(T entity, int type) {
//        if(type==6){
//            BASE base= (BASE) entity;
//            if(base.isSucceeded){
//                data.remove(poition);
//                adapter.setData(data);
//                List<AlarmClock> alarmClocks = AlarmClockOperate.getInstance().loadAlarmClocks();
//                for(AlarmClock alarmClock:alarmClocks){
//                    if(TextUtil.isNotEmpty(time)){
//                        String[] split = time.split(":");
//                        if(alarmClock.getHour()==Integer.valueOf(split[0])&&alarmClock.getMinute()==Integer.valueOf(split[1])){
//                            AlarmClockOperate.getInstance().deleteAlarmClock(alarmClock);
//
//                            // 关闭闹钟
//                            MyUtil.cancelAlarmClock(getActivity(),
//                                    alarmClock.getId());
//                            // 关闭小睡
//                            MyUtil.cancelAlarmClock(getActivity(),
//                                    -alarmClock.getId());
//
//                            NotificationManager notificationManager = (NotificationManager) getActivity()
//                                    .getSystemService(Activity.NOTIFICATION_SERVICE);
//                            // 取消下拉列表通知消息
//                            notificationManager.cancel(alarmClock.getId());
//                        }
//                    }
//                }
//
//            }else {
//                if(i!=0){
//                    return;
//                }
//                i++;
//                times.clear();
//                times.addAll(base.when);
//                mesure.name=name;
//                mesure.when=times;
//                String userId = SpUtil.getInstance().getUserId();
//                mesure.userId=userId;
//                mesure.type="time";
//                mesure.remindingFor="Measurement";
//                presenter.addMesure(mesure);
//            }
//        }else {
//            data= (List<Medicine>) entity;
//
//            adapter.setData(data);
//        }

    }

    private int poition;

    @Override
    public void toNextStep(int type) {

    }


    @Override
    public void showTomast(String msg) {

    }
}

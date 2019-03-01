package com.canplay.milk.mvp.component;


import com.canplay.milk.base.AppComponent;
import com.canplay.milk.fragment.DataFragment;
import com.canplay.milk.fragment.FileFragment;
import com.canplay.milk.fragment.HomeFragment;
import com.canplay.milk.fragment.RemindFragment;
import com.canplay.milk.fragment.SetFragment;
import com.canplay.milk.fragment.WikiPediaFragment;
import com.canplay.milk.mvp.ActivityScope;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.activity.account.EditorPwsActivity;
import com.canplay.milk.mvp.activity.account.ForgetPswActivity;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.mvp.activity.account.RegisteredActivity;
import com.canplay.milk.mvp.activity.account.RegisteredSecondActivity;
import com.canplay.milk.mvp.activity.home.AddBrandsActivity;
import com.canplay.milk.mvp.activity.home.AddMilkActivity;
import com.canplay.milk.mvp.activity.home.MilkDetailActivity;
import com.canplay.milk.mvp.activity.home.MilkListActivity;
import com.canplay.milk.mvp.activity.home.OutWatherActivity;
import com.canplay.milk.mvp.activity.home.PushMilkActivity;
import com.canplay.milk.mvp.activity.home.RemindMilkActivity;
import com.canplay.milk.mvp.activity.home.RemindSettingActivity;
import com.canplay.milk.mvp.activity.home.SearchMilkActivity;
import com.canplay.milk.mvp.activity.mine.EditorInfoActivity;
import com.canplay.milk.mvp.activity.mine.MineInfoActivity;
import com.canplay.milk.mvp.activity.mine.UpdateActivity;
import com.canplay.milk.mvp.activity.mine.UserAvarActivity;
import com.canplay.milk.mvp.activity.wiki.GroupRecordActivity;
import com.canplay.milk.mvp.activity.wiki.LookTImeActivity;
import com.canplay.milk.mvp.activity.wiki.NurseryActivity;
import com.canplay.milk.mvp.activity.wiki.PastWipiActivity;
import com.canplay.milk.mvp.activity.wiki.PastWipiSearchActivity;
import com.canplay.milk.mvp.activity.wiki.PreviewRecordActivity;
import com.canplay.milk.mvp.activity.wiki.SendRecordActivity;

import dagger.Component;

/**
 * Created by leo on 2016/9/27.
 */
@ActivityScope
@Component(dependencies = AppComponent.class)
public interface BaseComponent{

    void inject(PastWipiSearchActivity binderActivity);
    void inject(WikiPediaFragment binderActivity);
    void inject(HomeFragment binderActivity);
    void inject(EditorPwsActivity binderActivity);
    void inject(LookTImeActivity binderActivity);
    void inject(OutWatherActivity binderActivity);
    void inject(SearchMilkActivity binderActivity);
    void inject(MilkDetailActivity binderActivity);
    void inject(MilkListActivity binderActivity);
    void inject(AddBrandsActivity binderActivity);
    void inject(RemindMilkActivity binderActivity);
    void inject(RemindSettingActivity binderActivity);
    void inject(AddMilkActivity binderActivity);
    void inject(MainActivity binderActivity);
    void inject(FileFragment binderActivity);
    void inject(PushMilkActivity binderActivity);
    void inject(NurseryActivity binderActivity);
    void inject(EditorInfoActivity binderActivity);
    void inject(LoginActivity binderActivity);
    void inject(PreviewRecordActivity binderActivity);
    void inject(GroupRecordActivity binderActivity);
    void inject(PastWipiActivity binderActivity);
    void inject(SendRecordActivity binderActivity);
    void inject(MineInfoActivity binderActivity);
    void inject(UserAvarActivity binderActivity);
    void inject(UpdateActivity binderActivity);
    void inject(SetFragment binderActivity);

    void inject(ForgetPswActivity binderActivity);
    void inject(RegisteredSecondActivity binderActivity);
    void inject(RegisteredActivity binderActivity);
    void inject(DataFragment binderActivity);


}

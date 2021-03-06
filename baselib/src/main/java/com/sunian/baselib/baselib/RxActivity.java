package com.sunian.baselib.baselib;

import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.sunian.baselib.customview.DialogTip;
import com.sunian.baselib.util.HomeKeyBroadcastReceiver;
import com.sunian.baselib.util.ToastUtil;

/**
 * Created by fujun on 2018/4/8.
 */

public abstract class RxActivity<T extends RxPresenter, JB> extends BaseActivity implements IBaseView<JB> {


    protected T mPresenter;

    protected SmartRefreshLayout mSrl;
    private DialogTip dialogTip;
    protected HomeKeyBroadcastReceiver homeKeyBroadcastReceiver;

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        initRefresh();
        initPresenter();
        //  registerHomeKeyReceiver(this);
        if (mPresenter != null)
            mPresenter.attachView(this);


    }
  /*  private  void registerHomeKeyReceiver(Context context) {
          homeKeyBroadcastReceiver = new HomeKeyBroadcastReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(homeKeyBroadcastReceiver, homeFilter);
    }

    private  void unregisterHomeKeyReceiver(Context context) {

        if (null != homeKeyBroadcastReceiver) {
            context.unregisterReceiver(homeKeyBroadcastReceiver);
        }
    }*/


    /**
     * 初始化刷新控件
     */
    protected void initRefresh() {

    }

    /**
     * 初始化控制器
     */
    protected void initPresenter() {

    }


    @Override
    protected void onDestroy() {
        if (mSrl != null) {
            mSrl.finishRefresh(0);
            mSrl.finishLoadMore(0);
        }
        //  unregisterHomeKeyReceiver(this);
        if (mPresenter != null)
            mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showErrorMsg(String msg, int type) {
        if (msg != null)
            ToastUtil.show(msg, mContext);

    }

    @Override
    public void stateEmpty(String msg, int type) {

    }

    @Override
    public void stateLoading(String msg, int type) {
        showTip(false);
    }

    @Override
    public void stateMain(int type) {
        if (mSrl != null) {
            mSrl.finishRefresh();
            mSrl.finishLoadMore();
        }
        dismissTip();
    }

    @Override
    public void stateNoInternet(String msg, int type) {

    }

    @Override
    public void resultDate(JB data, int type) {

    }

    /**
     * 设置标题及背景色
     *
     * @param tvTitle-----
     * @param title-------String，标题
     * @param colorResource--int，标题背景色的id
     */
    protected void setTitle(TextView tvTitle, String title, int colorResource) {
        if (tvTitle == null)
            return;
        tvTitle.setText(title);
        if (colorResource == 0)
            return;
        tvTitle.setBackgroundResource(colorResource);
    }

    public void showTip(boolean isForce) {
        if (dialogTip == null)
            dialogTip = new DialogTip(mContext);
        if (isForce || isShowTip()) {
            if (!dialogTip.isShowing())
                dialogTip.show();
        }

    }

    protected void dismissTip() {
        if (dialogTip == null)
            return;
        if (dialogTip.isShowing())
            dialogTip.dismiss();

    }

    protected boolean isShowTip() {
        return true;
    }
}

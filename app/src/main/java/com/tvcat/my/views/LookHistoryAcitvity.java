package com.tvcat.my.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sunian.baselib.baselib.RxActivity;
import com.sunian.baselib.util.FastClick;
import com.sunian.baselib.util.StatusBarUtil;
import com.tvcat.R;
import com.sunian.baselib.beans.LookHistParseBean;
import com.sunian.baselib.beans.LookHistoryBean;
import com.tvcat.homepage.VideoWebActivity;
import com.tvcat.my.adapters.HistoryAdapter;
import com.tvcat.my.presenter.ILookHistoryView;
import com.tvcat.my.presenter.LookHistoryPresenter;

import java.util.List;

import butterknife.BindView;

public class LookHistoryAcitvity extends RxActivity<LookHistoryPresenter, Object> implements ILookHistoryView<Object> {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv)
    RecyclerView rv;


    private HistoryAdapter adapter;
    private int page = 1;



    @Override
    public int getLayout() {
        return R.layout.activity_look_history;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mPresenter = new LookHistoryPresenter();
    }

    @Override
    protected void initRefresh() {
        super.initRefresh();
        mSrl = findViewById(R.id.srl);
    }

    @Override
    protected void initListener() {


        ivBack.setOnClickListener(v -> finish());
        mSrl.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                mPresenter.getLookHistory(page);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                mPresenter.getLookHistory(page);
            }
        });

        adapter.setiClickBack((position, lookHistoryBean) -> {
            if (FastClick.isFastClick())
                return;
            if (lookHistoryBean == null || lookHistoryBean.getProvider() == null)
                return;
            mPresenter.parsePlayUrl(lookHistoryBean.getSource_url(), lookHistoryBean.getProvider().getId() + "");


        });

        mSrl.autoRefresh();
    }

    @Override
    protected void initEventAndData() {
        tvTitle.setText("观看历史");
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this);
        rv.setAdapter(adapter);
        StatusBarUtil.setColor(this, ContextCompat.getColor(mContext, R.color.main_color));
    }


    @Override
    public void resultLookHistory(List<LookHistoryBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (page == 1) {
            adapter.clearRefresh(list);
        } else {
            adapter.addMore(list);
        }

        page++;
    }

    @Override
    public void parseLook(LookHistParseBean lookHistParseBean) {

        if(lookHistParseBean == null)
            return;
        VideoWebActivity.start(mContext,lookHistParseBean.getUrl(),lookHistParseBean.getTitle(),lookHistParseBean.getSrc_url());

    }

    @Override
    public void showErrorMsg(String msg, int type) {
        if (type == 6008) {//冲值界面
            Intent intent = new Intent(mContext, VipChargeViewActivity.class);
            startActivity(intent);
        } else {
            super.showErrorMsg(msg, type);
        }

    }
}

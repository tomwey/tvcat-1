package com.tvcat.discover;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sunian.baselib.baselib.ComePresenter;
import com.sunian.baselib.baselib.RxFragment;
import com.tvcat.App;
import com.tvcat.ILauncherView;
import com.tvcat.LancherPresenter;
import com.tvcat.R;
import com.tvcat.beans.ConfigBean;
import com.tvcat.util.HttpConstance;
import com.tvcat.util.TipUtil;
import com.tvcat.util.WebViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiscoverFrg extends RxFragment<ComePresenter, ConfigBean> {
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.wv)
    WebView wv;
    @BindView(R.id.iv_back)
    ImageView ivBack;


    @Override
    protected void initPresenter() {
        mPresenter = new ComePresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.frg_dis;
    }

    @Override
    protected void adjustView(Bundle savedInstanceState) {
        super.adjustView(savedInstanceState);
        new WebViewUtil().setWebView(wv);
    }

    @Override
    protected void initListener() {
        super.initListener();

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 99) {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(newProgress);
                } else {
                    pb.setVisibility(View.GONE);
                }
            }
        });


        mPresenter.httpGetObject(HttpConstance.HTTP_CONFIG, null, ConfigBean.class, true);

        if (App.getConfigBean() == null) {
            mPresenter.httpGetObject(HttpConstance.HTTP_CONFIG, null, ConfigBean.class, true);
        } else {
            loadUrl();
        }

        ivBack.setOnClickListener(v -> {
            if (wv == null)
                return;
            if (wv.canGoBack())
                wv.goBack();
        });

    }

    void loadUrl() {
        wv.loadUrl(App.getConfigBean().getExplore_url());
    }

    public boolean backPress() {
        boolean back = false;

        if (wv.canGoBack()) {
            back = true;
            wv.goBack();
        }

        return back;

    }


    @Override
    public void onPause() {
        super.onPause();
        wv.onPause();
        wv.pauseTimers();
    }


    @Override
    public void resultDate(ConfigBean data, int type) {
        super.resultDate(data, type);
        App.setConfigBean(data);
        loadUrl();
    }

    @Override
    public void onResume() {
        super.onResume();
        wv.resumeTimers();
        wv.onResume();
    }

    @Override
    public void onDestroyView() {

        if (wv != null)
            wv.destroy();
        super.onDestroyView();
    }


}

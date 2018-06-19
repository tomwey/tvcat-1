package com.tvcat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tvcat.beans.ConfigBean;
import com.tvcat.util.CountUtil;
import com.tvcat.util.TipUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

public class LancherActivity extends AppCompatActivity implements ILauncherView {

    private CountUtil countUtil;
    private LancherPresenter lancherPresenter;
    private boolean isCanStarMan = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.lancher_activity);


        lancherPresenter = new LancherPresenter(this);
        lancherPresenter.getConfig();
        requestPermission();


    }

    @Override
    protected void onDestroy() {


        if (countUtil != null)
            countUtil.destroyAllCount();

        lancherPresenter.unSubscribe();

        super.onDestroy();
    }


    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            new RxPermissions(this).requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            startMain();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            requestPermission();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』

                            new AlertDialog.Builder(LancherActivity.this)
                                    .setTitle("权限申请")
                                    .setMessage("请在设置中打开存储权限，否则程序的某些功能无法正常运行")
                                    .setNegativeButton("退出", (dialog, which) -> {
                                        finish();
                                    })
                                    .setPositiveButton("设置", (dialog, which) -> {
                                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }).create().show();
                        }
                    });
        else
            startMain();
    }

    private void startMain() {
        if(!isCanStarMan)
            return;
        isCanStarMan = false;
        countUtil = new CountUtil();
        countUtil.countBackMainThread(3, aLong -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void noInterNet() {
        Toast.makeText(this, TipUtil.NO_NET, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getConfigFailed(String reason) {

        if (reason == null)
            return;
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resultConfig(ConfigBean configBean) {

    }
}

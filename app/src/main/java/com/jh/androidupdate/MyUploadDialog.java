package com.jh.androidupdate;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;

public class MyUploadDialog extends Dialog implements View.OnClickListener {

    private Context context = null;
    private View.OnClickListener onUpdateListener = null;

    /**
     * 更新的布局
     */
    private LinearLayout lin_update= null;
    // 进度条
    private ContentLoadingProgressBar progress_horizontal = null;
    // 百分比
    private TextView tv_percentage = null;
    // 当前进度
    private TextView tv_current_process = null;
    // 总进度
    private TextView tv_total_process = null;
    // 单位
    private TextView tv_model = null;

    /**
     * 按钮布局
     */
    private LinearLayout lin_btn = null;
    // 更新按钮
    private Button btn_update = null;
    // 取消按钮
    private Button btn_cancel = null;

    public MyUploadDialog(@NonNull Context context,View.OnClickListener onUpdateListener) {
        super(context);
        this.context = context;
        this.onUpdateListener = onUpdateListener;
        this.setCancelable(false);
        // 拿到Dialog的Window, 修改Window的属性
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        initView();
        setCurrentVisibility(1);
    }

    private void initView() {
        lin_update = findViewById(R.id.lin_update);
        progress_horizontal = findViewById(R.id.progress_horizontal);
        tv_percentage = findViewById(R.id.tv_percentage);
        tv_current_process = findViewById(R.id.tv_current_process);
        tv_total_process = findViewById(R.id.tv_total_process);
        tv_model = findViewById(R.id.tv_model);
        lin_btn = findViewById(R.id.lin_btn);
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                setCurrentVisibility(2);
                if (onUpdateListener!=null){
                    onUpdateListener.onClick(v);
                }
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }

    /**
     * 设置当前是否可见
     *
     * @param flag   1  隐藏更新的相关内容，展示按钮    2   隐藏按钮的相关内容，展示更新
     */
    public void setCurrentVisibility(int flag){
        //  初始化状态
        resetUpdateState();
        if (flag==1){
            lin_update.setVisibility(View.GONE);
            lin_btn.setVisibility(View.VISIBLE);
        }else{
            lin_update.setVisibility(View.VISIBLE);
            lin_btn.setVisibility(View.GONE);
        }
    }

    private void resetUpdateState() {
        tv_percentage.setText("0%");
        tv_current_process.setText("0");
        tv_total_process.setText("0");
        progress_horizontal.setProgress(0);
    }

    public void setTotalProcess(String totalProcess){
        tv_total_process.setText(totalProcess);
    }

    public void setCurrentProcess(String currentProcess){
        tv_current_process.setText(currentProcess);
    }

    public void setPercentage(int percentage){
        tv_percentage.setText(percentage+"%");
        progress_horizontal.setProgress(percentage);
    }
}

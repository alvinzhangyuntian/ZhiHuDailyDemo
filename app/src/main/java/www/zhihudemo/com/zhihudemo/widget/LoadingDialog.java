package www.zhihudemo.com.zhihudemo.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import www.zhihudemo.com.zhihudemo.R;


public class LoadingDialog extends AlertDialog {

    private TextView tips_loading_msg;

    private String message = null;

    public LoadingDialog(Context context) {
        super(context);
        message = getContext().getResources().getString(R.string.loading);
    }
    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        this.setCancelable(false);
    }
    /**
     * 参数最多的构造函数
     * @param context
     * @param message
     * @param cancelable
     * @param canceledOnTouchOutside
     */
    public LoadingDialog(Context context, String message, Boolean cancelable, Boolean canceledOnTouchOutside) {
        super(context);
        this.message = message;
        // 设置进度条是否可以按退回键取消
        this.setCancelable(cancelable);
     	// 设置点击进度对话框外的区域对话框是否消失
        this.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_loading);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        tips_loading_msg.setText(this.message);
    }

    public void setText(String message) {
        this.message = message;
        tips_loading_msg.setText(this.message);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

}
package hankin.softinputdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @BindView(R.id.tv_softinput_title)
    TextView mTitleTv;
    @BindView(R.id.lv_softinput) SoftListView mListView;
    private ArrayList<String> mList = new ArrayList<>();
    @BindView(R.id.et_softinput_content)
    EditText mContentEt;
    @BindView(R.id.ll_softinput_tabs)
    LinearLayout mTabsLl;
    @BindView(R.id.rl_softinput_bottom)
    RelativeLayout mBottomRl;
    /*用于第一次点击edittext时，获取软键盘高度*/
    private boolean isEtTouch;
    /*用于最后一次点击edittext，软键盘弹出时，按返回键让布局全屏，在触碰edittext时设为true，软键盘隐藏时设为false*/
    private boolean isEtTouchForFull;
    /*除去title栏的布局高度*/
    private int mMaxHeight;
    /*软键盘高度*/
    private int mSoftInputHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        for (int i=0;i<30;i++){
            mList.add("item"+i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(adapter);
        mListView.setSelection(mListView.getCount()-1);
        mListView.setOnLayoutListener(new SoftListView.OnLayoutListener() {
            @Override
            public void onLayoutChanged() {
                if (isEtTouchForFull){
                    setViewFull();
                }
            }
        });

        mContentEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isFullScreen()){
                    setViewNotFull();
                }
                mContentEt.setFocusableInTouchMode(true);
                mContentEt.setFocusable(true);
                isEtTouch = true;
                isEtTouchForFull = true;
                mBottomRl.setVisibility(View.GONE);
                return false;
            }
        });

        mTabsLl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mTabsLl.getGlobalVisibleRect(r);

                if (isEtTouch){
                    isEtTouch = false;
                    mTabsLl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mSoftInputHeight = getResources().getDisplayMetrics().heightPixels-r.bottom;//软键盘高度
                    Log.d("mydebug---", "软键盘高度 ： "+mSoftInputHeight);
                    mBottomRl.getLayoutParams().height = mSoftInputHeight;
                    mBottomRl.requestLayout();
                } else {
                    Rect rect = new Rect();
                    mTitleTv.getGlobalVisibleRect(rect);
                    mMaxHeight = r.bottom-rect.bottom;
                    Log.d("mydebug---", "mMaxHeight ： "+ mMaxHeight);
                }
            }
        });

    }

    public boolean isFullScreen(){
        return Math.abs(mMaxHeight - ((ViewGroup)mListView.getParent()).getHeight()) < 10;
    }

    public void setViewNotFull(){
        ViewGroup parentView = (ViewGroup)mListView.getParent();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)parentView.getLayoutParams();
        params.height = parentView.getHeight();
        params.weight = 0;
        parentView.requestLayout();
    }

    public void setViewFull(){
        ViewGroup parentView = (ViewGroup)mListView.getParent();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)parentView.getLayoutParams();
        params.height = 0;
        params.weight = 1;
        parentView.requestLayout();
    }

    @Override
    public void onBackPressed() {
        if (mBottomRl.getVisibility() == View.VISIBLE){
            setViewFull();
            mBottomRl.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void hideSoftInput(final Context context) {
        InputMethodManager inputMethodManager = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));
        final View currentFocusView = ((Activity)context).getCurrentFocus();
        if (currentFocusView != null) {
            final IBinder windowToken = currentFocusView.getWindowToken();
            if (inputMethodManager != null && windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    @OnClick({R.id.ll_softinput_0, R.id.ll_softinput_1, R.id.ll_softinput_2, R.id.ll_softinput_3})
    void click(View view){
        switch (view.getId()){
            case R.id.ll_softinput_0:
            case R.id.ll_softinput_1:
            case R.id.ll_softinput_2:
            case R.id.ll_softinput_3:
                if (mSoftInputHeight == 0) return;
                if (mBottomRl.getVisibility() == View.VISIBLE){
                    setViewFull();
                    mBottomRl.setVisibility(View.GONE);
                } else {
                    if (!isFullScreen()){
                        setViewNotFull();
                    }
                    mBottomRl.setVisibility(View.VISIBLE);
                }
                hideSoftInput(this);
                mContentEt.setFocusable(false);
                mTitleTv.requestFocus();
                isEtTouchForFull = false;
                break;
        }
    }

}

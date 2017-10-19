package hankin.softinputdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 *
 * Created by Hankin on 2017/10/13.
 * @email hankin.huan@gmail.com
 */

public class SoftListView extends ListView {

    private OnLayoutListener mOnLayoutListener;

    public SoftListView(Context context) {
        super(context);
    }
    public SoftListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SoftListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mOnLayoutListener!=null) mOnLayoutListener.onLayoutChanged();
    }

    public void setOnLayoutListener(OnLayoutListener listener){
        mOnLayoutListener = listener;
    }

    public interface OnLayoutListener{
        void onLayoutChanged();
    }

}

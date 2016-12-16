package com.sccp.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 该ListView嵌套在ScrollView里，可以显示多行数据（如果有），
 * 修复默认ListView嵌套在ScrollView里面只显示一行内容的情况。
 *
 * @author c_zoi@qq.com
 */
public class MultiLineListView extends ListView {
	
    public MultiLineListView(Context context) {
    	super(context);
    }
    
    public MultiLineListView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public MultiLineListView(Context context, AttributeSet attrs, int defStyle) {
      	super(context, attrs, defStyle);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
      	int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    	super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
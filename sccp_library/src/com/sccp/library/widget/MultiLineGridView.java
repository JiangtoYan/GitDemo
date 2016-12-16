package com.sccp.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 该GridView嵌套在ScrollView里，可以显示多行数据（如果有），
 * 修复默认GridView嵌套在ScrollView里面只显示一行内容的情况。
 *
 * @author c_zoi@qq.com
 */
public class MultiLineGridView extends GridView {   
	
    private boolean haveScrollbar = true;   
    
    public MultiLineGridView(Context context) {   
        super(context);   
    }
    
    public MultiLineGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);
    }
    
    public MultiLineGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);
    }
    
    /**
     * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true  
     * 
     * @param haveScrollbars
     */
    public void setHaveScrollbar(boolean haveScrollbar) {
        this.haveScrollbar = haveScrollbar;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
        if (haveScrollbar == false) {
        	
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
            super.onMeasure(widthMeasureSpec, expandSpec);   
        }
        else {   
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
        }   
    }   
}
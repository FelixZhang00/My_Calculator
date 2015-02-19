package com.example.felix.my_calculator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import org.wltea.expression.ExpressionEvaluator;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CalculatorActivity extends ActionBarActivity {

    private  final String TAG =this.getClass().getSimpleName() ;
    @InjectView(R.id.grid_buttons)
    GridView mGridButtons;

    @InjectView(R.id.edit_input)
    EditText mEditInput;

    private BaseAdapter mAdapter;

    // EditText显示的内容，mPreStr表示灰色表达式部分，要么为空，要么以换行符结尾
    private String mPreStr = "";
    // mLastStr表示显示内容的深色部分
    private String mLastStr = "";

    //判断是否是刚刚成功执行完一个表达式
    private boolean mIsExcuteNow = false;

    //html换行标签
    private final String newLine = "<br\\>";

    //gridview的所有按钮对应的按键内容
    private final String[] mTextBtns = new String[]{"Back", "(", ")", "CE",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "+",
            "0", ".", "=", "-",};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        mAdapter = new CalculatorAdapter(this,
                mTextBtns);
        mGridButtons.setAdapter(mAdapter);
        GridButtonItemClickListener listener = new GridButtonItemClickListener();
        mGridButtons.setOnItemClickListener(listener);

        // 这句话的目的是为了让EditText不能从键盘输入
        mEditInput.setKeyListener(null);


    }

    /**
     * 自定义计算器按键监听器
     */
    private class GridButtonItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String text = (String) parent.getAdapter().getItem(position);
            if ("=".equals(text)) {
                excuteExpression();
            } else if ("Back".equals(text)) { //删除一个字符
                if (mLastStr.length() == 0) { //现在是删除历史记录
                    if (mPreStr.length()!=0){
                        //删除换行符
                        mPreStr=mPreStr.substring(0,mPreStr.length()-newLine.length());
                        mIsExcuteNow=true;
                        //找打前一个换行符的位置
                        int index=mPreStr.lastIndexOf(newLine);
                        if(index==-1){ //没有找到，表示历史表达式只有一个
                            mLastStr=mPreStr;
                            mPreStr="";
                            mIsExcuteNow=false;
                        }else{
                            mLastStr=mPreStr.substring(index+newLine.length(),mPreStr.length());
                            mPreStr=mPreStr.substring(0,index+newLine.length());

                        }
                    }
                } else {
                    mLastStr = mLastStr.substring(0, mLastStr.length() - 1);
                }
                //显示内容
                setText();

            } else if ("CE".equals(text)) { //清空显示内容
                mPreStr = "";
                mLastStr = "";
                mIsExcuteNow = false;
                mEditInput.setText("");
            } else {
                //其它按键的情况
                if (mIsExcuteNow) {//如果刚刚成功执行了一个表达式，那么需要把当前表达式加到历史表达式后面并添加换行符
                    mIsExcuteNow = false;

                    mPreStr += mLastStr + newLine;
                    mLastStr = text;

                } else {
                    mLastStr += text;
                }
                //更新内容
                setText();
            }
        }
    }


    /**
     * 当按下 = 时，执行当前表达式，并判断是否有错误
     */
    private void excuteExpression() {
        Object result = null;
        try {
            result = ExpressionEvaluator.evaluate(mLastStr);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果捕获到异常，表示表达式执行失败，调用setError方法显示错误信息
            mEditInput.setError(e.getMessage());
            mEditInput.requestFocus();
            mIsExcuteNow = false;
            return;
        }

        //执行成功了，设置标志为true，同时更新最后的表达式的内容为 表达式+ “=” +result
        mIsExcuteNow = true;
        mLastStr += "=" + result;
        mEditInput.setError(null);
        //显示执行结果
        setText();
    }


    /**
     * 设置EditText的显示内容，主要是为了加上html颜色标签
     */
    private void setText() {
        final String[] tags = new String[]{"<font color='#858585'>", "<font color='#CD2626'>", "</font> "};
        StringBuilder builder = new StringBuilder();
        builder.append(tags[0]);
        builder.append(mPreStr);
        builder.append(tags[2]);

        builder.append(tags[1]);
        builder.append(mLastStr);
        builder.append(tags[2]);

        mEditInput.setText(Html.fromHtml(builder.toString()));
        mEditInput.setSelection(mEditInput.getText().length());
        //获取焦点
        mEditInput.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

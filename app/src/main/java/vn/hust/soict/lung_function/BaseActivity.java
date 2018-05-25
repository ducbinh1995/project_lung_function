package vn.hust.soict.lung_function;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseActivity extends AppCompatActivity {
    protected Context mContext;

    protected void initContext(){
        mContext = this;
    }

    protected void initView(){

    }

    protected void initController(){

    }

    protected void hidenKeyboard() {
        try {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void getFocus(EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        imm.showSoftInput(et, 0);
    }
}

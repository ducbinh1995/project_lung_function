package vn.hust.soict.lung_function;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.hust.soict.lung_function.utils.FontUtils;

/**
 * Created by ducbinh on 5/20/2018.
 */

public class UserAdapter extends BaseAdapter {

    Context mContext;
    private static LayoutInflater inflater = null;
    String[] listUserName;
    String[] listUserId;
    String[] listEmail;

    public UserAdapter(Context mContext, String[] listUserName, String[] listUserId, String[] listEmail) {
        this.mContext = mContext;
        this.listUserName = listUserName;
        this.listUserId = listUserId;
        this.listEmail = listEmail;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listUserName.length;
    }

    @Override
    public String getItem(int i) {
        return listUserId[i];
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final String userName = listUserName[i];
        final String email = listEmail[i];
        final String userId = getItem(i);
        final int index = i;
        View mView = view;
        if (mView == null) {
            mView = inflater.inflate(R.layout.user_row, null);
        }
        TextView mUserName = (TextView) mView.findViewById(R.id.tvUserName);
        TextView mEmail = (TextView) mView.findViewById(R.id.tvEmail);
        Button mBtnSeeHistory = (Button) mView.findViewById(R.id.btnSeeHistory);
        Button mBtnUnfollow = (Button) mView.findViewById(R.id.btnUnfollow);

        FontUtils.setFont(mUserName);
        FontUtils.setFont(mEmail);

        mUserName.setText(userName);
        mEmail.setText(email);
        mBtnSeeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserId(userId, userName, "history");
            }
        });

        mBtnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromArray(index);
                if (listUserId.length == 0){
                    notifyDataSetInvalidated();
                }
                else {
                    notifyDataSetChanged();
                }
                sendUserId(userId, userName,"unfollow");
            }
        });
        return mView;
    }

    public void removeFromArray(int index){
        int length = listUserId.length;
        List<String> list = new ArrayList<String>(Arrays.asList(listUserId));
        list.remove(index);
        listUserId = list.toArray(new String[length - 1]);
        list = new ArrayList<String>(Arrays.asList(listUserName));
        list.remove(index);
        listUserName = list.toArray(new String[length - 1]);
        list = new ArrayList<String>(Arrays.asList(listEmail));
        list.remove(index);
        listEmail = list.toArray(new String[length - 1]);
    }

    public void sendUserId(String userId, String userName, String action) {
        Intent intent = new Intent("send-user-id");
        intent.putExtra("userId",userId);
        intent.putExtra("userName", userName);
        intent.putExtra("action", action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}

package vn.hust.soict.lung_function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class ListFollowUserActivity extends BaseActivity {

    ListView mListView;
    String[] userName ;
    String[] userId;
    String[] email;
    Profile mProfile;
    JSONObject jsonResponse;
    String followUserId ;
    String followUserName;
    UserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_follow_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegisterReceive, new IntentFilter("send-user-id"));

        mProfile = AppData.getInstance().getProfile();
        getListFollow();
    }

    public void getListFollow() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "GetFollowed");
            parameters.put("user_id", mProfile.getUserId());
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        String url = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.REGISTER_URL);
        HttpURLConnection urlConnection = null;
        URL myUrl = null;
        try {
            myUrl = new URL(url);
        }
        catch (MalformedURLException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection = (HttpURLConnection) myUrl.openConnection();
        }
        catch (java.io.IOException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
            urlConnection.setRequestProperty(WebGlobal.HEADER_ACCEPT,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.setRequestProperty(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.connect();
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(parameters.toString());
            long startTime = System.currentTimeMillis();
            StringBuilder response = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time login: " + elapsedTime);
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output + "\n");
            }
            System.out.println(response);
            jsonResponse = new JSONObject(response.toString());
            JSONArray resultArray = jsonResponse.getJSONArray("result");
            if (resultArray.length() == 0) {
                Prompt.show(mContext,"Không có bệnh nhân");
            }
            else {
                userName = new String[resultArray.length()];
                userId = new String[resultArray.length()];
                email = new String[resultArray.length()];
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject resultObj = resultArray.getJSONObject(i);
                    userName[i] = resultObj.getString("name");
                    userId[i] = resultObj.getString("_id");
                    email[i] = resultObj.getString("email");
                }
                showListUser();
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public void showListUser () {
        mListView = (ListView) findViewById(R.id.listFollowUser);
        mAdapter = new UserAdapter(this, userName, userId, email);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    BroadcastReceiver mRegisterReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            followUserId = intent.getStringExtra("userId");
            followUserName = intent.getStringExtra("userName");
            String action = intent.getStringExtra("action");
            if (action.equals("history")) {
                getTimeline(followUserId, followUserName);
            }
            else {
                unFollow(followUserId);
            }
        }
    };

    public void unFollow(String followUserId){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "Unfollow");
            parameters.put("follower", mProfile.getUserId());
            parameters.put("followed", followUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.REGISTER_URL);
        HttpURLConnection urlConnection = null;
        URL myUrl = null;
        try {
            myUrl = new URL(url);
        }
        catch (MalformedURLException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection = (HttpURLConnection) myUrl.openConnection();
        }
        catch (java.io.IOException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
            urlConnection.setRequestProperty(WebGlobal.HEADER_ACCEPT,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.setRequestProperty(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.connect();
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(parameters.toString());
            long startTime = System.currentTimeMillis();
            StringBuilder response = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time login: " + elapsedTime);
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output + "\n");
            }
            System.out.println(response);
            jsonResponse = new JSONObject(response.toString());
            String result = jsonResponse.getString("status");
            if (result.equals("success")){
                mListView.setAdapter(mAdapter);
                mListView.invalidateViews();
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public void getTimeline(String followUserId, String followUserName) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "GetTimeline");
            parameters.put("user_id", followUserId);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        String url = MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.REGISTER_URL);
        HttpURLConnection urlConnection = null;
        URL myUrl = null;
        try {
            myUrl = new URL(url);
        }
        catch (MalformedURLException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection = (HttpURLConnection) myUrl.openConnection();
        }
        catch (IOException e){
            Log.e("Error = ", e.toString());
        }
        try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
            urlConnection.setRequestProperty(WebGlobal.HEADER_ACCEPT,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.setRequestProperty(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.REGISTER_CONTENT_TYPE);
            urlConnection.connect();
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(parameters.toString());
            long startTime = System.currentTimeMillis();
            StringBuilder response = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Time get time line: " + elapsedTime);
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output + "\n");
            }
            System.out.println(response);
            jsonResponse = new JSONObject(response.toString());
            if (jsonResponse != null) {
                Intent intent = new Intent(this, ResultListViewActivity.class);
                try {
                    JSONArray listdata = jsonResponse.getJSONArray("result");
                    intent.putExtra("resultList",listdata.toString());
                    intent.putExtra("userName", followUserName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            } else {
                Prompt.show(mContext, "Không có lịch sử đo");
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }
}

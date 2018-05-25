package vn.hust.soict.lung_function;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class FollowUserActivity extends BaseActivity {

    LinearLayout mLayoutSearchResult;
    Button btnSearch;
    Button btnFollow;
    EditText edSearchEmail;
    TextView tvResultName;
    Profile mProfile;
    String followed;

    String userId;
    String userName;

    private JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
        initController();
    }

    protected void initView() {
        mLayoutSearchResult = (LinearLayout) findViewById(R.id.layoutSearchResult);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edSearchEmail = (EditText) findViewById(R.id.edSearchUser);
        tvResultName = (TextView) findViewById(R.id.tvResultEmail);
        btnFollow = (Button) findViewById(R.id.btnFollow);

        FontUtils.setFont(edSearchEmail);
        FontUtils.setFont(tvResultName);
    }

    protected void initController() {
        mProfile = AppData.getInstance().getProfile();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidenKeyboard();
                searchEmail(edSearchEmail.getText().toString());
            }
        });
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followed.equals("0")) {
                    followUser();
                }
                else {
                    unFollowUser();
                }
            }
        });
    }

    public void unFollowUser(){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "Unfollow");
            parameters.put("follower", mProfile.getUserId());
            parameters.put("followed", userId);
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
                followed = "0";
                btnFollow.setText("Theo dõi");
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public void followUser() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "CreateFollow");
            parameters.put("follower", mProfile.getUserId());
            parameters.put("followed", userId);
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
            if (result.equals("success")) {
                followed = "1";
                btnFollow.setText("Bỏ theo dõi");
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public void searchEmail(String mEmail) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "SearchUser");
            parameters.put("email", mEmail);
            parameters.put("id", mProfile.getUserId());
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
            if (resultArray.length() == 0){
                Prompt.show(mContext,"Không tìm thấy bệnh nhân");
            }
            else {
                JSONObject resultObj = resultArray.getJSONObject(0);
                userId = resultObj.getString("_id");
                userName = resultObj.getString("name");
                followed = resultObj.getString("followed");
                mLayoutSearchResult.setVisibility(View.VISIBLE);
                tvResultName.setText(userName);
                if (followed.equals("1")) {
                    btnFollow.setText("Bỏ theo dõi");
                }
                else {
                    btnFollow.setText("Theo dõi");
                }
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
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
}

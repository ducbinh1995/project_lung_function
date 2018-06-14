package vn.hust.soict.lung_function;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class LoginActivity extends BaseActivity {

    private Button btnLogin;
    private Button btnSignup;
    private Button btnSetting;

    private EditText edUsername;
    private EditText edPassword;

    private String mEmail;
    private String mPassword;

    // Record Field
    private int bufferSize;

    private Profile mProfile;

    private String isCaretakers;
    private String userId;

    private JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        initContext();
        initView();
        initController();
    }

    protected void initView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSetting = (Button) findViewById(R.id.btnSetting);

        edUsername = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);
    }

    protected void initController() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidenKeyboard();
                if (validate()) {
                    login();
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidenKeyboard();
                signup();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSetupUrl();
            }
        });
    }

    private void addProfile (JSONObject jsonObject) {
        try {
            mProfile.setUserId(jsonObject.getString("user_id"));
            mProfile.setName(jsonObject.getString("name"));
            mProfile.setEmail(jsonObject.getString("email"));
            mProfile.setWeight(jsonObject.getString("weight"));
            mProfile.setBirthDay(jsonObject.getString("dayOfBirth"));
            mProfile.setHeight(jsonObject.getString("height"));
            mProfile.setMale(jsonObject.getString("gender"));
            mProfile.setRegion(jsonObject.getString("location"));
            mProfile.setSmoking(jsonObject.getString("smokingStatus"));
            mProfile.setCareTakers(jsonObject.getString("isCaretakers"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppData.getInstance().setProfile(mProfile);
    }

    public boolean validate() {
        mEmail = edUsername.getText().toString();
        mPassword = edPassword.getText().toString();
        if (mEmail.equals("") || !emailValidator(mEmail)) {
            Prompt.show(mContext,"Vui lòng nhập đúng định dạng email");
            return false;
        }

        if (mPassword.equals("")){
            Prompt.show(mContext,"Vui lòng nhập mật khẩu");
            return false;
        }

        return true;
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void login() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "Login");
            parameters.put("email", edUsername.getText().toString());
            parameters.put("password", edPassword.getText().toString());
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
            String status = jsonResponse.getString("status");
            if (status.equals("success")) {
                userId = jsonResponse.getString("user_id");
                isCaretakers = jsonResponse.getString("isCaretakers");
                addProfile(jsonResponse);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userID",userId);
                intent.putExtra("isCaretakers",isCaretakers);
                startActivity(intent);
            }
            else {
                Prompt.show(mContext,"Email hoặc mật khẩu không đúng");
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("userID",userId);
//        intent.putExtra("isCaretakers","1");
//        startActivity(intent);
    }

    private void signup() {
        Intent intent = new Intent(LoginActivity.this, AddPatientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void initContext() {
        super.initContext();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

        if (bufferSize <= AppConstant.BUFFER_SIZE_DEFAULT) {
            bufferSize = AppConstant.BUFFER_SIZE_DEFAULT;
        }
        mProfile = new Profile();
    }

    private void showDialogSetupUrl() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_url);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final EditText edUrl = (EditText) dialog.findViewById(R.id.edInputUrl);

        edUrl.setText(MSharedPreferences.getInstance(mContext).getString(AppConstant.KEY_SERVER_URL, WebGlobal.REGISTER_URL));

        FontUtils.setFont(dialog.findViewById(R.id.title), FontUtils.TYPE_NORMAL);
        FontUtils.setFont(dialog.findViewById(R.id.inputUrl));
        FontUtils.setFont(dialog.findViewById(R.id.btnCancel), FontUtils.TYPE_NORMAL);
        FontUtils.setFont(dialog.findViewById(R.id.btnOK), FontUtils.TYPE_NORMAL);

        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidenKeyboard();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidenKeyboard();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edUrl.getText().toString().trim();
                if (url.equals("")) {
                    Prompt.show(mContext, R.string.msg_input_url);
                } else {
                    hidenKeyboard();
                    dialog.dismiss();
//                    MSharedPreferences.getInstance(mContext).putString(AppConstant.KEY_SERVER_URL, url);
                    WebGlobal.REGISTER_URL = url;
                }
            }
        });

        dialog.show();

    }
}

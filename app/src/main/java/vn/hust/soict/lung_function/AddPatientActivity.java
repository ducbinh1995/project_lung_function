package vn.hust.soict.lung_function;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.Prompt;

public class AddPatientActivity extends BaseActivity {

    final String mDateFormat = "%02d-%02d-%04d";

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;

    private EditText mFullName;
    private EditText mWeight;
    private EditText mHeight;

    private DatePicker mBirthDay;

    private RadioGroup mGender;
    private RadioGroup mRegion;
    private RadioGroup mSmoking;
    private RadioGroup mCareTakes;

    private Button mBtnSignup;

    private Profile mProfile;

    private String isCaretakers;

    private String userId;

    private JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initContext();

        initView();
        initController();
    }


    @Override
    protected void initContext() {
        super.initContext();
        mProfile = new Profile();
    }

    @Override
    protected void initController() {
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidenKeyboard();

                addPatient();
            }
        });
    }

    @Override
    protected void initView() {
        LinearLayout layoutAdmin = (LinearLayout) findViewById(R.id.layoutAdmin);
        isCaretakers = getIntent().getStringExtra("isCaretakers");
        layoutAdmin.setVisibility(LinearLayout.GONE);

        mEmail = (EditText) findViewById(R.id.edEmail);
        mPassword = (EditText) findViewById(R.id.edPassword);
        mConfirmPassword = (EditText) findViewById(R.id.edConfirmPassword);

        mFullName = (EditText) findViewById(R.id.edFullName);
        mWeight = (EditText) findViewById(R.id.edWeight);
        mHeight = (EditText) findViewById(R.id.edHeight);

        mBirthDay = (DatePicker) findViewById(R.id.datePickerBirthDay);
        Calendar birthday = Calendar.getInstance();
        birthday.set(1994, 0, 1);
        mBirthDay.updateDate(birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH));

        mGender = (RadioGroup) findViewById(R.id.rgGender);
        mRegion = (RadioGroup) findViewById(R.id.rgRegion);
        mSmoking = (RadioGroup) findViewById(R.id.rgSmoking);
        mCareTakes = (RadioGroup) findViewById(R.id.rgCareTakers);

        mBtnSignup = (Button) findViewById(R.id.btnSignup);

        FontUtils.setFont(mBirthDay);

        FontUtils.setFont(findViewById(R.id.edEmail));
        FontUtils.setFont(findViewById(R.id.edPassword));
        FontUtils.setFont(findViewById(R.id.edFullName));
        FontUtils.setFont(findViewById(R.id.edWeight));
        FontUtils.setFont(findViewById(R.id.edHeight));

        FontUtils.setFont(findViewById(R.id.tvBirthDayHint));
        FontUtils.setFont(findViewById(R.id.tvGenderHint));
        FontUtils.setFont(findViewById(R.id.tvRegionHint));
        FontUtils.setFont(findViewById(R.id.tvSmokingHint));
        FontUtils.setFont(findViewById(R.id.tvCareTakersHint));
        FontUtils.setFont(findViewById(R.id.rbMale));
        FontUtils.setFont(findViewById(R.id.rbFemale));
        FontUtils.setFont(findViewById(R.id.rbRegionNorthen));
        FontUtils.setFont(findViewById(R.id.rbRegionCentral));
        FontUtils.setFont(findViewById(R.id.rbRegionSouth));
        FontUtils.setFont(findViewById(R.id.rbSmokingYes));
        FontUtils.setFont(findViewById(R.id.rbSmokingNo));
        FontUtils.setFont(findViewById(R.id.rbCareTakersYes));
        FontUtils.setFont(findViewById(R.id.rbCareTakersNo));
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

    private void addPatient() {
        String strTmp = mFullName.getText().toString();
        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_name);
//            getFocus(mWeight);
            return;
        }
        mProfile.setName(strTmp);

        strTmp = mEmail.getText().toString();
        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_email);
//            getFocus(mWeight);
            return;
        }
        if (!emailValidator(strTmp)){
            Prompt.show(mContext,"Vui lòng nhập đúng định dạng email");
            return;
        }
        mProfile.setEmail(strTmp);

        strTmp = mPassword.getText().toString();
        String strTmp2 = mConfirmPassword.getText().toString();
        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_password);
//            getFocus(mWeight);
            return;
        }

        if (strTmp.length() < 6) {
            Prompt.show(mContext, R.string.msg_input_password_length);
            return;
        }

        if (!strTmp.equals(strTmp2)) {
            Prompt.show(mContext,R.string.msg_input_confirm_password);
            return;
        }

        mProfile.setPassword(strTmp);

        String birthday = String.format(mDateFormat, mBirthDay.getDayOfMonth(), (mBirthDay.getMonth() + 1), mBirthDay.getYear());
        mProfile.setBirthDay(birthday);

        switch (mGender.getCheckedRadioButtonId()) {
            case R.id.rbMale:
                mProfile.setMale("male");
                break;
            case R.id.rbFemale:
                mProfile.setMale("female");
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_gender);
                return;
        }

        strTmp = mWeight.getText().toString();

        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_weight);
            getFocus(mWeight);
            return;
        }

        mProfile.setWeight(strTmp);

        strTmp = mHeight.getText().toString();

        if (strTmp.equals("")) {
            Prompt.show(mContext, R.string.msg_input_height);
            getFocus(mHeight);
            return;
        }

        mProfile.setHeight(strTmp);

        switch (mRegion.getCheckedRadioButtonId()) {
            case R.id.rbRegionNorthen:
                mProfile.setRegion(Profile.REGION_NORTHEN);
                break;
            case R.id.rbRegionCentral:
                mProfile.setRegion(Profile.REGION_CENTRAL);
                break;
            case R.id.rbRegionSouth:
                mProfile.setRegion(Profile.REGION_SOUTH);
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_region);
                return;
        }


        switch (mSmoking.getCheckedRadioButtonId()) {
            case R.id.rbSmokingYes:
                mProfile.setSmoking("y");
                break;
            case R.id.rbSmokingNo:
                mProfile.setSmoking("n");
                break;
            default:
                Prompt.show(mContext, R.string.msg_input_smoking);
                return;
        }

        switch (mCareTakes.getCheckedRadioButtonId()) {
            case R.id.rbCareTakersYes:
                mProfile.setCareTakers("1");
                break;
            case R.id.rbCareTakersNo:
                mProfile.setCareTakers("0");
                break;
            default:
                Prompt.show(mContext,R.string.msg_input_doctor);
                return;
        }

        login();

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

    private void login() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "CreateNewUser");
            parameters.put("email", mProfile.getEmail());
            parameters.put("password", mProfile.getPassword());
            parameters.put("name", mProfile.getName());
            parameters.put("day_of_birth", mProfile.getBirthDay());
            parameters.put("weight", mProfile.getWeight());
            parameters.put("height", mProfile.getHeight());
            parameters.put("location", mProfile.getRegion());
            parameters.put("gender", mProfile.isMale());
            parameters.put("smoking_status", mProfile.isSmoking());
            parameters.put("is_caretakers", mProfile.isCareTakers());
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        String url = WebGlobal.REGISTER_URL;
        Log.d("params", parameters.toString());
        URL myUrl = null;
        try {
            myUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            urlConnection = (HttpURLConnection) myUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
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
            System.out.println("Time register: " + elapsedTime);
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output + "\n");
            }
            System.out.println(response);
            jsonResponse = new JSONObject(response.toString());
            userId = jsonResponse.getString("user_id");
            isCaretakers = jsonResponse.getString("isCaretakers");
            addProfile(jsonResponse);
            Intent intent = new Intent(AddPatientActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isCaretakers",isCaretakers);
            intent.putExtra("userID",userId);
            startActivity(intent);
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
        }
        finally {
            urlConnection.disconnect();
        }
//        AddPatientActivity.this.finish();
    }
}

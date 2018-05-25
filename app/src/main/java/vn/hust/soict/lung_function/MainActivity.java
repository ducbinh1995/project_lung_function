package vn.hust.soict.lung_function;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import vn.hust.soict.lung_function.config.AppConstant;
import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.file.TempFile;
import vn.hust.soict.lung_function.file.WavFile;
import vn.hust.soict.lung_function.model.Profile;
import vn.hust.soict.lung_function.net.RestRequest;
import vn.hust.soict.lung_function.net.WebGlobal;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.MSharedPreferences;
import vn.hust.soict.lung_function.utils.Prompt;

public class MainActivity extends BaseActivity {

    private Dialog mProgressRecording;
    private TextView mDialogMessage;

    private Button btnLungFunction;
    private TextView edMail;
    private TextView edFullName;
    private TextView edBirthDay;
    private TextView edGender;
    private TextView edWeight;
    private TextView edHeight;
    private TextView edRegion;
    private TextView edSmoking;

    private Profile mProfile;

    private Handler mHandler;

    // Record Field
    private int bufferSize;
    private boolean recording;

    private AudioRecord recorder = null;

    private TempFile mTempFile;
    private WavFile mWavFile;

    private RestRequest mRestRequest;

    private String isCaretakers;
    private String userId;

    private JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContext();
        initView();

        requestRecordAudioAccessNetworkPermission();

        initController();
    }


    private void requestRecordAudioAccessNetworkPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidenKeyboard();
        mProfile = AppData.getInstance().getProfile();
        if (mProfile == null) {
            initData();
        }
        if (mProfile != null) {
            updateUIProfile();
        }
    }

    private void record() {
        if (recording) return;
        try {
            mTempFile = new TempFile(mContext);
            recording = true;
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    AppConstant.RECORDER_SAMPLERATE, AppConstant.RECORDER_CHANNELS,
                    AppConstant.RECORDER_AUDIO_ENCODING, bufferSize);

            mProgressRecording.show();
            recorder.startRecording();
            new Thread(hanldeRecording).start();
            mHandler.postDelayed(handlePostRecording, AppConstant.TIME_RECORD_DEFAULT);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Prompt.show(mContext, R.string.msg_file_no_found_exception);
        }
    }

    private Runnable hanldeRecording = new Runnable() {

        @Override
        public void run() {
            try {
                byte data[] = new byte[bufferSize];

                while (recording) {
                    recorder.read(data, 0, bufferSize);
                    if (mTempFile != null) {
                        mTempFile.write(data);
                    } else {
                        Prompt.show(mContext, R.string.msg_temp_file_null_point);
                        break;
                    }
                }
                recorder.release();
                recorder = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private ArrayList<Float>convertJSONToList( JSONArray jArray){
        ArrayList<Float> listdata = new ArrayList<Float>();
        if (jArray != null) {
            for (int i = 0; i  < jArray.length(); i++) {
                try {
                    listdata.add(new Float(jArray.getDouble(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return listdata;
    }

    private Runnable handlePostRecording = new Runnable() {
        @Override
        public void run() {
            try {
                // Dung qua trinh ghi am
                recording = false;
                // Luu file tam lai
                mTempFile.close();
                mWavFile = new WavFile(mContext,
                        mTempFile.getPathTempFile(),
                        AppConstant.RECORDER_SAMPLERATE,
                        AppConstant.NUMBER_CHANNELS,
                        AppConstant.BIT_PER_SAMPLE);
                // Luu file wave
                mWavFile.saveWaveFile();

//                mRestRequest = new RestRequest();
                mRestRequest.setHeaders();

                // Ket thuc ghi am
                String URL = WebGlobal.REGISTER_URL;
                JSONObject jsonObject = mRestRequest.postResponse(URL, mWavFile.getFileName(),userId);
//                LungFunction lungFunction = mRestRequest.postResponse(URL, "/storage/emulated/0/Music/RECORD.wav");

                // Xoa file tam thoi
                mTempFile.deleteFile();
                // Xoa file wave
                mWavFile.deleteFile();
                mProgressRecording.dismiss();

                if (jsonObject != null) {
//                    AppData.getInstance().setLungFunction(lungFunction);
                    Intent intent = new Intent(mContext, LungFunctionActivity.class);
                    try {
                        ArrayList<Float> listdata = convertJSONToList(jsonObject.getJSONArray("flow_curve"));
                        intent.putExtra("flow_curve", listdata);
                        listdata = convertJSONToList(jsonObject.getJSONArray("volumes"));
                        intent.putExtra("volumes", listdata);
                        intent.putExtra("PEF", jsonObject.getString("PEF"));
                        intent.putExtra("FVC", jsonObject.getString("FVC"));
                        intent.putExtra("FEV1", jsonObject.getString("FEV1"));
                        intent.putExtra("pred_PEF", jsonObject.getString("pred_PEF"));
                        intent.putExtra("pred_FVC", jsonObject.getString("pred_FVC"));
                        intent.putExtra("pred_FEV1", jsonObject.getString("pred_FEV1"));
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
//                    MainActivity.this.finish();
                } else {
                    Prompt.show(mContext, "LungFunction is NULL");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mProgressRecording.dismiss();
                Prompt.show(mContext, R.string.msg_file_no_found_exception);
            } catch (IOException e) {
                e.printStackTrace();
                mProgressRecording.dismiss();
                Prompt.show(mContext, R.string.msg_file_io_exception);
            }
        }
    };

    protected void initContext() {
        super.initContext();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

        recording = false;
        if (bufferSize <= AppConstant.BUFFER_SIZE_DEFAULT) {
            bufferSize = AppConstant.BUFFER_SIZE_DEFAULT;
        }
        mHandler = new Handler();
        mRestRequest = new RestRequest();
    }

    protected void initController() {

        btnLungFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfile == null) {
                    Prompt.show(mContext, R.string.msg_text_request_add_patient);
                }
                record();
            }
        });

    }

    private void initData() {
        mProfile = new Profile();
        mProfile.setEmail("ABC@MAIL.COM");
        mProfile.setName("LE CONG Tu");
        mProfile.setBirthDay("07/02/1994");
        mProfile.setMale("male");
        mProfile.setHeight("160");
        mProfile.setWeight("60");
        mProfile.setRegion(Profile.REGION_NORTHEN);
        mProfile.setSmoking("n");
    }

    private void updateUIProfile() {
        if (mProfile != null) {
            edMail.setText(mProfile.getEmail());
            edFullName.setText(mProfile.getName());
            edBirthDay.setText(mProfile.getBirthDay());
            switch (mProfile.isMale()) {
                case "male":
                    edGender.setText("Nam");
                    break;
                case "female":
                    edGender.setText("Nữ");
                    break;
                default:
                    edGender.setText("");
                    break;
            }
            edWeight.setText(mProfile.getWeight() + "kg");
            edHeight.setText(mProfile.getHeight() + "cm");
            switch (mProfile.getRegion()) {
                case Profile.REGION_NORTHEN:
                    edRegion.setText(R.string.region_northen);
                    break;
                case Profile.REGION_CENTRAL:
                    edRegion.setText(R.string.region_central);
                    break;
                case Profile.REGION_SOUTH:
                    edRegion.setText(R.string.region_south);
                    break;
                default:
                    edRegion.setText("");
            }
            switch (mProfile.isSmoking()) {
                case "y":
                    edSmoking.setText(R.string.smoking_yes);
                    break;
                case "n":
                    edSmoking.setText(R.string.smoking_no);
                    break;
                default:
                    edSmoking.setText("");
                    break;
            }
        }
    }

    protected void initView() {
        LinearLayout inputAdmin = (LinearLayout) findViewById(R.id.layoutAdminInfo);
        isCaretakers = getIntent().getStringExtra("isCaretakers");
        inputAdmin.setVisibility(LinearLayout.GONE);
        userId = getIntent().getStringExtra("userID");
        btnLungFunction = (Button) findViewById(R.id.btnLungFunction);

        edMail = (TextView) findViewById(R.id.edEmail);
        edFullName = (TextView) findViewById(R.id.edName);
        edBirthDay = (TextView) findViewById(R.id.edBirthDay);
        edGender = (TextView) findViewById(R.id.edGender);
        edWeight = (TextView) findViewById(R.id.edWeight);
        edHeight = (TextView) findViewById(R.id.edHeight);
        edRegion = (TextView) findViewById(R.id.edRegion);
        edSmoking = (TextView) findViewById(R.id.edSmoking);

        mProgressRecording = new Dialog(this, android.R.style.Theme_Translucent);
        mProgressRecording.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressRecording.setContentView(R.layout.dialog_recording);
        mProgressRecording.setCancelable(false);
        mProgressRecording.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        mDialogMessage = (TextView) mProgressRecording.findViewById(R.id.tvMessage);

        FontUtils.setFont(mDialogMessage);

        FontUtils.setFont(btnLungFunction, FontUtils.TYPE_NORMAL);
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void history() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("op", "GetTimeline");
            parameters.put("user_id", userId);
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
                Intent intent = new Intent(mContext, ResultListViewActivity.class);
                try {
                    JSONArray listdata = jsonResponse.getJSONArray("result");
                    intent.putExtra("resultList",listdata.toString());
                    intent.putExtra("userName", mProfile.getName());
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

    private void getListFollow() {
        Intent intent = new Intent(mContext,ListFollowUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void searchFollow() {
        Intent intent = new Intent(mContext,FollowUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem btnFollow = menu.findItem(R.id.btnFollow);
        MenuItem btnListFollow = menu.findItem(R.id.btnListFollow);
        MenuItem btnHistory = menu.findItem(R.id.btnHistory);
        if (isCaretakers.equals("1")) {
            btnFollow.setVisible(true);
            btnListFollow.setVisible(true);
            btnHistory.setVisible(false);
            btnLungFunction.setVisibility(View.GONE);
        }
        else {
            btnFollow.setVisible(false);
            btnListFollow.setVisible(false);
            btnHistory.setVisible(true);
            btnLungFunction.setVisibility(View.VISIBLE);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                logout();
                return true;
            case R.id.btnHistory:
                history();
                return true;
            case R.id.btnFollow:
                searchFollow();
                return true;
            case R.id.btnListFollow:
                getListFollow();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

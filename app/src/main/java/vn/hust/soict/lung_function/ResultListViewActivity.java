package vn.hust.soict.lung_function;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.hust.soict.lung_function.model.LungFunction;

public class ResultListViewActivity extends AppCompatActivity {
    ListView mListView;
    JSONArray listdata;
    LungFunction[] listResult;
    TextView tvHistoryTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvHistoryTitle = (TextView) findViewById(R.id.tvHistoryTitle);
        tvHistoryTitle.setText("Lịch sử đo của "+ getIntent().getStringExtra("userName"));

        String dataString = getIntent().getStringExtra("resultList");
        try {
            listdata = new JSONArray(dataString);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        listResult = new LungFunction[listdata.length()];
        for (int i = 0; i < listdata.length(); i++){
            try {
                JSONObject lungObj = listdata.getJSONObject(i);
                Float mPEF = Float.parseFloat(lungObj.getString("PEF"));
                Float mFVC = Float.parseFloat(lungObj.getString("FVC"));
                Float mFEV1 = Float.parseFloat(lungObj.getString("FEV1"));
                ArrayList<Float> flowCurve = convertJSONToList(lungObj.getString("flowCurve"));
                ArrayList<Float> volumes = convertJSONToList(lungObj.getString("volumes"));
                String time = lungObj.getString("time");
                LungFunction lungFunctionObj = new LungFunction();
                lungFunctionObj.setPEF(mPEF);
                lungFunctionObj.setFEV1(mFEV1);
                lungFunctionObj.setFVC(mFVC);
                lungFunctionObj.setFlow(flowCurve);
                lungFunctionObj.setVolume(volumes);
                lungFunctionObj.setTime(time);
                listResult[listdata.length()-1-i] = lungFunctionObj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mListView = (ListView) findViewById(R.id.result_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(20);
        mListView.setAdapter(new ResultAdapter(this, listResult));
    }

    private ArrayList<Float>convertJSONToList( String jArray){
        ArrayList<Float> listdata = new ArrayList<Float>();
        String replace = jArray.replace("[","");
        String replace1 = replace.replace("]","");
        List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
        for (String s : myList) {
            listdata.add(Float.parseFloat(s));
        }
        return listdata;
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

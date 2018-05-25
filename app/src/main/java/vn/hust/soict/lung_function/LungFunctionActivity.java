package vn.hust.soict.lung_function;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import vn.hust.soict.lung_function.utils.FontUtils;

public class LungFunctionActivity extends BaseActivity {

    private String mFormat = "%.2f";

    private TextView mPEF;
    private TextView mFEV1;
    private TextView mFVC;
    private TextView mFEV1divFVC;

    private TextView mPredPEF;
    private TextView mPredFEV1;
    private TextView mPredFVC;
    private TextView mPredFEV1divFVC;
    private TextView mDanger;

    private TextView mFlowLabel;
    private TextView mVolumeLabel;
    private LineChart mChart;

    private Float errorPEF;
    private Float errorFVC;
    private Float errorFEV1;
    private Float errorFEV1divFVC;

    private Double measuredPEF;
    private Double predPEF;
    private Double predFVC;
    private Double predFEV1;
    private Double predFEV1divFVC;

    private TextView mErrorPEF;
    private TextView mErrorFVC;
    private TextView mErrorFEV1;
    private TextView mErrorFEV1divFVC;

    private LinearLayout layoutDanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lung_function);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initContext();
        updateErrorValue();
        initView();
        updateUI();
    }

    private void updateErrorValue() {
        measuredPEF = Double.parseDouble(getIntent().getStringExtra("PEF"));
        predPEF = Double.parseDouble(getIntent().getStringExtra("pred_PEF"));
        predFEV1 = Double.parseDouble(getIntent().getStringExtra("pred_FEV1"));
        predFVC = Double.parseDouble(getIntent().getStringExtra("pred_FVC"));
        predFEV1divFVC = Double.parseDouble(getIntent().getStringExtra("pred_FEV1")) / Double.parseDouble(getIntent().getStringExtra("pred_FVC"));
        errorPEF = Float.parseFloat(getIntent().getStringExtra("PEF")) / Float.parseFloat(getIntent().getStringExtra("pred_PEF")) * 100;
        errorFVC = Float.parseFloat(getIntent().getStringExtra("FVC")) / Float.parseFloat(getIntent().getStringExtra("pred_FVC")) * 100;
        errorFEV1 = Float.parseFloat(getIntent().getStringExtra("FEV1")) / Float.parseFloat(getIntent().getStringExtra("pred_FEV1")) * 100;
        Float fev1DivFVC = (Float.parseFloat(getIntent().getStringExtra("FEV1"))) / Float.parseFloat(getIntent().getStringExtra("FVC"));
        Float predFEV1divFVC = (Float.parseFloat(getIntent().getStringExtra("pred_FEV1"))) / Float.parseFloat(getIntent().getStringExtra("pred_FVC"));
        errorFEV1divFVC =  fev1DivFVC / predFEV1divFVC * 100;
    }

    private void updateUI() {

        String dangerString = "";

        updateChart();

        mPEF.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("PEF"))));
        mFEV1.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("FEV1"))));
        mFVC.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("FVC"))));
        mFEV1divFVC.setText(String.format(mFormat, (Float.parseFloat(getIntent().getStringExtra("FEV1"))) / Float.parseFloat(getIntent().getStringExtra("FVC"))));

        mPredFVC.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("pred_FVC"))));
        mPredFEV1.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("pred_FEV1"))));
        mPredPEF.setText(String.format(mFormat, Float.parseFloat(getIntent().getStringExtra("pred_PEF"))));
        mPredFEV1divFVC.setText(String.format(mFormat, (Float.parseFloat(getIntent().getStringExtra("pred_FEV1"))) / Float.parseFloat(getIntent().getStringExtra("pred_FVC"))));

        mErrorPEF.setText(String.format(mFormat, errorPEF));
        mErrorFVC.setText(String.format(mFormat, errorFVC));
        mErrorFEV1.setText(String.format(mFormat, errorFEV1));
        mErrorFEV1divFVC.setText(String.format(mFormat, errorFEV1divFVC));

        if (errorPEF < 80.0) {
            layoutDanger.setVisibility(View.VISIBLE);
            dangerString += "Chỉ số PEF của bạn đo được có độ chính xác so với chỉ số dự đoán nhỏ hơn 80%\n";
        }

        if (errorFEV1 < 80.0) {
            layoutDanger.setVisibility(View.VISIBLE);
            dangerString += "Chỉ số FEV1 của bạn đo được có độ chính xác so với chỉ số dự đoán nhỏ hơn 80%\n";
        }

        if (errorFVC < 80.0) {
            layoutDanger.setVisibility(View.VISIBLE);
            dangerString += "Chỉ số FVC của bạn đo được có độ chính xác so với chỉ số dự đoán nhỏ hơn 80%\n";
        }

        dangerString += "Bạn nên đi kiểm tra với bác sĩ để có được những quyết định kịp thời và chính xác";

        mDanger.setText(dangerString);
    }

    @Override
    protected void initView() {
        layoutDanger = (LinearLayout) findViewById(R.id.layoutDanger);
        layoutDanger.setVisibility(View.INVISIBLE);
        mPEF = (TextView) findViewById(R.id.tvPEFValue);
        mFEV1 = (TextView) findViewById(R.id.tvFEV1Value);
        mFVC = (TextView) findViewById(R.id.tvFVCValue);
        mFEV1divFVC = (TextView) findViewById(R.id.tvFEV1divFVCValue);

        mPredPEF = (TextView) findViewById(R.id.tvPredPEFValue);
        mPredFEV1 = (TextView) findViewById(R.id.tvPredFEV1Value);
        mPredFVC = (TextView) findViewById(R.id.tvPredFVCValue);
        mPredFEV1divFVC = (TextView) findViewById(R.id.tvPredFEV1divFVCValue);

        mErrorPEF = (TextView) findViewById(R.id.tvErrorPEF);
        mErrorFVC = (TextView) findViewById(R.id.tvErrorFVC);
        mErrorFEV1 = (TextView) findViewById(R.id.tvErrorFEV1);
        mErrorFEV1divFVC = (TextView) findViewById(R.id.tvErrorFEV1divFVC);

        mDanger = (TextView) findViewById(R.id.tvDanger);

        mFlowLabel = (TextView) findViewById(R.id.tvFlowLable);
        mVolumeLabel = (TextView) findViewById(R.id.tvVolumeLable);
        mChart = (LineChart) findViewById(R.id.chartFlowVolume);

        FontUtils.setFont(mFlowLabel);
        FontUtils.setFont(mVolumeLabel);

        FontUtils.setFont(mPEF);
        FontUtils.setFont(mFEV1);
        FontUtils.setFont(mFVC);
        FontUtils.setFont(mFEV1divFVC);
        FontUtils.setFont(mPredFEV1);
        FontUtils.setFont(mPredFVC);
        FontUtils.setFont(mPredPEF);
        FontUtils.setFont(mPredFEV1divFVC);
        FontUtils.setFont(mErrorFEV1);
        FontUtils.setFont(mErrorFEV1divFVC);
        FontUtils.setFont(mErrorFVC);
        FontUtils.setFont(mErrorPEF);
        FontUtils.setFont(mDanger);

    }

    private void updateChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        ArrayList<Entry> predValues = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();

        List<Float> xData = (List<Float>) getIntent().getSerializableExtra("volumes");
        List<Float> yData = (List<Float>) getIntent().getSerializableExtra("flow_curve");
        float[] predXData = {0, (float) (predFVC*0.1), predFVC.floatValue()};
        float[] predYData = {0, predPEF.floatValue(), 0};
        int length = xData.size();
        if (length > yData.size()) length = yData.size();

        int predLength = predXData.length;

        for (int i = 0; i < length; i++) {
            values.add(new Entry(xData.get(i), yData.get(i)));
        }

        for (int i = 0; i < predLength; i++) {
            predValues.add(new Entry(predXData[i], predYData[i]));
        }

        LineDataSet set1 = new LineDataSet(values, "Flow_Volume");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.chart_label_line));
        set1.setValueTextColor(Color.GRAY);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.MAGENTA);
        set1.setHighLightColor(Color.MAGENTA);
        set1.setDrawCircleHole(false);

        LineDataSet set2 = new LineDataSet(predValues, "Pred_Flow_Volume");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(getResources().getColor(R.color.chart_pred_line));
        set2.setValueTextColor(Color.GRAY);
        set2.setLineWidth(1.5f);
        set2.setDrawCircles(false);
        set2.setDrawValues(false);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.MAGENTA);
        set2.setHighLightColor(Color.MAGENTA);
        set2.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData();
        data.addDataSet(set1);
        data.addDataSet(set2);
        data.setValueTextColor(Color.YELLOW);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.chart_label_text));
        xAxis.setCenterAxisLabels(false);
        xAxis.setTypeface(FontUtils.getTypeface());
        xAxis.setGranularity(0.5f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(mFormat, value);
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(getResources().getColor(R.color.chart_label_text));
        leftAxis.setDrawGridLines(false);
        leftAxis.setTypeface(FontUtils.getTypeface());
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        if (measuredPEF < predPEF) {
            leftAxis.setAxisMaximum(0.5f + predPEF.floatValue());
        }
        else {
            leftAxis.setAxisMaximum(0.5f + measuredPEF.floatValue());
        }
        leftAxis.setYOffset(-7f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(mFormat, value);
            }
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);


        mChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}

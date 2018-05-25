package vn.hust.soict.lung_function;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.utils.FontUtils;

/**
 * Created by ducbinh on 5/9/2018.
 */

public class ResultAdapter extends BaseAdapter {
    Context mContext;
    LungFunction[] data;
    private String mFormat = "%.2f";
    private static LayoutInflater inflater = null;

    public  ResultAdapter(Context mContext, LungFunction[] data){
        this.mContext = mContext;
        this.data = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public LungFunction getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LungFunction mData = data[i];
        View mView = view;
        if (mView == null) {
            mView = inflater.inflate(R.layout.result_row, null);
        }
        TextView mTime = (TextView) mView.findViewById(R.id.edTime);
        TextView mPEF = (TextView) mView.findViewById(R.id.tvPEFValue);
        TextView mFVC = (TextView) mView.findViewById(R.id.tvFVCValue);
        TextView mFEV1 = (TextView) mView.findViewById(R.id.tvFEV1Value);
        TextView mFEV1divFVC = (TextView) mView.findViewById(R.id.tvFEV1divFVCValue);

        TextView mFlowLabel = (TextView) mView.findViewById(R.id.tvFlowLable);
        TextView mVolumeLabel = (TextView) mView.findViewById(R.id.tvVolumeLable);
        LineChart mChart = (LineChart) mView.findViewById(R.id.chartFlowVolume);

        FontUtils.setFont(mFlowLabel);
        FontUtils.setFont(mVolumeLabel);

        FontUtils.setFont(mPEF);
        FontUtils.setFont(mFEV1);
        FontUtils.setFont(mFVC);
        FontUtils.setFont(mFEV1divFVC);
        FontUtils.setFont(mView.findViewById(R.id.tvPEF));
        FontUtils.setFont(mView.findViewById(R.id.tvFEV1));
        FontUtils.setFont(mView.findViewById(R.id.tvFVC));
        FontUtils.setFont(mView.findViewById(R.id.tvFEV1divFVC));
        FontUtils.setFont(mTime);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        ArrayList<Entry> values = new ArrayList<>();

        List<Float> xData = mData.getVolume();
        List<Float> yData = mData.getFlow();
        int length = xData.size();
        if (length > yData.size()) length = yData.size();

        for (int index = 0; index < length; index++) {
            values.add(new Entry(xData.get(index), yData.get(index)));
        }

        LineDataSet set1 = new LineDataSet(values, "Flow_Volume");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(R.color.chart_label_line);
        set1.setValueTextColor(Color.GRAY);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.MAGENTA);
        set1.setHighLightColor(Color.MAGENTA);
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
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
        xAxis.setTextColor(R.color.chart_label_text);
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
        leftAxis.setTextColor(R.color.chart_label_text);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTypeface(FontUtils.getTypeface());
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(mData.getPEF() + 0.5f);
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

        mPEF.setText(String.format(mFormat,mData.getPEF()));
        mFVC.setText(String.format(mFormat,mData.getFVC()));
        mFEV1.setText(String.format(mFormat,mData.getFEV1()));
        mFEV1divFVC.setText(String.format(mFormat,mData.getFEV1()/mData.getFVC()));
        mTime.setText(mData.getTime());
        return mView;
    }
}

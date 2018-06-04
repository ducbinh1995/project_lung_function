package vn.hust.soict.lung_function.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LungFunction {
    private float mPEF;
    private float mFEV1;
    private float mFVC;
    private List<Float> mFlow;
    private List<Float> mVolume;
    private String mTime;

    public LungFunction() {
        mFlow = new ArrayList<>();
        mVolume = new ArrayList<>();
    }

    public String getTime() {return mTime; }

    public void setTime(String mTime) {this.mTime = mTime;}

    public float getPEF() {
        return mPEF;
    }

    public void setPEF(float mPEF) {
        this.mPEF = mPEF;
    }

    public float getFEV1() {
        return mFEV1;
    }

    public void setFEV1(float mFEV1) {
        this.mFEV1 = mFEV1;
    }

    public float getFVC() {
        return mFVC;
    }

    public void setFVC(float mFVC) {
        this.mFVC = mFVC;
    }

    public List<Float> getVolume() {
        return mVolume;
    }

    public void setVolume(List<Float> mVolume) {
        this.mVolume = mVolume;
    }

    public List<Float> getFlow() {
        return mFlow;
    }

    public void setFlow(List<Float> mFlow) {
        this.mFlow = mFlow;
    }

    public void parse(JSONObject jsonObject) {
        try {
            mPEF = (float) jsonObject.getDouble("PEF");
            mFEV1 = (float) jsonObject.getDouble("FEV1");
            mFVC = (float) jsonObject.getDouble("FVC");
//            mDeltaVolume = (float) jsonObject.getDouble("DeltaVolume");
            JSONArray data = jsonObject.getJSONArray("Flow");

            if (data == null || data.length() == 0) return;

            for (int i = 0; i < data.length(); i++) {
                mFlow.add((float) data.getDouble(i));
            }

            data = jsonObject.getJSONArray("Volume");

            if (data == null || data.length() == 0) return;

            for (int i = 0; i < data.length(); i++) {
                mVolume.add((float) data.getDouble(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

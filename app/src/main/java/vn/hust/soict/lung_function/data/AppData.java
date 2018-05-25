package vn.hust.soict.lung_function.data;

import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.model.Profile;

/**
 * Created by tulc on 15/03/2017.
 */
public class AppData {
    private static AppData mAppData;

    private LungFunction mLungFunction;
    private Profile mProfile;

    public static synchronized AppData getInstance() {
        if (mAppData == null) {
            mAppData = new AppData();
        }
        return mAppData;
    }

    public LungFunction getLungFunction() {
        return mLungFunction;
    }

    public void setLungFunction(LungFunction mLungFunction) {
        this.mLungFunction = mLungFunction;
    }

    public Profile getProfile() { return mProfile; }

    public void  setProfile(Profile mProfile) {
        this.mProfile = mProfile;
    }
}

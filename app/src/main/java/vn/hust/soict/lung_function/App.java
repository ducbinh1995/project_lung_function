package vn.hust.soict.lung_function;

import android.app.Application;

import io.realm.Realm;
import vn.hust.soict.lung_function.utils.FontUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FontUtils.loadFont(getApplicationContext(), "Roboto-Light.ttf");
        FontUtils.loadFontNormal(getApplicationContext(), "Roboto-Regular.ttf");

        Realm.init(this);
    }
}

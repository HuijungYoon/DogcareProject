package yoonhuijung.dogcareproject.chatting;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChattingDatabaseRealm extends Application {
    @Override
    public void onCreate() {
    super.onCreate();
    Realm.init(this);
    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
    Realm.setDefaultConfiguration(realmConfiguration);
    }
}

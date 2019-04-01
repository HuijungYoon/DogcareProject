package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class ChattingDatabaseRealm extends Application {
    @Override
    public void onCreate() {
    super.onCreate();
    Realm.init(this);
    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
    Realm.setDefaultConfiguration(realmConfiguration);
    }
}

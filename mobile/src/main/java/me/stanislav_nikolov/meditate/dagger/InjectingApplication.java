package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;

/**
 * Created by stanley on 24.09.15.
 */
public class InjectingApplication extends Application {
    protected MeditateComponent _graph;

    @Override
    public void onCreate() {
        super.onCreate();

        _graph = DaggerMeditateComponent.builder()
                .meditateAppModule(new MeditateAppModule(this))
                .onDiskRealmModule(new OnDiskRealmModule())
                .build();

    }
}

package me.stanislav_nikolov.meditate;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import me.stanislav_nikolov.meditate.dagger.DaggerMeditateComponent;
import me.stanislav_nikolov.meditate.dagger.MeditateAppModule;
import me.stanislav_nikolov.meditate.dagger.MeditateComponent;

/**
 * Created by stanley on 24.09.15.
 */
public class MeditateApp extends Application {
    private static MeditateComponent graph;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        graph = DaggerMeditateComponent.builder()
                .meditateAppModule(new MeditateAppModule(this))
                .build();

    }

    public static MeditateComponent getGraph() {
        return graph;
    }
}

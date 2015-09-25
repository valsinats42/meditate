package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by stanley on 23.09.15.
 */
@Module
public class MeditateAppModule {

    private final Application app;

    public MeditateAppModule(Application app) {
        this.app = app;
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    Realm provideRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(app).build();
        return Realm.getInstance(config);
    }
}

package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by stanley on 14.10.15.
 */
@Module
public class InMemoryRealmModule {
    @Singleton
    @Provides
    Realm provideRealm(Application app) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(app)
                .inMemory()
                .build();

        return Realm.getInstance(configuration);
    }
}

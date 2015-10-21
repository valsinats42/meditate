package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.internal.Table;
import me.stanislav_nikolov.meditate.db.DbMeditationSession;

/**
 * Created by stanley on 14.10.15.
 */
@Module
public class OnDiskRealmModule {
    @Singleton
    @Provides
    Realm provideRealm(Application app) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(app)
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        if (version == 0) {
                            Table table = realm.getTable(DbMeditationSession.class);
                            table.convertColumnToNullable(table.getColumnIndex("startTime"));
                            table.convertColumnToNullable(table.getColumnIndex("endTime"));
                            table.convertColumnToNullable(table.getColumnIndex("comment"));
                            version++;
                        }

                        return version;
                    }
                })
                .schemaVersion(1)
                .build();

        return Realm.getInstance(configuration);
    }
}

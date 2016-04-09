package me.stanislav_nikolov.meditate.dagger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import me.stanislav_nikolov.meditate.R;
import me.stanislav_nikolov.meditate.adapters.LogAdapter;
import me.stanislav_nikolov.meditate.adapters.StatsAdapter;
import me.stanislav_nikolov.meditate.db.SessionDb;

/**
 * Created by stanley on 23.09.15.
 */
@Module
public class MeditateAppModule {

    private final Application app;

    public MeditateAppModule(Application app) {
        this.app = app;
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return app;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    MediaPlayer provideMediaPlayer() {
        return MediaPlayer.create(app, R.raw.reception_bell);
    }

    @Singleton
    @Provides
    NotificationManager provideNotificationManager() {
        return (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Singleton
    @Provides
    AlarmManager provideAlarmManager() {
        return (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
    }

    @Singleton
    @Provides
    SessionDb provideSessionDb(Realm realm) {
        return new SessionDb(realm);
    }

    @Provides
    StatsAdapter provideStatsAdapter(Context context, SessionDb db) {
        return new StatsAdapter(context, db);
    }

    @Provides
    LogAdapter provideLogAdapter(Context context, SessionDb db) {
        return new LogAdapter(context, db);
    }
}

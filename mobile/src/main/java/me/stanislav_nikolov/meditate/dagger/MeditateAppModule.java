package me.stanislav_nikolov.meditate.dagger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.stanislav_nikolov.meditate.R;

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
}

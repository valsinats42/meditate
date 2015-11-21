package me.stanislav_nikolov.meditate.dagger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.media.MediaPlayer;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import me.stanislav_nikolov.meditate.ui.LogFragment;
import me.stanislav_nikolov.meditate.ui.MeditationSessionActivity;
import me.stanislav_nikolov.meditate.ui.SitFragment;
import me.stanislav_nikolov.meditate.ui.StatsFragment;

/**
 * Created by stanley on 23.09.15.
 */
@Singleton
@Component(
        modules = {
                MeditateAppModule.class,
                OnDiskRealmModule.class,
        }
)
public interface MeditateComponent {
    void inject(SitFragment fragment);
    void inject(StatsFragment fragment);
    void inject(LogFragment fragment);
    void inject(MeditationSessionActivity fragment);

    Application application();
    Realm realm();
    MediaPlayer mediaPlayer();
    NotificationManager notificationManager();
    AlarmManager alarmManager();
}

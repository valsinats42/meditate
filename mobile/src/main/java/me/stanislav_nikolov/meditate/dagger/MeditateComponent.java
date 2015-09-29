package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;
import android.media.SoundPool;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import me.stanislav_nikolov.meditate.ui.LogFragment;
import me.stanislav_nikolov.meditate.ui.MeditationSessionActivity;
import me.stanislav_nikolov.meditate.ui.StatsFragment;

/**
 * Created by stanley on 23.09.15.
 */
@Singleton
@Component(
        modules = MeditateAppModule.class
)
public interface MeditateComponent {
    void inject(StatsFragment fragment);
    void inject(LogFragment fragment);
    void inject(MeditationSessionActivity fragment);

    Application application();
    Realm realm();
    SoundPool soundPool();
}

package me.stanislav_nikolov.meditate.dagger;

import android.app.Application;

import dagger.Component;
import io.realm.Realm;
import me.stanislav_nikolov.meditate.ui.LogFragment;
import me.stanislav_nikolov.meditate.ui.MeditationSessionActivity;
import me.stanislav_nikolov.meditate.ui.StatsFragment;

/**
 * Created by stanley on 23.09.15.
 */
@Component(
        modules = MeditateAppModule.class
)
public interface MeditateComponent {
    void inject(StatsFragment fragment);
    void inject(LogFragment fragment);
    void inject(MeditationSessionActivity fragment);

    Application application();
    Realm realm();
}

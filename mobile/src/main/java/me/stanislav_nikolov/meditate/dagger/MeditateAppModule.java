package me.stanislav_nikolov.meditate.dagger;

import android.annotation.TargetApi;
import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Singleton
    @Provides
    SoundPool provideSoundPool() {
        //noinspection deprecation
        return (Build.VERSION.SDK_INT >= 21) ?
            new SoundPool.Builder().build() :
            new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
    }
}

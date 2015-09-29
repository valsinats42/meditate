package me.stanislav_nikolov.meditate

import android.util.Log
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import me.stanislav_nikolov.meditate.dagger.InjectingApplication
import me.stanislav_nikolov.meditate.dagger.MeditateComponent
import timber.log.Timber

/**
 * Created by stanley on 19.09.15.
 */
public class MeditateApp : InjectingApplication() {

    lateinit public var graph: MeditateComponent

    override fun onCreate() {
        super.onCreate()

        graph = _graph

        Fabric.with(this, Crashlytics());

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
            when (priority) {
                Log.DEBUG, Log.VERBOSE -> return
                else -> Fabric.getLogger().log(priority, tag, message)
            }
        }
    }
}

package archknife.extension

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class can be used to establish the Activity and Fragment lifecycle regarding the usage of
 * the AndroidInjection class.
 *
 * @since 1.0.0
 * @see AndroidInjection
 */
@Singleton
open class AppInjector
@Inject constructor(private val contextProvider: ActivityContextProvider) : FragmentLifecycleCallbacks(), ActivityLifecycleCallbacks {

    /**
     * Attaches custom Activity lifecycle callbacks to the given Application object. These
     * callbacks will be used to determine when a new Activity is created which leads to resolving
     * it's Dagger dependencies.
     *
     * @since 1.0.0
     * @see ActivityLifecycleCallbacks
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    @CallSuper
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        handleActivity(activity)
        setContext(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    @CallSuper
    override fun onActivityResumed(activity: Activity) {
        setContext(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
        //Determines if the given Fragment is part of the Dagger structure.
        if (f is Injectable) {
            AndroidSupportInjection.inject(f)
        }
    }

    private fun setContext(activity: Activity) {
        contextProvider.context = activity
    }

    /**
     * Handles the given Activity object which has been created recently. This step will resolve
     * the Dagger dependencies and attaches custom Fragment lifecycle callbacks to the Activity.
     *
     * @since 1.0.0
     * @see HasSupportFragmentInjector
     * @see FragmentLifecycleCallbacks
     */
    private fun handleActivity(activity: Activity) {
        //Determines if the given Activity is part of the Dagger structure.
        if (activity is Injectable || activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
        }
    }
}
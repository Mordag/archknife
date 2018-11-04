package archknife.extension

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import dagger.android.*
import javax.inject.Inject

/**
 * Abstract class which takes care of the initialising of the Dagger related components. Other than
 * the ArchknifeApplication, this class handles a generics for the AppInjector class. Therefore, you
 * can easily inject your own AppInjector into the dagger component initialisation.
 *
 * @since 1.0.0
 */
abstract class ArchknifeApplicationGen<A: AppInjector> : Application(), HasActivityInjector,
        HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var appInjector: A

    private lateinit var appComponent: Any

    override fun onCreate() {
        super.onCreate()

        val daggerClass = Class.forName(javaClass.`package`!!.name.toString() + ".DaggerArchknifeComponent")
        val daggerBuilderClass = Class.forName(javaClass.`package`!!.name.toString() + ".DaggerArchknifeComponent\$Builder")
        val appComponentClass = Class.forName(javaClass.`package`!!.name.toString() + ".ArchknifeComponent")

        var builder: Any = daggerClass.getMethod("builder").invoke(null)
        builder = daggerBuilderClass.getDeclaredMethod("application", Application::class.java).invoke(builder, this@ArchknifeApplicationGen)
        appComponent = daggerBuilderClass.getDeclaredMethod("build").invoke(builder)
        appComponentClass.getDeclaredMethod("inject", javaClass).invoke(appComponent, this)

        appInjector.init(this)
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T> getAppComponent(): T {
        return appComponent as T
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return serviceInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return broadcastReceiverInjector
    }
}
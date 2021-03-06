package archknife.extension

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * Abstract class which takes care of the initialising of the Dagger related components. Other than
 * the ArchknifeApplication, this class handles a generics for the AppInjector class. Therefore, you
 * can easily inject your own AppInjector into the dagger component initialisation.
 *
 * @since 1.0.0
 */
abstract class ArchknifeApplicationGen<A : AppInjector> : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var appInjector: A

    private var appComponent: Any? = null

    override fun onCreate() {
        super.onCreate()

        val daggerClass = Class.forName(javaClass.`package`!!.name.toString() + ".DaggerArchknifeComponent")
        val daggerBuilderClass = Class.forName(javaClass.`package`!!.name.toString() + ".DaggerArchknifeComponent\$Builder")
        val appComponentClass = Class.forName(javaClass.`package`!!.name.toString() + ".ArchknifeComponent")

        var builder: Any? = daggerClass.getMethod("builder").invoke(null)
        builder = daggerBuilderClass.getDeclaredMethod("application", Application::class.java).invoke(builder, this@ArchknifeApplicationGen)
        appComponent = daggerBuilderClass.getDeclaredMethod("build").invoke(builder)
        appComponentClass.getDeclaredMethod("inject", javaClass).invoke(appComponent, this)

        appInjector.init(this)
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T> getAppComponent(): T? {
        return appComponent as T?
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }
}
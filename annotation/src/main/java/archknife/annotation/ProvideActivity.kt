package archknife.annotation

/**
 * This annotation can be used to include the Activity to the underlying Dagger structure.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ProvideActivity
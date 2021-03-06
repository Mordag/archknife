package org.demo.archknife

import archknife.annotation.ProvideModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@ProvideModule
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideTestObject(): TestObject {
        return TestObject()
    }
}
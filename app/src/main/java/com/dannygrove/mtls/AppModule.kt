package com.dannygrove.mtls

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun providesMtlsDatabase(app: Application): MTLSDatabase {
    return Room.databaseBuilder(
      app,
      MTLSDatabase::class.java,
      MTLSDatabase.DATABASE_NAME,
    ).build()
  }

  @Provides
  @Singleton
  fun provideMtlsRepository(db: MTLSDatabase): MtlsRepository {
    return MtlsRepositoryImpl(db.serverDao)
  }

  @Provides
  @Singleton
  fun provideServerUseCases(repository: MtlsRepository): ServerUseCases {
    return ServerUseCases(
      getServer = GetServersUseCase(repository),
      deleteServer = DeleteServerUseCase(repository),
      addServer = AddServerUseCase(repository),
    )
  }
}

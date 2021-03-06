/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.splattrak.splatnet

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.network.DelegatingSocketFactory
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.splattrak.splatnet.service.Splatnet
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.net.SocketFactory
import kotlin.reflect.KClass
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

private const val SPLATNET_BASE_URL = "https://splatoon2.ink"
internal const val SPLATNET_API_URL = "${SPLATNET_BASE_URL}/"
internal const val SPLATNET_ASSET_URL = "${SPLATNET_BASE_URL}/assets/splatnet"

@Qualifier @Retention(AnnotationRetention.BINARY) internal annotation class InternalApi

private class OkHttpClientLazyCallFactory(context: Context, debug: Boolean) : Call.Factory {

  private val client by lazy {
    createOkHttpClient(context.applicationContext, debug, DelegatingSocketFactory.create())
  }

  override fun newCall(request: Request): Call {
    Enforcer.assertOffMainThread()
    return client.newCall(request)
  }

  companion object {

    @JvmStatic
    @CheckResult
    private fun createOkHttpClient(
        context: Context,
        debug: Boolean,
        socketFactory: SocketFactory,
    ): OkHttpClient {
      Enforcer.assertOffMainThread()

      // Cache up to 1MB of data
      val diskCache = Cache(context.applicationContext.cacheDir, 1_000_000L)

      return OkHttpClient.Builder()
          .cache(diskCache)
          .addNetworkInterceptor(AlwaysCachingInterceptor())
          .socketFactory(socketFactory)
          .apply {
            if (debug) {
              addInterceptor(
                  HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            }
          }
          .build()
    }
  }

  private class AlwaysCachingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
      val cacheControl = CacheControl.Builder().maxAge(6, TimeUnit.HOURS).build()

      // Don't cache failure responses
      val response = chain.proceed(chain.request())
      return if (!response.isSuccessful) {
        Timber.w("Do not cache failed responses")
        response
      } else {
        val cachingStrategy = response.header(CACHE_CONTROL_HEADER)
        // If it has a cache time, don't touch it
        if (cachingStrategy != CACHE_CONTROL_NO_CACHING) {
          Timber.d("Response includes cache header, do not touch")
          response
        } else {
          // But if it has no cache time, we cache it for a bit
          Timber.d("Cache response for time $cacheControl")
          response.newBuilder().header(CACHE_CONTROL_HEADER, cacheControl.toString()).build()
        }
      }
    }

    companion object {
      private const val CACHE_CONTROL_HEADER = "Cache-Control"
      private const val CACHE_CONTROL_NO_CACHING = "no-cache"
    }
  }
}

@Module
abstract class SplatnetModule {

  @Binds
  @CheckResult
  @InternalApi
  internal abstract fun bindNetworkInteractor(impl: SplatnetNetworkInteractor): SplatnetInteractor

  @Binds
  @CheckResult
  internal abstract fun bindInteractor(impl: SplatnetInteractorImpl): SplatnetInteractor

  @Module
  companion object {

    @JvmStatic
    @CheckResult
    private fun createMoshi(): Moshi {
      return Moshi.Builder().build()
    }

    @JvmStatic
    @CheckResult
    private fun createRetrofit(context: Context, debug: Boolean, moshi: Moshi): Retrofit {
      return Retrofit.Builder()
          .baseUrl(SPLATNET_API_URL)
          .callFactory(OkHttpClientLazyCallFactory(context.applicationContext, debug))
          .addConverterFactory(MoshiConverterFactory.create(moshi))
          .build()
    }

    @Provides
    @JvmStatic
    @InternalApi
    @CheckResult
    internal fun provideNetworkCreator(
        context: Context,
        @Named("debug") debug: Boolean,
    ): NetworkServiceCreator {
      // Don't inject these to avoid needing Dagger API in build.gradle
      val retrofit = createRetrofit(context.applicationContext, debug, createMoshi())
      return object : NetworkServiceCreator {

        override fun <T : Any> create(target: KClass<T>): T {
          return retrofit.create(target.java)
        }
      }
    }

    @Provides
    @JvmStatic
    @InternalApi
    @CheckResult
    internal fun provideSplatnetService(
        @InternalApi serviceCreator: NetworkServiceCreator
    ): Splatnet {
      return serviceCreator.create(Splatnet::class)
    }
  }
}

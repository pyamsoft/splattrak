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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.network.DelegatingSocketFactory
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.splattrak.splatnet.service.Splatnet
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.net.SocketFactory
import kotlin.reflect.KClass

private const val SPLATNET_BASE_URL = "https://splatoon2.ink"
internal const val SPLATNET_API_URL = "${SPLATNET_BASE_URL}/"
internal const val SPLATNET_ASSET_URL = "${SPLATNET_BASE_URL}/assets/splatnet"

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class InternalApi

private class OkHttpClientLazyCallFactory(debug: Boolean) : Call.Factory {

    private val client by lazy {
        createOkHttpClient(debug, DelegatingSocketFactory.create())
    }

    override fun newCall(request: Request): Call {
        Enforcer.assertOffMainThread()
        return client.newCall(request)
    }

    companion object {

        @JvmStatic
        @CheckResult
        private fun createInterceptor(): Interceptor {
            return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        @JvmStatic
        @CheckResult
        private fun createOkHttpClient(
            debug: Boolean,
            socketFactory: SocketFactory,
        ): OkHttpClient {
            Enforcer.assertOffMainThread()

            return OkHttpClient.Builder()
                .socketFactory(socketFactory)
                .apply {
                    if (debug) {
                        addInterceptor(createInterceptor())
                    }
                }
                .build()
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
            return Moshi.Builder()
                .build()
        }

        @JvmStatic
        @CheckResult
        private fun createRetrofit(debug: Boolean, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl(SPLATNET_API_URL)
                .callFactory(OkHttpClientLazyCallFactory(debug))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        @Provides
        @JvmStatic
        @InternalApi
        @CheckResult
        internal fun provideNetworkCreator(@Named("debug") debug: Boolean): NetworkServiceCreator {
            // Don't inject these to avoid needing Dagger API in build.gradle
            val retrofit = createRetrofit(debug, createMoshi())
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
        internal fun provideSplatnetService(@InternalApi serviceCreator: NetworkServiceCreator): Splatnet {
            return serviceCreator.create(Splatnet::class)
        }
    }
}
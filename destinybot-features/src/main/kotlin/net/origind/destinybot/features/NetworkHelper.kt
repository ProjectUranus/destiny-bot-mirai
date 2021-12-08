package net.origind.destinybot.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

val client: OkHttpClient = OkHttpClient.Builder()
    .cache(Cache(directory = File("web_cache"), maxSize = 10L * 1024L * 1024L))
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .followRedirects(true)
    .followSslRedirects(true)
    .callTimeout(10, TimeUnit.SECONDS)
    .build()


suspend inline fun getBodyAsync(url: String, crossinline init: Request.Builder.() -> Unit = {}) = coroutineScope {
    val request = Request.Builder().apply {
        url(url)
        init()
    }.build()
    val call = client.newCall(request)
    val response = async(Dispatchers.IO) { call.execute().body?.string() ?: "" }
    response
}

suspend inline fun <reified T> getJson(url: String, crossinline init: Request.Builder.() -> Unit = {}): T =
    moshi.adapter(T::class.java).fromJson(getBodyAsync(url, init).await())!!

package com.crotsertech.waterairandyoumvp.data.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

actual fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp) {
        engine {
            config {
                retryOnConnectionFailure(true)
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
                dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        return try {
                            Dns.SYSTEM.lookup(hostname)
                        } catch (_: UnknownHostException) {
                            if (hostname == "waterairandyoumvp.myusa.cloud") {
                                listOf(InetAddress.getByAddress(hostname, byteArrayOf(172.toByte(), 241.toByte(), 164.toByte(), 34.toByte())))
                            } else {
                                throw UnknownHostException("Unable to resolve host $hostname")
                            }
                        }
                    }
                })
            }
        }
        config()
    }

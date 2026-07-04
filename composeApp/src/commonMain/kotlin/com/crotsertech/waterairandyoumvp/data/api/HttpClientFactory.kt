package com.crotsertech.waterairandyoumvp.data.api

import io.ktor.client.*
import io.ktor.client.engine.*

expect fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient

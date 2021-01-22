package cn.ac.origind.minecraft

import com.github.steveice10.mc.auth.service.AuthenticationService
import okhttp3.HttpUrl.Companion.toHttpUrl

class AuthlibAuthenticationService(val endpoint: String) : AuthenticationService() {
    init {
        baseUri = endpoint.toHttpUrl().toUri()
    }
}

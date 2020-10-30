package cn.ac.origind.destinybot.debug

import cn.ac.origind.destinybot.groupLog
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Protocol
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

class LatencyEventListener : EventListener() {
    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        groupLog(ioe.localizedMessage)
    }
}

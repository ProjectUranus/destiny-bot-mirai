package net.origind.destinybot.features.instatus

import com.electronwill.nightconfig.core.Config

object InstatusAPI {
    var apiKey: String? = null

    fun reloadConfig(config: Config) {
        apiKey = config.get("instatus.apiKey")
    }
}

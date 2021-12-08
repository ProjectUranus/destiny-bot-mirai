package net.origind.destinybot.features.minecraft

data class LatestManifest(var release: String? = null, var snapshot: String? = null)
data class Version(var id: String? = null, var type: String? = null, var url: String? = null, var time: String? = null, var releaseTime: String? = null)

data class MinecraftVersionManifest(var latest: LatestManifest? = null, var versions: List<Version>? = null)

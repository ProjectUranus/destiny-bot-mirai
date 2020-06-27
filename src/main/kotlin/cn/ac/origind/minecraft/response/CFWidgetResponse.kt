package cn.ac.origind.minecraft.response

data class CFWidgetDownloads(val monthly: Int, val total: Long)
data class CFWidgetUrls(val curseforge: String, val project: String)

data class CFWidgetResponse(val id: Int, val title: String, val summary: String,
val type: String, val urls: CFWidgetUrls, val thumbnail: String, val downloads: CFWidgetDownloads, val categories: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CFWidgetResponse) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}

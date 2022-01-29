package net.origind.destinybot.core.util

import com.squareup.moshi.*
import net.mamoe.mirai.contact.announcement.Announcement
import net.mamoe.mirai.contact.announcement.AnnouncementParameters

object AnnouncementAdapter : JsonAdapter<Announcement>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Announcement? {
        TODO("Not yet implemented")
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Announcement?) {
        writer.name("content").value(value?.content)
        writer.name("parameters")
        AnnouncementParametersAdapter.toJson(writer, value?.parameters)
    }
}

object AnnouncementParametersAdapter : JsonAdapter<AnnouncementParameters>() {
    @FromJson
    override fun fromJson(reader: JsonReader): AnnouncementParameters? {
        return null
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: AnnouncementParameters?) {
        writer.beginObject()
        writer.name("isPinned").value(value?.isPinned)
        writer.name("requireConfirmation").value(value?.requireConfirmation)
        writer.name("sendToNewMember").value(value?.sendToNewMember)
        writer.name("showEditCard").value(value?.showEditCard)
        writer.name("showPopup").value(value?.showPopup)
        writer.endObject()
    }

}

package net.origind.destinybot.core.util

import com.squareup.moshi.*
import net.mamoe.mirai.contact.ContactOrBot
import net.origind.destinybot.core.DestinyBot

object ContactAdapter : JsonAdapter<ContactOrBot>() {
    @FromJson
    override fun fromJson(reader: JsonReader): ContactOrBot? {
        return DestinyBot.bot.getStranger(reader.nextLong())
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ContactOrBot?) {
        writer.value(value?.id)
    }
}

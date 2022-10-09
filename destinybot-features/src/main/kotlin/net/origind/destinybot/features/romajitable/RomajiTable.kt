package net.origind.destinybot.features.romajitable;

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class Romaji(val hiragana: String, val katakana: String)

object RomajiTable {
    val romajiTable: Map<String, Romaji>
    val katakanaTable: Map<String, String>
    val maxLen: Int

    init {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        romajiTable =
            moshi
                .adapter<Map<String, Romaji>>(Types.newParameterizedType(Map::class.java, String::class.java, Romaji::class.java))
                .fromJson(Romaji::class.java.getResource("/Romaji.json")!!.readText())!!

        maxLen = romajiTable.keys.maxOf { it.length }

        katakanaTable =
            moshi
                .adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java))
                .fromJson(Romaji::class.java.getResource("/Katakana.json")!!.readText())!!
    }

    fun romajiConvert(text: String): String {
        val romaji = text.lowercase()
        val katakana = StringBuilder()
        var pos = 0
        while (pos < romaji.length) {
            var length = maxLen
            if (romaji.length - pos < length)
                length = romaji.length - pos

            var found = false

            while (length > 0 && !found) {
                var lol_str = try { romaji.substring(pos, pos + length) } catch (e: IndexOutOfBoundsException) { null }
                val obj = lol_str?.let { romajiTable[it] }
                if (obj != null) {
                    katakana.append(obj.katakana)
                    pos += length
                    found = true
                }
                length -= 1
            }

            if (!found) {
                katakana.append(romaji[pos])
                pos += 1
            }
        }

        return katakana.toString()
    }
}


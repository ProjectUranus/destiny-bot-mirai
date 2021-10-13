package net.origind.destinybot.features

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.origind.destinybot.features.destiny.data.UserData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object DataStore {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val users = hashMapOf<Long, UserData>()
    private val writeOptions = arrayOf(StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    val logger: Logger = LoggerFactory.getLogger("DataStore")
    private val usersDirectory: Path = Paths.get("users").toAbsolutePath()

    operator fun get(id: Long) = users.getOrPut(id) { UserData(id) }

    operator fun contains(id: Long) = users.containsKey(id)

    suspend fun init() {
        if (Files.notExists(usersDirectory)) {
            coroutineScope {
                launch(Dispatchers.IO) {
                    Files.createDirectories(usersDirectory)
                    logger.info("Created users directory {}", usersDirectory.toString())
                }
            }
        }
        load()
    }

    suspend fun save() {
        coroutineScope {
            for ((id, user) in users) {
                launch(Dispatchers.IO) {
                    Files.writeString(
                        usersDirectory.resolve("$id.json"),
                        moshi.adapter(UserData::class.java).toJson(user),
                        *writeOptions
                    )
                }
            }
        }
        logger.info("Saved {} users", users.size)
    }

    suspend fun load() {
        users.clear()
        coroutineScope {
            launch(Dispatchers.IO) {
                Files.list(usersDirectory).forEach {
                    val user =
                        moshi.adapter(UserData::class.java).fromJson(Files.readString(it, StandardCharsets.UTF_8))
                    if (user != null)
                        users[user.qq] = user
                    else
                        logger.warn("Cannot read user file $it")
                }
                logger.info("Loaded {} users", users.size)
            }
        }
    }

    suspend fun reload() {
        save()
        load()
    }
}

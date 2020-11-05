package cn.ac.origind.destinybot.data

import cn.ac.origind.destinybot.mapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object DataStore {
    private val users = hashMapOf<Long, UserData>()
    private val writeOptions = arrayOf(StandardOpenOption.WRITE, StandardOpenOption.CREATE)
    val logger = LoggerFactory.getLogger("DataStore")
    val usersDir = Paths.get("users").toAbsolutePath()

    operator fun get(id: Long) = users.getOrPut(id) { UserData(id) }

    operator fun contains(id: Long) = users.containsKey(id)

    suspend fun init() = withContext(Dispatchers.IO) {
        if (Files.notExists(usersDir)) {
            Files.createDirectories(usersDir)
            logger.info("Created users directory {}", usersDir.toString())
        }

        load()
    }

    suspend fun save() {
        withContext(Dispatchers.IO) {
            for ((id, user) in users) {
                Files.write(
                    usersDir.resolve("$id.json"),
                    mapper.writeValueAsBytes(user),
                    *writeOptions
                )
            }
            logger.info("Saved {} users", users.size)
        }
    }

    suspend fun load() {
        users.clear()
        withContext(Dispatchers.IO) {
            Files.list(usersDir).forEach {
                val user = mapper.readValue<UserData>(it.toFile())
                users[user.qq] = user
            }
            logger.info("Loaded {} users", users.size)
        }
    }

    suspend fun reload() {
        save()
        load()
    }
}

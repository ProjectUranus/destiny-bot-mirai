
import com.squareup.moshi.Types
import kotlinx.coroutines.runBlocking
import net.origind.destinybot.features.bilibili.getLatestWeeklyReportURL
import net.origind.destinybot.features.destiny.image.getImage
import net.origind.destinybot.features.destiny.image.toByteArray
import net.origind.destinybot.features.getBodyAsync
import net.origind.destinybot.features.github.CommitInfo
import net.origind.destinybot.features.injdk.InjdkDistribution
import net.origind.destinybot.features.moshi
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test


class DestinyBotTest {
    @Test
    fun testGitHub() {
        runBlocking {
            val type = Types.newParameterizedType(List::class.java, CommitInfo::class.java)
            val infos = moshi.adapter<List<CommitInfo>>(type).fromJson(
                getBodyAsync("https://api.github.com/repos/TRKS-Team/WFBot/commits?per_page=1").await())!!
            assertFalse(infos.isEmpty())
        }
    }

    @Test
    fun testWeeklyReport() {
        runBlocking {
            println(getLatestWeeklyReportURL())
            assertNotNull(getImage("https:${getLatestWeeklyReportURL()}").toByteArray())
        }
    }

    @Test
    fun testInjdk() {
        runBlocking {
            val jdkHtml = getBodyAsync("https://www.injdk.cn/").await()
            val regex = Regex("""<a href="(https://d\d\.injdk\.cn/openjdk/(.+/)+(\d+)/(\w+/)?.+)">(.+)</a>""")
            println(regex.findAll(jdkHtml).map { it.groupValues }.map {
                var distro = it[2].removeSuffix("/")
                val ext = it[4].removeSuffix("/")
                if (ext == "openj9") distro = ext
                InjdkDistribution(it[1], distro, it[3], ext, it[5])
            }.toList())
        }
    }
}

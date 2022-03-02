
import com.squareup.moshi.Types
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.origind.destinybot.features.bilibili.*
import net.origind.destinybot.features.destiny.bungieUserToDestinyUser
import net.origind.destinybot.features.destiny.image.getImage
import net.origind.destinybot.features.destiny.image.toByteArray
import net.origind.destinybot.features.getBodyAsync
import net.origind.destinybot.features.github.CommitInfo
import net.origind.destinybot.features.injdk.InjdkCommand
import net.origind.destinybot.features.moshi
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors


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

    val COOKIE = """"""

    @Test
    fun testBilibili() {
        runBlocking {
            val id = searchUser("SourceForge").first().mid
            println(getUserInfo(15480779))
            println(sameFollow(COOKIE, 15480779))
        }
    }

    @Test
    fun testVdb() {
        runBlocking {
            VdbAPI.updateList()
            val pool = Executors.newFixedThreadPool(16)
            val executor = pool.asCoroutineDispatcher()

            val list = VdbAPI.vTuberList.vtbs.asSequence().flatMap { it.accounts }.filter { it.platform == "bilibili" }
                .drop(1440)
                .forEach {
                runBlocking {
                    val response = follow("",
                        COOKIE,it.id)
                    if (response.code == 0) {
                        println("Followed ${it.id}")
                    } else if (response.code == 22013) {
                        println(response.message)
                    }
                    else {
                        throw Exception("Cannot follow ${it.id}: " + response)
                    }
                    delay(100)
                }
            }
        }
    }

    @Test
    fun testBungie() {
        runBlocking {
            println(bungieUserToDestinyUser("Strelizia", 5916))
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
            InjdkCommand.reload()
            println(InjdkCommand.jreDistro)
            println(InjdkCommand.jdkDistro)
        }
    }
}


import kotlinx.coroutines.runBlocking
import net.origind.destinybot.features.getJson
import net.origind.destinybot.features.github.CommitInfo
import org.junit.jupiter.api.Test

class DestinyBotTest {
    @Test
    fun testGitHub() {
        runBlocking {
            val infos = getJson<List<CommitInfo>>("https://api.github.com/repos/TRKS-Team/WFBot/commits?per_page=1")
            println(infos)
        }
    }
}

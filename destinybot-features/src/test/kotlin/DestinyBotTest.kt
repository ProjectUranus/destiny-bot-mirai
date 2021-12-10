
import com.squareup.moshi.Types
import kotlinx.coroutines.runBlocking
import net.origind.destinybot.features.getBodyAsync
import net.origind.destinybot.features.github.CommitInfo
import net.origind.destinybot.features.moshi
import org.junit.jupiter.api.Test

class DestinyBotTest {
    @Test
    fun testGitHub() {
        runBlocking {
            val type = Types.newParameterizedType(List::class.java, CommitInfo::class.java)
            val infos = moshi.adapter<List<CommitInfo>>(type).fromJson(
                getBodyAsync("https://api.github.com/repos/TRKS-Team/WFBot/commits?per_page=1").await())!!
            println(infos)
        }
    }
}


import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class TimerTest {
    @Test
    fun testTimedTask() {
        val runner = Executors.newFixedThreadPool(8).asCoroutineDispatcher()
        val scope = CoroutineScope(runner)
        scope.launch {
            while(true) {
                println("Task 1")
                delay(500)
            }
        }
        scope.launch {
            while(true) {
                println("Task 2")
                delay(2000)
            }
        }

        runBlocking {
            delay(6000)
            scope.cancel()
            println("Tasks cancelled")
            delay(6000)
        }
    }
}

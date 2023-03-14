import kotlinx.coroutines.runBlocking
import net.origind.destinybot.api.command.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TestCommandParsing {

	companion object {
		var gotSelfId: Long? = null
	}

	object KickCommand : AbstractCommand("/kick") {
		init {
			permission = "op.kick"
			arguments += ArgumentContext("id", QQArgument)
		}

		override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
			// 储存获取到的参数数据
			gotSelfId =  argument.getArgument("id")
		}
	}

	@Test
	fun testQQArgumentsParsingWithContext() = runBlocking {

		val command = "/kick self"
		val senderId = 999_999_999L
		val subjectId = 100_000_000L

		// 模拟执行命令

		// 配置数据
		val parser = CommandParser(command)
		val context = CommandContext(
			senderId = senderId,
			subjectId = subjectId,
			message = "This is a test message",
			time = System.currentTimeMillis()
		)

		// 移除指令名称
		parser.take()

		// 解析并执行
		KickCommand.parse(parser, ConsoleCommandExecutor, context)

		// 比较数据
		assertEquals(senderId, gotSelfId)
	}

	@Test
	fun testQQArgumentsParsingWithoutContext() = runBlocking {
		val command = "/kick @123456789"
		val senderId = 999_999_999L
		val subjectId = 100_000_000L

		// 模拟执行命令

		// 配置数据
		val parser = CommandParser(command)
		val context = CommandContext(
			senderId = senderId,
			subjectId = subjectId,
			message = "This is a test message",
			time = System.currentTimeMillis()
		)

		// 移除指令名称
		parser.take()

		// 解析并执行
		KickCommand.parse(parser, ConsoleCommandExecutor, context)

		// 比较数据
		assertEquals(123456789L, gotSelfId)
	}

}
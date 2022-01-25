package net.origind.destinybot.features.injdk

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.origind.destinybot.api.command.*
import net.origind.destinybot.features.getBodyAsync

object InjdkCommand: AbstractCommand("injdk") {
    var jreDistro = mapOf<String, List<InjdkDistribution>>()
    var jdkDistro = mapOf<String, List<InjdkDistribution>>()

    private val jdkRegex = Regex("""<a href="(https://d\d\.injdk\.cn/(\w+)/(.+/)*(\d+)/(\w+/)?.+)">(.+)</a>""")
    private val jreRegex = Regex("""<a href="(https://d\d\.injdk\.cn/(\w+)/jre/(.+/)*(\d+)?/(\w+/)?.+)"""")

    override val aliases: List<String> = listOf("java下载")

    val translateMap = mapOf("adoptopenjdk" to "AdoptOpenJDK", "jdk" to "OpenJDK", "redhat" to "Red Hat", "sapmachine" to "SAP SE", "openjdk" to "OpenJDK", "zulu" to "Zulu", "liberica" to "Liberica", "amazon" to "Amazon", "mc" to "Microsoft", "openj9" to "IBM OpenJ9", "oracle" to "Oracle")

    init {
        arguments += ArgumentContext("version", IntArgument, true)
        arguments += ArgumentContext("distro", StringArgument, true)
        arguments += ArgumentContext("jdk", StringArgument, true)
    }

    override suspend fun init() {
        reload()
    }

    override fun getHelp(): String = buildString {
        appendLine("提供最新的 Java 下载。")
        appendLine("用法：<版本> [提供商] [jre|jdk]")
        appendLine()
        appendLine("示例：injdk 8 - 提供 OpenJDK 1.8 下载")
        appendLine("示例：injdk 17 zulu jdk - 提供 Zulu 17 JDK 下载")
    }

    override suspend fun execute(argument: ArgumentContainer, executor: CommandExecutor, context: CommandContext) {
        if (argument.hasArgument("version")) {
            var company = if (argument.hasArgument("distro")) argument.getArgument("distro") else "openjdk"

            if (company == "adopt") {
                company = "adoptopenjdk"
            } else if (company == "jdk") {
                company = "openjdk"
            }

            val jdk = if (argument.hasArgument("jdk")) argument.getArgument("jdk") else "jre"
            if (company !in translateMap) executor.sendMessage("未知提供商 $company，可用提供商：${translateMap.keys.joinToString()}")
            if (jdk != "jre" && jdk != "jdk") executor.sendMessage("请选择 JDK 或 JRE 其中之一。")
            var version = argument.getArgument<Int>("version").toString()
            if (version == "1.8") version = "8"
            val distros = (if (jdk == "jdk") jdkDistro else jreDistro)[company] ?: emptyList()
            if (distros.isEmpty()) {
                executor.sendMessage("未找到这样的发行版。")
            } else {
                val distro = distros.filter { it.version == version }
                if (distro.isNotEmpty()) {
                    executor.sendMessage(buildString {
                        appendLine("${translateMap[company]} Java $version 下载：")
                        appendLine("Windows x64：${distro.find { it.program.contains("win") && it.program.contains("64") && it.program.contains("msi") }?.url ?: distro.find { it.program.contains("win") && it.program.contains("64") && it.program.contains("zip") }?.url}")

                        distro.find { it.program.contains("win") && it.program.contains("86") && it.program.contains("msi") }?.url ?: distro.find { it.program.contains("win") && it.program.contains("86") && it.program.contains("zip") }
                            ?.url
                            ?.let { appendLine("Windows x32：$it") }
                    })
                }
            }
        } else {
            executor.sendMessage(getHelp())
        }
    }

    suspend fun reload() {
        coroutineScope {
            launch {
                val jdkHtml = getBodyAsync("https://www.injdk.cn/").await()
                jdkDistro = jdkRegex.findAll(jdkHtml).map { it.groupValues }.map {
                    val group = it[2]
                    var distro = it[3].removeSuffix("/")
                    val ext = it[5].removeSuffix("/")
                    if (ext == "openj9") distro = ext
                    if (group == "oraclejdk") distro = "oracle"
                    InjdkDistribution(it[1], distro, it[4], ext, it[6])
                }.groupBy { it.company }
                println("JDK Distributions loaded")
            }
            launch {
                val jreHtml = getBodyAsync("https://d2.injdk.cn/jre.html").await()
                jreDistro = jreRegex.findAll(jreHtml).map { it.groupValues }.map {
                    val group = it[2]
                    var distro = it[3].removeSuffix("/")
                    val ext = it[5].removeSuffix("/")
                    if (ext == "openj9") distro = ext
                    if (group == "oraclejdk") distro = "oracle"
                    InjdkDistribution(it[1], distro, it[4], ext, it[1].substringAfterLast('/'))
                }.groupBy { it.company }
                println("JRE Distributions loaded")
            }
        }
    }
}
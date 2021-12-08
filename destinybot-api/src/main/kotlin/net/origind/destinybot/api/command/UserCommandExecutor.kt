package net.origind.destinybot.api.command

abstract class UserCommandExecutor : CommandExecutor {
    abstract fun groupContains(qq: Long): Boolean

    abstract fun sendImage(image: ByteArray)

    abstract fun sendPrivateImage(image: ByteArray)
}

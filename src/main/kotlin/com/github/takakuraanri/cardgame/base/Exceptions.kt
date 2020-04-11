package com.github.takakuraanri.cardgame.base

import net.mamoe.mirai.message.data.Message

class PlayerNotEnoughException: Exception("not enough players")

class PlayerFullException: Exception("players are full")

class RuleException(val msg: Message): Exception(msg.toString())

class PassException(): Exception()

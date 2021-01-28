package cn.ac.origind.destinybot.command

import cn.ac.origind.command.CommandParser
import cn.ac.origind.command.DoubleArgument
import cn.ac.origind.command.IntArgument
import cn.ac.origind.command.StringArgument
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class TestCommandParser {
    @Test
    fun test() {
        var parser = CommandParser(" hh 123 u 64.0   ")
        assertEquals(parser.take(), "hh")
        assertEquals(parser.take(), "123")
        assertEquals(parser.take(), "u")
        assertEquals(parser.take(), "64.0")
        try {
            parser.take()
            fail("Parser shouldn't take more")
        } catch (e: IndexOutOfBoundsException) {
        }

        parser = CommandParser(" hh 123 u 64.0   ")
        assertEquals("hh", parser.parse(StringArgument))
        assertEquals(123, parser.parse(IntArgument))
        assertEquals("u", parser.parse(StringArgument))
        assertEquals(64.0, parser.parse(DoubleArgument), 0.001)
    }
}

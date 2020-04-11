package com.github.takakuraanri.cardgame.base

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class UtilsKtTest {
    @Test
    @DisplayName("Continuous card detection")
    fun testContinuous() {
        assertTrue(listOf(Cards.CARD_3, Cards.CARD_4, Cards.CARD_5).isContinuously())
        assertFalse(listOf(Cards.CARD_2, Cards.CARD_3, Cards.CARD_4, Cards.CARD_5).isContinuously())
    }
}

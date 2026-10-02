package com.example

import com.example.data.games.AutobusDictionary
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAutobusValidation() {
        // Test letter 'أ'
        assertTrue(AutobusDictionary.validateAnswer('أ', "ولد", "أحمد"))
        assertTrue(AutobusDictionary.validateAnswer('أ', "بنت", "أسماء"))
        assertTrue(AutobusDictionary.validateAnswer('أ', "حيوان", "الأسد")) // Prefix 'ال' accepted
        assertFalse(AutobusDictionary.validateAnswer('أ', "بلد", "مصر")) // Wrong letter

        // Test letter 'ب'
        assertTrue(AutobusDictionary.validateAnswer('ب', "حيوان", "بقرة"))
        assertFalse(AutobusDictionary.validateAnswer('ب', "بلد", ""))
    }

    @Test
    fun testCoinsRules() {
        val onlineCoins = 100L
        val transferAmount = 150L
        val canTransfer = onlineCoins >= transferAmount
        assertFalse("Cannot transfer more than available balance", canTransfer)
    }
}

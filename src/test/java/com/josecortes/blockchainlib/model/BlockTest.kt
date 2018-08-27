package com.josecortes.blockchainlib.model

import com.josecortes.blockchainlib.transactions.Transaction
import com.josecortes.blockchainlib.utils.sha512
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BlockTest {
    @Before
    fun setUp() {

    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `create a valid sha512 key`() {
        val block = Block("data")
        var transaction = Transaction()
        transaction.transactionId = "1234"
        block.addTransaction(transaction)
        block.previousHash = "1234"
        Assert.assertTrue(block.calculateBlockHash() == ("1234" + "data" + "0" + transaction.transactionId).sha512())
    }

    @Test
    fun `validate Block hashable information`() {
        val block = Block("data")
        var transaction = Transaction()
        transaction.transactionId = "1234"
        block.addTransaction(transaction)
        block.previousHash = "1234"
        Assert.assertTrue(block.getBlockInfo() == ("1234" + "data" + "0" + transaction.transactionId))
    }

}
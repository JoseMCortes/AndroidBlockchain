package com.josecortes.blockchainlib.transactions

import android.util.Base64
import com.josecortes.blockchainlib.crypto.CryptoUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.security.KeyPair

@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
class TransactionsTest {

    lateinit var transaction: Transaction
    lateinit var sender: KeyPair
    lateinit var receiver: KeyPair

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        PowerMockito.mockStatic(Base64::class.java)
        `when`(Base64.encodeToString(any(), anyInt())).thenAnswer({ invocation -> java.util.Base64.getEncoder().encodeToString(invocation.getArguments()[0] as ByteArray) })

        sender = CryptoUtils.generateKeyPair()!!
        receiver = CryptoUtils.generateKeyPair()!!
        transaction = Transaction(sender.public, receiver.public, 10.0f)
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `calculate valid Transaction hash`() {
        val time = System.currentTimeMillis()
        val hash = transaction.calculateTransactionHash(time)
        Assert.assertNotNull(hash)
    }

    @Test
    fun `generate and check valid signature`() {
        transaction.generateSignature(sender.private)
        Assert.assertTrue(transaction.verifySignature())
    }

    @Test
    fun `generate and check tampered data invalidates signature`() {
        transaction.generateSignature(sender.private)
        transaction.amount = 30.0f
        Assert.assertFalse(transaction.verifySignature())
    }

}
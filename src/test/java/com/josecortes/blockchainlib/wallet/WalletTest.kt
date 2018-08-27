package com.josecortes.blockchainlib.wallet

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class WalletTest {

    lateinit var wallet: Wallet

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        wallet = Wallet()
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `create Public and Private wallet keys`() {
        wallet.generateKeyPair()
        Assert.assertNotNull(wallet.publicKey)
        Assert.assertNotNull(wallet.privateKey)
    }

}
package com.josecortes.blockchainlib.blockchain

import android.util.Base64
import com.josecortes.blockchainlib.crypto.CryptoUtils
import com.josecortes.blockchainlib.mining.Miner
import com.josecortes.blockchainlib.model.Block
import com.josecortes.blockchainlib.transactions.Transaction
import com.josecortes.blockchainlib.wallet.Wallet
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.security.KeyPair

@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
class BlockChainTest {
    lateinit var miner: Miner
    lateinit var mBlockChain: NeuralChain
    lateinit var transaction: Transaction
    lateinit var sender: KeyPair
    lateinit var receiver: KeyPair
    lateinit var transactionInput: Transaction.TransactionInput
    lateinit var smallTransactionOutput: Transaction.TransactionOutput
    lateinit var bigTransactionOutput: Transaction.TransactionOutput
    lateinit var wallet: Wallet

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        PowerMockito.mockStatic(Base64::class.java)
        PowerMockito.`when`(Base64.encodeToString(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenAnswer({ invocation -> java.util.Base64.getEncoder().encodeToString(invocation.getArguments()[0] as ByteArray) })


        miner = Mockito.mock(Miner::class.java)
        mBlockChain = NeuralChain(miner)

        sender = CryptoUtils.generateKeyPair()!!
        receiver = CryptoUtils.generateKeyPair()!!
        transaction = Transaction(sender.public, receiver.public, 10.0f)
        wallet = Wallet()
        wallet.generateKeyPair()

        transactionInput = Transaction.TransactionInput("1234")
        smallTransactionOutput = Transaction.TransactionOutput(receiver.public, 0.01f, "1234")
        bigTransactionOutput = Transaction.TransactionOutput(receiver.public, 20.0f, "1234")

    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `add a new Block`() {
        val block = Block("data")
        mBlockChain.addBlock(block)
        Assert.assertTrue(block.currentHash == block.calculateBlockHash())
        Assert.assertTrue(mBlockChain.blocks[0] == block)
    }

    @Test
    fun `validate chain with empty Blocks`() {
        Assert.assertTrue(mBlockChain.validateChain())
    }

    @Test
    fun `validate chain with valid Blocks`() {

        Mockito.`when`(miner.isValidBlock(ArgumentMatchers.anyString())).thenReturn(true)
        val block1 = Block("data1")
        val block2 = Block("data2")
        val block3 = Block("data3")

        mBlockChain.addBlock(block1)
        mBlockChain.addBlock(block2)
        mBlockChain.addBlock(block3)

        Assert.assertTrue(mBlockChain.blocks.size == 3)
        Assert.assertTrue(mBlockChain.validateChain())

    }

    @Test
    fun `validate chain with manipulated current Block data`() {
        val block1 = Block("data1")
        val block2 = Block("data2")
        val block3 = Block("data3")

        mBlockChain.addBlock(block1)
        mBlockChain.addBlock(block2)
        mBlockChain.addBlock(block3)

        block3.currentHash = "manipulated"

        Assert.assertTrue(mBlockChain.blocks.size == 3)
        Assert.assertFalse(mBlockChain.validateChain())
    }

    @Test
    fun `validate chain with manipulated previous Block data`() {
        val block1 = Block("data1")
        val block2 = Block("data2")
        val block3 = Block("data3")

        val manipulatedBlock3 = block2.copy()


        mBlockChain.addBlock(block1)
        mBlockChain.addBlock(block2)
        mBlockChain.addBlock(block3)

        mBlockChain.blocks[1] = manipulatedBlock3

        Assert.assertTrue(mBlockChain.blocks.size == 3)
        Assert.assertFalse(mBlockChain.validateChain())
    }

    @Test
    fun `process invalid tampered transaction`() {
        transaction.generateSignature(sender.private)
        transaction.amount = 30.0f
        Assert.assertFalse(mBlockChain.processTransaction(transaction))

    }

    @Test
    fun `process transaction without previous inputs`() {
        transaction.generateSignature(sender.private)
        Assert.assertFalse(mBlockChain.processTransaction(transaction))
    }

    @Test
    fun `process transaction without minimum amount`() {
        transaction.inputs = ArrayList()
        transactionInput.transactionOutputId = smallTransactionOutput.id
        transaction.inputs.add(transactionInput)

        mBlockChain.mapUtxo[smallTransactionOutput.id] = smallTransactionOutput
        transaction.generateSignature(sender.private)
        Assert.assertFalse(mBlockChain.processTransaction(transaction))
    }

    @Test
    fun `process transaction with minimum amount`() {
        transaction.inputs = ArrayList()
        transactionInput.transactionOutputId = bigTransactionOutput.id
        transaction.inputs.add(transactionInput)

        mBlockChain.mapUtxo[bigTransactionOutput.id] = bigTransactionOutput
        transaction.generateSignature(sender.private)
        Assert.assertTrue(mBlockChain.processTransaction(transaction))
        Assert.assertEquals(transaction.outputs.size, 2)
        Assert.assertTrue(mBlockChain.mapUtxo.size == 2)
    }

    @Test
    fun `get Balance for Wallet without entries`() {
        Assert.assertEquals(mBlockChain.getBalanceForWallet(wallet.publicKey!!), 0.0f)
    }

    @Test
    fun `get Balance for Wallet with entries`() {
        bigTransactionOutput.receiver = wallet.publicKey!!
        mBlockChain.mapUtxo[bigTransactionOutput.id] = bigTransactionOutput

        Assert.assertEquals(mBlockChain.getBalanceForWallet(wallet.publicKey!!), 20.0f)
    }

    @Test
    fun `send funds without enough Wallet balance`() {
        Assert.assertNull(mBlockChain.sendFunds(wallet, receiver.public, 10.0f))
    }

    @Test
    fun `send funds with enough Wallet balance`() {
        bigTransactionOutput.receiver = wallet.publicKey!!
        mBlockChain.mapUtxo[bigTransactionOutput.id] = bigTransactionOutput
        val transaction = mBlockChain.sendFunds(wallet, receiver.public, 10.0f)
        Assert.assertNotNull(transaction)
        Assert.assertEquals(transaction!!.amount, 10.0f)
        Assert.assertEquals(transaction.sender, wallet.publicKey)
        Assert.assertEquals(transaction.receiver, receiver.public)
    }

}
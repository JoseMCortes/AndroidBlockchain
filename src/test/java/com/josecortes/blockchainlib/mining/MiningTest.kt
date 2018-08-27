package com.josecortes.blockchainlib.mining

import com.josecortes.blockchainlib.model.Block
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class MiningTest {

    lateinit var minerFindZeros: Miner
    lateinit var minerStartsWith: Miner
    lateinit var block: Block

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        minerFindZeros = Miner(Miner.Algorithm.ALGORITHM_FIND_ZEROS, "2")
        minerStartsWith = Miner(Miner.Algorithm.ALGORITHM_STARTS_WITH, "ab")
        block = Block("1234")
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `calculate target for zeros`() {
        Assert.assertEquals(minerFindZeros.calculateTarget(), "00")
    }

    @Test
    fun `calculate target for header`() {
        Assert.assertEquals(minerStartsWith.calculateTarget(), "ab")
    }

    @Test
    fun `mine block searching for zeros`() {
        val foundHash = minerFindZeros.mineBlock(block)
        Assert.assertNotNull(foundHash)
        Assert.assertTrue(foundHash!!.startsWith("00"))
    }

    @Test
    fun `mine block searching for initial header`() {
        val foundHash = minerStartsWith.mineBlock(block)
        Assert.assertNotNull(foundHash)
        Assert.assertTrue(foundHash!!.startsWith("ab"))
    }

}
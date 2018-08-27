package com.josecortes.blockchainlib.mining

import android.support.annotation.WorkerThread
import com.josecortes.blockchainlib.model.Block
import javax.inject.Inject

open class Miner @Inject constructor(val algorithm: Algorithm, val algorithmParams: String) {

    enum class Algorithm { ALGORITHM_FIND_ZEROS, ALGORITHM_STARTS_WITH }

    /**
     * Given a Block and an Algorithm, finds a nonce that satisfied the Algorithm with the
     * parameters set in @param
     *
     * @param block The Block to mine
     *
     * @return the hash matching the algorithm criteria
     *
     * Warning! This method can takes a huge amount of computational effort depending on the parameters, so
     * never execute it on the UI Thread.
     */
    @WorkerThread
    fun mineBlock(block: Block): String? {
        val target = calculateTarget()
        return when (algorithm) {
            Algorithm.ALGORITHM_FIND_ZEROS -> mineZeros(block, target)
            Algorithm.ALGORITHM_STARTS_WITH -> mineStartsWith(block, target)
        }
    }

    /**
     * Uses the algorithm state to perform the search of the right hash
     */
    fun calculateTarget(): String {
        return when (algorithm) {
            Algorithm.ALGORITHM_FIND_ZEROS -> String(CharArray(algorithmParams.toInt()))
                    .replace('\u0000', '0')
            Algorithm.ALGORITHM_STARTS_WITH -> algorithmParams
        }
    }

    /**
     * Given a Block, finds a hash matching the specified numbers of 0s
     *
     * @param block the Block to mine.
     * @param target the num of 0s header to find ("000", "0000"...)
     */
    fun mineZeros(block: Block, target: String): String {
        return mineStartsWith(block, target)
    }

    /**
     * Given a Block, finds a hash that starts with the specified target
     *
     * @param block the Block to mine.
     * @param target the num of 0s header to find ("000", "abc"...)
     */
    fun mineStartsWith(block: Block, target: String): String {
        var resolvedHash: String? = null

        do {
            block.nonce++
            resolvedHash = block.calculateBlockHash()

        } while (!isValidBlock(resolvedHash!!, target))

        return resolvedHash
    }

    /**
     * Given a Block, validates if matches the Algorithm criteria
     *
     * @param hash The mined hash
     * @param target The target to match
     *
     * @return true if the Hash is a valid one, false otherwise
     */
    fun isValidBlock(hash: String, target: String): Boolean {
        return hash.startsWith(target)
    }

    open fun isValidBlock(hash: String?): Boolean {
        if (hash == null)
            return true
        return isValidBlock(hash, calculateTarget())
    }
}
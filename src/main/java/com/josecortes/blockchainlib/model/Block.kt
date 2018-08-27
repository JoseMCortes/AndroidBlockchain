package com.josecortes.blockchainlib.model

import com.josecortes.blockchainlib.transactions.Transaction
import com.josecortes.blockchainlib.utils.sha512
import java.util.*

data class Block constructor(val data: String) {

    // Hash of the previous Block
    var previousHash: String? = null
    // Hash of the current Block
    var currentHash: String? = null
    // Nonce used to generated a valid data hash
    var nonce: Int = 0
    // MerkleTree containing the Hash of the transactions.@See <a href="https://bitcoin.org/en/glossary/merkle-tree"</a>
    var transactionsTree: MerkleTree? = null
    // Simple list of transactions
    var transactionsList: ArrayList<Transaction>? = null

    /**
     * Given a block, retrieves its info and calculates the hash
     */
    fun calculateBlockHash(): String {
        return getBlockInfo().sha512()
    }

    /**
     * Returns the Hashable information
     */
    fun getBlockInfo(): String {
        val transactionsData = transactionsTree?.data ?: ""
        return previousHash + data + nonce.toString() + transactionsData
    }

    /**
     * Adds a Transaction to the block (updates the internal references to Transaction)
     */
    fun addTransaction(transaction: Transaction) {
        if (transactionsList == null) {
            transactionsList = ArrayList()
        }

        if (transactionsTree == null)
            transactionsTree = MerkleTree(null)

        transactionsList!!.add(transaction)
        transactionsTree!!.addItemInBfs(transaction.transactionId!!)
    }


}
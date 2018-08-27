package com.josecortes.blockchainlib.blockchain

import com.josecortes.blockchainlib.mining.Miner
import com.josecortes.blockchainlib.model.Block
import com.josecortes.blockchainlib.transactions.Transaction
import com.josecortes.blockchainlib.transactions.Transaction.TransactionInput
import com.josecortes.blockchainlib.wallet.Wallet
import java.security.PublicKey
import javax.inject.Inject

class NeuralChain @Inject constructor(val miner: Miner) {
    private var minimumTransaction = 0.1f
    // Unspended transactions. @see <a href="https://bitcoin.org/en/glossary/unspent-transaction-output</a>
    var mapUtxo = HashMap<String, Transaction.TransactionOutput>()
    var blocks = ArrayList<Block>()

    /**
     * Given a transaction, validates it against the "business rules" of the chain. If success,
     * creates the output transactions and updates the list of UTXOs
     *
     * @param transaction the transaction to process into the Blockchain
     */
    fun processTransaction(transaction: Transaction): Boolean {
        if (!transaction.verifySignature()) {
            // Transaction signature invalid, some info has been manipulated.
            return false
        }

        for (i in transaction.inputs) {
            i.UTXO = mapUtxo[i.transactionOutputId]
        }

        if (transaction.getInputsValue() < minimumTransaction) {
            // Transaction doesn't meet the minimum transaction rule
            return false
        }

        // Generate transactions output and the remaining "change" back to the sender.
        val leftOver = transaction.getInputsValue() - transaction.amount!! //get value of inputs then the left over change:
        transaction.transactionId = transaction.calculateTransactionHash(System.currentTimeMillis())
        transaction.outputs.add(Transaction.TransactionOutput(transaction.receiver!!, transaction.amount!!, transaction.transactionId!!)) //send value to recipient
        transaction.outputs.add(Transaction.TransactionOutput(transaction.sender!!, leftOver, transaction.transactionId!!)) //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (o in transaction.outputs) {
            mapUtxo[o.id] = o
        }

        // Updates transactions input
        for (i in transaction.inputs) {
            if (i.UTXO == null) continue
            mapUtxo.remove(i.UTXO!!.id)
        }

        return true
    }

    /**
     * Given a public key wallet, returns the balance for the current Blockchain instance.
     * Note: it could be possible to deploy multiple instances, as Ethereum does with Ropsten, Kovan
     * and Rinkeby
     *
     * @param publicKey The public key of the Wallet
     */
    fun getBalanceForWallet(publicKey: PublicKey): Float {
        var total = 0f
        for (item in mapUtxo.entries) {
            val utxo = item.value
            if (utxo.belongsToWallet(publicKey)) { //if output belongs to me ( if coins belong to me )
                mapUtxo[utxo.id] = utxo //add it to our list of unspent transactions.
                total += utxo.amount
            }
        }
        return total
    }


    /**
     * Given a sender Wallet, a recipient and an amount, creates a Transaction representing
     * the sending of the money to that account.
     *
     * Note that sender needs to provide the whole Wallet information (Public and Private keys) in
     * orde to create and sign the transaction.
     *
     * @param sender The sender Wallet containing the basic information to sign the transaction
     * @param recipient Recipient public key
     * @param value The amount to send, if the sender has enough funds.
     */
    fun sendFunds(sender: Wallet, recipient: PublicKey, value: Float): Transaction? {
        if (getBalanceForWallet(sender.publicKey!!) < value) {
            // Not enough funds!
            return null
        }

        // Creates the list of Transaction inputs
        val inputs = ArrayList<TransactionInput>()

        var total = 0f
        for (utxo in sender.UTXOs.values) {
            total += utxo.amount
            inputs.add(TransactionInput(utxo.id))
            if (total > value) break
        }

        val newTransaction = Transaction(sender.publicKey, recipient, value)
        newTransaction.inputs = inputs
        newTransaction.generateSignature(sender.privateKey!!)

        for (input in inputs) {
            sender.UTXOs.remove(input.transactionOutputId)
        }
        return newTransaction
    }

    /**
     * Adds a new Block into the Blockchain, updating the hashes chain
     *
     * @param block The Block to add
     */
    fun addBlock(block: Block) {
        val lastExistingHash = if (blocks.isEmpty()) null else blocks.last().currentHash
        block.previousHash = lastExistingHash
        block.currentHash = block.calculateBlockHash()
        blocks.add(block)
    }

    /**
     * Validates the good standing of the chain.
     * @return true if valid, false otherwise
     */
    fun validateChain(): Boolean {

        for (i in 1 until blocks.size) {
            val currentBlock = blocks[i]
            val previousBlock = blocks[i - 1]

            System.out.println(currentBlock.currentHash + " " + currentBlock.calculateBlockHash())
            if (currentBlock.currentHash != currentBlock.calculateBlockHash()) {
                return false
            }

            if (currentBlock.previousHash != previousBlock.currentHash) {
                return false
            }

            if (!miner.isValidBlock(currentBlock.currentHash)) {
                return false
            }

        }

        return true
    }

    /**
     * Given a mined Block, adds the Transaction to that Block.
     *
     * @param transaction the transaction to add into the Block
     * @param block The Block to add
     */
    fun addTransaction(transaction: Transaction?, block: Block): Boolean {
        if (transaction == null) return false
        if (block.previousHash !== "0") {
            if (!processTransaction(transaction)) {
                println("Transaction failed to process. Discarded.")
                return false
            }
        }
        block.addTransaction(transaction)

        return true
    }

}
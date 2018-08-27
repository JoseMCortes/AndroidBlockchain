package com.josecortes.blockchainlib.transactions

import com.josecortes.blockchainlib.crypto.CryptoUtils
import com.josecortes.blockchainlib.utils.sha512
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Transaction representing the sending of an amount from an Account to another one, following a simplified
 * version of @see <a href="https://en.bitcoin.it/wiki/Transaction"</a>
 */
class Transaction constructor(var sender: PublicKey? = null,
                              var receiver: PublicKey? = null,
                              var amount: Float? = null) {

    var transactionId: String? = null // Transaction hash (sha-512)
    var signature: ByteArray? = null // Signature of the data in the transaction
    var inputs = ArrayList<TransactionInput>()
    var outputs = ArrayList<TransactionOutput>()

    /**
     * Calculates the transaction Hash using the data of the Block.
     *
     * @param timestamp the Timestamp of the callee
     */
    fun calculateTransactionHash(timestamp: Long): String {
        val data = CryptoUtils.getStringFromKey(sender!!) +
                CryptoUtils.getStringFromKey(receiver!!) +
                amount.toString() +
                timestamp.toString()

        return data.sha512()
    }

    /**
     * Generates a signature with all the sensitive data that should never be tampered.
     *
     * @param privateKey The private Key of the sender account
     */
    fun generateSignature(privateKey: PrivateKey) {
        val data = CryptoUtils.getStringFromKey(sender!!) + CryptoUtils.getStringFromKey(receiver!!) + java.lang.Float.toString(amount!!)
        signature = CryptoUtils.applyECDSASig(privateKey, data)
    }

    /**
     * Verifies that the data of the transaction has been properly signed with the signature.
     *
     * @return true if verified correctly, false otherwise
     */
    fun verifySignature(): Boolean {
        val data = CryptoUtils.getStringFromKey(sender!!) + CryptoUtils.getStringFromKey(receiver!!) + java.lang.Float.toString(amount!!)
        return CryptoUtils.verifyECDSASig(sender!!, data, signature!!)
    }

    /**
     * Returns the sum amount of all the inputs
     *
     * @return the total amount of the inputs for this Transaction
     */
    fun getInputsValue(): Float {
        var total = 0f
        for (i in inputs) {
            if (i.UTXO == null) continue
            total += i.UTXO!!.amount
        }
        return total
    }

    /**
     * Returns the sum amount of all the outputs
     *
     * @return the total amount of the outputs for this Transaction
     */
    fun getOutputsValue(): Float {
        var total = 0f
        for (o in outputs) {
            total += o.amount
        }
        return total
    }

    /**
     * Class representing a Transaction input for this Transaction @see <a href="https://en.bitcoin.it/wiki/Transaction"</a>
     */
    class TransactionInput(var transactionOutputId: String) {
        var UTXO: TransactionOutput? = null //Contains the Unspent transaction output
    }

    /**
     * Class representing a Transaction output for this Transaction @see <a href="https://en.bitcoin.it/wiki/Transaction"</a>
     */
    class TransactionOutput(var receiver: PublicKey,
                            var amount: Float,
                            var parentTransactionId: String) {

        var id: String

        init {
            val data = CryptoUtils.getStringFromKey(receiver) + amount.toString() + parentTransactionId
            this.id = data.sha512()
        }

        fun belongsToWallet(publicKey: PublicKey): Boolean {
            return publicKey === receiver
        }
    }

}
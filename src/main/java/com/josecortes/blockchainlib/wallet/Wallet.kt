package com.josecortes.blockchainlib.wallet

import com.josecortes.blockchainlib.crypto.CryptoUtils
import com.josecortes.blockchainlib.transactions.Transaction.TransactionOutput
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Wallet representing the Entitiy that will store the currency of this Blockchain
 */
class Wallet {
    var privateKey: PrivateKey? = null
    var publicKey: PublicKey? = null
    var UTXOs = HashMap<String, TransactionOutput>() //only mapUtxo owned by this wallet.

    fun generateKeyPair() {
        val keyPair = CryptoUtils.generateKeyPair()
        keyPair?.let {
            privateKey = keyPair.private
            publicKey = keyPair.public
        }
    }

}
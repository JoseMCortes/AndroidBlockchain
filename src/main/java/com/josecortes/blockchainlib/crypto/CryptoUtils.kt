package com.josecortes.blockchainlib.crypto

import android.util.Base64
import java.security.*
import java.security.spec.ECGenParameterSpec

/**
 * Some Utils to use in the signing and verification of Blocks and Transactions.
 */
class CryptoUtils {
    companion object {
        init {
            Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        }

        /**
         * Generates a pair of Public and Private Keys using an Elliptic Curve Digital Signature Algorithm ( ECDSA)
         * (Bitcoin uses secp256k1 with the ECDSA algorithm).
         */
        fun generateKeyPair(): KeyPair? {
            try {
                val keyGen = KeyPairGenerator.getInstance("ECDSA", "BC")
                val random = SecureRandom.getInstance("SHA1PRNG")
                val ecSpec = ECGenParameterSpec("prime192v1")
                keyGen.initialize(ecSpec, random)
                return keyGen.generateKeyPair()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * Given a PrivateKey (usually representing the owner of a Wallet) and an input,
         * returns the signed representation of the input.
         *
         * @param privateKey The private key to sign with.
         * @param input The string information to sign
         *
         * @return An array of bytes containing the result of applying the signature to the input.
         */
        fun applyECDSASig(privateKey: PrivateKey, input: String): ByteArray {
            val dsa: Signature
            var output: ByteArray
            try {
                dsa = Signature.getInstance("ECDSA", "BC")
                dsa.initSign(privateKey)
                val strByte = input.toByteArray()
                dsa.update(strByte)
                val realSig = dsa.sign()
                output = realSig
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            return output
        }

        /**
         * Given some signed data and a Public Key, checks if the data has been properly signed by
         * the corresponding PrivateKey.
         *
         * @param publicKey The public key of the pair Public-Private
         * @param data The data to verify
         * @param signature The signature bytes to be validated.
         */
        fun verifyECDSASig(publicKey: PublicKey, data: String, signature: ByteArray): Boolean {
            try {
                val ecdsaVerify = Signature.getInstance("ECDSA", "BC")
                ecdsaVerify.initVerify(publicKey)
                ecdsaVerify.update(data.toByteArray())
                return ecdsaVerify.verify(signature)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        /**
         * Given a Key (public or private), return its Base64 encoding.
         *
         * @param key The key to convert to
         */
        fun getStringFromKey(key: Key): String {
            return Base64.encodeToString(key.encoded, Base64.DEFAULT)
        }
    }

}
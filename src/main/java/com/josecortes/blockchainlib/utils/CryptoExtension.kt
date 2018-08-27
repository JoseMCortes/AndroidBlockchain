package com.josecortes.blockchainlib.utils

import java.security.MessageDigest

/**
 * String extension to provide sha-512 operations to the necessary Strings.
 */
fun String.sha512(): String {
    val digest = MessageDigest.getInstance("SHA-512")
    val bytes = digest.digest(this.toByteArray(Charsets.UTF_8))
    return bytes.fold("", { str, it -> str + "%02x".format(it) })
}

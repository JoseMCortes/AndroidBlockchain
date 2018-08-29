# AndroidBlockchain

Very basic Blockchain library implementing approaches used by Bitcoin blockchain (see https://bitcoin.org/en/developer-guide#block-chain). 
The Blockchain offers and basic implementation to create basic Transactions, add them to Blocks and create a whole BlockChain offering validation of tampering and data integrity.

![Blockchain](https://user-images.githubusercontent.com/4429438/44645574-635efe00-a9d8-11e8-8907-af2706288c5a.png)

To keep the record of the Transactions inside a block, basic Merkle Tree data structures has been used (see https://bitcoin.org/en/glossary/merkle-tree)

Different basic algorithms for Mining are provided. Also, take into account that parameters for these algorithms should be adjusted for a Mobile device, as they can lead to a big CPU and battery consumption. Use Dagger injection to provide different implementations of mining algorithms.

Basic Elliptic curve algorithms has been used in order to generate Key-Pair values to provide Wallets, it is possible to replace or improve them as needed.

## Technology stack:

* Android (Kotlin and some Java)
* JUnit, Mockito and PowerMock for testing purposes.
* Dagger 2 to perform dependency injection
* BouncyCastle to provide implementations of the ECDSA (Elliptic Curve Digital Signature Algorithm)


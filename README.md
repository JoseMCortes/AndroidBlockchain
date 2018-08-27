# AndroidBlockchain

Basic Blockchain library implementing approaches used by Bitcoin blockchain (see https://bitcoin.org/en/developer-guide#block-chain). 
The Blockchain offers and basic implementation to create basic Transactions, add them to Blocks and create a whole BlockChain offering validation of tampering and data integrity.

To keep the record of the Transactions inside a block, basic Merkle Tree data structures has been used (see https://bitcoin.org/en/glossary/merkle-tree)

## Technology stack:

* Android (Kotlin and some Java)
* JUnit, Mockito and PowerMock for testing purposes.
* Dagger 2 to perform dependency injection
* BouncyCastle to provide implementations of the ECDSA (Elliptic Curve Digital Signature Algorithm)


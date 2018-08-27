package com.josecortes.blockchainlib.injection.module


import com.josecortes.blockchainlib.mining.Miner
import dagger.Module
import dagger.Provides
import dagger.Reusable

/**
 * Creates instances of Miners. It is possible to provide different Algorithms to mine in order
 * to test performance
 */
@Module
object MinerModule {

    @Provides
    @Reusable
    @JvmStatic
    open fun provideItemRepository(): Miner {
        return Miner(Miner.Algorithm.ALGORITHM_FIND_ZEROS, "3")
    }

}
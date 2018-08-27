package com.josecortes.blockchainlib.injection.component

import com.josecortes.blockchainlib.blockchain.NeuralChain
import com.josecortes.blockchainlib.injection.module.MinerModule
import dagger.Component
import javax.inject.Singleton


/**
 * Component providing inject() methods to inject different instances of the Miner. This way
 * our Blockchains would be able to try diferent Miner algorithms
 */

@Singleton
@Component(modules = [(MinerModule::class)])
interface MinerComponent {

    @Component.Builder
    interface Builder {
        fun build(): MinerComponent
        fun minerModule(minerModule: MinerModule): Builder
    }

    fun inject(blockChain: NeuralChain)

}
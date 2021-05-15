package tests.doubles.stubs

import adnet.*
import auction.AuctionData
import io.reactivex.rxjava3.core.Single
import kotlin.random.Random

fun smallDelay() = Random(100).nextLong(50, 60)

fun bigDelay() = Random(100).nextLong(1000, 1200)

class AlwaysCachingAdNet(appId: String) : AdNetBase(appId) {

    override fun initialize(): Single<InitializationInfo> = Single.create { emitter ->
        Thread.sleep(smallDelay())
        emitter.onSuccess(InitializationInfo(this, InitializationResult.Successful))
        println("$appId initialized!")
    }

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO()
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

class SlowAdNet(appId: String) : AdNetBase(appId) {

    override fun initialize(): Single<InitializationInfo> = Single.create { emitter ->
        Thread.sleep(bigDelay())
        emitter.onSuccess(InitializationInfo(this, InitializationResult.Successful))
        println("$appId initialized!")
    }

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO("Not yet implemented")
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

package tests.doubles.stubs

import adnet.*
import auction.AuctionData
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun smallDelay() = Random(100).nextLong(40, 45)

fun bigDelay() = Random(100).nextLong(1300, 1400)

class AlwaysCachingAdNet(appId: String) : AdNetBase(appId) {

    override fun initialize(): Single<InitializationInfo> = Single
        .create<InitializationInfo?> { emitter ->
            println("$appId initialized!")
            emitter.onSuccess(InitializationInfo(this, InitializationResult.Successful))
        }
        .delay(smallDelay(), TimeUnit.MILLISECONDS)

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO()
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

class AdNetWithSlowInitializing(appId: String) : AdNetBase(appId) {

    override fun initialize(): Single<InitializationInfo> = Single
        .create<InitializationInfo?> { emitter ->
            emitter.onSuccess(InitializationInfo(this, InitializationResult.Successful))
        }
        .delay(bigDelay(), TimeUnit.MILLISECONDS)

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO("Not yet implemented")
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

class AdNetBrokenOnInitialization(appId: String) : AdNetBase(appId) {

    override fun initialize(): Single<InitializationInfo> = Single
        .create<InitializationInfo?> { emitter ->
            val throwable = Exception("Broken ad-net: $appId")
            emitter.onError(throwable)
            throw throwable
        }
        .delay(bigDelay(), TimeUnit.MILLISECONDS)

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO("Not yet implemented")
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

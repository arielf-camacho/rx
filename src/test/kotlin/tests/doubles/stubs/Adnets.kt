package tests.doubles.stubs

import adnet.AdNet
import adnet.CacheParameters
import adnet.CacheResult
import adnet.InitializationResult
import auction.AuctionData
import io.reactivex.rxjava3.core.Single

class AlwaysCachingAdNet(override val appId: String) : AdNet {
    override fun initialize(): Single<InitializationResult> = Single.just(InitializationResult.Successful)

    override fun cache(cacheParameters: CacheParameters): Single<CacheResult> {
        TODO("Not yet implemented")
    }

    override fun getAuctionData(): Single<AuctionData> {
        TODO("Not yet implemented")
    }
}

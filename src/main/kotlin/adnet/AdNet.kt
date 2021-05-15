package adnet

import auction.AuctionData
import io.reactivex.rxjava3.core.Single

interface AdNet {
    val appId: String

    fun initialize(): Single<InitializationInfo>

    fun cache(cacheParameters: CacheParameters): Single<CacheResult>

    fun getAuctionData(): Single<AuctionData>
}

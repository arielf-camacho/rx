import adnet.AdNet
import adnet.InitializationResult
import adnet.CacheResult
import io.reactivex.rxjava3.core.Single

class SDK(private val adNets: Iterable<AdNet>) {
    fun initialize(): Single<InitializationResult> {
        TODO()
    }

    fun cache(): Single<CacheResult> {
        TODO()
    }
}

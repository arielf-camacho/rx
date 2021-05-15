import adnet.AdNet
import adnet.CacheResult
import adnet.InitializationResult
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.concatAll
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val AD_NET_INITIALIZATION_TIMEOUT = 200L

class SDK(private var adNets: Iterable<AdNet>) {

    fun initialize(): Single<InitializationResult> = Observable
        .create<AdNet> { adNetEmitter ->
            adNets.forEach { adNetEmitter.onNext(it) }
            adNetEmitter.onComplete()
        }
        .subscribeOn(Schedulers.newThread())
        .map { adNet ->
            adNet
                .initialize()
                .timeout(adNets.count() * AD_NET_INITIALIZATION_TIMEOUT, TimeUnit.MILLISECONDS)
                .filter { it.result != adnet.InitializationResult.Successful }
                .map { it.adNet }
        }
        .reduce(mutableListOf<AdNet>(), { list, maybeAdNet ->
            list.add(maybeAdNet.blockingGet()!!)
            list
        })
        .map { InitializationResult(it) }
        .toMaybe()
        .toSingle()

    fun cache(): Single<CacheResult> {
        TODO()
    }

    data class InitializationResult(val initializedAdNets: Iterable<AdNet>)
}

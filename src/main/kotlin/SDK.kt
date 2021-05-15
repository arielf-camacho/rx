import adnet.AdNet
import adnet.CacheResult
import adnet.InitializationInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val AD_NET_INITIALIZATION_TIMEOUT = 100L

class SDK(private var adNets: Iterable<AdNet>) {

    fun initialize(): Single<InitializationResult> = Observable
        .create<AdNet> { adNetEmitter ->
            adNets.forEach { adNetEmitter.onNext(it) }
            adNetEmitter.onComplete()
        }
        .subscribeOn(Schedulers.io())
        .flatMap(::initializeAdNet)
        .filter { it.result == adnet.InitializationResult.Successful }
        .reduce(mutableListOf<AdNet>()) { list, initializationInfo ->
            list.add(initializationInfo.adNet)
            list
        }
        .map { InitializationResult(it) }

    private fun initializeAdNet(adNet: AdNet) = Observable.create<InitializationInfo> { emitter ->
        adNet
            .initialize()
            .timeout(AD_NET_INITIALIZATION_TIMEOUT, TimeUnit.MILLISECONDS)
            .filter { it.result == adnet.InitializationResult.Successful }
            .defaultIfEmpty(InitializationInfo(adNet, adnet.InitializationResult.Invalid))
            .subscribe(
                {
                    emitter.onNext(it)
                    emitter.onComplete()
                },
                {
                    emitter.onNext(InitializationInfo(adNet, adnet.InitializationResult.Invalid))
                    emitter.onComplete()
                }
            )
    }

    fun cache(): Single<CacheResult> {
        TODO()
    }

    data class InitializationResult(val initializedAdNets: Iterable<AdNet>)
}

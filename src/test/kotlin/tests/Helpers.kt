package tests

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import java.util.concurrent.TimeUnit

const val EMIT_TIMEOUT = 200L

fun <T> Single<T>.toTestObserver(timeout: Long = EMIT_TIMEOUT): TestObserver<T> =
    this.timeout(timeout, TimeUnit.MILLISECONDS).test()

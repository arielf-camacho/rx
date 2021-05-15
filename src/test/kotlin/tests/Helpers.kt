package tests

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.Suite
import java.util.concurrent.TimeUnit

const val EMIT_TIMEOUT = 200L

fun <T> Single<T>.toTestObserver(timeout: Long = EMIT_TIMEOUT): TestObserver<T> {
    var observer = this

    if (timeout > 0)
        observer = observer.timeout(timeout, TimeUnit.MILLISECONDS)

    return observer.test()
}

fun Suite.eit(description: String, timeout: Long = Long.MAX_VALUE, body: TestBody.() -> Unit) {
    it(description, timeout = timeout, body = body)
}

package tests.functional

import AD_NET_INITIALIZATION_TIMEOUT
import SDK
import adnet.AdNet
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tests.doubles.stubs.AdNetBrokenOnInitialization
import tests.doubles.stubs.AlwaysCachingAdNet
import tests.doubles.stubs.AdNetWithSlowInitializing
import tests.eit
import tests.toTestObserver
import java.util.concurrent.TimeUnit

class SDKTest : Spek({
    val adNetWaitTimeout = AD_NET_INITIALIZATION_TIMEOUT + 100L

    fun createSdk(adNets: Iterable<AdNet>): SDK = SDK(adNets = adNets)

    describe("Initialization feature") {
        context("when all ad-nets correctly initialize") {
            eit("they all should have initialized correctly and the SDK must emit a result") {
                // given, some AdNets that always initialize properly
                val adNets = (1..3).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it")) }
                val timeout = adNets.size * adNetWaitTimeout

                val sdk = createSdk(adNets)

                // when attempting to initialize them
                val observer = sdk.initialize().toTestObserver(timeout)

                // then all of them were initialized, and the initialization since it went successful, the SDK must
                // have emitted a success value
                observer.await(timeout, TimeUnit.MILLISECONDS)
                observer.assertValue { actualResult ->
                    assertThat(actualResult.initializedAdNets).hasSize(adNets.size)
                    assertThat(actualResult.initializedAdNets).containsAll(adNets)
                    true
                }
                observer.assertComplete()

                adNets.forEach { verify(exactly = 1) { it.initialize() } }
            }
        }

        context("when there are slow ad-nets") {
            eit(
                "they all should have get called their initialize method, but never wait on them to finish, nor they" +
                        "should be selected as good AdNets to run the auctions"
            ) {
                // given, some AdNets that always initialize properly and others that are slow
                val goodAdNets = (1..2).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it")) }
                val slowAdNets = (1..2).map { spyk(AdNetWithSlowInitializing("SlowAdNet: #$it")) }
                val adNets = slowAdNets + goodAdNets
                val timeout = adNets.size * adNetWaitTimeout

                val sdk = createSdk(adNets)

                // when attempting to initialize them
                val observer = sdk.initialize().toTestObserver(timeout)

                // then all of them were initialized, and in the initialization result the slow adNets should not figure
                observer.await(timeout, TimeUnit.MILLISECONDS)
                observer.assertValue { actualResult ->
                    assertThat(actualResult.initializedAdNets).hasSize(goodAdNets.size)
                    assertThat(actualResult.initializedAdNets).containsAll(goodAdNets)
                    true
                }
                observer.assertComplete()

                adNets.forEach { verify(exactly = 1) { it.initialize() } }
            }
        }

        context("when there are broken ad-nets") {
            it(
                "they all must have been called to initialize, but on the initialization result only the good ones " +
                        "must be found"
            ) {
                // given, some AdNets that always initialize properly and others that are slow
                val goodAdNets = (1..2).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it")) }
                val slowAdNets = (1..2).map { spyk(AdNetBrokenOnInitialization("SlowAdNet: #$it")) }
                val adNets = slowAdNets + goodAdNets
                val timeout = adNets.size * adNetWaitTimeout

                val sdk = createSdk(adNets)

                // when attempting to initialize them
                val observer = sdk.initialize().toTestObserver(timeout)

                // then all of them were initialized, and in the initialization result the slow adNets should not figure
                observer.await(timeout, TimeUnit.MILLISECONDS)
                observer.assertValue { actualResult ->
                    assertThat(actualResult.initializedAdNets).containsAll(goodAdNets)
                    true
                }
                observer.assertComplete()

                adNets.forEach { verify(exactly = 1) { it.initialize() } }
            }
        }
    }
})

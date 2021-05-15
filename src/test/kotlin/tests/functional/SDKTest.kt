package tests.functional

import SDK
import adnet.AdNet
import io.mockk.spyk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tests.doubles.stubs.AlwaysCachingAdNet
import tests.doubles.stubs.SlowAdNet
import tests.toTestObserver

class SDKTest : Spek({
    val adNetWaitTimeout = 500000L

    fun createSdk(adNets: Iterable<AdNet>): SDK = SDK(adNets = adNets)

    describe("Initialization feature") {
        context("when all adNets correctly initialize") {
            it("they all should have initialized correctly and the SDK must emit a result", timeout = Long.MAX_VALUE) {
                // given, some AdNets that always initialize properly
                val adNets = (1..3).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it")) }
                val timeout = adNets.size * adNetWaitTimeout

                val sdk = createSdk(adNets)

                // when attempting to initialize them
                val observer = sdk.initialize().toTestObserver(timeout)

                // then all of them were initialized, and the initialization since it went successful, the SDK must
                // have emitted a success value
                Thread.sleep(timeout)
                observer.assertComplete()
                observer.assertValue(SDK.InitializationResult(adNets))

                adNets.forEach { verify(exactly = 1) { it.initialize() } }
            }
        }

        context("when there are slow adNets") {
            it("they all should have get called their initialize method and not wait for the slow network to finish") {
                // given, some AdNets that always initialize properly and others that are slow
                val goodAdNets = (1..2).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it")) }
                val slowAdNets = (1..2).map { spyk(SlowAdNet("SlowAdNet: #$it")) }
                val adNets = goodAdNets + slowAdNets
                val timeout = adNets.size * adNetWaitTimeout

                val sdk = createSdk(adNets)

                // when attempting to initialize them
                val observer = sdk.initialize().toTestObserver(timeout)

                // then all of them were initialized, and in the initialization result the slow adNets should not figure
                observer.assertComplete()
                observer.assertValue(SDK.InitializationResult(goodAdNets))

                adNets.forEach { verify(exactly = 1) { it.initialize() } }
            }
        }
    }
})

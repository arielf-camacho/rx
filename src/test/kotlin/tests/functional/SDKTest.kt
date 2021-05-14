package tests.functional

import SDK
import adnet.AdNet
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import tests.doubles.stubs.AlwaysCachingAdNet
import tests.toTestObserver

class SDKTest : Spek({
    fun createSdk(adNets: Iterable<AdNet>): SDK = SDK(adNets = adNets)

    describe("Initialization feature") {
        it("when all adNets correctly initialize") {
            // given
            val adNets = (1..3).map { spyk(AlwaysCachingAdNet("GoodAdNet: #$it") }
            val sdk = createSdk(adNets)

            // when
            val result = sdk.initialize().toTestObserver()


        }
    }
})

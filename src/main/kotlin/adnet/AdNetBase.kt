package adnet

abstract class AdNetBase(override val appId: String) : AdNet {
    override fun toString() = appId
}

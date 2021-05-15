package adnet

enum class InitializationResult {
    Successful,
    Timeout,
    Invalid
}

data class InitializationInfo(val adNet: AdNet, val result: InitializationResult)

package adnet

enum class InitializationResult {
    Successful,
    Failed
}

data class InitializationInfo(val adNet: AdNet, val result: InitializationResult)

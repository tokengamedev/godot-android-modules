package gaml.applovinmax

import org.godotengine.godot.Dictionary

/**
 * FData class to hold the Options for Mediator
 */
class MediationOptions(d: Dictionary) {
    companion object{
        const val DEFAULT_AUTOLOAD_AD: Boolean = true
        const val DEFAULT_RETRY_ON_LOAD_FAILURE: Boolean = true
        const val DEFAULT_RETRY_COUNT : Int = 6
        const val MAX_RETRY_COUNT: Int = 10

    }

    var autoloadAds = DEFAULT_AUTOLOAD_AD
    var retryOnLoadFailure = DEFAULT_RETRY_ON_LOAD_FAILURE
    var maxRetries = DEFAULT_RETRY_COUNT


    init {
        autoloadAds = if (d.containsKey("autoload_ads")) d["autoload_ads"] as Boolean else DEFAULT_AUTOLOAD_AD
        retryOnLoadFailure = if (d.containsKey("retry_on_load_failure")) d["retry_on_load_failure"] as Boolean else DEFAULT_RETRY_ON_LOAD_FAILURE
        maxRetries = if (d.containsKey("max_retries")) {
            val retries = d["max_retries"] as Int
            if (retries <= 0 || !retryOnLoadFailure) 0
            else if (retries > MAX_RETRY_COUNT) MAX_RETRY_COUNT
            else retries
        } else DEFAULT_RETRY_COUNT

    }

    override fun toString(): String {
        return "{autoload: $autoloadAds, retryOnFailure: $retryOnLoadFailure, maxRetries: $maxRetries }"
    }
}
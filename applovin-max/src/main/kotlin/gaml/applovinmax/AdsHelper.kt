package gaml.applovinmax

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import org.godotengine.godot.Dictionary

/**
 * Helper Class to get Godot Dictionary out of Java/Kotlin Objects
 */
class AdsHelper {
    companion object{
        /**
         * Gets the Max error as Godot dictionary
         */
        fun getAdError(error: MaxError?): Dictionary{
            val errorDict = Dictionary()
            if (error != null){
                errorDict["code"] = error.code
                errorDict["message"] = error.message
                errorDict["network_code"] = error.mediatedNetworkErrorCode
                errorDict["network_message"] = error.mediatedNetworkErrorMessage
            }
            return errorDict
        }

        /**
         * Gets the Max Ad as Godot Dictionary
         */
        fun getAdDetails(ad: MaxAd?): Dictionary{
            val adDict = Dictionary()
            if (ad != null){
                adDict["unit_id"] = ad.adUnitId
                adDict["type"] = ad.format.label
                adDict["network_name"] = ad.networkName
                adDict["network_placement"] = ad.networkPlacement
                adDict["placement"] = ad.placement
            }
            return adDict
        }

        /**
         * Gets the Max Reward as a Godot Dictionary
         */
        fun getAdReward(reward: MaxReward?): Dictionary {
            val rewardDict = Dictionary()
            if (reward != null){
                rewardDict["label"] = reward.label
                rewardDict["amount"] = reward.amount
            }
            return rewardDict
        }
    }
}
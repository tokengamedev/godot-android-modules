package gaml.ogury.consent.manager

import com.ogury.cm.OguryChoiceManager.Answer
import com.ogury.core.OguryError
import org.godotengine.godot.Dictionary

class ConsentHelper {
    companion object{

        fun getAnswer(answer: Answer) : Dictionary{
            val answerDictionary = Dictionary()
            answerDictionary["name"] = answer.name
            when(answer){
                Answer.NO_ANSWER -> answerDictionary["value"] = 0
                Answer.FULL_APPROVAL -> answerDictionary["value"] = 1
                Answer.PARTIAL_APPROVAL -> answerDictionary["value"] = 2
                Answer.REFUSAL -> answerDictionary["value"] = 3
                Answer.CCPAF_SALE_ALLOWED -> answerDictionary["value"] = 10
                Answer.CCPAF_SALE_DENIED -> answerDictionary["value"] = 11
            }
            return answerDictionary

        }

        fun getError(error: OguryError): Dictionary{
            val errorDictionary = Dictionary()
            errorDictionary["code"] = error.errorCode
            errorDictionary["message"] = error.message ?: ""
            return errorDictionary
        }
    }
}
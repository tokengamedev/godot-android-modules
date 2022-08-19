import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*


/*
* Task to upload the aar file to a given location
*/
abstract class AarUploadTask:  DefaultTask() {

    // Holds the aar file (built artifact)
    @get:InputFile
    abstract val aarFile: RegularFileProperty

    // Holds the full file path to be copied to
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    // Performs the task
    @TaskAction
    fun taskAction(){

        val inputFile = aarFile.get().asFile
        val outputFile = outputFile.get().asFile

        inputFile.copyTo(outputFile, true)

        println("Aar file uploaded to: ${outputFile.path}")

    }
}
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
/*
* Task to upload a file to a given location
*/
abstract class UploadTask:  DefaultTask() {

    // Holds the aar file (built artifact)
    @get:InputFile
    abstract val inFile: RegularFileProperty

    // Holds the full file path to be copied to
    @get:OutputFile
    abstract val outFile: RegularFileProperty

    // Performs the task
    @TaskAction
    fun taskAction(){

        val inputFile = inFile.get().asFile
        val outputFile = outFile.get().asFile

        inputFile.copyTo(outputFile, true)

        println("File uploaded to: ${outputFile.path}")

    }
}
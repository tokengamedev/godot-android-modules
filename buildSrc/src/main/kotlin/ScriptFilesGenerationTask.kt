import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

/*
* Task to generate the gdap file in the given location
* Check https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#android-plugin
* for file format
* */
abstract class ScriptFilesGenerationTask:  DefaultTask() {


    // Directory where the file will be created
    @get:OutputFile
    abstract val configFile: RegularFileProperty

    // Directory where the file will be created
    @get:OutputFile
    abstract val scriptFile: RegularFileProperty


    /**
     * Plugin.cfg
     */
    // Name of the Module
    @get:Input
    abstract val moduleName: Property<String>

    // type of binary (remote or local)
    @get:Input
    abstract val author: Property<String>

    // Binary file Name
    @get:Input
    abstract val pluginDescription: Property<String>

    // Binary file Name
    @get:Input
    abstract val version: Property<String>

    // Directory where the file will be created
    @get:Input
    abstract val scriptFileName: Property<String>

    // Name of the Module
    @get:InputFile
    abstract val templatePath: RegularFileProperty

    // Directory where the file will be created
    @get:Input
    abstract val assetFilePath: Property<String>



    // Performs the task
    @TaskAction
    fun taskAction(){

        // Create plugin.cfg file
        val pluginConfigFile = configFile.get().asFile
        pluginConfigFile.createNewFile()

        val configText = """
            [plugin]

            name="${moduleName.get()}"
            description="${pluginDescription.get()}"
            author="${author.get()}"
            version="${version.get()}"
            script="${scriptFileName.get()}"
        """.trimIndent()

        pluginConfigFile.writeText(configText)

        // create the script file
        val scriptFile = scriptFile.get().asFile
        scriptFile.createNewFile()

        val templateText = templatePath.get().asFile.readText()
        scriptFile.writeText(templateText.format(moduleName.get(), assetFilePath.get()))

        println("Script files Created.")
    }
}
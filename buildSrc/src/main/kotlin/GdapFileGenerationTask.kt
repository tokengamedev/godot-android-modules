import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

/*
* Task to generate the gdap file in the given location
* Check https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html#android-plugin
* for file format
* */
abstract class GdapFileGenerationTask:  DefaultTask() {


    // Directory where the file will be created
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    /**
     * Config section data
     */
    // Name of the Module
    @get:Input
    abstract val moduleName: Property<String>

    // type of binary (remote or local)
    @get:Input
    abstract val binaryType: Property<String>

    // Binary file Name
    @get:Input
    abstract val binaryFile: Property<String>


    /**
     * Dependencies section data
     */
    // Name of the Module
    @get:Input
    abstract val hasDependencies: Property<Boolean>

    // Name of the Module
    @get:Input
    abstract val localDependencies: Property<String>

    // type of binary (remote or local)
    @get:Input
    abstract val remoteDependencies: Property<String>

    // Binary file Name
    @get:Input
    abstract val customMavenRepos: Property<String>



    // Performs the task
    @TaskAction
    fun taskAction(){

        val gdapFile = outputFile.get().asFile
        gdapFile.createNewFile()

        val gdapConfigText = """
            [config]
            
            name="${moduleName.get()}"
            binary_type="${binaryType.get()}"
            binary="${binaryFile.get()}"
        """.trimIndent()


        val gdapDependenciesText =  StringBuilder("")
        if (hasDependencies.get()){
            gdapDependenciesText.appendLine().appendLine() // Add two lines for gap
            gdapDependenciesText.appendLine( "[dependencies]").appendLine()

            if (localDependencies.get().isNotEmpty())
                gdapDependenciesText.appendLine( "local=[${localDependencies.get()}]")
            if (remoteDependencies.get().isNotEmpty())
                gdapDependenciesText.appendLine( "remote=[${remoteDependencies.get()}]")
            if (customMavenRepos.get().isNotEmpty())
                gdapDependenciesText.appendLine( "custom_maven_repos=[${customMavenRepos.get()}]")
        }
        val gdapText = gdapConfigText + gdapDependenciesText.toString()
        gdapFile.writeText(gdapText)

        println("Created gdap file ${gdapFile.path}")
    }
}

import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import com.android.build.api.variant.AndroidComponentsExtension
import kotlin.io.path.Path


abstract class GdapPlugin: Plugin<Project> {

    override fun apply(project: Project) {

        // First copy
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants(androidComponents.selector().all()) { variant ->

            //println("Configuring release version, ${variant.name}")

            // Setup the task names
            val copyTask = "${variant.name}CopyAarFile"
            val generateTask = "${variant.name}GenerateScriptFiles"

            // register the tasks
            val copyTaskProvider = project.tasks.register(copyTask, UploadTask::class.java )
            val scriptGeneratorProvider = project.tasks.register(generateTask, ScriptFilesGenerationTask::class.java )

            val moduleName = "${project.property("moduleName")}"
            val artifact = variant.artifacts.get(SingleArtifact.AAR)
            val outputFileName = "$moduleName.${project.version}.${variant.name}.aar"
            val scriptFileName = moduleName.replace("(?<=.)[A-Z]".toRegex(), "_$0").lowercase()+"_plugin.gd"
            val outputLocation = Path(project.property("outputLocation").toString(), moduleName).toString()
            val templateLocation = Path(project.property("templateLocation").toString(),scriptFileName)

            // configure the tasks
            copyTaskProvider.configure{
                it.inFile.set(artifact)
                it.outFile.set(File(outputLocation, outputFileName))
            }

            scriptGeneratorProvider.configure {
                it.dependsOn(copyTaskProvider)
                it.configFile.set(File(outputLocation, "plugin.cfg"))
                it.scriptFile.set(File(outputLocation, scriptFileName))
                // Config section data
                it.moduleName.set(moduleName)
                it.pluginDescription.set("${project.property("pluginDescription") ?: ""}")
                it.scriptFileName.set(scriptFileName)
                it.assetFilePath.set(outputFileName)
                it.version.set(project.version.toString())
                it.author.set("${project.property("author") ?: ""}")
                it.templatePath.set(templateLocation.toFile())
                //it.binaryFile.set(outputFileName)
                //it.binaryType.set("${project.property("binary_type")}")

//
//                if (project.hasProperty("remoteDependencies") || project.hasProperty("localDependencies")) {
//                    it.hasDependencies.set(true)
//
//                    if (project.hasProperty("remoteDependencies")) {
//                        it.remoteDependencies.set("${project.property("remoteDependencies")}")
//
//                        if (project.hasProperty("customMavenRepos"))
//                            it.customMavenRepos.set("${project.property("customMavenRepos")}")
//                        else
//                            it.customMavenRepos.set("")
//                    } else
//                        it.remoteDependencies.set("")
//
//                    if (project.hasProperty("localDependencies"))
//                        it.localDependencies.set("${project.property("localDependencies")}")
//                    else
//                        it.localDependencies.set("")
//                }
//                else {
//                    it.hasDependencies.set(false)
//                    it.remoteDependencies.set("")
//                    it.localDependencies.set("")
//                    it.customMavenRepos.set("")
//                }
            }

            // Configure the  tasks to run
            project.tasks.whenTaskAdded { task ->
                if (variant.name == "release" && task.name == "assembleRelease") {
                    task.finalizedBy(generateTask)
                }
                else if (variant.name == "debug" && task.name == "assembleDebug") {
                    task.finalizedBy(generateTask)
                }
            }
        }
    }
}
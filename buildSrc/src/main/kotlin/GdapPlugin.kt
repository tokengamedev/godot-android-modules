
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import com.android.build.api.variant.AndroidComponentsExtension


abstract class GdapPlugin: Plugin<Project> {

    override fun apply(project: Project) {

        // First copy
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants(androidComponents.selector().all()) { variant ->

            //println("Configuring release version, ${variant.name}")

            // Setup the task names
            val copyTask = "${variant.name}CopyAarFile"
            val generateTask = "${variant.name}GenerateGdapFile"

            // register the tasks
            val copyTaskProvider = project.tasks.register(copyTask, AarUploadTask::class.java )
            val gdapGeneratorProvider = project.tasks.register(generateTask, GdapFileGenerationTask::class.java )

            val moduleName = "${project.property("moduleName")}"
            val artifact = variant.artifacts.get(SingleArtifact.AAR)
            val outputFileName = "$moduleName.${project.version}.${variant.name}.aar"

            // configure the tasks
            copyTaskProvider.configure{
                it.aarFile.set(artifact)
                it.outputFile.set(File(project.property("outputLocation").toString(), outputFileName))
            }

            gdapGeneratorProvider.configure {
                it.dependsOn(copyTaskProvider)

                it.outputFile.set(File(project.property("outputLocation").toString(), "$moduleName.gdap"))
                // Config section data
                it.moduleName.set(moduleName)
                it.binaryFile.set(outputFileName)
                it.binaryType.set("${project.property("binary_type")}")


                if (project.hasProperty("remoteDependencies") || project.hasProperty("localDependencies")) {
                    it.hasDependencies.set(true)

                    if (project.hasProperty("remoteDependencies")) {
                        it.remoteDependencies.set("${project.property("remoteDependencies")}")

                        if (project.hasProperty("customMavenRepos"))
                            it.customMavenRepos.set("${project.property("customMavenRepos")}")
                        else
                            it.customMavenRepos.set("")
                    } else
                        it.remoteDependencies.set("")

                    if (project.hasProperty("localDependencies"))
                        it.localDependencies.set("${project.property("localDependencies")}")
                    else
                        it.localDependencies.set("")
                }
                else {
                    it.hasDependencies.set(false)
                    it.remoteDependencies.set("")
                    it.localDependencies.set("")
                    it.customMavenRepos.set("")
                }
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
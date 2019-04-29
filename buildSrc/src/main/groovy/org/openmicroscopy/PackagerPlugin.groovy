package org.openmicroscopy

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.jvm.tasks.Jar
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.extensions.InstallOptionsContainer
import org.openmicroscopy.extensions.implementation.DefaultInstallOptions

class PackagerPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(InsightPlugin)

        InstallOptionsContainer installOptionsContainer =
                project.extensions.getByName("deploy") as InstallOptionsContainer

        // Configure main install options (insight)
        InstallOptions main = installOptionsContainer.getByName(JavaPackagerPlugin.MAIN_DEPLOY_NAME)
        main.exe {
            it.icon = project.file("icons/omeroinsight.ico")
        }
        main.dmg {
            it.icon = project.file("icons/omeroinsight.icns")
        }

        createImporterInstaller(installOptionsContainer)
    }

    private void createImporterInstaller(InstallOptionsContainer container) {
        // Create install option for importer
        container.create("importer", new Action<DefaultInstallOptions>() {
            @Override
            void execute(DefaultInstallOptions importer) {
                // Use the command line arguments from the 'run' task
                def exec = project.tasks.getByName(TASK_RUN_IMPORTER) as JavaExec
                importer.mainClassName.set(exec.main)
                importer.arguments.set(exec.args)
                importer.javaOptions.set(exec.jvmArgs)

                // The mainJar is the archive created by the 'jar' task
                def jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
                importer.mainJar.set(jar.archiveFileName)
                importer.applicationVersion.set(jar.archiveVersion)

                // Use the files from the 'installDist' task
                Sync installDistTask = project.tasks.getByName(DistributePlugin.DISTRIBUTION_IMPORTER) as Sync
                importer.outputFile.set(project.layout.buildDirectory.file("packaged/${importer.name}/${installDistTask.destinationDir.name}"))
                importer.applicationName.set(installDistTask.destinationDir.name)
                importer.sourceDir.set(installDistTask.destinationDir)
                importer.sourceFiles.from(project.fileTree(installDistTask.destinationDir).include("**/*.*"))
            }
        })
    }

}

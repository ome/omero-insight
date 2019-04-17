package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.extensions.InstallOptionsContainer

@CompileStatic
class InsightPlugin implements Plugin<Project> {

    public static final String GROUP_APPLICATION = ApplicationPlugin.APPLICATION_GROUP

    public static final String TASK_RUN_IMPORTER = "runImporter"

    public static final String MAIN_INSIGHT = "org.openmicroscopy.shoola.Main"

    private static final List DEFAULT_JVM_ARGS = ["-Xms256m", "-Xmx1024m"]

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        // We want these applied first
        project.pluginManager.apply(InsightBasePlugin)
        project.pluginManager.apply(ApplicationPlugin)
        project.pluginManager.apply(JavaPackagerPlugin)

        // Configure insight tasks
        addRunImporter()

        // Configure java tasks
        configureApplicationPlugin()

        // Configure install options
        configurePackagerPlugin()
    }

    private void configureApplicationPlugin() {
        JavaApplication javaApplication =
                project.extensions.getByName("application") as JavaApplication

        javaApplication.mainClassName = MAIN_INSIGHT
        javaApplication.applicationDefaultJvmArgs = DEFAULT_JVM_ARGS

        project.tasks.named(ApplicationPlugin.TASK_RUN_NAME, JavaExec).configure {
            it.setArgs(["container.xml", String.valueOf(project.buildDir)])
        }

        project.tasks.named(ApplicationPlugin.TASK_START_SCRIPTS_NAME, CreateStartScripts).configure {
            it.classpath += project.fileTree(dir: "src/config", include: "**/*.xml")
            it.defaultJvmOpts += ["-Duser.dir=MY_APP_HOME/"]
            it.doLast { CreateStartScripts last ->
                last.unixScript.text = last.unixScript.text.replace("MY_APP_HOME", "\$APP_HOME")
                last.windowsScript.text = last.windowsScript.text.replace("MY_APP_HOME", "%~dp0..")
            }
        }
    }

    private TaskProvider<JavaExec> addRunImporter() {
        JavaPluginConvention javaPluginConvention =
                project.convention.getPlugin(JavaPluginConvention)

        SourceSet main =
                javaPluginConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        project.tasks.register(TASK_RUN_IMPORTER, JavaExec, new Action<JavaExec>() {
            @Override
            void execute(JavaExec run) {
                run.setDescription("Runs this project as the OMERO.importer application")
                run.setGroup(GROUP_APPLICATION)
                run.setClasspath(main.runtimeClasspath)
                run.setJvmArgs(DEFAULT_JVM_ARGS)
                run.setArgs(["containerImporter.xml", String.valueOf(project.buildDir)])
                run.setMain(MAIN_INSIGHT)
            }
        })
    }

    private void configurePackagerPlugin() {
        InstallOptionsContainer installOptionsContainer =
                project.extensions.getByName("deploy") as InstallOptionsContainer

        // Configure main install options (insight)
        InstallOptions main =
                installOptionsContainer.getByName(JavaPackagerPlugin.MAIN_DEPLOY_NAME)
        main.exe {
            it.icon = project.file("icons/omeroinsight.ico")
        }
        main.dmg {
            it.icon = project.file("icons/omeroinsight.icns")
        }
    }

}

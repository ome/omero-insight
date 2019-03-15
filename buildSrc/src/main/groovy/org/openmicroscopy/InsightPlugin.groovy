package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.file.CopySpec
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts
import org.gradle.jvm.tasks.Jar
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.extensions.InstallOptionsContainer

import javax.inject.Inject
import java.text.SimpleDateFormat

@CompileStatic
class InsightPlugin implements Plugin<Project> {

    public static final String GROUP_APPLICATION = ApplicationPlugin.APPLICATION_GROUP

    public static final String GROUP_BUILD = BasePlugin.BUILD_GROUP

    public static final String TASK_PROCESS_CONFIGS = "processConfigs"

    public static final String TASK_RUN_IMPORTER = "runImporter"

    public static final String TASK_OMERO_IMAGEJ_JAR = "imageJJar"

    public static final String TASK_IMPORTER_START_SCRIPTS = "importerStartScripts"

    public static final String MAIN_INSIGHT = "org.openmicroscopy.shoola.Main"

    public static final String MAIN_IMAGEJ = "org.openmicroscopy.shoola.MainIJPlugin"

    public static final String DISTRIBUTION_NAME_INSIGHT = "OMERO.insight"

    public static final String DISTRIBUTION_NAME_IMAGEJ = "OMERO.imagej"

    public static final String DISTRIBUTION_IMAGEJ = "imagej"

    private static final List DEFAULT_JVM_ARGS = ["-Xms256m", "-Xmx1024m"]

    private Project project

    private final ProjectLayout layout

    private final ProviderFactory providers

    @Inject
    InsightPlugin(ProjectLayout layout, ProviderFactory providers) {
        this.layout = layout
        this.providers = providers
    }

    @Override
    void apply(Project project) {
        this.project = project

        // We want these applied first
        project.pluginManager.apply(ApplicationPlugin)
        project.pluginManager.apply(JavaPackagerPlugin)

        // Configure java tasks
        addProcessConfigs()
        configureJarTask()
        configureApplicationPlugin()

        JavaPluginConvention javaPluginConvention =
                project.convention.getPlugin(JavaPluginConvention)

        SourceSet mainSrcSet =
                javaPluginConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        // Configure insight tasks
        addRunImporter(mainSrcSet)
        addCreateImageJJar(mainSrcSet)

        // Start scripts
        addInsightStartScript()

        // Distro configs
        configureMainDistribution()

        // Configure install options
        configurePackagerPlugin()
    }

    /**
     * Creates a jar similar to processResources in that it
     * copies config files from src dir to build dir.
     */
    private TaskProvider<Sync> addProcessConfigs() {
        TaskProvider<Sync> processConfigs = project.tasks.register(TASK_PROCESS_CONFIGS, Sync, new Action<Sync>() {
            @Override
            void execute(Sync sync) {
                sync.setDescription("Copies xml config files from src to build directory")
                sync.from("${project.projectDir}/src/config")
                sync.into("${project.buildDir}/config")
            }
        })

        project.tasks.named(JavaPlugin.CLASSES_TASK_NAME).configure { Task task ->
            task.dependsOn(processConfigs)
        }

        processConfigs
    }

    private void configureJarTask() {
        project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar).configure { Jar jar ->
            jar.doFirst(addManifest(MAIN_INSIGHT))
        }
    }

    private void configureApplicationPlugin() {
        JavaApplication javaApplication =
                project.extensions.getByName("application") as JavaApplication

        javaApplication.mainClassName = MAIN_INSIGHT
        javaApplication.applicationDefaultJvmArgs = DEFAULT_JVM_ARGS

        project.tasks.named(ApplicationPlugin.TASK_RUN_NAME, JavaExec).configure { JavaExec run ->
            run.setArgs(["container.xml", String.valueOf(project.buildDir)])
        }
    }

    private void configureMainDistribution() {
        DistributionContainer distributionContainer =
                project.extensions.getByName("distributions") as DistributionContainer

        // Copy files from src/config to install/<name>/config
        CopySpec configSpec = project.copySpec { CopySpec s ->
            s.from("src/config")
            s.into("config")
        }

        // Configure "main" distribution to create OMERO.insight
        Distribution main = distributionContainer.getByName(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
        main.baseName = DISTRIBUTION_NAME_INSIGHT
        main.contents.with(configSpec)

        // Create and configure imageJ distribution
        distributionContainer.create(DISTRIBUTION_IMAGEJ) { Distribution imageJ ->
            imageJ.baseName = DISTRIBUTION_NAME_IMAGEJ
            imageJ.contents.with(configSpec)

            CopySpec libChildSpec = project.copySpec()
            libChildSpec.into("lib")
            libChildSpec.from(project.tasks.named(TASK_OMERO_IMAGEJ_JAR))
            libChildSpec.from(getRuntimeClasspathConfiguration())

            CopySpec childSpec = project.copySpec()
            childSpec.with(libChildSpec)

            imageJ.contents.with(childSpec)
        }
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

    private TaskProvider<JavaExec> addRunImporter(SourceSet sourceSet) {
        project.tasks.register(TASK_RUN_IMPORTER, JavaExec, new Action<JavaExec>() {
            @Override
            void execute(JavaExec run) {
                run.setDescription("Runs this project as the OMERO.importer application")
                run.setGroup(GROUP_APPLICATION)
                run.setClasspath(sourceSet.runtimeClasspath)
                run.setJvmArgs(DEFAULT_JVM_ARGS)
                run.setArgs(["containerImporter.xml", String.valueOf(project.buildDir)])
                run.setMain(MAIN_INSIGHT)
            }
        })
    }

    private TaskProvider<Jar> addCreateImageJJar(SourceSet main) {
        project.tasks.register(TASK_OMERO_IMAGEJ_JAR, Jar, new Action<Jar>() {
            @Override
            void execute(Jar jar) {
                // This might not be the best way to ensure a parity of names
                Jar jarTask = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
                String imageJName = jarTask.archiveBaseName.get().replace("insight", "imagej")

                jar.setDescription("Creates a jar for use with ImageJ")
                jar.setGroup(GROUP_BUILD)
                jar.dependsOn(project.tasks.getByName(JavaPlugin.CLASSES_TASK_NAME))
                jar.from(main.output)
                jar.archiveBaseName.set(imageJName)
                jar.doFirst(addManifest(MAIN_IMAGEJ))
            }
        })
    }

    private TaskProvider<CreateStartScripts> addInsightStartScript() {
        project.tasks.register(TASK_IMPORTER_START_SCRIPTS, CreateStartScripts, new Action<CreateStartScripts>() {
            @Override
            void execute(CreateStartScripts startScripts) {
                startScripts.setDescription("Creates OS specific scripts to run the project as a JVM application.");
                startScripts.setClasspath(project.tasks.getByName(JavaPlugin.JAR_TASK_NAME).outputs.files +
                        project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME))
                startScripts.mainClassName = MAIN_INSIGHT
                startScripts.applicationName = "OMERO.importer"
                startScripts.outputDir = new File(project.buildDir, "scripts")
                startScripts.executableDir = "bin"
                startScripts.defaultJvmOpts = ["-Xms256m", "-Xmx1024m"]
            }
        })
    }

    private Action<? extends Task> addManifest(String mainClass) {
        return new Action<Jar>() {
            @Override
            void execute(Jar jar) {
                jar.manifest.attributes(createBasicManifest(mainClass))
                jar.manifest.attributes["Class-Path"] =
                        getRuntimeClasspathConfiguration().collect { it.name }.join(" ")
            }
        }
    }

    private Map<String, ?> createBasicManifest(String mainClass) {
        return ["Implementation-Title"  : project.name.replace("[^A-Za-z0-9]", ""),
                "Implementation-Version": project.version,
                "Built-Date"            : new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                "Built-JDK"             : System.getProperty("java.version"),
                "Built-Gradle"          : project.gradle.gradleVersion,
                "Main-Class"            : mainClass]
    }

    private Configuration getRuntimeClasspathConfiguration() {
        project.configurations.findByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
    }

}

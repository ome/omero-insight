package org.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.file.CopySpec

class DistributePlugin implements Plugin<Project> {

    public static final String DISTRIBUTION_NAME_INSIGHT = "OMERO.insight"

    public static final String DISTRIBUTION_NAME_IMAGEJ = "OMERO.imagej"

    public static final String DISTRIBUTION_IMAGEJ = "imagej"

    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.pluginManager.apply(DistributePlugin)
        project.pluginManager.apply(InsightBasePlugin)

        DistributionContainer distributionContainer =
                project.extensions.getByName("distributions") as DistributionContainer

        // Copy files from src/config to install/<name>/config
        CopySpec configSpec = project.copySpec { CopySpec s ->
            s.from("src/config")
            s.into("config")
        }

        configureMainDistribution(distributionContainer, configSpec)
        createImageJPluginDistribution(distributionContainer, configSpec)
    }

    private void configureMainDistribution(DistributionContainer distributionContainer, CopySpec configSpec) {
        // Configure "main" distribution to create OMERO.insight
        Distribution main = distributionContainer.getByName(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
        main.baseName = DISTRIBUTION_NAME_INSIGHT
        main.contents.with(configSpec)
    }

    private void createImageJPluginDistribution(DistributionContainer distributionContainer, CopySpec configSpec) {
        // Create and configure imageJ distribution
        distributionContainer.create(DISTRIBUTION_IMAGEJ) { Distribution imageJ ->
            imageJ.baseName = DISTRIBUTION_NAME_IMAGEJ
            imageJ.contents.with(configSpec)

            CopySpec libChildSpec = project.copySpec()
            libChildSpec.into("lib")
            libChildSpec.from(project.tasks.named(InsightBasePlugin.TASK_OMERO_IMAGEJ_JAR))
            libChildSpec.from(Utils.getRuntimeClasspathConfiguration(project))

            CopySpec childSpec = project.copySpec()
            childSpec.with(libChildSpec)

            imageJ.contents.with(childSpec)
        }
    }
}

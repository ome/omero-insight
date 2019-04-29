/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts
import org.gradle.api.tasks.bundling.Tar

@CompileStatic
class DistributePlugin implements Plugin<Project> {

    public static final String DISTRIBUTION_NAME_INSIGHT = "OMERO.insight"

    public static final String DISTRIBUTION_NAME_IMPORTER = "OMERO.importer"

    public static final String DISTRIBUTION_NAME_IMAGEJ = "OMERO.imagej"

    public static final String DISTRIBUTION_IMPORTER = "importer"

    public static final String DISTRIBUTION_IMAGEJ = "imagej"

    public static final String TASK_IMPORTER_START_SCRIPTS = "importerStartScripts"

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(InsightBasePlugin)
        project.pluginManager.apply(ApplicationPlugin)

        // Add CreateStartScripts task for importer, much like what the ApplicationPlugin does for "main" distribution
        addImporterCreateScriptsTask()

        DistributionContainer distributionContainer =
                project.extensions.getByName("distributions") as DistributionContainer

        // Copy files from src/config to install/<name>/config
        CopySpec configSpec = project.copySpec { CopySpec s ->
            s.from("src/config")
            s.into("config")
        }

        configureMainDistribution(distributionContainer, configSpec)
        createImporterDistribution(distributionContainer, configSpec)
        createImageJPluginDistribution(distributionContainer, configSpec)

        // Skip tar tasks
        project.tasks.withType(Tar).configureEach {
            it.setEnabled(false)
        }
    }

    private TaskProvider<CreateStartScripts> addImporterCreateScriptsTask() {
        project.tasks.register(TASK_IMPORTER_START_SCRIPTS, CreateStartScripts, new Action<CreateStartScripts>() {
            @Override
            void execute(CreateStartScripts css) {
                css.mainClassName = InsightBasePlugin.MAIN_INSIGHT
                css.defaultJvmOpts = InsightBasePlugin.DEFAULT_JVM_ARGS
                css.applicationName = "omero-importer"
                css.outputDir = new File(project.getBuildDir(), "scripts")
                css.executableDir = "bin"
                configureStartScripts(css)
            }
        })
    }

    private void configureMainDistribution(DistributionContainer distributionContainer, CopySpec configSpec) {
        // Configure "main" distribution to create OMERO.insight
        Distribution main = distributionContainer.getByName(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
        main.baseName = DISTRIBUTION_NAME_INSIGHT
        main.contents.with(configSpec)
    }

    private void createImporterDistribution(DistributionContainer distributionContainer, CopySpec configSpec) {
        // Create and configure imageJ distribution
        distributionContainer.create(DISTRIBUTION_IMPORTER) { Distribution importer ->
            importer.baseName = DISTRIBUTION_NAME_IMPORTER
            importer.contents.with(configSpec)

            CopySpec libChildSpec =
                    createLibSpec(project.tasks.named(JavaPlugin.JAR_TASK_NAME))

            CopySpec binChildSpec =
                    createBinSpec(project.tasks.named(TASK_IMPORTER_START_SCRIPTS))

            CopySpec childSpec = project.copySpec()
            childSpec.with(libChildSpec)
            childSpec.with(binChildSpec)

            importer.contents.with(childSpec)
        }
    }

    private void createImageJPluginDistribution(DistributionContainer distributionContainer, CopySpec configSpec) {
        // Create and configure imageJ distribution
        distributionContainer.create(DISTRIBUTION_IMAGEJ) { Distribution imageJ ->
            imageJ.baseName = DISTRIBUTION_NAME_IMAGEJ
            imageJ.contents.with(configSpec)

            CopySpec libChildSpec =
                    createLibSpec(project.tasks.named(InsightBasePlugin.TASK_OMERO_IMAGEJ_JAR))

            CopySpec childSpec = project.copySpec()
            childSpec.with(libChildSpec)

            imageJ.contents.with(childSpec)
        }
    }

    private CopySpec createBinSpec(TaskProvider<? extends Task> startScriptTask) {
        CopySpec binSpec = project.copySpec()
        binSpec.into("bin")
        binSpec.from(startScriptTask)
        binSpec.setFileMode(0755)
        return binSpec
    }

    private CopySpec createLibSpec(TaskProvider<? extends Task> jarTask) {
        CopySpec libSpec = project.copySpec()
        libSpec.into("lib")
        libSpec.from(jarTask)
        libSpec.from(Utils.getRuntimeClasspathConfiguration(project))
        return libSpec
    }

    static void configureStartScripts(CreateStartScripts css) {
        css.defaultJvmOpts += ["-Duser.dir=MY_APP_HOME/"]
        css.doLast { CreateStartScripts last ->
            last.unixScript.text = last.unixScript.text.replace("MY_APP_HOME", "\$APP_HOME")
            last.windowsScript.text = last.windowsScript.text.replace("MY_APP_HOME", "%~dp0..")
            // Fix for https://github.com/gradle/gradle/issues/1989
            last.windowsScript.text = last.windowsScript.text.replaceAll('set CLASSPATH=.*', 'set CLASSPATH=.;%APP_HOME%/lib/*')
        }
    }
}

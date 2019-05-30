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
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.jvm.tasks.Jar
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.extensions.InstallOptionsContainer
import org.openmicroscopy.extensions.implementation.WinOptions

@CompileStatic
class PackagerPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(DistributePlugin)
        project.pluginManager.apply(JavaPackagerPlugin)

        InstallOptionsContainer installOptionsContainer =
                project.extensions.getByName("deploy") as InstallOptionsContainer

        // Configure main install options (insight)
        InstallOptions main = installOptionsContainer.getByName(JavaPackagerPlugin.MAIN_DEPLOY_NAME)
        main.icon = "${project.projectDir}/icons/omeroInsight"
        main.arguments = ["container.xml"]
        main.exe(winOptions)
        main.msi(winOptions)

        createImporterInstaller(installOptionsContainer)
    }

    private void createImporterInstaller(InstallOptionsContainer container) {
        JavaExec exec = project.tasks.getByName(InsightPlugin.TASK_RUN_IMPORTER) as JavaExec
        Jar jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
        Sync distTask = project.tasks.getByName(
                "install${DistributePlugin.DISTRIBUTION_IMPORTER.capitalize()}Dist") as Sync

        // Create install option for importer
        container.create("importer", new Action<InstallOptions>() {
            @Override
            void execute(InstallOptions importer) {
                importer.icon = "${project.projectDir}/icons/omeroImporter.ico"
                importer.arguments = ["containerImporter.xml"]
                importer.mainClassName = exec.main
                importer.javaOptions = exec.jvmArgs
                importer.mainJar = jar.archiveFileName
                importer.applicationVersion = jar.archiveVersion
                importer.outputFile = "${project.buildDir}/packaged/${distTask.name}/${distTask.destinationDir.name}"
                importer.applicationName = distTask.destinationDir.name
                importer.sourceDir = distTask.destinationDir
                importer.sourceFiles.from(project.fileTree(distTask.destinationDir).include("**/*.*"))
                importer.exe(winOptions)
                importer.msi(winOptions)
            }
        })

        Platform.installerTypesAsString.each { String type ->
            String name = JavaPackagerPlugin.makeTaskName("importer", type)
            project.tasks.named(name).configure { Task task ->
                task.dependsOn(distTask)
            }
        }
    }

    static Action<? extends WinOptions> getWinOptions() {
        return new Action<WinOptions>() {
            @Override
            void execute(WinOptions opts) {
                opts.startMenuGroup = "OMERO insight"
            }
        }
    }

}

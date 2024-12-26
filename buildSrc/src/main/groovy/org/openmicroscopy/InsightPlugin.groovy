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
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.application.CreateStartScripts

@CompileStatic
class InsightPlugin implements Plugin<Project> {

    public static final String GROUP_APPLICATION = ApplicationPlugin.APPLICATION_GROUP

    public static final String TASK_RUN_IMPORTER = "runImporter"

    public static final String MAIN_INSIGHT = "org.openmicroscopy.shoola.Main"

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        // We want these applied first
        project.pluginManager.apply(InsightBasePlugin)
        project.pluginManager.apply(ApplicationPlugin)

        // Configure importer tasks
        addRunImporter()

        // Configure java tasks
        configureApplicationPlugin()
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
                run.setJvmArgs(InsightBasePlugin.DEFAULT_JVM_ARGS)
                run.setArgs(["containerImporter.xml", String.valueOf(project.buildDir)])
                run.setMain(MAIN_INSIGHT)
            }
        })
    }

    private void configureApplicationPlugin() {
        JavaApplication javaApplication =
                project.extensions.getByName("application") as JavaApplication

        javaApplication.mainClass.set(MAIN_INSIGHT)
        javaApplication.applicationDefaultJvmArgs = InsightBasePlugin.DEFAULT_JVM_ARGS

        project.tasks.named(ApplicationPlugin.TASK_RUN_NAME, JavaExec).configure {
            it.setArgs(["container.xml", String.valueOf(project.buildDir)])
        }

        project.tasks.named(ApplicationPlugin.TASK_START_SCRIPTS_NAME, CreateStartScripts).configure {
            Utils.configureStartScripts(it)
        }
    }

}

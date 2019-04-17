package org.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class PublishPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.pluginManager.apply(MavenPublishPlugin)
        project.pluginManager.apply(InsightBasePlugin)

        addPublishImageJJar()
    }

    private void addPublishImageJJar() {
        PublishingExtension publishing =
                project.extensions.getByName(PublishingExtension.NAME) as PublishingExtension

        publishing.publications.create("imageJPlugin", MavenPublication) { MavenPublication pub ->
            pub.artifact(project.tasks.getByName(InsightBasePlugin.TASK_OMERO_IMAGEJ_JAR))
            pub.setArtifactId("omero-imagej")
            pub.pom { MavenPom p ->
                p.licenses { MavenPomLicenseSpec spec ->
                    spec.license {
                        it.name.set("GNU General Public License, Version 2")
                        it.url.set("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                        it.distribution.set("repo")
                        it.comments.set("An omero plugin for ImageJ")
                    }
                }
                p.withXml { XmlProvider xp ->
                    Node repositoriesNode = xp.asNode().appendNode("repositories")
                    project.repositories.each { ArtifactRepository repo ->
                        if (repo instanceof MavenArtifactRepository) {
                            Node repositoryNode = repositoriesNode.appendNode("repository")
                            repositoryNode.appendNode("id", repo.name)
                            repositoryNode.appendNode("name", repo.name)
                            repositoryNode.appendNode("url", repo.url)
                        }
                    }
                }
            }
        }
    }

}

pipeline {
    agent {
        label 'testintegration'
    }

    environment {
        // Default credentials for testing on devspace
        MAVEN_SNAPSHOTS_REPO_URL = 'http://nexus:8081/nexus/repository/maven-internal/'
        MAVEN_USER = 'admin'
        MAVEN_PASSWORD = 'admin123'

        // Disable Gradle daemon
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }

    stages {
        stage('Build') {
            steps {
                // Currently running on a build node with multiple jobs so incorrect jar may be cached
                // (Moving to Docker should fix this)
                sh 'gradle --init-script init-ci.gradle publishToMavenLocal --refresh-dependencies'
            }
        }
        stage('Deploy') {
            steps {
                sh 'gradle --init-script init-ci.gradle publish'
                archiveArtifacts artifacts: 'build/distributions/*.zip'
            }
        }
    }

    post {
        always {
            // Cleanup workspace
            deleteDir()
        }
    }
}

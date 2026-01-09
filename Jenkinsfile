pipeline {
    agent any

    tools {
        // Maven configured in Jenkins as "M3"
        maven "M3"
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {

        stage('Checkout') {
            steps {
                // Clean workspace to avoid corrupted git state
                deleteDir()

                // Retry checkout in case of network glitch
                retry(3) {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [
                            // Shallow clone prevents large fetch failures
                            [$class: 'CloneOption', depth: 1, shallow: true, noTags: false]
                        ],
                        userRemoteConfigs: [[
                            url: 'https://github.com/Varaprasad-22/captone_backend.git'
                        ]]
                    ])
                }
            }
        }

        stage('Build') {
            steps {
                bat "mvn -Dmaven.test.failure.ignore=true clean package"
                   bat "docker compose down --remove-orphans"
                       bat "docker compose up --build -d"
            }
        }
    }
}

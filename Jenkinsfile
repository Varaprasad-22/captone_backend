pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }
 environment {
        SONAR_TOKEN = credentials('sonar-token')
    }
    stages {
        stage('Build') {
    steps {
        git branch: 'main', url: 'https://github.com/Varaprasad-22/captone_backend.git'
                bat "mvn -Dmaven.test.failure.ignore=true package"

                bat "docker compose down --remove-orphans"
    }
}
        

    }
}

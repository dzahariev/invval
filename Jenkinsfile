pipeline {
    // install JDK 17 and Maven on Jenkins node
    agent any
    tools {
        jdk 'JDK17'
        maven 'mvn3.9.0'
    }
    stages {
        stage ('init') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage("build") {
            steps {
                echo 'Build step started ...'
                sh 'mvn clean install'
            }
        }
        stage("clean") {
            steps {
                echo 'Clean step started ...'
                sh 'mvn clean'
            }
        }
    }
}

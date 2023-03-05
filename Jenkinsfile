pipeline {
    // install JDK 11 and Maven on Jenkins node
    agent any
    tools {
        jdk 'JDK11'
        maven 'mvn3.9.0'
    }
    stages {
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

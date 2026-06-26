pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log --oneline -5'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn --batch-mode clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn --batch-mode test -P unit-tests'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'main'
                    changeset 'src/main/**'
                    changeset 'src/test/**'
                    changeset 'pom.xml'
                }
            }
            steps {
                sh 'mvn --batch-mode test -P integration-tests'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Coverage Report') {
            steps {
                sh 'mvn --batch-mode jacoco:report'
            }
            post {
                always {
                    publishHTML(target: [
                        reportDir  : 'target/coverage-report',
                        reportFiles: 'index.html',
                        reportName : 'Coverage Report',
                        keepAll    : true
                    ])
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
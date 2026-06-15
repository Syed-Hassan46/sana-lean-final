pipeline {
    agent any

    environment {
        JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
        MAVEN_OPTS        = '-Xmx512m -XX:MaxMetaspaceSize=128m'
        MVN               = './mvnw --batch-mode --no-transfer-progress'
        TEST_REPORTS_DIR  = 'target/surefire-reports'
        COVERAGE_DIR      = 'target/coverage-report'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    triggers {
        pollSCM('H/5 * * * *')
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
                sh '''
                    java -version
                    ${MVN} clean compile
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                sh '''
                    ${MVN} test \
                        -P unit-tests \
                        -Djacoco.skip=false
                '''
            }
            post {
                always {
                    junit testResults: "${TEST_REPORTS_DIR}/*.xml",
                          allowEmptyResults: true
                    archiveArtifacts artifacts: "${TEST_REPORTS_DIR}/**",
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    changeset 'src/main/**'
                    changeset 'src/test/**'
                    changeset 'pom.xml'
                }
            }
            steps {
                sh '''
                    ${MVN} test \
                        -P integration-tests \
                        -Djacoco.skip=false
                '''
            }
            post {
                always {
                    junit testResults: "${TEST_REPORTS_DIR}/*.xml",
                          allowEmptyResults: true
                    archiveArtifacts artifacts: "${TEST_REPORTS_DIR}/**",
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Coverage Report') {
            steps {
                sh '''
                    ${MVN} verify \
                        -P all-tests \
                        -DskipTests=false \
                        jacoco:report
                '''
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing         : true,
                        alwaysLinkToLastBuild: false,
                        keepAll              : true,
                        reportDir            : "${COVERAGE_DIR}",
                        reportFiles          : 'index.html',
                        reportName           : 'JaCoCo Coverage Report'
                    ])
                    archiveArtifacts artifacts: "${COVERAGE_DIR}/**",
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Archive') {
            steps {
                sh 'echo "Archiving final artefacts..."'
                archiveArtifacts artifacts: 'target/**/*.jar,target/surefire-reports/**',
                                 allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline PASSED — all selected tests green.'
        }
        failure {
            echo 'Pipeline FAILED — review test results above.'
        }
        unstable {
            echo 'Pipeline UNSTABLE — one or more tests failed.'
        }
        always {
            cleanWs(
                cleanWhenNotBuilt: false,
                deleteDirs: true,
                disableDeferredWipeout: true,
                notFailBuild: true,
                patterns: [[pattern: '.gitignore', type: 'EXCLUDE']]
            )
        }
    }
}

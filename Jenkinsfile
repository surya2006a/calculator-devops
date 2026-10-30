
pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'surya2006a/suryasumeet-calculator'
        REGISTRY_CREDS = 'dockerhub-creds'
        NOTIFY_EMAIL = 'sumeet.surya.singh@gmail.com'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                    -t ${DOCKER_IMAGE}:${BUILD_NUMBER} \
                    -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${REGISTRY_CREDS}",
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS'
                )]) {
                    sh '''
                        echo "$DH_PASS" | docker login \
                        -u "$DH_USER" --password-stdin

                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker compose up -d --remove-orphans
                    docker compose ps

                    sleep 15

                    curl --fail http://localhost:8083/actuator/health
                '''
            }
        }
    }

    post {

        success {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "SUCCESS: ${JOB_NAME} #${BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <h2>Build Successful!</h2>

                    <p><b>Project:</b> ${JOB_NAME}</p>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Status:</b> SUCCESS</p>

                    <p>Your application was built, tested,
                    pushed to Docker Hub and deployed.</p>

                    <p><a href="${BUILD_URL}">View Build Details</a></p>
                """
            )
        }

        failure {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "FAILED: ${JOB_NAME} #${BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <h2>Build Failed!</h2>

                    <p><b>Project:</b> ${JOB_NAME}</p>
                    <p><b>Build Number:</b> ${BUILD_NUMBER}</p>
                    <p><b>Status:</b> FAILURE</p>

                    <p>Please check the Jenkins console output
                    to identify the error.</p>

                    <p><a href="${BUILD_URL}console">View Console Output</a></p>
                """
            )
        }

        aborted {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "ABORTED: ${JOB_NAME} #${BUILD_NUMBER}",
                body: "Build ${BUILD_NUMBER} of ${JOB_NAME} was aborted. ${BUILD_URL}"
            )
        }

        always {
            echo 'Pipeline execution finished. Email notification processed.'
        }
    }
}
```

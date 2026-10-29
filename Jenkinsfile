pipeline {
    agent any

    tools {
        maven 'Maven3'   // Manage Jenkins > Tools > Maven installations (name: Maven3)
        jdk   'JDK17'    // Manage Jenkins > Tools > JDK installations   (name: JDK17)
    }

    triggers {
        githubPush()             // GitHub webhook
        pollSCM('H/5 * * * *')   // fallback if webhook is not reachable
    }

    environment {
        DOCKER_IMAGE = 'your-dockerhub-username/calculator-api'   // <-- change this
        NOTIFY_EMAIL = 'your-email@example.com'                   // <-- change this
        REGISTRY_CREDS = 'dockerhub-creds'                        // Jenkins credentials ID
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
                    docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDS}",
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
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
                    export DOCKER_IMAGE=${DOCKER_IMAGE}
                    export IMAGE_TAG=${BUILD_NUMBER}
                    docker compose pull calculator || true
                    docker compose up -d --remove-orphans

                    echo "Waiting for the stack to become healthy..."
                    for i in $(seq 1 30); do
                        if docker compose exec -T nginx wget -qO- http://localhost/actuator/health | grep -q '"status":"UP"'; then
                            echo "Deployment healthy"
                            docker compose ps
                            exit 0
                        fi
                        sleep 5
                    done
                    echo "Health check failed"
                    docker compose logs --tail=50
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build succeeded and was deployed.\n\nImage: ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}\nDetails: ${env.BUILD_URL}"
        }
        failure {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build failed.\n\nCheck the console output: ${env.BUILD_URL}console"
        }
    }
}


pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'surya2006a/suryasumeet-calculator'
        NOTIFY_EMAIL = 'sumeetsurya.singh@iiitb.ac.in'
        REGISTRY_CREDS = 'dockerhub-creds'
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
                bat 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% -t %DOCKER_IMAGE%:latest .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${REGISTRY_CREDS}",
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS'
                )]) {
                    bat '''
                        docker login -u "%DH_USER%" --password-stdin
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    set IMAGE_TAG=%BUILD_NUMBER%
                    docker compose up -d --remove-orphans
                    docker compose ps
                    powershell -NoProfile -Command "Start-Sleep -Seconds 15; try { $r = Invoke-WebRequest -Uri 'http://localhost:8083/actuator/health' -UseBasicParsing; if ($r.StatusCode -ne 200) { exit 1 }; Write-Host $r.Content } catch { Write-Error $_; exit 1 }"
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution finished. Check stage results above.'
        }
    }
}

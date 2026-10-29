
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'surya2006a/suryasumeet-calculator'
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
                bat '''
                    wsl -d Ubuntu -- docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% -t %DOCKER_IMAGE%:latest -f /mnt/c/ProgramData/Jenkins/.jenkins/workspace/suryasumeet-calculator/Dockerfile /mnt/c/ProgramData/Jenkins/.jenkins/workspace/suryasumeet-calculator
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
                    bat '''
                        wsl -d Ubuntu -- bash -lc "echo '%DH_PASS%' | docker login -u '%DH_USER%' --password-stdin"
                        if errorlevel 1 exit /b 1

                        wsl -d Ubuntu -- docker push %DOCKER_IMAGE%:%BUILD_NUMBER%
                        if errorlevel 1 exit /b 1

                        wsl -d Ubuntu -- docker push %DOCKER_IMAGE%:latest
                        if errorlevel 1 exit /b 1
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    wsl -d Ubuntu -- bash -lc "cd /mnt/c/ProgramData/Jenkins/.jenkins/workspace/suryasumeet-calculator && docker compose up -d --remove-orphans && docker compose ps"
                    if errorlevel 1 exit /b 1

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

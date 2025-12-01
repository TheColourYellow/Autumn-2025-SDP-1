pipeline {
    agent any
    tools {
        maven 'Maven3'
    }
    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${JMETER_HOME}\\bin;${env.PATH}"

        SONARQUBE_SERVER = 'SonarQubeServer'
        SONAR_TOKEN = 'squ_ce4bae748802340e4d224893eafa16ba7e382c46'

        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17'
        JMETER_HOME = 'C:\\Program Files\\tools\\apache-jmeter-5.6.3'

        DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
        DOCKERHUB_REPO = 'bookstore'
        DOCKER_IMAGE = 'sdp1-project'
        DOCKER_IMAGE_TAG = 'latest'
        DOCKERHUB_USER = 'tarunip'
    }
    stages {
        stage('Setup Maven') {
            steps {
                script {
                    def mvnHome = tool name: 'Maven3', type: 'maven'
                    env.PATH = "${mvnHome}/bin:${env.PATH}"
                }
            }
        }
        stage('Checkout') {
            steps {
                git branch:'main', url:'https://github.com/TheColourYellow/Autumn-2025-SDP-1.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                bat 'mvn jacoco:report'
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    bat """
                        ${tool 'SonarScanner'}\\bin\\sonar-scanner ^
                        -Dsonar.projectKey=bookstore ^
                        -Dsonar.sources=src ^
                        -Dsonar.projectName=BookstoreApp ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.login=${env.SONAR_TOKEN} ^
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }
        stage('Non-Functional Test') {
            steps {
                bat 'jmeter -n -t tests/performance/demo.jmx -l result.jtl'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    bat "docker build -t ${DOCKERHUB_USER}/${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} ."
                }
            }
        }
        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_USER}/${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'result.jtl', allowEmptyArchive: true
            perfReport sourceDataFiles: 'result.jtl'
        }
    }
}
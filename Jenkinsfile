#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        DOCKER_REPO = 'aminaaahmed323/demo-app' // Define DOCKER_REPO if needed
    }
    stages {
        stage('Increment version') {
            steps {
                script {
                    echo 'Incrementing app version...'
                    sh "mvn build-helper:parse-version versions:set " +
                       "-DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} " +
                       "versions:commit"
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "${version}-${env.BUILD_NUMBER}"
                }
            }
        }
        stage('Build app') {
            steps {
                script {
                    echo 'Building the application...'
                    sh 'mvn clean package'
                }
            }
        }
        stage('Build image') {
            steps {
                script {
                    echo 'Building the Docker image...'
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t ${env.DOCKER_REPO}:${env.IMAGE_NAME} ."
                        sh "echo $PASS | docker login -u $USER --password-stdin ${env.DOCKER_REPO_SERVER}"
                        sh "docker push ${env.DOCKER_REPO}:${env.IMAGE_NAME}"
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying Docker image...'
                }
            }
        }
    }
}

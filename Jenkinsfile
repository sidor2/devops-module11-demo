def gv

pipeline {   
    agent any
    tools {
        maven 'Maven'
    }
    
    environment {
        DOCKER_REPO_SERVER = '851725525319.dkr.ecr.us-west-2.amazonaws.com'
        DOCKER_REPO = '${DOCKER_REPO_SERVER}/eks-demo-app'
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }

        stage("incerement version") {
            steps {
                script {
                    gv.incrementVersion()
                }
            }
        }

        stage("build jar") {
            steps {
                script {
                    gv.buildJar()

                }
            }
        }

        stage("build image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }

        stage("deploy") {
            environment {
                // KUBECONFIG = credentials('kubeconfig')
                APP_NAME = 'eks-demo-app'
            }
            steps {
                script {
                    gv.deployApp()
                }
            }
        }

        stage("commit to github") {
            steps {
                script {
                    gv.commitToGithub('github-key', 'devops-module11-demo', 'main')
                }
            }
        }

    }
} 

def gv

pipeline {   
    agent any
    tools {
        maven 'Maven'
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
                    gv.commitToGithub('github-key', 'eks-demo-app', 'main')
                }
            }
        }

    }
} 

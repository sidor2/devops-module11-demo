def incrementVersion() {
    echo 'incrementing the version...'
    sh 'mvn build-helper:parse-version versions:set \
        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
        versions:commit'
    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = matcher[0][1]
    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
}


def buildJar() {
    echo 'building the application...'
    sh 'mvn clean package'
}

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'jenkins-access', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t ilsoldier/eks-demo-app:${IMAGE_NAME} .'
        sh 'echo $PASS | docker login -u $USER --password-stdin'
        sh 'docker push ilsoldier/eks-demo-app:${IMAGE_NAME}'
    }
}

def deployApp() {
    echo 'deploying the application...'
    // sh 'kubectl create deployment nginx-deployment --image=ilsoldier/eks-demo-app:${IMAGE_NAME}'
    sh 'envsubst < Kubernetes/deployment.yaml | kubectl apply -f -'
    sh 'envsubst < Kubernetes/service.yaml | kubectl apply -f -'
}

def commitToGithub(String sshkey, String reponame, String branchname) {
    sshagent(["$sshkey"]) {
        sh 'git config --global user.email "jenkins@example.com"'
        sh 'git config --global user.name "jenkins"'

        sh 'git status'
        sh 'git branch'
        sh 'git config --list'

        sh "git remote set-url origin git@github.com:sidor2/$reponame\\.git"
        sh 'git add .'
        sh 'git commit -m "ci: version bump"'
        sh "git push origin HEAD:$branchname"
    }
}

return this

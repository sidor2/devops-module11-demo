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
    // withCredentials([usernamePassword(credentialsId: 'ecr-creds', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
    sh '''
    aws ecr get-login-password --region us-west-2 --profile iam-roles-anywhere | \
    docker login --username AWS --password-stdin 851725525319.dkr.ecr.us-west-2.amazonaws.com
    '''
    sh 'docker build -t ${DOCKER_REPO}:${IMAGE_NAME} .'
    sh 'echo $PASS | docker login -u $USER --password-stdin ${DOCKER_REPO_SERVER}'
    sh 'docker push ${DOCKER_REPO}:${IMAGE_NAME}'
    // }
}

def deployApp() {
    echo 'deploying the application...'
    // Step 1: Check current deployment status (optional but useful for debugging)
    sh '''
    echo "Current Deployment Status:"
    kubectl get deployments -n default
    '''
    
    // Step 2: Update the deployment with the new image or configuration
    sh '''
    kubectl set image deployment/java-app java-app=${DOCKER_REPO}:${IMAGE_NAME} -n default
    '''
    
    // Step 3: Verify the update (optional but recommended)
    sh '''
    echo "Updated Deployment Status:"
    kubectl rollout status deployment/java-app -n default
    '''
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

@Library('deploy')
import deploy

def deployLib = new deploy()

node {
    def commitHash, commitHashShort, commitUrl
    def repo = "navikt"
    def application = "fpsoknad-mottak"
    def committer, committerEmail, changelog, releaseVersion, nextVersion // metadata
    def mvnHome = tool "maven-3.3.9"
    def mvn = "${mvnHome}/bin/mvn"
    def appConfig = "nais.yaml"
    def dockerRepo = "repo.adeo.no:5443"
    def groupId = "nais"
    def zone = 'fss'
    def namespace = 'default'

    stage("Checkout") {
        cleanWs()
        echo 'Checking out..'
        withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
            withEnv(['HTTPS_PROXY=http://webproxy-internett.nav.no:8088']) {
                sh(script: "git clone --single-branch --branch version2 https://${token}:x-oauth-basic@github.com/${repo}/${application}.git .")
            }
        }
        echo 'Getting git statuses..'
        commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        commitUrl = "https://github.com/${repo}/${application}/commit/${commitHash}"
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)
        releaseVersion = "${env.major_version}.${env.BUILD_NUMBER}-${commitHashShort}"
        echo 'Changelog ${changelog}'
        echo 'commitHash ${commitHash}'
        echo 'commitHashShort ${commitHashShort}'
        echo 'commitUrl ${commitUrl}'
        echo 'Notifying github..'
        notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'pending', "Build #${env.BUILD_NUMBER} has started")
        echo 'Notofied github OK..'
        currentBuild.displayName = "${releaseVersion}"
    }

    stage("Build and test only") {
        try {
            sh "${mvn} versions:set -B -DnewVersion=${releaseVersion}"
            sh "mkdir -p /tmp/${application}"
            sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${application} -B -e"
            slackSend([
                color  : 'good',
                message: "Build <${env.BUILD_URL}|#${env.BUILD_NUMBER}> (<${commitUrl}|${commitHashShort}>) of ${repo}/${application}@master by ${committer} passed  (${changelog})"
            ])
        }
        catch (Exception e) {
            currentBuild.result = 'FAILURE'
            slackSend([
                color  : 'danger',
                message: "Build <${env.BUILD_URL}|#${env.BUILD_NUMBER}> (<${commitUrl}|${commitHashShort}>) of ${repo}/${application}@master by ${committer} failed (${changelog})"
            ])
        }
        finally {
            junit '**/target/surefire-reports/*.xml'
        }
        sh "${mvn} versions:revert"
        notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'success', "Build #${env.BUILD_NUMBER} has finished")
    }
}
    
  def notifyGithub(owner, repo, context, sha, state, description) {
    def postBody = [
        state      : "${state}",
        context    : "${context}",
        description: "${description}",
        target_url : "${env.BUILD_URL}"
    ]
    def postBodyString = groovy.json.JsonOutput.toJson(postBody)

    withEnv(['HTTPS_PROXY=http://webproxy-internett.nav.no:8088']) {
        withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
            sh """
                curl -H 'Authorization: token ${token}' \
                    -H 'Content-Type: application/json' \
                    -X POST \
                    -d '${postBodyString}' \
                    'https://api.github.com/repos/${owner}/${repo}/statuses/${sha}'
            """
        }
    }
  
}

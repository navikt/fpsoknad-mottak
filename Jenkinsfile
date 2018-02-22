@Library('deploy')
import deploy

def deployLib = new deploy()

node {
    def commitHash, commitHashShort, commitUrl, currentVersion
    def repo = "navikt"
    def application = "fpsoknad-mottak"
    def committer, committerEmail, changelog, releaseVersion, nextVersion // metadata
    def mvnHome = tool "maven-3.3.9"
    def mvn = "${mvnHome}/bin/mvn"
    def appConfig = "nais.yaml"
    //def dockerRepo = "repo.adeo.no:5443"
    def dockerRepo = "docker.adeo.no:5000"
    def branch = "master"
    def groupId = "nais"
    def environment = 't1'
    def zone = 'fss'
    def namespace = 'default'

    stage("Checkout") {
        cleanWs()
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                sh(script: "git clone https://github.com/${repo}/${application}.git .")
        }
        commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        releaseVersion = "${env.major_version}.${env.BUILD_NUMBER}-${commitHashShort}"
        currentBuild.displayName = "${releaseVersion}"
        commitUrl = "https://github.com/${repo}/${application}/commit/${commitHash}"
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)
        notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'pending', "Build #${env.BUILD_NUMBER} has started")
    }

    stage("Build & publish") {
       try {
         sh "${mvn} versions:set -B -DnewVersion=${releaseVersion}"
         sh "mkdir -p /tmp/${application}"
         sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${application} -B -e"
          slackSend([
               color: 'good',
               message: "Build <${env.BUILD_URL}|#${env.BUILD_NUMBER}> (<${commitUrl}|${commitHashShort}>) of ${repo}/${application}@master by ${committer} passed  (${changelog})"
           ])
       }
       catch (Exception e) {
           slackSend([
               color: 'danger',
               message: "Build <${env.BUILD_URL}|#${env.BUILD_NUMBER}> (<${commitUrl}|${commitHashShort}>) of ${repo}/${application}@master by ${committer} failed (${changelog})"
           ])
        }
        finally {
            junit '**/target/surefire-reports/*.xml'
        }
        sh "docker build --build-arg version=${releaseVersion} --build-arg app_name=${application} -t ${dockerRepo}/${application}:${releaseVersion} ."
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexusUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            sh "curl --fail -v -F r=m2internal -F hasPom=false -F e=yaml -F g=${groupId} -F a=${application} -F " + "v=${releaseVersion} -F p=yaml -F file=@${appConfig} -u ${env.USERNAME}:${env.PASSWORD} http://maven.adeo.no/nexus/service/local/artifact/maven/content"
            sh "docker push ${dockerRepo}/${application}:${releaseVersion}"
        }
        sh "${mvn} versions:revert"
        notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'success', "Build #${env.BUILD_NUMBER} has finished")
  }

    stage("Deploy to preprod") {
        callback = "${env.BUILD_URL}input/Deploy/"
        environment = "${env.testenv}"
        def deploy = deployLib.deployNaisApp(application, releaseVersion, environment, zone, namespace, callback, committer).key
       try {
            timeout(time: 15, unit: 'MINUTES') {
                input id: 'deploy', message: "Check status here:  https://jira.adeo.no/browse/${deploy}"
            }
        } catch (Exception e) {
            throw new Exception("Deploy feilet :( \n Se https://jira.adeo.no/browse/" + deploy + " for detaljer", e)
        }   
    }

    stage("Tag") {
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
             withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                 sh ("git tag -a ${releaseVersion} -m ${releaseVersion}")
             }
       }
      notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'success', "Build #${env.BUILD_NUMBER} has finished")
    }
}

def notifyGithub(owner, repo, context, sha, state, description) {
    def postBody = [
            state: "${state}",
            context: "${context}",
            description: "${description}",
            target_url: "${env.BUILD_URL}"
    ]
    def postBodyString = groovy.json.JsonOutput.toJson(postBody)

    withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
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

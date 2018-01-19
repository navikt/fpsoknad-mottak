@Library('deploy')
import deploy

def deployLib = new deploy()

node {
    def commitHash, commitHashShort, commitUrl, currentVersion
    def repo = "navikt"
    def application = "fpsoknad-oppslag"
    def committer, committerEmail, changelog, pom, releaseVersion, nextVersion // metadata
    def mvnHome = tool "maven-3.3.9"
    def mvn = "${mvnHome}/bin/mvn"
    def appConfig = "nais.yaml"
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
        commitUrl = "https://github.com/${repo}/${application}/commit/${commitHash}"
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)
        notifyGithub(repo, application, 'continuous-integration/jenkins', commitHash, 'pending', "Build #${env.BUILD_NUMBER} has started")
    }

    stage("Initialize") {
        pom = readMavenPom file: 'pom.xml'
        releaseVersion = pom.version.tokenize("-")[0]
    }

   // stage("Valildate version and dependencies") {
   //    sh "${mvn} -Pvalidation validate"
    //}

    stage("Build, test and install artifact") {
       try {
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
  }
    stage("Release") {
            sh "${mvn} versions:set -B -DnewVersion=${releaseVersion} -DgenerateBackupPoms=false"
            sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${application} -B -e"
            sh "docker build --build-arg version=${releaseVersion} --build-arg app_name=${application} -t ${dockerRepo}/${application}:${releaseVersion} ."
            sh "git commit -am \"set version to ${releaseVersion} (from Jenkins pipeline)\""
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
              withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                   sh ("git push https://${token}:x-oauth-basic@github.com/navikt/fpsoknad-oppslag.git master")
                   sh ("git tag -a ${application}-${releaseVersion} -m ${application}-${releaseVersion}")
                   sh ("git push https://${token}:x-oauth-basic@github.com/navikt/fpsoknad-oppslag.git --tags")
               }
            }
    }
    stage("Publish artifact") {
            pathInRepo = "no/nav/foreldrepenger"
            sh "${mvn} clean deploy -DskipTests -B -e"
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexusUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                sh "curl --fail -v -u ${env.USERNAME}:${env.PASSWORD} --upload-file ${appConfig} https://repo.adeo.no/repository/raw/${pathInRepo}/${application}/${releaseVersion}/nais.yaml"
            }
            sh "docker push ${dockerRepo}/${application}:${releaseVersion}"
    }


    stage("Deploy to t") {
        callback = "${env.BUILD_URL}input/Deploy/"
        deployLib.testCmd(releaseVersion)
        deployLib.testCmd(committer)
        def deploy = deployLib.deployNaisApp(application, releaseVersion, environment, zone, namespace, callback, committer).key
        echo "Check status here:  https://jira.adeo.no/browse/${deploy}"
    }

    // Add test of preprod instance here

    stage("Update project version") {
        def versions = releaseVersion.tokenize(".");
        nextVersion = versions[0] +  "." + versions[1] +  "." + (versions[2].toInteger() + 1) + "-SNAPSHOT"
        sh "${mvn} versions:set -B -DnewVersion=${nextVersion} -DgenerateBackupPoms=false"
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
             withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                 sh "git commit -am \"updated to new dev-version ${nextVersion} after release by ${committer}\""
                 sh ("git push https://${token}:x-oauth-basic@github.com/navikt/fpsoknad-oppslag.git master")
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

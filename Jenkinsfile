
node {
   def commitHash, commitHashShort, commitUrl, currentVersion
   def repo = "navikt"
   def application = "fpsoknad-oppslag"
   def committer, committerEmail, changelog, pom, releaseVersion, nextVersion // metadata
   def mvnHome = tool "maven-3.3.9"
   def mvn = "${mvnHome}/bin/mvn"
   def appConfig = "nais.yaml"
   def dockerRepo = "repo.adeo.no:5443"
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

   stage("Publish artifacts") {
      sh "${mvn} clean deploy -DskipTests -B -e"
      withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexusUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
         sh "curl --fail -v -u ${env.USERNAME}:${env.PASSWORD} --upload-file ${appConfig} https://repo.adeo.no/repository/raw/${groupId}/${application}/${releaseVersion}/nais.yaml"
         sh "docker login -u ${env.USERNAME} -p ${env.PASSWORD} ${dockerRepo} && docker push ${dockerRepo}/${application}:${releaseVersion}"
      }
   }

   stage("Deploy to preprod") {
      callback = "${env.BUILD_URL}input/Deploy/"
      testCmd(releaseVersion)
      testCmd(committer)
      def deploy = deployNaisApp(application, releaseVersion, environment, zone, namespace, callback, committer).key
      echo "Check status here:  https://jira.adeo.no/browse/${deploy}"
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

def deployNaisApp(app, version, environment, zone, namespace, callback, reporter) {
   parsedEnvironment = getEnvironmentId(environment)
   parsedZone = getZone(zone)

   println("Init deploy with the following input")
   println("Application: \t ${app}")
   println("Version: \t ${version}")
   println("Environment: \t ${environment} (translated to: ${parsedEnvironment})")
   println("Zone: \t ${zone} (translated to: ${parsedZone})")
   println("Namespace: \t ${namespace}")
   println("On behalf of: \t ${reporter}")
   println("Will callback on ${callback}")

   withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'jiraServiceUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
      def postBody = [
         fields: [
            project          : [key: 'DEPLOY'],
            issuetype        : [id: '14302'],
            customfield_14811: [id: parsedEnvironment, value: parsedEnvironment],
            customfield_14812: "${app}:${version}",
            customfield_19413: namespace,
            customfield_19610: [id: parsedZone, value: parsedZone],
            customfield_17410: callback,
            customfield_19015: [id: "22707", value: "Yes"],
            summary          : "Automatisk deploy på vegne av ${reporter}"
         ]
      ]

      def postBodyString = groovy.json.JsonOutput.toJson(postBody)
      def base64encoded = "${env.USERNAME}:${env.PASSWORD}".bytes.encodeBase64().toString()


      def response = httpRequest url: 'https://jira.adeo.no/rest/api/2/issue/', customHeaders: [[name: "Authorization", value: "Basic ${base64encoded}"]], consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: postBodyString
      def slurper = new groovy.json.JsonSlurper()
      return slurper.parseText(response.content);
   }
}

def testCmd(arg){
   println ("Echo " + arg)
   return "shiny " + arg
}

def getEnvironmentId(environment) {
   envMap = [
      'u1': '16657',
      't1': '16557',
      't5': '16561',
      't6': '16562',
      't7': '16563',
      't11': '16567',
      'q0': '16824',
      'q1': '16825',
      'q2': '16652',
      'q6': '16648',
      'p' : '17658'
   ]
   if (environment.isInteger()) {
      return environment //Assume its already correct
   } else {
      return envMap[environment]
   }
}

def getZone(zone) {
   zoneMap = [
      'fss': '23451',
      'sbs': '23452'
   ]
   if (zone.isInteger()) {
      return zone //Assume its already correct
   } else {
      return zoneMap[zone]
   }
}



pipeline {
    agent any
    tools {
        maven 'Maven3'
    }
    stages {
        stage ('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/chenchu84/DevOps-Demo-WebApp.git']]])
            }
        }
        stage ('StaticCodeAnalysis') {
            steps {
                    withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonarqube') {
                    sh 'mvn sonar:sonar -D sonar.login=admin -D sonar.password=admin'
                }
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean install'
            }
             post {
                 always {
                     jiraSendBuildInfo branch: 'https://tcs-devops-case-study.atlassian.net/browse/DC-1', site: 'tcs-devops-case-study.atlassian.net'
                 }
            }
        }
        stage ('JiraNotification') {
            steps {
                jiraComment body: 'Build is Success', issueKey: 'DC-1'
            }
        }
        stage ('DeployTest') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://35.192.223.169:8080')], contextPath: 'QAWebapp', war: '**/*.war'
            }
        }
        stage ('SonarBuild') {
            steps {
                sh 'mvn package'
            }
        }
        stage ('SlackNotificationBuild') {
           steps {
                slackSend channel: 'alerts', message: 'Test Deployment Success', teamDomain: 'friends-dover', tokenCredentialId: 'SlackNotifications'
           }
        }
        stage ('jFrogserver') {
            steps {
                rtServer (
                    id: 'artifactory',
                    url: 'https://devopsscriptedpipeline.jfrog.io/artifactory',
                    username: 'jenkins',
                    password: 'jenkins',
                    bypassProxy: true,
                     timeout: 300)
            }
        }
        stage ('jFrogserverupload') {
            steps {
                rtUpload (
                    serverId: 'artifactory',
                        spec: '''{
                        "files": [{"pattern": "**/*.war", "target": "jenkins/WEBPOC/AVNCommunication/1.0/" }] }''',
                    buildName: 'descriptivepipeline1',
                    buildNumber: '50')
            }
        }
        stage ('Testbuild') {
            steps {
                sh 'mvn test -f functionaltest/pom.xml'
            }
        }
        stage('UnitTest'){
            steps{
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '\\functionaltest\\target\\surefire-reports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
            }
        }
        stage('PerformanceTest'){
            steps{
                blazeMeterTest credentialsId: 'BlazeMeter', testId: '8656444.taurus', workspaceId: '683047'
            }
        }
        stage ('DeployToProd') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://35.192.223.169:8080')], contextPath: 'ProdWebapp', war: '**/*.war'
            }
        }
        stage ('ProdBuild') {
            steps {
                sh 'mvn clean install -f Acceptancetest/pom.xml'
            }
        }
        stage('SanityTest'){
            steps{
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '\\Acceptancetest\\target\\surefire-reports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
            }
        }
        stage ('SlackNotificationProd') {
            steps {
                slackSend channel: 'alerts', message: 'Prod Deployment Success', teamDomain: 'friends-dover', tokenCredentialId: 'SlackNotifications'
            }
        }
    }
    
}

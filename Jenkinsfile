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
                    jiraComment body: 'Build is Successful', issueKey: 'DC-1'
                }
            }
        }
        stage ('DeployTest') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://35.192.223.169:8080')], contextPath: 'QAWebapp', war: '**/*.war'
            }
            post {
                always {
                    jiraSendDeploymentInfo environmentId: 'Test', environmentName: 'test', environmentType: 'test', serviceIds: ['http://35.192.223.169:8080/QAWebapp'], site: 'tcs-devops-case-study.atlassian.net', state: 'successful'
                    jiraComment body: 'Test Deployment Successful', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: 'Test Deployment Success', teamDomain: 'friends-dover', tokenCredentialId: 'SlackNotifications'
                }
            }
        }
        stage ('SonarBuild') {
            steps {
                sh 'mvn package'
            }
        }
        stage ('Artifactory') {
            steps {
                rtServer (
                    id: 'artifactory',
                    url: 'https://devopsscriptedpipeline.jfrog.io/artifactory',
                    username: 'jenkins',
                    password: 'jenkins',
                    bypassProxy: true,
                     timeout: 300)
                 rtUpload (
                    serverId: 'artifactory',
                        spec: '''{
                        "files": [{"pattern": "**/*.war", "target": "jenkins/WEBPOC/AVNCommunication/1.0/" }] }''',
                    buildName: 'descriptivepipeline1',
                    buildNumber: '50')
            }
        }
        stage ('UnitTest') {
            steps {
                sh 'mvn test -f functionaltest/pom.xml'
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
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://34.122.114.228:8080')], contextPath: 'ProdWebapp', war: '**/*.war'
            }
            post {
                always {
                    jiraSendDeploymentInfo environmentId: 'Prod', environmentName: 'prod', environmentType: 'production', serviceIds: ['http://34.122.114.228:8080/ProdWebapp'], site: 'tcs-devops-case-study.atlassian.net', state: 'successful'
                    jiraComment body: 'Deployment successful', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: 'Prod Deployment Successful', teamDomain: 'friends-dover', tokenCredentialId: 'SlackNotifications'
                }
            }
        }
        stage ('SanityTest') {
            steps {
                sh 'mvn clean install -f Acceptancetest/pom.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '\\Acceptancetest\\target\\surefire-reports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
            }
        }
    }
    
}

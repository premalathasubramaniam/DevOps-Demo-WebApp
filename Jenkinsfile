pipeline {
    agent any
    tools {
        maven 'Maven3.6.3'
    }
    stages {
        stage ('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git']]])
            }
        }
        stage ('Static Code Analysis') {
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
                     slackSend channel: 'alerts', message: "Build is Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
                }
            }
        }
        stage ('DeployTest') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://13.68.186.95:8080')], contextPath: 'QAWebapp', war: '**/*.war'
            }
            post {
                always {
                    jiraSendDeploymentInfo environmentId: 'Test', environmentName: 'test', environmentType: 'test', serviceIds: ['http://13.68.186.95:8080/QAWebapp'], site: 'tcs-devops-case-study.atlassian.net', state: 'successful'
                    jiraComment body: 'Test Deployment Successful', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: "Deploy To Test Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
                }
            }
        }
        stage ('Artifactory') {            
            steps {                
                rtServer (
                        id: 'artifactory',
                        url: 'https://devopsscriptedpipeline.jfrog.io/artifactory',
                        username: 'jenkins',
                        password: 'Sai@feb2202',
                        bypassProxy: true,
                        timeout: 300
                )                
                rtUpload (
                serverId: 'artifactory',
                spec: '''{ "files": [{"pattern": "**/*.war", "target": "jenkins/WEBPOC/AVNCommunication/1.0/"}]}''',
                    buildName: 'descriptivepipeline1',
                    buildNumber: '50')               
            }
        }         
        stage ('Unit Test') {
            steps {
                sh 'mvn test -f functionaltest/pom.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '\\functionaltest\\target\\surefire-reports', reportFiles: 'index.html', reportName: 'Unit Test Report', reportTitles: ''])
            }
        }
        stage('Performance Test'){
            steps{
                blazeMeterTest credentialsId: 'blazemeter', testId: '8666515.taurus', workspaceId: '685131'
            }
        }
        stage ('Deploy To Prod') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://34.121.139.252:8080')], contextPath: 'ProdWebapp', war: '**/*.war'
            }
            post {
                always {
                    jiraSendDeploymentInfo environmentId: 'Prod', environmentName: 'prod', environmentType: 'production', serviceIds: ['http://34.121.139.252:8080/ProdWebapp'], site: 'tcs-devops-case-study.atlassian.net', state: 'successful'
                    jiraComment body: 'Deployment Successful', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: "Deploy To Production Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
                }
            }
        }
        stage ('Sanity Test') {
            steps {
                sh 'mvn clean install -f Acceptancetest/pom.xml'
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '\\Acceptancetest\\target\\surefire-reports', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])
                slackSend channel: 'alerts', message: "Sanity Test Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
            }
        }
    }
    
}

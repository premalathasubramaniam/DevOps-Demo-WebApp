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
                    jiraComment body: 'Build is Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)', issueKey: 'DC-1'
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
                    jiraComment body: 'Test Deployment Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: "Deploy To Test Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
                }
            }
        }
        stage ('Artifactory') {
            steps {
                rtServer (
                    id: 'artifactory',
                    url: 'https://tcsdevops10.jfrog.io/artifactory',
                    username: 'deploy',
                    password: 'Pappu$1990',
                    bypassProxy: true,
                     timeout: 300)
                 rtUpload (
                    serverId: 'artifactory',
                        spec: '''{
                        "files": [{"pattern": "**/*.war", "target": "devops-casestudy/" }] }''',
                    buildName: 'devops-casestudy',
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
                blazeMeterTest credentialsId: 'blazemeter', testId: '8666515.taurus', workspaceId: '685131'
            }
        }
        stage ('DeployToProd') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '', url: 'http://52.150.20.154:8080')], contextPath: 'ProdWebapp', war: '**/*.war'
            }
            post {
                always {
                    jiraSendDeploymentInfo environmentId: 'Prod', environmentName: 'prod', environmentType: 'production', serviceIds: ['http://52.150.20.154:8080/ProdWebapp'], site: 'tcs-devops-case-study.atlassian.net', state: 'successful'
                    jiraComment body: 'Deployment successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)', issueKey: 'DC-1'
                    slackSend channel: 'alerts', message: "Deploy To Prod Successful ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", teamDomain: 'friends-dover', tokenCredentialId: 'slack-alert'
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

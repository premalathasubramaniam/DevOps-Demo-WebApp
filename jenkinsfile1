pipeline {
  agent any
  stages {
    stage('Static Code Analysis') { // Get code
      steps {
        // get code from our Git repository
        git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('build') {
      steps {
      //sh 'mvn --version'
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('UI Test') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('Performance Test') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('Deploy To QA') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('Store Artifact') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('Deploy To Prod') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
    stage('Sanity Test') {
      steps {
      git 'https://github.com/premalathasubramaniam/DevOps-Demo-WebApp.git'
      }
    }
  }
}

#!groovy
@Library('jenkins-shared-lib@main') _
pipeline {
	agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: ubuntu
            image: ubuntu:20.04
            command:
            - cat
            tty: true
        '''
    }
  }
	stages {
    	stage('CD Pipeline') {
    		steps {
        			git branch: 'master', changelog: false, poll: false, url: 'https://github.com/keidar/DevOpsExpertsAdvanced.git'
          script{
              sh 'apt-get install sudo'
              sh 'sudo apt-get install curl gpg'
              sh 'curl https://baltocdn.com/helm/signing.asc | gpg --dearmor |  tee /usr/share/keyrings/helm.gpg > /dev/null'
              sh 'sudo apt-get install apt-transport-https --yes'
              sh 'echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | tee /etc/apt/sources.list.d/helm-stable-debian.list'
              sh 'apt-get update'
              sh 'apt-get install helm'
              sh " helm upgrade --install rabbit ./k8s-project/helm_charts/rabbitmq"
              sh " helm upgrade --install rabbit ./k8s-project/helm_charts/consumer"
              sh " helm upgrade --install rabbit ./k8s-project/helm_charts/producer"
          }
    	}
    }
  }
}


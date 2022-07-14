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
          - name: docker
            image: docker:latest
            command:
            - cat
            tty: true
            volumeMounts:
             - mountPath: /var/run/docker.sock
               name: docker-sock
          volumes:
          - name: docker-sock
            hostPath:
              path: /var/run/docker.sock
        '''
    }
	}
	stages {
    	stage('CI Pipeline') {
    		steps {
        		container('docker') {
        			git branch: 'master', changelog: false, poll: false, url: 'https://github.com/avielb/rmqp-example.git'
          script{
            jenkinsSharedLibrary.docker_login()
            sh "docker build -t keidarb/k8s_project:consumer -f consumer/Dockerfile consumer/."
            sh "docker build -t keidarb/k8s_project:producer -f producer/Dockerfile producer/."
            sh "docker push keidarb/k8s_project:consumer"
            sh "docker push keidarb/k8s_project:producer"
            }
        	}
    	}
    }
  }
    post {
      	always {
			container('docker') {
			sh 'docker logout'
			}
      	}
    }
}


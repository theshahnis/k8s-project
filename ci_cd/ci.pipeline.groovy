
podTemplate(label: 'docker-agent', containers: [containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)],
  volumes: [hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')]
){
    node('docker-agent') {
        stage('Git clone') {
            git 'https://github.com/avielb/rmqp-example.git'
        }
        stage('Build docker images') {
            container('docker') {
                sh "docker login -u theshahnis -p af3735a8-fc9f-4956-a017-5f48f7127c6e"
                sh "docker build -f consumer/Dockerfile -t theshahnis/k8s-project:consumer consumer/."
                sh "docker build -f producer/Dockerfile -t theshahnis/k8s-project:producer producer/."
            }
        }
        stage('Push docker images') {
            container('docker') {
                sh "docker push theshahnis/k8s-project:consumer"
                sh "docker push theshahnis/k8s-project:producer"
            }
        }
    }
}
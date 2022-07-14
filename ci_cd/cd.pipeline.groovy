podTemplate(label: 'helm-agent', containers: [containerTemplate(name: 'helm', image: 'alpine/k8s:1.21.13', command: 'cat', ttyEnabled: true)]){
    node('helm-agent') {
        stage('Git clone') {
            git branch: 'main', url: 'https://github.com/theshahnis/k8s-project.git'
        }
        stage('Helm upgrade') {
            container('helm') {
                dir("helm_charts"){
                    sh "helm upgrade --install -n default producer producer"
                    sh "helm upgrade --install -n default producer consumer"
                }
            }
        }
    }
}
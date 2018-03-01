#!/usr/local/bin/groovy

def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
        containerTemplate(name: 'gradle', image: 'gradle:4.5.1-jdk9', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)
],
        volumes: [
                hostPathVolume(mountPath: '/home/gradle/.gradle', hostPath: '/tmp/jenkins/.gradle'),
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
        ]) {
    node(label) {
        stage('Run docker') {
            container('docker') {
                sh "docker run hello-world"
            }
        }
        stage('Run helm') {
            container('helm') {
                sh "helm init"
                sh "helm upgrade --install redis stable/redis"
            }
        }
    }
}


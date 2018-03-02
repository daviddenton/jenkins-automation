#!/usr/local/bin/groovy

def label = "worker-${UUID.randomUUID().toString()}"

def version = "latest"
def imageName = "myimage"
def region = "eu-west-2"

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

        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false,
                  extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/daviddenton/jenkins-automation']]])

        withCredentials([
                string(credentialsId: 'aws_ecr_password', variable: 'awsEcrPassword'),
                string(credentialsId: 'aws_account_number', variable: 'awsAccountNumber')
        ]) {
            stage('Build image and push to registry') {
                container('docker') {
                    def imageTag = "${awsAccountNumber}.dkr.ecr.${region}.amazonaws.com/dsp/${imageName}:${version}"
                    sh "docker build -t ${imageTag} ."

                    withAWS(credentials:'aws_credentials') {
                        sh ecrLogin()
                        sh "docker image ls"
                        sh "docker push ${imageTag}"
                    }
                }
            }
        }

        stage('Deploy to k8s') {
            container('helm') {
                sh "helm init"
                sh "helm upgrade --install redis stable/redis"
            }
        }
    }
}


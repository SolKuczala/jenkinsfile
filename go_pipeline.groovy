pipeline {
      options { timestamps () }
      agent {label 'Dockerlab-Agent'}
      tools {
        go 'GO'
      }

    stages {
          stage('Clean Workspace') {
            steps {
                script {
                    deleteDir()
                }
               }
        }

        stage('Checkout') {
              steps {
                script {
                      checkout([$class : 'GitSCM', ,
                    doGenerateSubmoduleConfigurations: false,
                    submoduleCfg : [],
                    branches : [[name: "${env.BUILD_BRANCH}"]],
                    userRemoteConfigs : [[credentialsId: "${GIT_CREDENTIAL}", url: "${SOURCE_GIT_URL}"]]])
                }
              }
        }

        stage('Build & deploy go'){
                steps {
                        sh "docker-compose up --build"
                    }
                }
        }
       
    post { 
        success { 
            sh 'echo done ;)'//end program ctrl+c
        }    
    }
}
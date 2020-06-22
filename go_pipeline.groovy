pipeline {
      options { timestamps () }
      agent {label 'Dockerlab-Agent'}
      tools {
        go 'GO'
      }

    parameters {
        choice(name: 'MODE', choices: ['api', 'cli'], description: 'Pick something')
        string(name: 'PORT_OPTION', defaultValue: '9090', description: 'insert desired local port')
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
                input{
                    message "choose the file you want to build"
                    parameters {
                           choice(name: 'DOCKER_OPTION', choices: ['docker-compose', 'dockerfile'], description: 'Pick something')
                    }
                }
                
                steps {
                    script{   
                        if (env.DOCKER_OPTION == 'dockerfile'){
                            sh "docker build ."
                            //worked
                        }else {
                            sh "docker run -e MODE=${params.MODE} -p ${params.PORT_OPTION}:9090 -it {id or docker tag, como y de donde lo saco}"
                        }
                        
                    }
                    //"docker run -e MODE=${params.MODE} -p ${params.PORT_OPTION}:9090 -it {id or docker tag}"puerto
                    //sh 'docker run -e MODE=cli -it(${params.OPTIONS}) {id or docker tag}'
                }
        }
    }    
    post { 
        success { 
            sh 'echo done ;)'//end program ctrl+c
        }    
        //failure {
        //    mail to: soledad.kuczala@synclab.net, subject: 'The Pipeline failed :('
        //}
    }
}
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Unit Tests') {
            steps {
                withCredentials([file(credentialsId: 'snowflake-kafka-connector-test-creds', variable: 'CREDFILE')]) {
                    sh 'mv $CREDFILE profile.json'
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}

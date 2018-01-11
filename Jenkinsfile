node {
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      git 'https://github.com/NixLabAdmin/Opencell.git/'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
      mvnHome = tool 'Maven 3.5.0'
   }
   stage('Build') {
      // Run the maven build
      if (isUnix()) {
         sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
      } else {
         bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
      }
   }
   stage('Results') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }
   
   stage('SonarQube analysis') {
	git 'https://github.com/NixLabAdmin/Opencell.git/'
    def scannerHome = tool 'SonarQube Scanner NiXLabTOOLS1';
    withSonarQubeEnv('SonarServer-NixLabTOOLS1') {
    sh "${scannerHome}/bin/sonar-runner"
  }
}
}
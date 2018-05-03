import com.sap.cloud.sdk.s4hana.pipeline.BashUtils

def call(Map parameters = [:]) {
    handleStepErrors(stepName: 'executeWhitesourceScanMaven', stepParameters: parameters) {
        final script = parameters.script
        def pomPath = parameters.pomPath ?: 'pom.xml'
        try {
            mavenExecute(
                script: script,
                m2Path: s4SdkGlobals.m2Directory,
                pomPath: pomPath,
                goals: 'org.whitesource:whitesource-maven-plugin:update',
                flags: "--batch-mode -Dorg.whitesource.orgToken=${BashUtils.escape(parameters.orgToken)} -Dorg.whitesource.product=${BashUtils.escape(parameters.product)} -Dorg.whitesource.checkPolicies=true"
            )
        } finally {
            archiveArtifacts artifacts: 'target/site/whitesource/**', allowEmptyArchive: true
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true,
                         reportDir   : 'target/site/whitesource',
                         reportFiles : 'index.html', reportName: 'Whitesource Policy Check (Maven)'])
        }
    }
}



# Build

    ../apache-maven-3.5.3/bin/mvn clean package

# Run Locally

Might not work cause of plugin issues

    ../apache-maven-3.5.3/bin/mvn azure-functions:run

# Deploy to Kubernetes

    ../apache-maven-3.5.3/bin/mvn azure-functions:deploy

# If you get javax/xml/bind/JAXBException

    export MAVEN_OPTS="$MAVEN_OPTS --add-modules java.xml.bind"


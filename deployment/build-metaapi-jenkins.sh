#!/bin/bash
source ~/.bash_profile

SVC_NAME=meta

sdk use java 17.0.2-open
sdk use maven 3.6.3

java -version
mvn  -version

echo "*******************"
export TAPIS_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)
echo "tapis-java TAPIS_VERSION = ${TAPIS_VERSION}"
export GIT_COMMIT=$(git log -1 --pretty=format:"%h")
export GIT_BRANCH=$GIT_BRANCH
echo "git commit revision $GIT_COMMIT"
echo "GIT_BRANCH=$GIT_BRANCH"
echo "*******************"

echo "************************ Building service: $SVC_NAME"
echo "************************ Full install: mvn clean install"
mvn clean install -DskipTests=true


cd ..

# Publish Image
echo "Publish = $Publish"
if [ "$Publish" == "true" ]; then
  cd $WORKSPACE
  docker login -u $USERNAME -p $PASSWD

	echo '************************ Building & Publishing Metadata Image'
    deployment/build-metaapi.sh dev
    echo '************************ push tapis/metaapi:dev'
	docker push tapis/metaapi:dev
	docker tag tapis/metaapi:dev tapis/metaapi:dev-${TAPIS_VERSION}
	docker push tapis/metaapi:dev-${TAPIS_VERSION}

  cd $WORKSPACE
 fi

# Deploy Service
echo "Deploy = $Deploy"
if [ "$Deploy" == "true" ]; then
  echo "************************ Deploying Service: $SVC_NAME"
  # SSH to cic02 as the tapisdev account with access to the tapisdev k8s namespace -
  # delete pod to make it automatically pull the latest.
  ssh -i /home/jenkins/.ssh/Jenkins-2018 tapisdev@cic02 'cd ~/tapis-kube/meta/api; ./burndown; ./burnup'
fi

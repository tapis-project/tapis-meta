#!/bin/bash


###########################################################
#  This script helps build images for service specified
#  It relies on Docker 18.06.0-ce and acts as a template
#  for future Tapis services and building an image from a
#  set of maven artifacts.
#
# environment : TAPIS_VERSION set to the version in tapis/pom.xml 
#
# usage : $TAPIS_ROOT/deployment/build-metaapi.sh <env branch>
#  example deployment/build-metaapi.sh dev
#
###########################################################

# Assumes mvn clean install has been run

PrgName=$(basename "$0")

USAGE="Usage: deployment/build-meta.sh { dev staging prod } [ -push ]"

export SRVC=meta
# $(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout) > versions.txt
# mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout > version.txt
export VER=$(cat $(pwd)/api/target/classes/tapis.version)
export TAPIS_ENV=$1
export BUILD_DATE=$(date +%Y%m%d%H%M)
echo "SRVC: ${SRVC}"
echo "VER: ${VER}"
echo "TAPIS_ENV: ${TAPIS_ENV}"
echo "BUILD_DATE: ${BUILD_DATE}"

# Check number of arguments
if [ $# -lt 1 ]; then
  echo $USAGE
  exit 1
fi

export TAPIS_ROOT=$(pwd)
export SRVC_DIR="${TAPIS_ROOT}/api/target"
export GIT_COMMIT=$(git log -1 --pretty=format:"%h")
echo "TAPIS_ROOT: ${TAPIS_ROOT}"
echo "SRVC_DIR: ${SRVC_DIR}"
echo "GIT_COMMIT: ${GIT_COMMIT}"
echo ""

# changes in image naming now make these equivalent except for production which uses latest.
export TAG=${TAPIS_ENV}
echo "TAG: ${TAG}"
echo ""

export IMAGE_NAME="tapis/${SRVC}api:${TAPIS_ENV}"
# Docker image is created with a unique tag: tapis/<SVC_NAME>-<ENV>-<VER>-<COMMIT>-<YYYYmmddHHMM>
export VERBOSE_IMAGE_NAME="tapis/${SRVC}api:${TAPIS_ENV}-${VER}-${GIT_COMMIT}-${BUILD_DATE}"
export IMAGE_BUILD_DIR="${TAPIS_ROOT}/deployment/tapis-${SRVC}api"
export BUILD_FILE="${IMAGE_BUILD_DIR}/Dockerfile"
export WAR_NAME=meta    # matches final name in pom file
echo "IMAGE_NAME: ${IMAGE_NAME}"
echo "IMAGE_BUILD_DIR: ${IMAGE_BUILD_DIR}"
echo "BUILD_FILE: ${BUILD_FILE}"
echo "WAR_NAME: ${WAR_NAME}"
echo ""

cd deployment/tapis-metaapi
echo  "for builds outside of jenkins run a mvn clean install -DskipTests"
echo " ***   do a build on metaapi  "
echo " ***   mvn clean install -DskipTests"
# mvn clean install -DskipTests

echo "";echo ""

cd ${TAPIS_ROOT}  # jump back up to project root directory

echo "***      removing any old service war meta directory from Docker build context"
echo "***      ${IMAGE_BUILD_DIR}/${WAR_NAME} "
if test -d "${IMAGE_BUILD_DIR}/${WAR_NAME}"; then
   echo " removing old war deployment ..."
   rm -rf "${IMAGE_BUILD_DIR}/${WAR_NAME}"
fi

echo "";echo ""

echo "***          copy the new service package directory to our docker build directory "
echo "***   cp -r ${SRVC_DIR}/${WAR_NAME} ${IMAGE_BUILD_DIR}/ "
            cp -r ${SRVC_DIR}/${WAR_NAME}  ${IMAGE_BUILD_DIR}/

echo "";echo ""

echo " ***   jump to the deployment build directory "
echo " ***   cd ${IMAGE_BUILD_DIR}"
             cd ${IMAGE_BUILD_DIR}

echo "";echo ""

echo "***      building the docker image from deployment directory docker build tapis-${SRVC}api/Dockerfile"
echo "***      docker image build --build-arg VER=${VER} --build-arg GIT_COMMIT=${GIT_COMMIT} --build-arg BUILD_DATE=${BUILD_DATE} -t ${IMAGE_NAME} . "
               docker image build --build-arg VER=${VER} --build-arg GIT_COMMIT=${GIT_COMMIT} --build-arg BUILD_DATE=${BUILD_DATE} -t ${IMAGE_NAME} .

echo "";echo ""

echo "***    push the image to docker hub "
echo "***      IMAGE_NAME should look like tapis/metaapi:${TAPIS_ENV}"
echo "***      IMAGE_NAME  ${IMAGE_NAME}"
echo "***      VERBOSE_IMAGE_NAME should look like tapis/${SRVC}api:${TAPIS_ENV}-${VER}-${GIT_COMMIT}-${BUILD_DATE}"
echo "***      VERBOSE_IMAGE_NAME ${VERBOSE_IMAGE_NAME}"
echo ""
echo "***      Push to docker hub : docker push ${IMAGE_NAME}"
               docker push "$IMAGE_NAME"
echo ""
echo "***      Tag and Push to private registry : docker push jenkins2.tacc.utexas.edu:5000/${VERBOSE_IMAGE_NAME}"
                docker tag ${IMAGE_NAME} jenkins2.tacc.utexas.edu:5000/${VERBOSE_IMAGE_NAME}
                docker push jenkins2.tacc.utexas.edu:5000/${VERBOSE_IMAGE_NAME}


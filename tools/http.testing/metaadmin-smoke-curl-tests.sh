#!/bin/bash

source $1
echo "Values used: "
cat $1

echo "------------------------ "
echo " # This script takes a single input variable specifying the"
echo " # environment file to use."
echo " # Assumptions:"
echo " #   The test user is always the meta admin user"
echo " #   with user name 'meta' and tenant 'admin'. The token will reflect these values."
echo " #   The meta/admin user has permissions set to allow full access to all Databases hosted. "
echo " #    A setup call to SK may be required. "
echo "------------------------ "
echo ""
export db="MetaTstDB"
export coll="MetaTstCollection"
export docId="metaTstDocument"
export aggr="somename"
export header2="X-Tapis-User: meta"
export header3="X-Tapis-Tenant: admin"
export header4="Content-Type: application/json"
echo ""
echo " ------------------------ "
echo "    Meta Service Tests "
echo " ------------------------ "
echo " ------------------------ "
echo "    General resource      "
echo " ------------------------ "
echo "Test: Healthcheck  "
RESULT=`curl $includeOutput -w '%{http_code}'  $base_url/v3/meta/healthcheck 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    Root resource "
echo " ------------------------ "
echo "Test: ListDBNames "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/ 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    DB resource "
echo " ------------------------ "

echo "Test: CreateDB for $db "
RESULT=`curl $includeOutput -X PUT -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db 2>/dev/null`
      if [[ $RESULT == *"201"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""


echo "Test: ListCollectionNames for $db "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo "Test: Metadata for $db "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/_meta 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    Collection resource "
echo " ------------------------ "

echo "Test: CreateCollection for $db/$coll "
RESULT=`curl $includeOutput -X PUT -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll 2>/dev/null`
      if [[ $RESULT == *"201"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo "Test: ListDocuments for $db/$coll "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo "Test: getCollectionSize for $db/$coll "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll/_size 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo "Test: getCollectionMetadata for $db/$coll "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll/_meta 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    Document resource "
echo " ------------------------ "

echo "Test: CreateDocument for $db/$coll/$docId "
RESULT=`curl $includeOutput  -X PUT -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" -d "{ \"name\": \"test document slt 4.28.2020-15:23\", \"jimmyList\":[\"1\",\"3\"],\"description\": \"PUT tstDoc1 for testing\"}" $base_url/v3/meta/$db/$coll/$docId 2>/dev/null`
       if [[ $RESULT == *"201"* ]]; then
         echo "PASS $RESULT"
 			else
 				echo "FAIL $RESULT"
 			fi
 echo ""

echo "Test: getDocument for $db/$coll/$docId "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll/$docId 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    Index resource "
echo " ------------------------ "
echo "Test: ListIndexes for $db/$coll/_indexes "
RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll/_indexes 2>/dev/null`
      if [[ $RESULT == *"200"* ]]; then
        echo "PASS $RESULT"
			else
				echo "FAIL $RESULT"
			fi
echo ""

echo " ------------------------ "
echo "    Aggregation resource "
echo " ------------------------ "
echo "Test: UseAggregation for $db/$coll/_aggr/$aggr "
# RESULT=`curl $includeOutput -w '%{http_code}' -H "$header1" -H "$header2" -H "$header3" -H "$header4" $base_url/v3/meta/$db/$coll/_aggr/$aggr 2>/dev/null`
#       if [[ $RESULT == *"200"* ]]; then
#         echo "PASS $RESULT"
# 			else
# 				echo "FAIL $RESULT"
# 			fi
# echo ""


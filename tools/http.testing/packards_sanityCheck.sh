#!/usr/bin/env bash

export TOKEN_MASTER=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJjYWQ3YWE1OC01OWZhLTRkYjQtYThhZS1kNmJmN2ZmMGEzY2UiLCJpc3MiOiJodHRwczovL21hc3Rlci5kZXZlbG9wLnRhcGlzLmlvL3YzL3Rva2VucyIsInN1YiI6Im1ldGFAbWFzdGVyIiwidGFwaXMvdGVuYW50X2lkIjoibWFzdGVyIiwidGFwaXMvdG9rZW5fdHlwZSI6ImFjY2VzcyIsInRhcGlzL2RlbGVnYXRpb24iOmZhbHNlLCJ0YXBpcy9kZWxlZ2F0aW9uX3N1YiI6bnVsbCwidGFwaXMvdXNlcm5hbWUiOiJtZXRhIiwidGFwaXMvYWNjb3VudF90eXBlIjoic2VydmljZSIsImV4cCI6MTU5ODE4NDQyNH0.zO1u3epR3vV0JIGPfBxTonx3zSJNo2t46vr48aU8SavaGWU-iJnLO8cRG66GkaOAYlDZxtDfqSANy21Fl7RgasAj-dQvgi9XctvD6SSZBVruErY7sN-UoQA3sCKPsukjSDBvTZQIU8GPIHIoZ8AXU5sCwxfqMYMgXcxxy3JT3oHahQAZXkJCgZvoS0Sryszl3Pl4LpwCGkL3ZfLOSjzyYuh8IEiSDEUKAHHRCJ9ldxtJT-APFGkdY1Xd53jB5jkjcwnDio9XO77U4eaO-PkwxCOpdWHYX-kfqhP40wCvX4l6BVxoyRdWTsbHK1lzSFmrhZ4C0aBafUS5kfWVBBeCPA

#  should return a 200 with an empty json array [] or later there may be documents in the array.
#  200 indicates all three containers  sercurity - core - mongodb are up and working.
curl -v -H "X-Tapis-Token: $TOKEN_MASTER" -H "X-Tapis-Tenant: master" -H "X-Tapis-User: meta" 'https://master.tapis.io/v3/meta/tapistst'

# test core - mongodb
# again 200 indicates core to mongodb is working
curl -v 'http://c002.rodeo.tacc.utexas.edu:32263/tapistst'

# need mongodb client here
export MONGO_URI=mongodb://restheart:al9WqcHKd1gbs6f7N7MZnKPY7FjbYJGgT%2FtwkcMN3cY%3D@c002.rodeo.tacc.utexas.edu:31171/?authSource=admin
mongo $MONGO_URI sanity.js

# commands in sanity.js
# db.adminCommand( { listDatabases: 1 } )
# exit

# reponse

# MongoDB shell version v4.2.0
# connecting to: mongodb://c002.rodeo.tacc.utexas.edu:31171/?authSource=admin&compressors=disabled&gssapiServiceName=mongodb
# Implicit session: session { "id" : UUID("812f48d3-b188-43e7-85c6-40b8ca7c02ef") }
# MongoDB server version: 4.2.0
# {
#         "databases" : [
#                 {
#                         "name" : "StreamsStagingDB",
#                         "sizeOnDisk" : 40960,
#                         "empty" : false
#                 },
#                 {
#                         "name" : "admin",
#                         "sizeOnDisk" : 102400,
#                         "empty" : false
#                 },
#                 {
#                         "name" : "config",
#                         "sizeOnDisk" : 110592,
#                         "empty" : false
#                 },
#                 {
#                         "name" : "local",
#                         "sizeOnDisk" : 73728,
#                         "empty" : false
#                 },
#                 {
#                         "name" : "tapistst",
#                         "sizeOnDisk" : 40960,
#                         "empty" : false
#                 }
#         ],
#         "totalSize" : 368640,
#         "ok" : 1
# }
# bye

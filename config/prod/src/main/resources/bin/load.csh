#!/bin/csh -f

#
# Configure
# 
set SERVER_CODE=~/code
set SERVER_CONFIG=~/config/config.properties
set SERVER_DATA=~/data
set SERVER=flase
echo "------------------------------------------------"
echo "Starting ...`/bin/date`"
echo "------------------------------------------------"
echo "SERVER_CODE = $SERVER_CODE"
echo "SERVER_DATA = $SERVER_DATA"
echo "SERVER_CONFIG = $SERVER_CONFIG"
echo "SERVER = $SERVER"

echo "    Run Createdb ...`/bin/date`"
cd $SERVER_HOME/admin/db
mvn install -PCreatedb -Drun.config.ts=$SERVER_CONFIG >&! mvn.log
if ($status != 0) then
    echo "ERROR running createdb"
    cat mvn.log
    exit 1
endif

echo "    Clear indexes ...`/bin/date`"
cd $SERVER_HOME/admin/lucene
mvn install -PReindex -Drun.config.ts=$SERVER_CONFIG -Dserver=$SERVER >&! mvn.log
if ($status != 0) then
    echo "ERROR running lucene"
    cat mvn.log
    exit 1
endif

echo "    Load SNOMEDCT ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PRF2-full -Drun.config.ts=$SERVER_CONFIG -Dserver=$SERVER -Dterminology=SNOMEDCT -Dversion=latest -Dinput.dir=$SERVER_DATA/snomedct-20140731-minif >&! mvn.log
if ($status != 0) then
    echo "ERROR loading SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Load ICD9CM ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PClaML -Drun.config.ts=$SERVER_CONFIG -Dserver=$SERVER -Dterminology=ICD9CM -Dversion=2013 -Dinput.file=$SERVER_DATA/icd9cm-2013.xml >&! mvn.log
if ($status != 0) then
    echo "ERROR loading ICD9CM"
    cat mvn.log
    exit 1
endif

echo "    Add SNOMEDCT project ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PProject -Drun.config.ts=$SERVER_CONFIG -Dserver=$SERVER \
  -Dname="Sample Project" -Ddescription="Sample project." \
  -Dterminology=SNOMEDCT -Dversion=latest \
  -Dscope.concepts=138875005 -Dscope.descendants.flag=true \
  -Dadmin.user=admin >&! mvn.log
if ($status != 0) then
    echo "ERROR adding project for SNOMEDCT"
    cat mvn.log
    exit 1
endif


echo "    Start SNOMEDCT editing ...`/bin/date`"
cd $SERVER_HOME/admin/release
mvn install -PStartEditingCycle -Drun.config.ts=$SERVER_CONFIG \
  -Dserver=$SERVER -Drelease.version=20150131 -Dterminology=SNOMEDCT \
  -Dversion=latest >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Start ICD9CM editing ...`/bin/date`"
cd $SERVER_HOME/admin/release
mvn install -PStartEditingCycle -Drun.config.ts=$SERVER_CONFIG \
  -Dserver=$SERVER -Drelease.version=20150101 -Dterminology=ICD9CM \
  -Dversion=2013 >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for ICD9CM"
    cat mvn.log
    exit 1
endif

echo "------------------------------------------------"
echo "Finished ...`/bin/date`"
echo "------------------------------------------------"

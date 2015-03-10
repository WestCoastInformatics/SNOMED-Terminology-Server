#!/bin/csh -f

#
# Configure
# 
set SERVER_CODE=~/code
set SERVER_CONFIG=~/config/config.properties
set SERVER_DATA=~/data

echo "------------------------------------------------"
echo "Starting ...`/bin/date`"
echo "------------------------------------------------"
echo "SERVER_CODE = $SERVER_CODE"
echo "SERVER_DATA = $SERVER_DATA"
echo "SERVER_CONFIG = $SERVER_CONFIG"

echo "    Run Createdb ...`/bin/date`"
cd $SERVER_HOME/admin/db
mvn install -PCreatedb -Drun.config=$SERVER_CONFIG >&! mvn.log
if ($status != 0) then
    echo "ERROR running createdb"
    cat mvn.log
    exit 1
endif

echo "    Clear indexes ...`/bin/date`"
cd $SERVER_HOME/admin/lucene
mvn install -PReindex -Drun.config=$SERVER_CONFIG >&! mvn.log
if ($status != 0) then
    echo "ERROR running lucene"
    cat mvn.log
    exit 1
endif

echo "    Load SNOMEDCT ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PRF2-snapshot -Drun.config=$SERVER_CONFIG -Dterminology=SNOMEDCT -Dinput.dir=$SERVER_DATA/snomedct-20140731-mini >&! mvn.log
if ($status != 0) then
    echo "ERROR loading SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Load ICD9CM ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PClaML -Drun.config=$SERVER_CONFIG -Dterminology=ICD9CM -Dinput.file=$SERVER_DATA/icd9cm-2013.xml >&! mvn.log
if ($status != 0) then
    echo "ERROR loading ICD9CM"
    cat mvn.log
    exit 1
endif

echo "    Add SNOMEDCT project ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn install -PAddProject -Drun.config=%SERVER_CONFIG% \
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
mvn install -PStartEditingCycle -Drun.config=$SERVER_CONFIG \
  -Drelease.version=20150131 -Dterminology=SNOMEDCT \
  -Dterminology.version=20140731 >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Start ICD9CM editing ...`/bin/date`"
cd $SERVER_HOME/admin/release
mvn install -PStartEditingCycle -Drun.config=$SERVER_CONFIG \
  -Drelease.version=20150101 -Dterminology=ICD9CM \
  -Dterminology.version=2013 >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for ICD9CM"
    cat mvn.log
    exit 1
endif

echo "------------------------------------------------"
echo "Finished ...`/bin/date`"
echo "------------------------------------------------"

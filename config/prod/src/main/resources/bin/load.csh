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
echo "SERVER_DATA = $SERVER_CODE"
echo "SERVER_CONFIG = $SERVER_CODE"

echo "    Run updatedb with hibernate.hbm2ddl.auto = create ...`/bin/date`"
cd $SERVER_HOME/admin/updatedb
mvn -Drun.config=$SERVER_CONFIG -Dhibernate.hbm2ddl.auto=create install >&! mvn.log
if ($status != 0) then
    echo "ERROR running updatedb"
    cat mvn.log
    exit 1
endif

echo "    Clear indexes ...`/bin/date`"
cd $SERVER_HOME/admin/lucene
mvn -Drun.config=$SERVER_CONFIG install >&! mvn.log
if ($status != 0) then
    echo "ERROR running lucene"
    cat mvn.log
    exit 1
endif

echo "    Load SNOMEDCT ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn -PRF2-snapshot -Drun.config=$SERVER_CONFIG -Dterminology=SNOMEDCT -Dinput.dir=$SERVER_DATA/snomedct-20140731-data install >&! mvn.log
if ($status != 0) then
    echo "ERROR loading SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Load ICD9CM ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn -PClaML -Drun.config=$SERVER_CONFIG -Dterminology=ICD9CM -Dinput.file=$SERVER_DATA/icd9cm-2013.xml install >&! mvn.log
if ($status != 0) then
    echo "ERROR loading ICD9CM"
    cat mvn.log
    exit 1
endif

echo "    Start SNOMEDCT editing ...`/bin/date`"
cd $SERVER_HOME/admin/release
mvn -PStartEditingCycle -Drun.config=$SERVER_CONFIG \
  -Drelease.version=20150131 -Dterminology=SNOMEDCT \
  -Dterminology.version=20140731 install >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Start ICD9CM editing ...`/bin/date`"
cd $SERVER_HOME/admin/release
mvn -PStartEditingCycle -Drun.config=$SERVER_CONFIG \
  -Drelease.version=2015 -Dterminology=ICD9CM \
  -Dterminology.version=2013 install >&! mvn.log
if ($status != 0) then
    echo "ERROR starting editing for ICD9CM"
    cat mvn.log
    exit 1
endif

echo "------------------------------------------------"
echo "Finished ...`/bin/date`"
echo "------------------------------------------------"

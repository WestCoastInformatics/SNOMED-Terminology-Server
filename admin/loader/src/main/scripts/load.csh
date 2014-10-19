#!/bin/csh -f

#
# Check OTF_CODE_HOME
#
if ($?SERVER_HOME == 0) then
	echo "ERROR: SERVER_HOME must be set"
	exit 1
endif

#
# Check SERVER_CONFIG
#
if ($?SERVER_CONFIG == 0) then
	echo "ERROR: SERVER_CONFIG must be set"
	exit 1
endif

echo "------------------------------------------------"
echo "Starting ...`/bin/date`"
echo "------------------------------------------------"

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
mvn -PSNOMEDCT -Drun.config=$SERVER_CONFIG install >&! mvn.log
if ($status != 0) then
    echo "ERROR loading SNOMEDCT"
    cat mvn.log
    exit 1
endif

echo "    Load ICD9CM ...`/bin/date`"
cd $SERVER_HOME/admin/loader
mvn -PICD9CM -Drun.config=$SERVER_CONFIG install >&! mvn.log
if ($status != 0) then
    echo "ERROR loading ICD9CM"
    cat mvn.log
    exit 1
endif

echo "------------------------------------------------"
echo "Finished ...`/bin/date`"
echo "------------------------------------------------"

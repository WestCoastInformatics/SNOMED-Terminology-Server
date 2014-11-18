@echo off
REM
REM This script is used to load OTF mapping project data for the development
REM environment.  It loads the mini version of SNOMEDCT and assumes you have
REM acquired your data from http://mapping.snomedtools.org/data/dev.zip
REM

REM
REM Set environment variables at system level
REM
REM set MVN_HOME=C:/apache-maven-3.2.1
REM set SERVER_HOME="C:/SNOMED-Terminology-Server"
REM set SERVER_CONFIG="C:/data/config.properties"
REM

echo ------------------------------------------------
echo Starting ...%date% %time%
echo ------------------------------------------------
if DEFINED MVN_HOME (echo MVN_HOME  = %MVN_HOME%) else (echo MVN_HOME must be defined
goto trailer)
if DEFINED SERVER_HOME (echo SERVER_HOME = %SERVER_HOME%) else (echo SERVER_HOME must be defined
goto trailer)
if DEFINED SERVER_CONFIG (echo SERVER_CONFIG = %SERVER_CONFIG%) else (echo SERVER_CONFIG must be defined
goto trailer)
set error=0
pause

echo     Run updatedb with hibernate.hbm2ddl.auto = create ...%date% %time%
cd %SERVER_HOME%/admin/updatedb
call %MVN_HOME%/bin/mvn -Drun.config=%SERVER_CONFIG% -Dhibernate.hbm2ddl.auto=create install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Clear indexes ...%date% %time%
cd %SERVER_HOME%/admin/lucene
call %MVN_HOME%/bin/mvn -Drun.config=%SERVER_CONFIG% install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load SNOMEDCT ...%date% %time%
cd %SERVER_HOME%/admin/loader
call %MVN_HOME%/bin/mvn -PSNOMEDCT -Drun.config=%SERVER_CONFIG% install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load ICD9CM ...%date% %time%
cd %SERVER_HOME%/admin/loader
call %MVN_HOME%/bin/mvn -PICD9CM -Drun.config=%SERVER_CONFIG% install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

:trailer
echo ------------------------------------------------
IF %error% NEQ 0 (
echo There were one or more errors.  Please reference the mvn.log file for details. 
set retval=-1
) else (
echo Completed without errors.
set retval=0
)
echo Starting ...%date% %time%
echo ------------------------------------------------

pause



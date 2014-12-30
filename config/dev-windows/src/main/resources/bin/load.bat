@echo off
REM
REM This script is used to load terminology server data for the development
REM environment.  This data can be found in the config/data folder of the
REM distribution.
REM

REM
REM Set environment variables at system level
REM
set SERVER_CODE="C:/workspace/SNOMED-Terminology-Server"
set SERVER_DATA="C:/termserver/data"
set SERVER_CONFIG="C:/termserver/config/config.properties"

echo ------------------------------------------------
echo Starting ...%date% %time%
echo ------------------------------------------------
if DEFINED SERVER_CODE (echo SERVER_CODE = %SERVER_CODE%) else (echo SERVER_CODE must be defined
goto trailer)
if DEFINED SERVER_DATA(echo SERVER_DATA= %SERVER_DATA%) else (echo SERVER_DATA must be defined
goto trailer)
if DEFINED SERVER_CONFIG (echo SERVER_CONFIG = %SERVER_CONFIG%) else (echo SERVER_CONFIG must be defined
goto trailer)
set error=0
pause

echo     Run updatedb with hibernate.hbm2ddl.auto = create ...%date% %time%
cd %SERVER_CODE%/admin/updatedb
call mvn -Drun.config=%SERVER_CONFIG% -Dhibernate.hbm2ddl.auto=create install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Clear indexes ...%date% %time%
cd %SERVER_CODE%/admin/lucene
call mvn -Drun.config=%SERVER_CONFIG% install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load SNOMEDCT ...%date% %time%
cd %SERVER_CODE%/admin/loader
call mvn -PRF2-snapshot -Drun.config=%SERVER_CONFIG% -Dterminology=SNOMEDCT -Dinput.dir=%SERVER_DATA%/snomedct-20140731-mini install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load ICD9CM ...%date% %time%
cd %SERVER_CODE%/admin/loader
call mvn -PClaML -Drun.config=%SERVER_CONFIG% -Dterminology=ICD9CM -Dinput.file=%SERVER_DATA%/icd9cm-2013.xml install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Start SNOMED editing ...%date% %time%
cd %SERVER_CODE%/admin/release
call mvn -PStartEditingCycle -Drelease.version=20150131 -Dterminology=SNOMEDCT -Dterminology.version=20140731 -Drun.config=%SERVER_CONFIG% install 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Start ICD9CM editing ...%date% %time%
cd %SERVER_CODE%/admin/release
call mvn -PStartEditingCycle -Drelease.version=20150101 -Dterminology=ICD9CM -Dterminology.version=2013 -Drun.config=%SERVER_CONFIG% install 1> mvn.log
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


@echo off
REM Copyright 2015 West Coast Informatics, LLC
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
set SERVER=false

echo ------------------------------------------------
echo Starting ...%date% %time%
echo ------------------------------------------------
if DEFINED SERVER_CODE (echo SERVER_CODE = %SERVER_CODE%) else (echo SERVER_CODE must be defined
goto trailer)
if DEFINED SERVER_DATA (echo SERVER_DATA= %SERVER_DATA%) else (echo SERVER_DATA must be defined
goto trailer)
if DEFINED SERVER_CONFIG (echo SERVER_CONFIG = %SERVER_CONFIG%) else (echo SERVER_CONFIG must be defined
goto trailer)
if DEFINED SERVER (echo SERVER = %SERVER%) else (echo SERVER must be defined
goto trailer)
set error=0
pause

echo     Run Createdb ...%date% %time%
cd %SERVER_CODE%/admin/db
call mvn install -PCreatedb -Drun.config.ts=%SERVER_CONFIG% 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Clear indexes ...%date% %time%
cd %SERVER_CODE%/admin/lucene
call mvn install -PReindex -Drun.config.ts=%SERVER_CONFIG% -Dserver=%SERVER% 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load SNOMEDCT ...%date% %time%
cd %SERVER_CODE%/admin/loader
call mvn install -PRF2-full -Drun.config.ts=%SERVER_CONFIG% -Dserver=%SERVER% -Dterminology=SNOMEDCT -Dinput.dir=%SERVER_DATA%/snomedct-20140731-minif 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Load ICD9CM ...%date% %time%
cd %SERVER_CODE%/admin/loader
call mvn install -PClaML -Drun.config.ts=%SERVER_CONFIG% -Dserver=%SERVER% -Dterminology=ICD9CM -Dversion=2013 -Dinput.file=%SERVER_DATA%/icd9cm-2013.xml 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Add SNOMED project ...%date% %time%
cd %SERVER_CODE%/admin/loader
call mvn install -PProject -Drun.config.ts=%SERVER_CONFIG% -Dserver=%SERVER% -Dname="Sample Project" -Ddescription="Sample project." -Dterminology=SNOMEDCT -Dversion=latest -Dscope.concepts=138875005 -Dscope.descendants.flag=true -Dadmin.user=admin 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Start SNOMED editing ...%date% %time%
cd %SERVER_CODE%/admin/release
call mvn install -PStartEditingCycle -Drelease.version=20150131 -Dserver=%SERVER% -Dterminology=SNOMEDCT -Dversion=latest -Drun.config.ts=%SERVER_CONFIG% 1> mvn.log
IF %ERRORLEVEL% NEQ 0 (set error=1
goto trailer)
del /Q mvn.log

echo     Start ICD9CM editing ...%date% %time%
cd %SERVER_CODE%/admin/release
call mvn install -PStartEditingCycle -Drelease.version=20150101 -Dserver=%SERVER% -Dterminology=ICD9CM -Dversion=2013 -Drun.config.ts=%SERVER_CONFIG% 1> mvn.log
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



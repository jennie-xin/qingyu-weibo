@REM Maven Wrapper startup script for Windows
@echo off
set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

@REM Find java.exe
set JAVA_EXE=java
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute
echo Error: JAVA_HOME is not set and no 'java' command could be found in your PATH.
goto end

:execute
"%JAVA_EXE%" -jar %WRAPPER_JAR% %*

:end

@echo off
setlocal enabledelayedexpansion

:: Déclaration des variables
set "work_dir=C:\Users\ASUS\Documents\GitHub\spring"
set "temp=%work_dir%\temp"
set "web_apps=C:\tomcat\webapps"


set "config_xml=%work_dir%\config"
set "frame=%work_dir%\Framework"
@REM set "src=%work_dir%\src"
set "lib=%work_dir%\lib"

set "war_name=sprint3"

:: Effacer le dossier [temp]
if exist "%temp%" (
    rd /s /q "%temp%"
)


mkdir "%temp%\WEB-INF\lib"
mkdir "%temp%\WEB-INF\classes"

xcopy "%config_xml%\*" "%temp%\WEB-INF"

@REM copier le jar
xcopy /s /i "%lib%\*.jar" "%temp%\WEB-INF\lib"

dir /s /B "%frame%\*.java" > frame.txt


dir /s /B "%lib%\*.jar" > libs.txt
:: Construire le classpath
set "classpath="
for /F "delims=" %%i in (libs.txt) do set "classpath=!classpath!%%i;"
:: Exécuter la commande javac
javac -d "%temp%\WEB-INF\classes" -cp "%classpath%" @frame.txt

@REM javac -d "%temp%\WEB-INF\classes" -cp "%classpath%" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del frame.txt
del libs.txt

cd "%frame%"
jar cvf "FrontController.jar" *
@REM jar cvf "ControllerAnnotation.jar" *
xcopy /s /i "*.jar" "%temp%\WEB-INF\lib"

cd "%temp%"
jar cf "%work_dir%\%war_name%.war" *

:: Effacer le fichier .war dans [web_apps] s'il existe
if exist "%web_apps%\%war_name%.war" (
    del /f /q "%web_apps%\%war_name%.war"
    del /f /q "%web_apps%\%war_name%"
)

:: Copier le fichier .war vers [web_apps]
copy /y "%work_dir%\%war_name%.war" "%web_apps%"


echo finished
endlocal
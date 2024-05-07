@echo off
setlocal enabledelayedexpansion

:: Déclaration des variables
set "work_dir=C:\Users\itu\Pictures\Git"
set "config_xml=%work_dir%\config"
set "frame=%work_dir%\Framework"
set "lib=%work_dir%\lib"



dir /s /B "%frame%\*.java" > sources.txt

dir /s /B "%lib%\*.jar" > libs.txt
:: Construire le classpath
set "classpath="
for /F "delims=" %%i in (libs.txt) do set "classpath=!classpath!%%i;"
:: Exécuter la commande javac
javac -d "%frame%" -cp "%classpath%" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt
del libs.txt

cd "%frame%"
jar cvf "FrontController.jar" *

echo finished
endlocal
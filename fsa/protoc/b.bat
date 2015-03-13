@echo off
set errorlevel=0
echo.
echo generate proto
protoc --java_out=./ FamilySaferPb.proto
echo success!

set curr_dir=%~dp0
set source_dir=%curr_dir%mobi
set dest_dir=%curr_dir%..\src\mobi
echo %curr_dir%
echo %source_dir%
echo %dest_dir%

echo copy proto java file to base
xcopy %source_dir% %dest_dir% /e /r /-y
rd /s /q mobi
echo success!
pause
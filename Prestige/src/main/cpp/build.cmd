@echo off
setlocal
if not defined JAVA_HOME (
  echo Set JAVA_HOME to a complete JDK installation.
  exit /b 1
)
set "PRESTIGE_VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
if not exist "%PRESTIGE_VSWHERE%" (
  echo Install Visual Studio Build Tools with the C++ x64 tools.
  exit /b 1
)
for /f "usebackq delims=" %%V in (`"%PRESTIGE_VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do set "PRESTIGE_VS=%%V"
if not defined PRESTIGE_VS exit /b 1
call "%PRESTIGE_VS%\VC\Auxiliary\Build\vcvars64.bat" >nul
if errorlevel 1 exit /b %errorlevel%
cd /d "%~dp0"
cl /nologo /LD /EHsc /MD /std:c++17 /O2 /I"%JAVA_HOME%\include" /I"%JAVA_HOME%\include\win32" bridge.cpp /link /OUT:..\resources\portable\portable-bridge.dll
exit /b %errorlevel%

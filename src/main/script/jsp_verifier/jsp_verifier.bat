@echo off
pushd %~p0
node bin\jsp_verifier %*
pause
popd
echo on

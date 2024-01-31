@echo off

set LIST=ABL ACF AIG AIL AXF BPL CBL CRF DBF DBL DGL HDF HDL HKL HMF HNF HNL HWF HWL IBK KBF KBL KDB KLP KYO LIN LTF MEZ MGF MRA MTL NHF NHL ORL PLI PST SFI SHL SLI TYL

set i=0
(for %%a in (%LIST%) do (
  echo "[%%a] build start!"
  echo "index: %i%"

  IF "%i%"=="0" (
      ant -buildfile %%a_build.xml create_run_jar
  ) ELSE (
      ant -buildfile %%a_build.xml create_run_jar  -Dmvn_build_skip=true
  )

  set /A i+=1

  echo "[%%a] build finished!"
))

echo "total build finished!!!!"

pause
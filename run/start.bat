::后台运行
%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
set prjPath=F:\project\pwd\commonutil\
java -jar %prjPath%run\base-1.0-SNAPSHOT.jar
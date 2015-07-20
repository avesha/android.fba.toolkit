adb shell su -c chmod 777 /data/data/com.sample.app2/databases
adb shell mkdir /sdcard/fba_tmp
adb push c:\database.db /sdcard/fba_tmp/database.db
adb shell su -c cp /sdcard/fba_tmp/database.db /data/data/com.sample.app2/databases
adb shell rm -r /sdcard/fba_tmp
timeout 5
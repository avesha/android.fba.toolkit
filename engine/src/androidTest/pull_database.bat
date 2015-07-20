adb shell su -c chmod 777 /data/data/com.sample.app2/databases/database.db
adb shell mkdir /sdcard/fba_tmp
adb shell su -c cp /data/data/com.sample.app2/databases/database.db /sdcard/fba_tmp
adb pull /sdcard/fba_tmp/database.db c:\database.db
adb shell rm -r /sdcard/fba_tmp
timeout 5
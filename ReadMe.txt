finding avd emulator port:

using ADB from C:\Users\<UserName>\AppData\Local\Android\Sdk\platform-tools

adb devices


add port forwarding:
adb forward tcp:4444 tcp:4444

go to:
http://localhost:4444/




links:
https://stackoverflow.com/questions/32863647/android-emulator-finding-port-number
https://developer.android.com/studio/run/emulator-networking


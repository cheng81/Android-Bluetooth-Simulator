# Android Bluetooth Simulator

## What is it

It's a tcp-based implementation of part of the android bluetooth API.
As for now, you can communicate between different emulators using the RFComm protocol, you can start a discovery phase and enable/disable the bluetooth.

What you need to do in order to use the simulator instead of the android API, is to change the import from `android.bluetooth` to `dk.itu.android.bluetooth` (and also add the `INTERNET` permission in the android manifest file).

There are two slight modifies to use the simulator:

 - call `getSerializableExtra` instead of `getParcelableExtra` to get a `BluetoothDevice` from the discovery intents:

 `BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);`

 you'll need to call

 `BluetoothDevice device = intent.getSerializableExtra(BluetoothDevice.EXTRA_DEVICE);`

 ..this is just because I didn't had time to look at how the parcel-stuff works. If you want, feel free to make the necessary modifications and push a commit ;)

 - call `BluetoothAdapter.SetContext(this);` at some point (the `onCreate` method is fine) in your activity/service.

## How do I use it?

You will have to follow some steps:

 - download everything in this repository
 - compile and install into at least 2 android emulators the *android-bt-simulator* application
  - a precompiled version of the application is provided in the *dist* directory
  - there are 3 source dicrectories, `src`, `src-sysactivities` and `src-testapp`. The application need all of them, but for the library jar you just need to compile the main one (`src`). The jar library is provided as convenience in the `dist-shared` directory
 - compile the *btsim-server* and run it.
  - there is an already precompiled jar in *dist*, execute `java -jar btsimserver.jar --help` to see some options (if you don't have the `adb` command in your path, you will need to set the `adb.path` variable)

You need to install the application because it will handle the *system activities* of the bluetooth, like switching on/off the radio and start a discovery.

Then you can create a new android project.
My preferred workflow, at this point, is:

 - add the necessary permissions for the bluetooth AND for internet access (required by the bt simulator).
 - add the *btsim.jar* to the libraries of the project
 - add the call to `BluetoothAdapter.SetContext(this);` in the `onCreate` method of the activity
 - start using the bluetooth API, importing the `dk.itu.android.bluetooth` version
 - set up two launch configurations for the project (one for each android emulator)

Then, depending on your needs, when you want to deploy the application on an actual device, you will need to delete or comment the `SetContext` call (as this will not compile, since it is not part of the android bluetooth api, but just a custom call for the simulator) and delete and re-import all the bluetooth stuff (this time using the `android.bluetooth` classes).

I hope this will be useful for somebody, I know that it implements just a subset of the API and it is not possible to put into play different devices than the emulators themselves, but until we got something in the android emulator itself, this is what we got :D
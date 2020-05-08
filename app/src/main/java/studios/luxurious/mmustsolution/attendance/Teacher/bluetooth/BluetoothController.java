package studios.luxurious.mmustsolution.attendance.Teacher.bluetooth;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.Closeable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothController implements Closeable {

    private final BluetoothAdapter bluetooth;
    private final BroadcastReceiverDelegator broadcastReceiverDelegator;
    private final Activity context;
    private boolean bluetoothDiscoveryScheduled;
    public BluetoothController(Activity context,BluetoothAdapter adapter) {
        this.context = context;
        this.bluetooth = adapter;
        this.broadcastReceiverDelegator = new BroadcastReceiverDelegator(context);
    }
    public boolean isBluetoothEnabled() {
        return bluetooth.isEnabled();
    }
    public void startDiscovery() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        if (!bluetooth.startDiscovery()) {
            Toast.makeText(context, "Error while starting device discovery!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void turnOnBluetooth() {
        bluetooth.enable();
    }

    public void turnOffBluetooth() {
        bluetooth.disable();
    }


    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }

    @Override
    public void close() {
        this.broadcastReceiverDelegator.close();
    }


    public boolean isDiscovering() {
        return bluetooth.isDiscovering();
    }

    public void cancelDiscovery() {
        if(bluetooth != null) {
            bluetooth.cancelDiscovery();
        }
    }

    public void turnOnBluetoothAndScheduleDiscovery() {
        this.bluetoothDiscoveryScheduled = true;
        turnOnBluetooth();
    }


    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }
        return deviceName;
    }

}

package studios.luxurious.mmustsolution.attendance.Student.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.Closeable;
import java.util.Arrays;

import studios.luxurious.mmustsolution.attendance.CryptUtil;
import studios.luxurious.mmustsolution.attendance.Student.Home_Fragment_Expandable;

public class BroadcastReceiverDelegator extends BroadcastReceiver implements Closeable {

    private final Context context;

    Home_Fragment_Expandable fragment_expandable;


    public BroadcastReceiverDelegator(Context context, Home_Fragment_Expandable fragment) {
        this.context = context;

        fragment_expandable = fragment;

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(this, filter);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                String bluetoothName = BluetoothController.deviceName(device);
                String[] fetched = bluetoothName.split("split");

                if (fetched.length > 1) {
                    String unit_ids = fetched[0].trim();
                    String start_time = fetched[1].trim();

                    try {
                        String decrypted_unit_ids = CryptUtil.decrypt(unit_ids);
                        String decrypted_start_time = CryptUtil.decrypt(start_time);

                        String[] unit_idss = decrypted_unit_ids.split(",");

                        String studentSelectedUnitId = fragment_expandable.getUnitID();

                        if (Arrays.asList(unit_idss).contains(studentSelectedUnitId)) {

                            fragment_expandable.dialogmessage("Lesson found", studentSelectedUnitId, decrypted_start_time);
                        }

                    } catch (Exception ignored) { }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:


                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:

                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:

                break;
            default:
                // Does nothing.
                break;
        }
    }


    @Override
    public void close() {
        context.unregisterReceiver(this);
    }
}

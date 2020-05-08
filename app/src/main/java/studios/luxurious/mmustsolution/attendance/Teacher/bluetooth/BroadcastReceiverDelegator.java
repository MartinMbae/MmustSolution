package studios.luxurious.mmustsolution.attendance.Teacher.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.io.Closeable;

public class BroadcastReceiverDelegator extends BroadcastReceiver implements Closeable {

    private final Context context;

    public BroadcastReceiverDelegator(Context context) {
        this.context = context;

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
        switch (action) {
            case BluetoothDevice.ACTION_FOUND :
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//78:7D:48:51:0F:FA
                Toast.makeText(context, "receivednae " + BluetoothController.deviceToString(device), Toast.LENGTH_SHORT).show();

                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :

                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED :

                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED :

                break;
            default :
                // Does nothing.
                break;
        }
    }


    @Override
    public void close() {
        context.unregisterReceiver(this);
    }
}

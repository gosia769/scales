package com.example.scales;

import android.bluetooth.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

public class ScalesActivity extends AppCompatActivity {

    private static final String TAG = "Iksde";

    private boolean mConnected;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mGatt;
    BluetoothDevice device;
    int retries = 10;
    TextView read;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scales);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        read = findViewById(R.id.weight_value);
        device = mBluetoothAdapter.getBondedDevices().stream().filter(mBluetoothAdapter -> mBluetoothAdapter.getName().startsWith("BL weight")).findAny().orElse(null);
        new Thread(() -> connectDevice(Objects.requireNonNull(device))).start();
    }


    private void connectDevice(BluetoothDevice device) {
        log("Connecting to " + device.getAddress());
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback, 2);
    }

    // Messaging

    private void sendMessage(String msg) {
        if (!mConnected) {
            return;
        }

        BluetoothGattCharacteristic characteristic =  mGatt.getServices().get(2)
                .getCharacteristics()
                .stream().filter(bluetoothGattCharacteristic -> bluetoothGattCharacteristic.getUuid().equals(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")))
                .findAny().orElse(null);


        byte[] message = msg.getBytes(Charset.forName("UTF-8"));

        if (characteristic != null) {
            characteristic.setValue(message);
            boolean success = mGatt.writeCharacteristic(characteristic);
            if (success) {
                log("Success");
            } else {
                log("Przypeks");
            }
        }

    }

    public void log(String msg) {
        Log.d(TAG, msg);
    }


    public void setConnected(boolean connected) {
        mConnected = connected;
    }

    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
        if (retries > 0) {
            retries--;
            connectDevice(device);
        }
    }

    private class GattClientCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            log("onConnectionStateChange newState: " + newState);

            if (status == BluetoothGatt.GATT_FAILURE) {
                log("Connection Gatt failure status " + status);
                disconnectGattServer();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to device " + gatt.getDevice().getAddress());
                setConnected(true);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from device");
                disconnectGattServer();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Device service discovery unsuccessful, status " + status);
                return;
            }

            mGatt.getServices().forEach(bluetoothGattService -> {
                log(bluetoothGattService.getUuid().toString());
            });

            BluetoothGattCharacteristic characteristic =  mGatt.getServices().get(2)
                    .getCharacteristics()
                    .stream().filter(bluetoothGattCharacteristic -> {
                        log(bluetoothGattCharacteristic.getUuid().toString());
                        return bluetoothGattCharacteristic.getUuid().equals(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
                    })
                    .findAny().orElse(null);
             enableCharacteristicNotification(gatt, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic written successfully");
            } else {
                log("Characteristic write unsuccessful, status: " + status);
                disconnectGattServer();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic read successfully");
                readCharacteristic(characteristic);
            } else {
                log("Characteristic read unsuccessful, status: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            log("Characteristic changed, " + characteristic.getUuid().toString());
            readCharacteristic(characteristic);
        }

        private void enableCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true);
            if (characteristicWriteSuccess) {
                log("Characteristic notification set successfully for " + characteristic.getUuid().toString());
            } else {
                log("Characteristic notification set failure for " + characteristic.getUuid().toString());
            }
        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] messageBytes = characteristic.getValue();
            String message = new String(messageBytes);
            log("Received message: " + message);
            if (message.length() > 3) {
                read.setText(message);
            }
        }
    }
}

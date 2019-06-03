package com.example.scales

import android.app.Activity
import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


class ScalesActivity : Activity() {
    lateinit var myLabel: TextView
    lateinit var myTextbox: EditText
    var mBluetoothAdapter: BluetoothAdapter? = null
    lateinit var mmSocket: BluetoothSocket
    lateinit var mmDevice: BluetoothDevice
    lateinit var mmOutputStream: OutputStream
    lateinit var mmInputStream: InputStream
    lateinit var workerThread: Thread
    lateinit var readBuffer: ByteArray
    var readBufferPosition: Int = 0
    var counter: Int = 0
    @Volatile
    var stopWorker: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scales)

        val openButton = findViewById(R.id.open) as Button
        val sendButton = findViewById(R.id.send) as Button
        val closeButton = findViewById(R.id.close) as Button
        myLabel = findViewById(R.id.label) as TextView
        myTextbox = findViewById(R.id.entry) as EditText

        //Open Button
        openButton.setOnClickListener {
                findBT()
                openBT()
        }

        //Send Button
        sendButton.setOnClickListener {
            try {
                sendData()
            } catch (ex: IOException) {
            }
        }

        //Close button
        closeButton.setOnClickListener {
            try {
                closeBT()
            } catch (ex: IOException) {
            }
        }
    }

    fun findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            myLabel.text = "No bluetooth adapter available"
        }

        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, 0)
        }

        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                if (device.name == "BL weight") {
                    mmDevice = device
                    break
                }
            }
        }
        myLabel.text = "Bluetooth Device Found"
    }

    @Throws(IOException::class)
    fun openBT() {


        val gattCallback = BluetoothGattCallback() {

            }
        }
        var gatt: BluetoothGatt = mmDevice.connectGatt(this, true, gattCallback)


        mmSocket.connect()

        mmOutputStream = mmSocket.outputStream

        Log.i("KURWA", mmSocket.isConnected.toString())
        beginListenForData()

        myLabel.text = "Bluetooth Opened"
    }

    internal fun beginListenForData() {
        val handler = Handler()
        val delimiter: Byte = 10 //This is the ASCII code for a newline character

        stopWorker = false
        readBufferPosition = 0
        readBuffer = ByteArray(1024)
        workerThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted && !stopWorker) {
                try {
                    val bytesAvailable = mmInputStream.available()
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        mmInputStream.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b == delimiter) {
                                val encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.size)
                                val data = String(encodedBytes, Charset.forName("US-ASCII"))
                                readBufferPosition = 0

                                handler.post { myLabel.text = data }
                            } else {
                                readBuffer[readBufferPosition++] = b
                            }
                        }
                    }
                } catch (ex: IOException) {
                    stopWorker = true
                }

            }
        })

        workerThread.start()
    }

    @Throws(IOException::class)
    internal fun sendData() {
        var msg = myTextbox.text.toString()
        msg += "\n"
        mmOutputStream.write(msg.toByteArray())
        myLabel.text = "Data Sent"
    }

    @Throws(IOException::class)
    internal fun closeBT() {
        stopWorker = true
        mmOutputStream.close()
        mmInputStream.close()
        mmSocket.close()
        myLabel.text = "Bluetooth Closed"
    }
}
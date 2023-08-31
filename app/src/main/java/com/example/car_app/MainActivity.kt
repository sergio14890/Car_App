package com.example.car_app


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders
import java.io.IOException
import java.util.*
import kotlin.random.Random


const val REQUEST_ENABLE_BT = 1


class MainActivity : AppCompatActivity() {

    lateinit var mBtAdapter: BluetoothAdapter
    var mAddressDevices: ArrayAdapter<String>? = null
    var mNameDevices: ArrayAdapter<String>? = null

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        val idBtnOnBT = findViewById<Button>(R.id.idBtnOnBT)
        val idBtnOffBT = findViewById<Button>(R.id.idBtnOffBT)
        val idBtnConect = findViewById<Button>(R.id.idBtnConnect)
        val idBtnEnviar = findViewById<Button>(R.id.idBtnSend)

        val idBtnOpen = findViewById<Button>(R.id.idBtnOpen)
        val idBtnClose = findViewById<Button>(R.id.idBtnClose)

        val idBtnDispBT = findViewById<Button>(R.id.idBtnDispBT)
        val idSpinDisp = findViewById<Spinner>(R.id.idSpinDisp)
        val idTextOut = findViewById<EditText>(R.id.idTextOut)

        val circularFillableLoaders = findViewById<View>(R.id.circularFillableLoaders) as CircularFillableLoaders
        var progress: Int
        val slider = findViewById(R.id.slider) as SeekBar
        slider.max = 110


        val someActivityResultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == REQUEST_ENABLE_BT) {
                Log.i("MainActivity", "ACTIVIDAD REGISTRADA")
            }
        }

        //Inicializacion del bluetooth adapter
        mBtAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        //Checar si esta encendido o apagado
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Bluetooth is available on this device!", Toast.LENGTH_LONG).show()
        }

        //--------------------------------------------------
        //Boton Encender bluetooth
        idBtnOnBT.setOnClickListener {
            if (mBtAdapter.isEnabled) {
                //Si ya está activado
                Toast.makeText(this, "Bluetooth is already activated!", Toast.LENGTH_LONG).show()
            } else {
                //Encender Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBtIntent)
            }
        }

        //Boton apagar bluetooth
        idBtnOffBT.setOnClickListener {
            if (!mBtAdapter.isEnabled) {
                //Si ya está desactivado
                Toast.makeText(this, "Bluetooth is already deactivated!", Toast.LENGTH_LONG).show()
            } else {
                //Encender Bluetooth
                mBtAdapter.disable()
                Toast.makeText(this, "Bluetooth has been deactivated!", Toast.LENGTH_LONG).show()
            }
        }

        //Boton dispositivos emparejados
        idBtnDispBT.setOnClickListener {
            if (mBtAdapter.isEnabled) {

                val pairedDevices: Set<BluetoothDevice>? = mBtAdapter?.bondedDevices
                mAddressDevices!!.clear()
                mNameDevices!!.clear()

                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mAddressDevices!!.add(deviceHardwareAddress)
                    //........... EN ESTE PUNTO GUARDO LOS NOMBRE A MOSTRARSE EN EL COMBO BOX
                    mNameDevices!!.add(deviceName)
                }

                //ACTUALIZO LOS DISPOSITIVOS
                idSpinDisp.setAdapter(mNameDevices)
            } else {
                val noDevices = "No device could be connected!"
                mAddressDevices!!.add(noDevices)
                mNameDevices!!.add(noDevices)
                Toast.makeText(this, "First connect a bluetooth device!", Toast.LENGTH_LONG).show()
            }
        }

        idBtnConect.setOnClickListener {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {

                    val IntValSpin = idSpinDisp.selectedItemPosition
                    m_address = mAddressDevices!!.getItem(IntValSpin).toString()
                    Toast.makeText(this,m_address,Toast.LENGTH_LONG).show()
                    // Cancel discovery because it otherwise slows down the connection.
                    mBtAdapter?.cancelDiscovery()
                    val device: BluetoothDevice = mBtAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }

                Toast.makeText(this,"Successful connection!",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "Successful connection!")

            } catch (e: IOException) {
                //connectSuccess = false
                e.printStackTrace()
                Toast.makeText(this,"Connection error!",Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "Connection error!")
            }
        }




        circularFillableLoaders.setOnClickListener {
            val r = Random(100)
            progress = r.nextInt(110-1)+1;
            slider.setProgress(slider.getProgress() + progress);
            circularFillableLoaders.setProgress(progress);
            Toast.makeText(this, "Progress level is " + progress + " %", Toast.LENGTH_LONG).show();
        }

        slider?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                circularFillableLoaders.setProgress(progress);
                // write custom code for progress is changed
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                var mensaje_out: String = seek.progress.toString()
                sendCommand(mensaje_out)
                Toast.makeText(this@MainActivity,
                    "Progress is: " + seek.progress + "%",
                    Toast.LENGTH_SHORT).show()
            }
        })

        idBtnOpen.setOnClickListener {
            circularFillableLoaders.setProgress(110);
            sendCommand("0")
        }

        idBtnClose.setOnClickListener {
            circularFillableLoaders.setProgress(0);
            sendCommand("120")
        }

        idBtnEnviar.setOnClickListener {

            if(idTextOut.text.toString().isEmpty()){
                Toast.makeText(this, "The entry cannot be empty!", Toast.LENGTH_SHORT)
            }else{
                var mensaje_out: String = idTextOut.text.toString()
                sendCommand(mensaje_out)
            }
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
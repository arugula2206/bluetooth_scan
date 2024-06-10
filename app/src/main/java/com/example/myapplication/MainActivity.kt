package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var btPermission = false //アクセス許可の有無を表す

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)//アクティビティ作成時に呼び出されて、レイアウトを設定
    }

    fun scanBt(view: View) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) {//bluetoothアクセスの許可がないとき
            Toast.makeText(this, "BT接続が許可されていません", Toast.LENGTH_LONG).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {//Android APIが11以上なら、BLUETOOTH_CONNECTパーミッションをリクエスト
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            } else {//11より下の場合はBLUETOOTHパーミッションをリクエスト
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH)
            }
        }
    }

    private val blueToothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {//isGranted : Bluetoothアクセスが許可されたかどうか, Bluetoothアクセスが許可→Bluetoothアダプタが有効かを確認し、有効であればBluetoothスキャンを開始
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
            btPermission = true

            if (bluetoothAdapter?.isEnabled == false) {//Bluetoothが無効なら、Bluetooth有効化
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)
            } else {//有効ならスキャン開始
                btScan()
            }
        } else {
            btPermission = false
        }
    }

    private val btActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            btScan()
        }
    }

    private fun btScan() = Toast.makeText(this, "BT接続できます", Toast.LENGTH_LONG).show()
}

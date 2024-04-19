package edu.cofc.android.phonecall

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import edu.cofc.android.phonecall.databinding.ActivityMainBinding
import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.makeCallButton.setOnClickListener {
            checkPhonePermission()
        }

        binding.numberToCall.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
    private val requestPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission())
       { isGranted: Boolean ->
            if (isGranted) {
                makeCall()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_denied_title)
                    .setMessage(R.string.permission_denied_msg)
                    .setNegativeButton(R.string.ok)
                    { dialog, _ -> dialog.cancel()
                    }
                    .show()
            }
        }


    private fun checkPhonePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            makeCall()
        }
        else if (shouldShowRequestPermissionRationale(
                Manifest.permission.CALL_PHONE)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_request_title)
                .setMessage(R.string.permission_request_msg)
                .setPositiveButton(R.string.yes) { _, _ ->
                    requestPermissionLauncher.launch(
                        Manifest.permission.CALL_PHONE
                    )
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)

        }
    }


    private fun makeCall() {
        try {
            val phoneNumToCall: String = binding.numberToCall.text.toString()
            val uri = Uri.parse("tel:$phoneNumToCall")
            val intent = Intent(Intent.ACTION_CALL, uri)
            startActivity(intent)
        } catch (ex: SecurityException) {
            val logTag = "MainActivity.makeCall()"
            val errorMsg = "No permission to make phone call."
            Log.e(logTag, errorMsg, ex)
        }
    }
}

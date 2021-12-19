package com.example.firebasestorage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.firebasestorage.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var dataText: EditText? = null
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        dataText = findViewById(R.id.dataText)

        storageRef = FirebaseStorage.getInstance().getReference()
        val fileRef: StorageReference = storageRef!!.child("sample.txt")

        var localFile: File? = null
        try {
            localFile = File.createTempFile("sample", "txt")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val finalLocalFile = localFile!!
        fileRef.getFile(localFile!!).addOnSuccessListener {
            val path = finalLocalFile.absolutePath
            dataText!!.setText(path)
        }.addOnFailureListener {
            // Handle any errors
        }

        val ONE_MEGABYTE = (1024 * 1024).toLong()
        fileRef.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener { bytes ->
                val data = String(bytes!!)
                val arr = data.split("\n".toRegex()).toTypedArray()
                var res = ""
                for (i in arr.indices) {
                    res += """
            ${i + 1}:${arr[i]}
            
            """.trimIndent()
                }
                dataText!!.setText(res)
            }.addOnFailureListener {
                // Handle any errors
            }
    }

    // ファイルのアップロードを行う。
    fun doAction(view: View?) {
        val data = dataText!!.text.toString() + ""
        val newfileRef = storageRef!!.child("newfile")
        val uploadTask = newfileRef.putBytes(data.toByteArray())
        uploadTask.addOnSuccessListener {
            Toast.makeText(
                this@MainActivity, "upload data!",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    // ファイルのメタデータを取得する。
    fun doAction2(view: View?) {
        storageRef!!.child("sample.txt").metadata
            .addOnSuccessListener { metadata ->
                val create = Date()
                val update = Date()
                create.setTime(metadata.creationTimeMillis)
                update.setTime(metadata.updatedTimeMillis)
                val res = """
            * Metadata *
            name: ${metadata.name}
            fullpath: ${metadata.path}
            bucket: ${metadata.bucket}
            size: ${metadata.sizeBytes}
            created: $create
            update: $update
            contenttype: ${metadata.contentType}
            """.trimIndent()
                dataText!!.setText(res)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
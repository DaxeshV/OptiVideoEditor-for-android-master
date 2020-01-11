
package com.obs.marveleditor

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.obs.marveleditor.fragments.OptiMasterProcessorFragment

class MainActivity : AppCompatActivity() , OptiMasterProcessorFragment.CallProgress{

    var builder :  AlertDialog.Builder? = null
    var dialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)

        builder = AlertDialog.Builder(this)
        builder!!.setCancelable(false) // if you want user to wait for some process to finish,
        builder!!.setView(R.layout.layout_loading_dialog)
        dialog = builder!!.create()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, OptiMasterProcessorFragment()).commit()
    }

    override fun showProgress() {
        dialog!!.show()
    }

    override fun hideProgress() {
        dialog!!.dismiss()
    }
}

package com.example.ganeshtikone.kotlinasync

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ImageDownloaderAsyncTask.OnImageDownloadListener {

    private var executor: ImageDownloaderAsyncTask? = null
    private val imageUrl = "http://indiebookbutler.com/wp-content/uploads/2015/08/Free.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupListener()
    }

    override fun onPause() {
        // Unregister listener once activity goes in background
        executor?.unregisterListener()
        executor = null
        super.onPause()
    }

    /**
     * Set OnClick Listener on button
     */
    private fun setupListener() {

        downloadButton.setOnClickListener(View.OnClickListener {
            startDownloadingImage()
        })

        downloadWithPicassoButton.setOnClickListener(View.OnClickListener {
            downloadWithPicasso()
        })
    }

    /**
     * Download Image using Picasso library
     */
    private fun downloadWithPicasso(){
        Picasso.with(this)
                .load(imageUrl)
                .into(downloadImageView)
    }

    /**
     * Start Executing AsyncTask class
     * to download image
     */
    private fun startDownloadingImage() {

        executor =  ImageDownloaderAsyncTask(this)
        executor?.execute(imageUrl)
    }

    /**
     * Callback method from interface OnImageDownloadListener
     * @param bitmap
     *        If image download success, result bitmap from remote server image
     */
    override fun onImageDownloadComplete(bitmap: Bitmap) {
        downloadImageView.setImageBitmap(bitmap)
    }

    /**
     * Callback method from interface OnImageDownloadListener
     * @param error
     *        Error message if download failed
     */
    override fun onImageDownloadFailed(error: String) {
        // call runOnUiThread
        // because exceptions are handle in doInBackground method
        // which runs on WorkerThread
        // to avoid Loopr.prepare() error

        runOnUiThread {
           toast(error)
           executor = null
       }
    }

    /**
     * Extension function to display toast
     * @param message
     *        message need be shown in Toast
     */
    fun MainActivity.toast(message:String){
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show()
    }

}

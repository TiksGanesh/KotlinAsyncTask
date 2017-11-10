package com.example.ganeshtikone.kotlinasync

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import android.os.Message
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by ganeshtikone on 8/11/17.
 * AsyncTask subclass
 */
class ImageDownloaderAsyncTask(listener: OnImageDownloadListener) : AsyncTask<String, Void, Bitmap?>() {


    private var mListener:OnImageDownloadListener? = listener



    override fun doInBackground(vararg args: String?): Bitmap? {

        var bitmap: Bitmap? = null

        try {
            val url = URL(args[0])
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10 * 60 * 60
            connection.readTimeout = 10 * 60 * 60
            connection.doInput = true
            connection.doOutput = true

            connection.connect()
            val responseCode = connection.responseCode
            if (HTTP_OK == responseCode) {
                if (null != connection.inputStream) {
                    val inputStream = connection.inputStream
                    //val data = inputStream.bufferedReader().use { it.readText() }
                    //Log.e(ImageDownloaderAsyncTask::class.simpleName,data)
                    bitmap = BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options())
                    return bitmap
                }
            } else {
                val httpErrorMessage = "Error Response Code: ${responseCode}"
                Log.e("####", httpErrorMessage)
                mListener?.onImageDownloadFailed(httpErrorMessage)

            }

        }catch (mex:MalformedURLException){
            mex.printStackTrace()
            mListener?.onImageDownloadFailed(mex.localizedMessage)
        }catch (ioEx:IOException){
            ioEx.printStackTrace()
            mListener?.onImageDownloadFailed(ioEx.localizedMessage)
        }catch (ex:Exception){
            ex.printStackTrace()
            mListener?.onImageDownloadFailed(ex.localizedMessage)
        }
        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if (null != result) {
            mListener?.onImageDownloadComplete(result)
        }
    }

    fun unregisterListener(){
        mListener = null
    }

    /**
     * Interface OnImageDownloadListener
     * onImageDownloadComplete : if download from server is success
     * onImageDownloadFailed : if download image failed
     */
    interface OnImageDownloadListener{
        fun onImageDownloadComplete(bitmap:Bitmap)
        fun onImageDownloadFailed(error:String)
    }
}
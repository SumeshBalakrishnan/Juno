package com.interview.juno

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Thumbnails.MINI_KIND
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.interview.juno.network.ApiHelper
import com.interview.juno.network.RetrofitBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    var cal = Calendar.getInstance()
    lateinit var videoPath: String

    var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        setupViewModel()
        setupObservers()
    }

    private fun initUI() {
        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                view.maxDate = cal.timeInMillis
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        btn_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        btn_play.setOnClickListener {
            Toast.makeText(this, " Video starting please wait...", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(videoPath), "video/*")
            startActivity(intent)
            vdovw.start()
        }

    }


    private fun updateDateInView() {
        //  val myFormat = "MM/dd/yyyy" // mention the format you need
        val myFormat = "YYYY-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        //textview_date!!.text = sdf.format(cal.getTime())
        Log.e("Date", sdf.format(cal.time))
        selectedDate = sdf.format(cal.time)
        setupObservers()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        ).get(MainViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getUsers(selectedDate).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {

                        if (it.data?.mediaType.equals("image")) {
                            Glide.with(this)
                                .load(it.data?.url)
                                .into(image)
                            image.visibility = View.VISIBLE
                            image.animate()
                            // image.zoom(Motioneve)

                        } else {
                            Log.e("VIDEO ", it.data?.url)
                            videoPath = it.data?.url.toString()
                            vdovw.visibility = View.VISIBLE
                            vdovw.setVideoPath(videoPath)
                            btn_play.visibility = View.VISIBLE
                            image.setImageBitmap(getThumblineImage(it.data?.url))
                        }
                        txt_title.text = it.data?.title
                        txt_desc.text = it.data?.explanation
                        progressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun getThumblineImage(videoPath: String?): Bitmap? {
        return ThumbnailUtils.createVideoThumbnail(videoPath, MINI_KIND)
    }
}
package com.example.viewrenderingex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.viewrenderingex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler(Looper.getMainLooper())

        val imageList = arrayListOf<Int>()
        imageList.add(R.drawable.moon1)
        imageList.add(R.drawable.moon2)
        imageList.add(R.drawable.moon3)
        imageList.add(R.drawable.moon1)
        imageList.add(R.drawable.moon2)
        imageList.add(R.drawable.moon3)
        imageList.add(R.drawable.moon1)
        imageList.add(R.drawable.moon2)
        imageList.add(R.drawable.moon3)
        imageList.add(R.drawable.moon1)
        imageList.add(R.drawable.moon2)
        imageList.add(R.drawable.moon3)
        imageList.add(R.drawable.moon1)
        imageList.add(R.drawable.moon2)
        imageList.add(R.drawable.moon3)

        Thread {
            for (image in imageList) {

                runOnUiThread {
                    binding.imageview.setImageResource(image)
                }
                Thread.sleep(2000)
            }
        }.start()
    }
}
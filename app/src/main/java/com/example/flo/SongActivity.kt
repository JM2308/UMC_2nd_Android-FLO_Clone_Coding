package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding : ActivitySongBinding
    lateinit var song : Song
    lateinit var timer : Timer
    var playFlag : Int = 0
    private var mediaPlayer : MediaPlayer? = null
    private var gson: Gson = Gson() //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songBackButton.setOnClickListener {
            finish()
        }

        binding.songPlayButton.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPlayerStopButton.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songRepeatButton.setOnClickListener {
            binding.songRepeatButton.visibility = View.GONE
            binding.songRepeat1Button.visibility = View.VISIBLE
            playFlag = 1
        }

        binding.songRepeat1Button.setOnClickListener {
            binding.songRepeat1Button.visibility = View.GONE
            binding.songRepeatPlaylistButton.visibility = View.VISIBLE
            playFlag = 2
        }

        binding.songRepeatPlaylistButton.setOnClickListener {
            binding.songRepeatPlaylistButton.visibility = View.GONE
            binding.songRepeatButton.visibility = View.VISIBLE
            playFlag = 0
        }

        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            binding.songTopTitle.text = intent.getStringExtra("title")
            binding.songTopSinger.text = intent.getStringExtra("singer")
        }
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.songSeekBar.progress * song.playTime) / 100) / 1000

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

        // 원래는 데이터 하나하나 전송해야 함!
        // editor.putString("title", song.title)
        // editor.putString("singer", song.singer)...
        // But, 이를 편리하게 하기 위해 JSON을 사용!!
        // JSON을 사용하기 위해 먼저 Song 객체를 JSON으로 바꿔줘야함!
        // 이때 사용하는 것이 GSON!

        val songJson = gson.toJson(song)
        // git에서 commit과 push라고 생각!
        // 즉, apply까지 적용해야 실제 저장 공간에 저장되는 것!
        editor.putString("songData", songJson)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initSong() {
        if (intent.hasExtra("title") && intent.hasExtra("singer")) {
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }
        startTimer()
    }

    private fun setPlayer(song : Song) {
        binding.songTopTitle.text = intent.getStringExtra("title")!!
        binding.songTopSinger.text = intent.getStringExtra("singer")!!
        binding.songStartTime.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTime.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songSeekBar.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        setPlayerStatus(song.isPlaying)
    }

    private fun setPlayerStatus(isPlaying : Boolean) {
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songPlayButton.visibility = View.GONE
            binding.songPlayerStopButton.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songPlayButton.visibility = View.VISIBLE
            binding.songPlayerStopButton.visibility = View.GONE

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }

    private fun startTimer() {
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying : Boolean = true) : Thread() {
        private var second : Int = 0
        private var mills : Float = 0f

        override fun run() {
            super.run()

            try {
                while (true) {
                    if (second >= playTime) {
                        if (playFlag == 1)
                            startTimer()

                        break;
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50
                        runOnUiThread {
                            binding.songSeekBar.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTime.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++;
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}
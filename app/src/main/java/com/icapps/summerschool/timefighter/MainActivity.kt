package com.icapps.summerschool.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private var gameStarted = false

    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var tapButton: Button

    private lateinit var bounceAnimation: Animation
    private lateinit var blinkAnimation: Animation

    private var score = 0

    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDownInMillis = 60000L
    private val countDownIntervalInMillis = 1000L
    private var timeLeft = initialCountDownInMillis

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE"
        private const val TIME_LEFT_KEY = "TIME_LEFT"
        private const val GAME_STARTED_KEY = "GAME_STARTED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreTextView = findViewById(R.id.score_text)
        timerTextView = findViewById(R.id.timer_text)
        tapButton = findViewById(R.id.tapButton)

        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)

        tapButton.setOnClickListener {
            tapButton.startAnimation(bounceAnimation)
            scoreTextView.startAnimation(blinkAnimation)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getLong(TIME_LEFT_KEY)
            gameStarted = savedInstanceState.getBoolean(GAME_STARTED_KEY)

            Log.d(TAG, "Restored game state (score: $score) (timeLeft: $timeLeft)")

            restoreGame()
        } else {
            resetGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeft)
        outState.putBoolean(GAME_STARTED_KEY, gameStarted)
        countDownTimer.cancel()

        Log.d(TAG, "Saved game state (score: $score) (timeLeft: $timeLeft)")

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        Log.d(TAG, "SHOWING INFO")
        AlertDialog.Builder(this)
            .setTitle(R.string.aboutDialogTitle_text)
            .setMessage(R.string.aboutDialogMessage_text)
            .create()
            .show()
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        val toastText = getString(R.string.endGameToast_text, score)
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()

        resetGame()
    }

    private fun resetGame() {
        score = 0
        updateScoreText()
        updateTimerText(initialCountDownInMillis / 1000)

        countDownTimer = initCountDownTimer()

        gameStarted = false
    }

    private fun restoreGame() {
        updateScoreText()
        updateTimerText(timeLeft / 1000)

        countDownTimer = initCountDownTimer(timeLeft)

        if (gameStarted) {
            countDownTimer.start()
        }
    }

    private fun incrementScore() {
        if (!gameStarted){
            startGame()
        }
        score++
        updateScoreText()
    }

    private fun updateScoreText() {
        val newScoreText = getString(R.string.score_text, score)
        scoreTextView.text = newScoreText
    }

    private fun updateTimerText(timeLeft: Long) {
        val newTimerText = getString(R.string.timer_text, timeLeft)
        timerTextView.text = newTimerText
    }

    private fun initCountDownTimer(initialCountDown: Long = initialCountDownInMillis): CountDownTimer {
            return object : CountDownTimer(initialCountDown, countDownIntervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateTimerText(timeLeft / 1000)
            }

            override fun onFinish() {
                endGame()
            }
        }
    }
}
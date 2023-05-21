package org.hyperskill.stopwatch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import kotlin.random.Random
private const val CHANNEL_ID = "org.hyperskill"
private const val NOTIFICATION_ID = 393939
class MainActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var textView: TextView
    private lateinit var progressBar : ProgressBar
    private var secondsLimit: Int? = null
    private var secondCount: Int = 0

    private var handler = Handler(Looper.getMainLooper())
    private var isThreadRunning = false
    private var seconds = 1
    private var runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun run() {

            textView.text = transformTimer(seconds++)
            handler.postDelayed(this, 1000)
            if (secondsLimit != null) {
                if (seconds == secondsLimit!! + 1) {
                    startNotification()
                }
                    if (seconds > secondsLimit!! + 1) {
                        textView.setTextColor(Color.RED)
                    }
                }
            // Изменяем цвет прогрессбара каждую секунду
            val color = Random.nextInt()
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)

        }
    }

    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "org.hyperskill"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            }
           var  notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Register the channel with the system
            var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle("Notification")
                .setContentText("Time exceeded")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.createNotificationChannel(channel)
            var notification = notificationBuilder.build()
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
            //Флаг FLAG_INSISTENT указывает, что уведомление должно
            // быть настойчивым и продолжать проигрывать звуковой сигнал до тех пор,
            // пока пользователь не отреагирует на него, например, нажмет на него или удалит его.
            notification.flags = notification.flags or Notification.FLAG_INSISTENT
            notificationManager.notify(393939,notification)
    }

}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById<Button>(R.id.startButton)
        resetButton = findViewById<Button>(R.id.resetButton)
        textView = findViewById<TextView>(R.id.textView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        var settingsButton = findViewById<Button>(R.id.settingsButton)

        startButton.setOnClickListener {
            secondCount = 0
            textView.setTextColor(Color.BLACK)
            progressBar.isVisible = true
            if (!isThreadRunning) {
                isThreadRunning = true
                handler.postDelayed(runnable, 1000)
            }
            settingsButton.isEnabled = false
        }

        resetButton.setOnClickListener {
            isThreadRunning = false
            handler.removeCallbacks(runnable)
            seconds = 0
            textView.text = "00:00"
            progressBar.isVisible = false
            settingsButton.isEnabled = true
            textView.setTextColor(Color.BLACK)

        }
        settingsButton.setOnClickListener {

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Set upper limit in seconds")
            val editText = EditText(this)
            editText.id = R.id.upperLimitEditText
            editText.inputType = InputType.TYPE_CLASS_NUMBER

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL

            layout.addView(editText)

            builder.setView(layout)

            builder.setPositiveButton("OK") { dialog, which ->
                secondsLimit = editText.text.toString().toInt()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun transformTimer(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

}
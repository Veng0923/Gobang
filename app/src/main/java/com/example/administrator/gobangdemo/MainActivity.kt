package com.example.administrator.gobangdemo


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        restart.onClick { gameView.restart() }
        undo.onClick { gameView.undo() }
        white.onClick { gameView.setMode(GameView.MODE_COMPUTERTOPERSON);modePanel.visibility = View.VISIBLE;bwPanel.visibility = View.GONE;gameView.restart() }
        pperson.onClick { gameView.setMode(GameView.MODE_PERSONTOPERSON);gameView.restart() }
        ccomputer.onClick { modePanel.visibility = View.GONE;bwPanel.visibility = View.VISIBLE }
        black.onClick { gameView.setMode(GameView.MODE_PERSONTOCOMPUTER);modePanel.visibility = View.VISIBLE;bwPanel.visibility = View.GONE;gameView.restart() }
    }
}

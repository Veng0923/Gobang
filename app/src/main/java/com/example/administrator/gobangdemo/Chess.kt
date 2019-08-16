package com.example.administrator.gobangdemo

import kotlin.math.round

/**
 * Created by Administrator on 2018/3/8/008.
 */
class Chess(var row : Int =15,var column: Int=15)  {
    var chess2D: Array<IntArray>
    init {
        chess2D = Array(row){IntArray(column)}
        initChess()
    }
    private fun initChess() {
        for (i in 0 until row){
            for (j in 0 until column){
                chess2D[i][j] = 0
            }
        }
    }
}
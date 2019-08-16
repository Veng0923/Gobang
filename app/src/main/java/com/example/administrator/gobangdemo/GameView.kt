package com.example.administrator.gobangdemo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.time.format.TextStyle
import java.util.*

/** Created by Administrator on 2018/3/8/008. */
class GameView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var mWidth = 0f
    private var mHeight = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                val widthSize = MeasureSpec.getSize(widthMeasureSpec);
                val heightSize = MeasureSpec.getSize(heightMeasureSpec); blockSize = (widthSize - offX * 2) / (column - 1)
                this.setMeasuredDimension(widthSize, (offY * 2 + (row - 1) * blockSize).toInt())

            }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
//        blockSize = (w - offX*2) / (column-1)
        radius = (blockSize - 20) / 2
        currentLength = blockSize / 3
    }

    private var radius = 0f
    private var background = 0
    private var chess: Chess
    private var row = 0
    private var column = 0
    private var blockSize = 0f
    private val mPaint = Paint()
    private var map: Array<IntArray>
    private lateinit var dialog: AlertDialog.Builder
    private var degree: Array<IntArray>

    init {
        chess = Chess()
        map = chess.chess2D
        row = chess.row
        column = chess.column
        background = resources.getColor(R.color.chessBackground)
//        black = IntArray(row){IntArray(column)}
        degree = Array(row) { IntArray(column) }
        initDegree()
        initPaint()
        initDialog()
    }

    private fun initDegree() {
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (i <= (row / 2)&&j<=(column/2)) {
                    degree[i][j] = Math.min(i, j) + 1
                } else if(i<=(row/2)&&j>(column/2)){
                    degree[i][j] = degree[ i][column-1-j]
                }else{
                    degree[i][j] = degree[row - i-1][j]
                }
            }
        }
    }

    private fun initDialog() {
        dialog = AlertDialog.Builder(context)
        dialog.apply {
            setNegativeButton("取消", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                }
            })
            setPositiveButton("重新开始", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    restart()
                }
            })

        }
    }


    private var offX = 20f
    private var offY = 30f
    private fun initPaint() {
        mPaint.color = resources.getColor(R.color.chessLine)
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 3f
    }

    private var stack = Stack<Int>()
    private var currentLength = 0f
    private var currentWidth = 8f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawColor(background)
            for (j in 0 until column) {
                it.drawLine(j * blockSize.toFloat() + offX, 0f + offY, j * blockSize + offX, (row - 1) * blockSize + offY, mPaint)
            }
            for (j in 0 until row) {
                it.drawLine(0f + offX, j * blockSize.toFloat() + offY, (column - 1) * blockSize + offX, j * blockSize + offY, mPaint)
            }
//            val textPaint = Paint()
//            textPaint.color = Color.RED
//            textPaint.style = Paint.Style.STROKE
//            textPaint.style = Paint.Style.FILL
//            textPaint.textSize = 35f
            for (i in 0 until row) {
                for (j in 0 until column) {
                    if (map[i][j] == 2) {
                        mPaint.style = Paint.Style.FILL
                        it.drawCircle(offX + j * blockSize, offY + i * blockSize, radius, mPaint)
//                        it.drawText("${degree[i][j]}-$i-$j ",offX + j * blockSize, offY + i * blockSize,textPaint)
                    } else if (map[i][j] == 1) {
//                        mPaint.style = Paint.Style.STROKE
                        val path1 = Path()
                        val path2 = Path()
                        mPaint.style = Paint.Style.FILL
                        mPaint.color = Color.WHITE
                        path1.addCircle(offX + j * blockSize, offY + i * blockSize, radius , Path.Direction.CW)
                        it.drawPath(path1,mPaint)
                        mPaint.style = Paint.Style.STROKE
                        mPaint.color = Color.BLACK
                        path2.addCircle(offX + j * blockSize, offY + i * blockSize, radius, Path.Direction.CW)
//                        path2.op(path1, Path.Op.DIFFERENCE)
                        it.drawPath(path2, mPaint)
//                        it.drawText("${degree[i][j]}-$i-$j ",offX + j * blockSize, offY + i * blockSize,textPaint)
                    }
                }
            }
            val path1 = Path()
            val path2 = Path()
            path1.addRect(currentY * blockSize - currentLength / 2 + offX, currentX * blockSize - currentWidth / 2 + offY, currentY * blockSize + currentLength / 2 + offX, currentX * blockSize + currentWidth / 2 + offY, Path.Direction.CW)
            path2.addRect(currentY * blockSize - currentWidth / 2 + offX, currentX * blockSize - currentLength / 2 + offY, currentY * blockSize + currentWidth / 2 + offX, currentX * blockSize + currentLength / 2 + offY, Path.Direction.CW)
            path1.op(path2, Path.Op.UNION)
            mPaint.color = Color.RED
            mPaint.style = Paint.Style.FILL
            it.drawPath(path1, mPaint)
            mPaint.color = Color.BLACK
//            if(currentX!=-1){
//                it.drawText("${degree[currentX][currentY]}-$currentX-$currentY ",offX + currentY * blockSize, offY + currentX * blockSize,textPaint)
//            }
        }
    }

    private var who = 1 //1为黑
    private var isOver = false
    private var computer = 0
    private var person = 0
    private var isComputer = false
    private var currentX = -1
    private var currentY = -1
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let {
            val x = it.getX() - offX + blockSize / 2
            val y = it.getY() - offY + blockSize / 2
            val j = (x / blockSize).toInt()
            val i = (y / blockSize).toInt()
            if (computer == 0) {
                log("$i $j ----------")
                if (it.action == MotionEvent.ACTION_UP) {
                    if (i < row && j < column && map[i][j] == 0 && !isOver) {
                        stack.push(i)
                        stack.push(j)
                        stack.push(map[i][j])
                        if (who % 2 == 0) {
                            map[i][j]++
                            if (isWin(i, j)) {
                                dialog.apply { setMessage("白子赢了"); show() }
                            }
                        } else {
                            map[i][j] += 2
                            if (isWin(i, j)) {
                                dialog.apply { setMessage("黑子赢了");show() }
                            }
                        }
                        currentX = i
                        currentY = j
                        who++
                        invalidate()
                    }
                }
            } else if (computer == 1) {
                person = 2
                if (it.action == MotionEvent.ACTION_UP) {
                    pc(i, j)
                }
            } else if (computer == 2) {
                person = 1
                pc(i, j)
            }

        }
        return false
    }

    fun cp(){
        if(isComputer){
//            initDegree()
            countDegree()
            down()
            currentX = computerX
            currentY = computerY
            stack.push(computerX)
            stack.push(computerY)
            stack.push(0)
            who++
            if (isWin(computerX, computerY)) {
                dialog.apply { setMessage("电脑赢了");show() }
            }
            isComputer = false
        }

    }
    fun pc(i: Int, j: Int) {
        if (!isComputer && i < row && j < column && map[i][j] == 0) {
            currentX = i
            currentY = j
            stack.push(i)
            stack.push(j)
            stack.push(map[i][j])
            map[i][j] += person
            isComputer = true
            if (isWin(i, j)) {
                dialog.apply { setMessage("你赢了!");show() }
                isComputer = false
            } else {
//                initDegree()
                countDegree()
                down()
                currentX = computerX
                currentY = computerY
                stack.push(computerX)
                stack.push(computerY)
                stack.push(0)
                who++
                if (isWin(computerX, computerY)) {
                    dialog.apply { setMessage("电脑赢了");show() }
                }
                who++
                isComputer = false
            }
        }
//        who++
        invalidate()
    }

    fun isWin(i: Int, j: Int): Boolean {
        var count1 = 1
        for (x in -4..4) {
            if (i + x >= 0 && i + x < row ) {
                if (map[i + x][j] == (who % 2 + 1)) {
                    count1++
                }else if ( count1<=5 ){
                    count1 = 1
                }
            }
        }
        var count2 = 1
        for (x in -4..4) {
            if (j + x >= 0 && j + x < column ){
                if(map[i][j + x] == (who % 2 + 1)) {
                    count2++
                 }else if(count2<=5){
                    count2 = 1
                }
            }
        }
        var count3 = 1
        for (x in -4..4) {
            if (i + x >= 0 && i + x < row && j + x >= 0 && j + x < column ) {
                if (map[i + x][j + x] == (who % 2 + 1)) {
                    count3++
                }else if(count3<=5){
                    count3 = 1
                }
            }
        }
        var count4 = 1
        for (x in -4..4) {
            if (i + x >= 0 && i + x < row && j - x >= 0 && j - x < column ) {
                if (map[i + x][j - x] == (who % 2 + 1)) {
                    count4++
                }else if(count4<=5){
                    count4 = 1
                }
            }
        }
        if (count1 > 5 || count2 > 5 || count3 > 5 || count4 > 5) {
            isOver = true
            return true
        }
        return false
    }
    private fun isPin(): Boolean{
        var count = 0
        for(i in 0..row){
            for(j in 0..column){
                if(map[i][j]!=0){
                    count++
                }
            }
        }
        if(count>=row*column){
            return true
        }
        return false
    }

    private fun initMap() {
        for (i in 0 until row) {
            for (j in 0 until column) {
                map[i][j] = 0
            }
        }
    }

    fun restart() {
        stack.clear()
        who = 1
        isOver = false
        currentY = -1
        currentX = -1
        computerX = -1
        computerY = -1
        initMap()
        if (computer != MODE_PERSONTOPERSON) {
            initDegree()
        }
        if(computer == MODE_COMPUTERTOPERSON){
            isComputer = true
            cp()
        }
        invalidate()
    }

    fun undo() {
        if (!stack.isEmpty()) {
            if (computer == MODE_PERSONTOPERSON) {
                undo_()
            } else {
                for (i in 1..2) {
                    undo_()
                }
            }
            if(!stack.isEmpty()){
                val value = stack.pop()
                val j = stack.pop()
                val i = stack.pop()
                currentX = i
                currentY = j
                stack.push(i)
                stack.push(j)
                stack.push(value)
            }else{
                currentX = -1
                currentY = -1
            }
            invalidate()
        }
    }

    private fun undo_(): Unit {
        if(!stack.isEmpty()){
            val value = stack.pop()
            val j = stack.pop()
            val i = stack.pop()
            map[i][j] = value
        }
    }

    private var computerX = -1
    private var computerY = -1
    fun down() {
        var max = 0
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (map[i][j] == 0 && degree[i][j] > max) {
                    max = degree[i][j]
                    computerX = i
                    computerY = j
                }
            }
        }
        map[computerX][computerY] += computer
        initDegree()
    }

    private val computerDegree2 = 40
    private val computerDegree2_1 = 25
    private val computerDegree2_5 = 15

    private val personDegree2 = 30
    private val personDegree2_2 =35

    private val personDegree3 = 1700
    private val personDegree3_0 = 1400
    private val personDegree3_1 = 170
    private val personDegree3_2 = 1800
    private val personDegree3_5 = 130

    private val computerDegree3 = 180
    private val computerDegree3_1 = 60
    private val computerDegree3_2 = 190

    private val personDegree4 = 8000
    private val personDegree4_0 = 9000
    private val personDegree4_2 = 10000
    private val personDegree4_1 = 2500
    private val personDegree4_5 = 600

    private val computerDegree4 = 2000
    private val computerDegree4_1 = 200
    private val computerDegree4_2 = 2500
    private val computerDegree4_5 = 100

    private val computerDegree5 = 20000
    private val computerDegree5_2 = 25000

    private val min = 6
//    fun countDegree2(){
//        for(i in 0 until row){
//            for(j in 0 until column){
//
//            }
//        }
//    }
    fun countDegree() {
        for (i in 0 until row) {
            for (j in 0 until column) {
                if (map[i][j] == person) {
                    //下
                    var count = 0
                    var flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && i + x < row) {
                            if (map[i + x][j] == person){if (x!=-1) count++ }else if (map[i + x][j] == computer) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            2->{
                                if(i-1>0&&map[i+1][j]==computer){
                                        for(x in 1..4){
                                            degree[i+x][j] += min
                                        }
                                }else{
                                    for(x in -1..4){
                                        if (map[i + x][j] == 0) {
////                                            degree[i + x][j] += Math.max(personDegree2,degree[i + x][j])
                                            degree[i + x][j] += personDegree2
                                            if (x == -1) {
////                                                degree[i + x][j] += Math.max(personDegree2,degree[i + x][j])
                                                degree[i + x][j] += personDegree2
                                            }else if(x == 1){
////                                                degree[i + x][j] += Math.max(personDegree2_2,degree[i + x][j])
                                                degree[i + x][j] += personDegree2_2
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> if(i+4<row&&map[i+4][j]!= person){
                                    for (x in -1..4) {
                                        if (i + x >= 0 && i + x < row) {
                                            if (map[i + x][j] == 0) {
//                                                degree[i + x][j] += Math.max(personDegree3,degree[i + x][j])
                                                if (x == -1) {
//                                                    degree[i + x][j] += Math.max(personDegree3_0,degree[i + x][j])
                                                    degree[i + x][j] += personDegree3_0
                                                }else if(x<4){
//                                                    degree[i + x][j] += Math.max(personDegree3_2,degree[i + x][j])
                                                    degree[i + x][j] += personDegree3_2
                                                }else{
                                                    degree[i + x][j] += personDegree3_5
                                                }
                                            }
                                        }
                                    }
                                }

                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < row) {
                                    if (map[i + x][j] == 0) {
//                                        degree[i + x][j] += Math.max(personDegree4,degree[i + x][j])
                                        if (x == -1) {
//                                            degree[i + x][j] += Math.max(personDegree4_0,degree[i + x][j])
                                            degree[i + x][j] += personDegree4_0
                                        }else if(x<4){
//                                            degree[i + x][j] += Math.max(personDegree4_2,degree[i + x][j])
                                            degree[i + x][j] += personDegree4_2
                                        }else{
                                            degree[i + x][j] += personDegree4
                                        }
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            3 -> {
                                if (i - 1 >= 0) {
                                    if (map[i - 1][j] == computer) {
                                        for (x in 1..4) {
                                            if (i + x >= 0 && i + x < column) {
                                                if (map[i + x][j] == 0) {
                                                    degree[i + x][j] += min
                                                }
                                            }
                                        }
                                    } else if (map[i - 1][j] == 0) {
//                                        degree[i - 1][j] += Math.max(personDegree3_1,degree[i - 1][j])
                                        if(i+3<column&&map[i+3][j] == computer){
                                            degree[i - 1][j] += personDegree3_1
                                        }else{

                                        }

                                        if(map[i+3][j] == computer){
                                            degree[i+4][j] += min
                                            degree[i - 1][j] = personDegree3_1
                                        }
                                    }else {
                                        for (x in 1..4) {
                                            if (i + x >= 0 && i + x < column) {
                                                if (map[i + x][j] == 0) {
                                                    degree[i + x][j] += min
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    for (x in -1..4) {
                                        if (i + x >= 0 && i + x < column) {
//                                            if (map[i + x][j] == 0) degree[i + x][j] +=Math.max(personDegree3_5,degree[i + x][j])
                                            if (map[i + x][j] == 0) degree[i + x][j] += personDegree3_5
                                        }
                                    }
                                }
                            }
                            4 -> {
                                if (i - 1 >= 0) {
                                    if (map[i - 1][j] == computer) {
                                        for (x in 1..4) {
                                            if (i + x >= 0 && i + x < column) {
//                                                if (map[i + x][j] == 0) degree[i + x][j] += Math.max(personDegree4_1,degree[i + x][j] )
                                                if (map[i + x][j] == 0) degree[i + x][j] += personDegree4_1
                                            }
                                        }
                                    } else if (map[i - 1][j] == 0) {
                                        if(map[i+4][j] == computer){
//                                            degree[i-1][j] += Math.max(personDegree4_1,degree[i-1][j])
                                            degree[i-1][j] += personDegree4_1
                                        }else{
                                            degree[i - 1][j] += min
                                        }
                                    }
                                } else {
                                    for (x in -1..4) {
                                        if (i + x >= 0 && i + x < column) {
//                                            if (map[i + x][j] == 0) degree[i + x][j] += Math.max(personDegree4_5,degree[i + x][j])
                                            if (map[i + x][j] == 0) degree[i + x][j] += personDegree4_5
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //右下
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row) {
                            if (map[i + x][j + x] == person){if(x!=-1){count++ }}else if (map[i + x][j + x] == computer) flag++
                        }
                    }
                    log("$i---$j----$flag---------------")
                    if (flag < 1) {
                        when (count) {
                            2->{
                                if(i -1 >= 0 && i -1 < column && j -1 >0&&map[i-1][j-1]==computer){
                                    for(x in 1..4){
                                        degree[i+x][j+x] += min
                                    }
                                }else{
                                    for (x in -1..4) {
                                        if (i + x >= 0 && i + x < column &&j+x>=0&& j + x < row) {
                                            if (map[i + x][j + x] == 0) {
//                                                degree[i + x][j + x] += Math.max(personDegree2,degree[i + x][j + x])
                                                degree[i + x][j + x] += personDegree2
                                                if (x == -1) {
//                                                    degree[i + x][j + x] += Math.max(personDegree2,degree[i + x][j + x] )
                                                    degree[i + x][j + x] += personDegree2
                                                }else if(x < 2){
//                                                    degree[i + x][j + x] += Math.max(personDegree2_2,degree[i + x][j + x] )
                                                    degree[i + x][j + x] += personDegree2_2
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 ->if(i+4<row&&j+x<column&&map[i+4][j+4]!=person){
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < column && j + x in 0..row) {
                                        if (map[i + x][j + x] == 0) {
//                                            degree[i + x][j + x] += Math.max(personDegree3,degree[i + x][j + x])
                                            if (x == -1) {
//                                                degree[i + x][j + x] += Math.max(personDegree3_0,degree[i + x][j + x] )
                                                degree[i + x][j + x] += personDegree3_0
                                            }else if(x < 4){
//                                                degree[i + x][j + x] += Math.max(personDegree3_2,degree[i + x][j + x] )
                                                degree[i + x][j + x] += personDegree3_2
                                            }else{
                                                degree[i + x][j + x] += personDegree3_5

                                            }
                                        }
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j + x < row) {
                                    if (map[i + x][j + x] == 0) {
//                                        degree[i + x][j + x] += Math.max( personDegree4,degree[i + x][j + x] )
                                        if (x == -1) {
//                                            degree[i + x][j + x] += Math.max(personDegree4_0, degree[i + x][j + x] )
                                            degree[i + x][j + x] += personDegree4_0
                                        }else if(x<4){
//                                            degree[i + x][j + x] += Math.max(personDegree4_2, degree[i + x][j + x] )
                                            degree[i + x][j + x] += personDegree4_2
                                        }else{
                                            degree[i + x][j + x] += personDegree4

                                        }
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            3 -> if (i - 1 >= 0 && j - 1 >= 0) {
                                if (map[i - 1][j - 1] == computer) {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j + x < row) {
                                            if (map[i + x][j + x] == 0) degree[i + x][j + x] += min
                                        }
                                    }
                                } else if (map[i - 1][j - 1] == 0) {
//                                    degree[i - 1][j - 1] += Math.max(personDegree3_1,degree[i - 1][j - 1])
                                    degree[i - 1][j - 1] += personDegree3_1
                                    if(map[i+3][j+3]==computer){
                                        degree[i+4][j+4] += min
                                        degree[i-1][j-1] = personDegree3_1
                                    }
                                }else {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j + x < row) {
                                            if (map[i + x][j + x] == 0) degree[i + x][j + x] += min
                                        }
                                    }
                                }
                            } else {
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < column && j + x < row) {
//                                        if (map[i + x][j + x] == 0) degree[i + x][j + x] += Math.max( personDegree3_5,degree[i + x][j + x] )
                                        if (map[i + x][j + x] == 0) degree[i + x][j + x] += personDegree3_5
                                    }
                                }
                            }
                            4 -> if (i - 1 >= 0 && j - 1 >= 0) {
                                if (map[i - 1][j - 1] == computer) {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j + x < row) {
//                                            if (map[i + x][j + x] == 0) degree[i + x][j + x] +=Math.max( personDegree4_1,degree[i + x][j + x] )
                                            if (map[i + x][j + x] == 0) degree[i + x][j + x] += personDegree4_1
                                        }
                                    }
                                } else if (map[i - 1][j - 1] == 0) {
                                    if(map[i+4][j+4]==computer){
//                                        degree[i - 1][j - 1] += Math.max( personDegree4_1,degree[i - 1][j - 1] )
                                        degree[i - 1][j - 1] += personDegree4_1
                                    }else{
                                        degree[i - 1][j - 1] += min
                                    }
                                }
                            } else {
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < column && j + x < row) {
//                                        if (map[i + x][j + x] == 0) degree[i + x][j + x] += Math.max( personDegree4_5,degree[i + x][j + x] )
                                        if (map[i + x][j + x] == 0) degree[i + x][j + x] += personDegree4_5
                                    }
                                }
                            }
                        }

                    }
                    //右
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (j + x >= 0 && j + x < row) {
                            if (map[i][j + x] == person) { if(x!=-1)count++} else if (map[i][j + x] == computer) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            2->{
                                if(j -1>= 0 && map[i][j-1]==computer){
                                    for(x in 1..4){
                                        degree[i][j+x] += min
                                    }
                                }else{
                                    for (x in -1..4) {
                                        if (j + x >= 0 && j + x < row) {
                                            if (map[i][j + x] == 0) {
//                                                degree[i][j + x] += Math.max(personDegree2 ,degree[i][j + x] )
                                                degree[i][j + x] += personDegree2
                                                if (x == -1) {
//                                                    degree[i][j + x] += Math.max( personDegree2,degree[i][j + x]  )
                                                    degree[i][j + x] += personDegree2
                                                }else if (x <2 ){
//                                                    degree[i][j + x] += Math.max( personDegree2_2,degree[i][j + x]  )
                                                    degree[i][j + x] +=  personDegree2_2
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            3 ->if(j+4<column&&map[i][j+4]!= person){
                                for (x in -1..4) {
                                    if (j + x >= 0 && j + x < row) {
                                        if (map[i][j + x] == 0) {
//                                            degree[i][j + x] += Math.max(personDegree3 ,degree[i][j + x] )
                                            if (x == -1) {
//                                                degree[i][j + x] += Math.max( personDegree3_0,degree[i][j + x]  )
                                                degree[i][j + x] +=  personDegree3_0
                                            }else if (x <4 ){
//                                                degree[i][j + x] += Math.max( personDegree3_2,degree[i][j + x]  )
                                                degree[i][j + x] +=  personDegree3_2
                                            }else{
                                                degree[i][j + x] += personDegree3_5

                                            }
                                        }
                                    }
                                }
                            }

                            4 -> for (x in -1..4) {
                                if (j + x >= 0 && j + x < row) {
                                    if (map[i][j + x] == 0) {
//                                        degree[i][j + x] +=Math.max(personDegree4,degree[i][j + x]  )

                                        if (x == -1) {
//                                            degree[i][j + x] += Math.max(personDegree4_0,degree[i][j + x] )
                                            degree[i][j + x] += personDegree4_0
                                        }else if(x < 4){
//                                            degree[i][j + x] += Math.max(personDegree4_2,degree[i][j + x] )
                                            degree[i][j + x] += personDegree4_2
                                        }else{
                                            degree[i][j + x] += personDegree4
                                        }
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            3 -> {
                                if (j - 1 >= 0) {
                                    if (map[i][j - 1] == computer) {
                                        for (x in 1..4) {
                                            if (j + x >= 0 && j + x < row) {
                                                if (map[i][j + x] == 0) degree[i][j + x] += min
                                            }
                                        }
                                    } else if (map[i][j - 1] == 0) {
//                                        degree[i][j - 1] += Math.max(personDegree3_1,degree[i][j - 1] )
                                        degree[i][j - 1] +=  personDegree3_1
                                        if(map[i][j+3]==computer){
                                            degree[i][j+4] += min
                                            degree[i][j - 1] = personDegree3_1
                                        }
                                    }else{
                                        for (x in 1..4) {
                                            if (j + x >= 0 && j + x < row) {
                                                if (map[i][j + x] == 0) degree[i][j + x] += min
                                            }
                                        }
                                    }
                                } else {
                                    for (x in -1..4) {
                                        if (j + x >= 0 && j + x < row) {
//                                            if (map[i ][j + x ] == 0) degree[i][j + x] += Math.max(personDegree3_5,degree[i][j + x]  )
                                            if (map[i ][j + x ] == 0) degree[i][j + x] += personDegree3_5
                                        }
                                    }
                                }
                            }
                            4 -> {
                                if (j - 1 >= 0) {
                                    if (map[i][j - 1] == computer) {
                                        for (x in 1..4) {
                                            if (j + x >= 0 && j + x < row) {
//                                                if (map[i][j + x] == 0) degree[i][j + x] += Math.max( personDegree4_1 ,degree[i][j + x])
                                                if (map[i][j + x] == 0) degree[i][j + x] +=   personDegree4_1
                                            }
                                        }
                                    } else if (map[i][j - 1] == 0) {
                                        if(map[i][j+4]==computer){
//                                            degree[i][j - 1] += Math.max( personDegree4_1 ,degree[i][j - 1])
                                            degree[i][j - 1] +=   personDegree4_1
                                        }else{
                                            degree[i][j - 1] += min
                                        }

                                    }
                                } else {
                                    for (x in -1..4) {
                                        if (j + x >= 0 && j + x < row) {
//                                            if (map[i][j + x] == 0) degree[i][j + x] += Math.max( personDegree4_5,degree[i][j + x] )
                                            if (map[i][j + x] == 0) degree[i][j + x] +=  personDegree4_5
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //左下
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                            if (map[i + x][j - x] == person){if(x!=-1) count++ }else if (map[i + x][j - x] == computer) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            2->{
                                if(i-1>=0&&j+1<column&&map[i-1][j+1]==computer){
                                    for(x in 1..4){
                                        degree[i+x][j-x] += min
                                    }
                                }else{
                                    for (x in -1..4) {
                                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                                            if (map[i + x][j - x] == 0) {
//                                                degree[i + x][j - x] += Math.max(personDegree2,degree[i + x][j - x]  )
                                                degree[i + x][j - x] += personDegree2
                                                if (x == -1) {
//                                                    degree[i + x][j - x] += Math.max(personDegree2 ,degree[i + x][j - x] )
                                                    degree[i + x][j - x] += personDegree2
                                                }else if(x<2){
//                                                    degree[i + x][j - x] += Math.max(personDegree2_2 ,degree[i + x][j - x] )
                                                    degree[i + x][j - x] += personDegree2_2
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> if(i+4<row&&j-4>0&&map[i+4][j-4]!=person){
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < row && j - x >= 0 && j - x < column) {
                                        if (map[i + x][j - x] == 0) {
//                                            degree[i + x][j - x] += Math.max(personDegree3,degree[i + x][j - x]  )
                                            if (x == -1) {
//                                                degree[i + x][j - x] += Math.max(personDegree3_0 ,degree[i + x][j - x] )
                                                degree[i + x][j - x] +=  personDegree3_0
                                            }else if(x<4){
//                                                degree[i + x][j - x] += Math.max(personDegree3_2 ,degree[i + x][j - x] )
                                                degree[i + x][j - x] += personDegree3_2
                                            }else{
                                                degree[i + x][j - x] += personDegree3_5
                                            }
                                        }

                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                                    if (map[i + x][j - x] == 0) {
//                                        degree[i + x][j - x] += Math.max(personDegree4,degree[i + x][j - x]  )
                                        if (x == -1) {
//                                            degree[i + x][j - x] += Math.max(personDegree4_0 ,degree[i + x][j - x] )
                                            degree[i + x][j - x] += personDegree4_0
                                        }else if(x < 4){
//                                            degree[i + x][j - x] += Math.max(personDegree4_2 ,degree[i + x][j - x] )
                                            degree[i + x][j - x] += personDegree4_2
                                        }else{
                                            degree[i + x][j - x] += personDegree4

                                        }
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            3 -> if (i - 1 < column && j + 1 >= 0) {
                                if (map[i - 1][j + 1] == computer) {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                                            if (map[i + x][j - x] == 0) degree[i + x][j - x] += min
                                        }
                                    }
                                } else if (map[i - 1][j + 1] == 0) {
//                                    degree[i - 1][j + 1] += Math.max(personDegree3_1,degree[i - 1][j + 1] )
                                    degree[i - 1][j + 1] += personDegree3_1
                                    if(map[i+3][j-3] == computer){
                                        degree[i+4][j-4] += min
                                        degree[i - 1][j + 1] = personDegree3_1
                                    }
                                }else {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                                            if (map[i + x][j - x] == 0) degree[i + x][j - x] += min
                                        }
                                    }
                                }
                            } else {
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
//                                        if (map[i + x][j - x] == 0) degree[i + x][j - x] += Math.max(personDegree3_5,degree[i + x][j - x] )
                                        if (map[i + x][j - x] == 0) degree[i + x][j - x] += personDegree3_5
                                    }
                                }
                            }
                            4 -> if (i + 1 < column && j - 1 >= 0) {
                                if (map[i - 1][j + 1] == computer) {
                                    for (x in 1..4) {
                                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
//                                            if (map[i + x][j - x] == 0) degree[i + x][j - x] += Math.max(personDegree4_1,degree[i + x][j - x] )
                                            if (map[i + x][j - x] == 0) degree[i + x][j - x] += personDegree4_1
                                        }
                                    }
                                } else if (map[i - 1][j + 1] == 0) {
                                    if(map[i+4][j-4]==computer){
//                                        degree[i - 1][j + 1] += Math.max(personDegree4_1,degree[i - 1][j + 1]  )
                                        degree[i - 1][j + 1] += personDegree4_1
                                    }else{
                                        degree[i - 1][j + 1] += min
                                    }

                                }
                            } else {
                                for (x in -1..4) {
                                    if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
//                                        if (map[i + x][j - x] == 0) degree[i + x][j - x] += Math.max(personDegree4_5,degree[i + x][j - x] )
                                        if (map[i + x][j - x] == 0) degree[i + x][j - x] += personDegree4_5
                                    }
                                }
                            }
                        }

                    }
                } else if (map[i][j] == computer) {
                    //下
                    var count = 0
                    var flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && i + x < column) {
                            if (map[i + x][j] == computer){if(x!=-1) count++ }else if (map[i + x][j] == person) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            1 -> {
                                for (x in -1..2) {
                                    if (i + x >= 0 && i + x < column ){
//                                        degree[i+x][j] += Math.max(computerDegree2-Math.abs(i),degree[i+x][j])
                                        degree[i+x][j] += computerDegree2-Math.abs(i)
                                    }
                                }
                            }
                            2 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < row&&map[i+x][j]==0 ) {
//                                    degree[i + x][j] += Math.max(computerDegree3-Math.abs(i),degree[i + x][j] )
                                    degree[i + x][j] += computerDegree3-Math.abs(i)
                                    if (x == 1){
//                                        degree[i + x][j] += Math.max(computerDegree3_2,degree[i + x][j] )
                                        degree[i + x][j] += computerDegree3_2
                                    }
                                }
                            }
                            3 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < row && map[i + x][j] == 0) {
                                    if(x==-1||x==3){
//                                        degree[i + x][j] += Math.max(computerDegree4, degree[i + x][j])
                                        degree[i + x][j] += computerDegree4
                                    }else if(x in 1..2){
//                                        degree[i + x][j] += Math.max(computerDegree4_2, degree[i + x][j])
                                        degree[i + x][j] += computerDegree4_2
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && map[i + x][j] == 0){
//                                    degree[i + x][j] += Math.max(computerDegree5 ,degree[i + x][j] )
                                    degree[i + x][j] += computerDegree5
                                    if(x in 1..3){
                                        degree[i+x][j] += computerDegree5_2
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            1 -> if (i - 1 >= 0 && map[i - 1][j] == person) {
                                for (x in -1..1) if (i + x < column && map[i + x][j] == 0) {
//                                    degree[i + x][j] += Math.max(computerDegree2_1 ,degree[i + x][j] )
                                    degree[i + x][j] += computerDegree2_1
                                }
                            } else {
                                if (i - 1 >= 0 && map[i - 1][j] == person){
                                    if(map[i+1][j]==person){
                                        for(x in 2..4){
                                            degree[i+x][j] += min
                                        }
                                    }else{
//                                        degree[i+1][j] += Math.max(computerDegree2,degree[i+1][j])
                                        degree[i+1][j] += computerDegree2
                                    }
                                }
                            }
                            2 -> if (i - 1 >= 0 && map[i - 1][j] == person) {
                                for (x in -1..4) if (i + x < column && map[i + x][j] == 0) {
//                                    degree[i + x][j] += Math.max(computerDegree3_1-Math.abs(x) ,degree[i + x][j] )
                                    degree[i + x][j] += computerDegree3_1-Math.abs(x)
                                    if(x ==-1){
//                                        degree[i + x][j] += Math.max(computerDegree3 ,degree[i + x][j] )
                                        degree[i + x][j] += computerDegree3
                                    }
                                }
                            } else {
//                                if(i - 1 >= 0 && map[i - 1][j] == 0 ){
                                    if(i+2<row&&map[i+2][j]==person||i+1<row&&map[i+1][j]==person){
                                        for(x in 2..4){
                                            if(i+x < row&&map[i+x][j]==0){
                                                degree[i+x][j] += min
                                            }
                                        }
                                    }else{
//                                        degree[i-1][j] += Math.max(computerDegree3,degree[i-1][j] )
                                        degree[i-1][j] += computerDegree3
                                    }
//                                }
                            }
                            3 -> if (i - 1 >= 0 && map[i - 1][j] == person) {
                                for (x in -1..4) if (i + x < column && map[i + x][j] == 0) {
                                    degree[i + x][j] += min
                                }
                            } else {
                                if(i - 1 >= 0 && map[i - 1][j] == 0){
//                                    degree[i - 1][j] += Math.max(computerDegree4_1, degree[i - 1][j] )
                                    degree[i - 1][j] += computerDegree4_1
                                }else{
                                    for(x in 1..4){
                                        degree[i+x][j] += min
                                    }
                                    if(map[i+4][j]==person){
                                        for(x in 1..4){
                                            degree[i+x][j] += computerDegree5
                                        }
                                    }
                                }
                            }
                            4 -> for (x in -1..4) if (i + x < column && map[i + x][j] == 0){
                                degree[i + x][j] += min
                                if (x==-1&&map[i+4][j] == person||x == 4&&map[i-1][j] == person){
//                                    degree[i + x][j] += Math.max(computerDegree5 ,degree[i + x][j] )
                                    degree[i + x][j] += computerDegree5
                                }
                            }
                        }
                    }
                    //右下
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row) {
                            if (map[i + x][j + x] == computer) {if(x!=-1) count++ } else if (map[i + x][j + x] == person) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            1 -> {
                                for (x in -1..2) {
                                    if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row ){
//                                        degree[i + x][j + x] += Math.max(computerDegree2-Math.abs(i) ,degree[i + x][j + x] )
                                        degree[i + x][j + x] += computerDegree2-Math.abs(i)
                                    }
                                }
                            }
                            2 -> for (x in -1..4) {
                                if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0) {
//                                    degree[i + x][j + x] += Math.max(computerDegree3-Math.abs(x),degree[i + x][j + x] )
                                    degree[i + x][j + x] += computerDegree3-Math.abs(x)
                                    if (x == 1){
//                                        degree[i + x][j + x] += Math.max(computerDegree3_2 ,degree[i + x][j + x] )
                                        degree[i + x][j + x] += computerDegree3_2
                                    }
                                }
                            }
                            3 -> for (x in -1..4) {
                                if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0){
                                    if(x==-1||x==3){
//                                        degree[i + x][j + x] += Math.max(computerDegree4 ,degree[i + x][j + x] )
                                        degree[i + x][j + x] += computerDegree4
                                    }else if(x in 1..2){
//                                        degree[i + x][j + x] += Math.max(computerDegree4_2 ,degree[i + x][j + x] )
                                        degree[i + x][j + x] += computerDegree4_2
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0){
//                                    degree[i + x][j + x] += Math.max(computerDegree5 ,degree[i + x][j + x] )
                                    degree[i + x][j + x] += computerDegree5
                                    if(x in 1..3){
                                        degree[i + x][j + x] += computerDegree5_2
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            1 -> if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == person) {
                                for (x in 1..4) if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0) {
//                                    degree[i + x][j + x] += Math.max(computerDegree2_1 ,degree[i + x][j + x] )
                                    degree[i + x][j + x] += computerDegree2_1
                                }
                            } else {
                                if(i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == 0){
                                    if(i+1<row&&j+1<column&&map[i+1][j+1]==person){
                                        for(x in 2..4){
                                            degree[i+x][j+x] += min
                                        }
                                    }else{
//                                        degree[i+1][j+1] += Math.max(computerDegree2,degree[i+1][j+1] )
                                        degree[i+1][j+1] += computerDegree2
                                    }
                                }
                            }
                            2 -> if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == person) {
                                for (x in -1..4) if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0) {
//                                    degree[i + x][j + x] += Math.max(computerDegree3_1-Math.abs(x) ,degree[i + x][j + x] )
                                    degree[i + x][j + x] += computerDegree3_1-Math.abs(x)
                                    if(x==-1){
//                                        degree[i + x][j + x] += Math.max(computerDegree3 ,degree[i + x][j + x] )
                                        degree[i + x][j + x] += computerDegree3
                                    }
                                }
                            } else {
//                                if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == 0){
                                    if(i+2<row&&j+2<column&&map[i+2][j+2]==person||i+1<row&&j+1<column&&map[i+1][j+1]==person){
                                        for( x in 2..4){
                                            if(i+x<row&&j+x<column&&map[i+x][j+x]==0){
                                                degree[i+x][j+x] += min
                                            }
                                        }
                                    }else {
//                                        degree[i-1][j-1]+= Math.max(computerDegree3,  degree[i-1][j-1] )
                                        degree[i-1][j-1]+= computerDegree3
                                    }
//                                }
                            }
                            3 -> if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == person) {
                                for (x in -1..4) if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0) {
                                    degree[i + x][j + x] += min
                                }
                            } else {
                                if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1] == 0){
//                                    degree[i - 1][j - 1] += Math.max(computerDegree4_1,degree[i - 1][j - 1]  )
                                    degree[i - 1][j - 1] += computerDegree4_1
                                }else {
                                    for(x in 1..4){
                                        degree[i+x][j+x] += min
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && j + x >= 0 && i + x < column && j + x < row && map[i + x][j + x] == 0){
                                    degree[i + x][j + x] += min
                                    if(x == -1&&map[i+4][j+4]==person||x==4&&map[i-1][j-1] == person){
                                        degree[i + x][j + x] += computerDegree5
                                    }
                                }
                            }
                        }
                    }
                    //右
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (j + x >= 0 && j + x < row) {
                            if (map[i][j + x] == computer) {if(x!=-1) count++ } else if (map[i][j + x] == person) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            1 -> {
                                for (x in -1..2) {
//                                    if (j + x >= 0 && j + x < row ) degree[i][j + x] += Math.max(computerDegree2-Math.abs(i),degree[i][j + x]  )
                                    if (j + x >= 0 && j + x < row ) degree[i][j + x] += computerDegree2-Math.abs(i)
                                }
                            }
                            2 -> for (x in -1..4) {
                                if (j + x >= 0 && j + x < row && map[i][j + x] == 0){
//                                    degree[i][j + x] += Math.max(computerDegree3-Math.abs(x),degree[i][j + x] )
                                    degree[i][j + x] += computerDegree3-Math.abs(x)
                                    if (x == 1){
//                                        degree[i][j + x] += Math.max(computerDegree3_2 ,degree[i][j + x] )
                                        degree[i][j + x] += computerDegree3_2
                                    }
                                }
                            }
                            3 -> for (x in -1..4) {
                                if (j + x >= 0 && j + x < row && map[i][j + x] == 0) {
                                    if(x==-1||x==3){
//                                        degree[i][j + x] += Math.max(computerDegree4 ,degree[i][j + x] )
                                        degree[i][j + x] += computerDegree4
                                    }else if(x in 1..2){
//                                        degree[i][j + x] += Math.max(computerDegree4_2 ,degree[i][j + x] )
                                        degree[i][j + x] += computerDegree4_2
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (j + x >= 0 && j + x < row && map[i][j + x] == 0){
//                                    degree[i][j + x] += Math.max(computerDegree5 ,degree[i][j + x] )
                                    degree[i][j + x] += computerDegree5
                                    if(x in 1..3){
                                        degree[i][j + x] += computerDegree5_2
                                    }
                                }
                            }
                        }
                    } else if (flag == 1) {
                        when (count) {
                            1 -> if (j - 1 >= 0 && map[i][j - 1] == person) {
                                for (x in 1..4) if (j + x >= 0 && j + x < row && map[i][j + x] == 0) {
//                                    degree[i][j + x] += Math.max(computerDegree2_1 ,degree[i][j + x] )
                                    degree[i][j + x] += computerDegree2_1
                                }
                            } else {
                                if(j - 1 >= 0 && map[i][j - 1] == 0){
                                    if(j+1<column&&map[i][j+1]==person){
                                        for (x in 2..4){
                                            degree[i][j+x] += min
                                        }
                                    }else{
//                                        degree[i][j-1] += Math.max(computerDegree2,degree[i][j-1]  )
                                        degree[i][j-1] += computerDegree2
                                    }
                                }
                            }
                            2 -> if (j - 1 >= 0 && map[i][j - 1] == person) {
                                for (x in 1..4) if (i + x < column && map[i][j + x] == 0) {
//                                    degree[i][j + x] += Math.max(computerDegree3_1-Math.abs(x) ,degree[i][j + x] )
                                    degree[i][j + x] += computerDegree3_1-Math.abs(x)
                                    if(x ==-1){
//                                        degree[i][j + x] += Math.max(computerDegree3 ,degree[i][j + x] )
                                        degree[i][j + x] += computerDegree3
                                    }
                                }
                            } else {
//                                if(j - 1 >= 0 && map[i][j - 1] == 0){
                                    if(j+2<column&&map[i][j+2]==person||j+1<column&&map[i][j+1]==person){
                                        for(x in 2..4){
                                            if(j+x<row&&map[i][j+x]==0){
                                                degree[i][j+x] += min
                                            }
                                        }
                                    }else {
//                                        degree[i][j-1]+= Math.max(computerDegree3,degree[i][j-1]  )
                                        degree[i][j-1]+=  computerDegree3
                                    }
//                                }
                            }
                            3 -> if (j - 1 >= 0 && map[i][j - 1] == person) {
                                for (x in 1..4) if (i + x < column && map[i][j + x] == 0) {
                                    degree[i][j + x] += min
                                }
                            } else {
                                if(j - 1 >= 0 && map[i][j - 1] == 0 ){
//                                    degree[i][j-1] += Math.max(computerDegree4_1,degree[i][j-1])
                                    degree[i][j-1] +=  computerDegree4_1
                                }else{
                                    for(x in 1..4){
                                        degree[i][j+x] += min
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (j + x >= 0 && j + x < row && map[i][j + x] == 0) {
                                    degree[i][j + x] += min
                                    if(x ==-1&&map[i][j+4]==person||x==4&&map[i][j-1] ==person){
                                        degree[i][j + x] += computerDegree5
                                    }
                                }
                            }
                        }
                    }
                    //左下
                    count = 0
                    flag = 0
                    for (x in 0..4) {
                        if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row) {
                            if (map[i + x][j - x] == computer) {if(x!=-1) count++ } else if (map[i + x][j - x] == person) flag++
                        }
                    }
                    if (flag < 1) {
                        when (count) {
                            1 -> {
                                for (x in -1..2) {
                                    if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row)
//                                    degree[i + x][j - x] += Math.max(computerDegree2-Math.abs(i),degree[i + x][j - x]  )
                                    degree[i + x][j - x] += computerDegree2-Math.abs(i)
                                }
                            }
                            2 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && map[i + x][j - x]== 0) {
//                                    degree[i + x][j - x] += Math.max(computerDegree3-Math.abs(x),degree[i + x][j - x] )
                                    if(x ==1){
//                                        degree[i + x][j - x] += Math.max(computerDegree3_2 ,degree[i + x][j - x] )
                                        degree[i + x][j - x] += computerDegree3_2
                                    }
                                }
                            }
                            3 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && j + x < row && map[i + x][j - x] == 0) {
                                    if(x==-1||x==3){
//                                        degree[i + x][j - x] += Math.max(computerDegree4 ,degree[i + x][j - x] )
                                        degree[i + x][j - x] += computerDegree4
                                    }else if(x in 1..2){
//                                        degree[i + x][j - x] += Math.max(computerDegree4_2 ,degree[i + x][j - x] )
                                        degree[i + x][j - x] += computerDegree4_2
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && map[i + x][j - x] == 0) {
//                                    degree[i + x][j - x] += Math.max(computerDegree5 ,degree[i + x][j - x] )
                                    degree[i + x][j - x] += computerDegree5
                                    if(x in 1..3){
                                        degree[i + x][j - x] += computerDegree5_2
                                    }

                                }
                            }

                        }
                    } else if (flag == 1) {
                        when (count) {
                            1 -> if (i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1 ] == person) {
                                for (x in 1..4) if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row ) {
//                                    degree[i + x][j - x] += Math.max( computerDegree2_1, degree[i + x][j - x] )
                                    degree[i + x][j - x] += computerDegree2_1
                                }
                            } else {
                                if(i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1 ] == 0){
                                    if(i+1<row&&j-1>=0&&map[i+1][j-1] == person){
                                        for (x in 2..4){
                                            degree[i+x][j-x] += min
                                        }
                                    }else {
//                                        degree[i-1][j+1] += Math.max(computerDegree2 ,degree[i-1][j+1]  )
                                        degree[i-1][j+1] += computerDegree2
                                    }
                                }
                            }
                            2 -> if (i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1] == person) {
                                for (x in 1..4) if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && map[i + x][j - x] == 0) {
//                                    degree[i + x][j - x] += Math.max(computerDegree3_1-Math.abs(x),degree[i + x][j - x] )
                                    degree[i + x][j - x] +=  computerDegree3_1-Math.abs(x)
                                }
                            } else {
//                                if(i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1] == 0){
                                    if(i+2<row&&j-1<column&&map[i+2][j-2]==person||i+1<row&&j-1<column&&map[i+1][j-1]==person){
                                        for(x in 2..4){
                                            if(i+x<row&&j-x>=0&&map[i+x][j-x]==0){
                                                degree[i+x][j-x] += min
                                            }
                                        }
                                    }else{
//                                        degree[i-1][j+1]+= Math.max(computerDegree3,degree[i-1][j+1]  )
                                        degree[i-1][j+1]+= computerDegree3
                                    }
//                                }
                            }
                            3 -> if (i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1] == person) {
                                for (x in 1..4) if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && map[i + x][j - x] == 0) {
                                    degree[i + x][j - x] += min
                                }
                            } else {
                                if (i - 1 >= 0 && j + 1 < row && map[i - 1][j + 1] == 0){
//                                    degree[i - 1][j + 1] += Math.max(computerDegree4_1,degree[i - 1][j + 1]  )
                                    degree[i - 1][j + 1] +=  computerDegree4_1
                                }else{
                                    for(x in 1..4){
                                        degree[i+x][j-x] += min
                                    }
                                }
                            }
                            4 -> for (x in -1..4) {
                                if (i + x >= 0 && i + x < column && j - x >= 0 && j - x < row && map[i + x][j - x] == 0){
                                    degree[i + x][j - x] += min
                                    if(x ==-1&&map[i+4][j-4]==person||x==4&&map[i-1][j+1]==person){
                                        degree[i + x][j - x] += computerDegree5
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        public val MODE_PERSONTOPERSON = 0
        public val MODE_PERSONTOCOMPUTER = 1
        public val MODE_COMPUTERTOPERSON = 2
    }

    fun setMode(mode: Int) {
        computer = mode
        if(mode == MODE_COMPUTERTOPERSON){
            isComputer = true
            person = 1
            computer = 2
        }
    }
}
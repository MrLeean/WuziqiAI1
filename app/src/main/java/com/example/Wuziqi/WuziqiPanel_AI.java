package com.example.Wuziqi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class WuziqiPanel_AI extends View {

	private static final String SCORE_INT_ARRAY = "score_int_array";
	private MainActivity mainActivity;
	private WelcomeActivity welcomeActivity;
	private ScoreList scoreList;
	private ScoreRecord scoreRecord;

	private SharedPreferences mSharePreference;

	private int mPanelWidth;
	private float mLineHeight;
	private int MAX_LINE = 10;

	private Paint mPaint = new Paint();

	//定义【普通】黑白棋子
	private Bitmap mWhitePiece;
	private Bitmap mBlackPiece;
	private Bitmap mCurrentBlackPiece;
	private Bitmap mCurrentWhitePiece;

	//定义【标数黑白棋子】资源数组
	private TypedArray ar_black = getContext().getResources().obtainTypedArray(R.array.black_pieces);
	private TypedArray ar_white = getContext().getResources().obtainTypedArray(R.array.white_pieces);
	private int lenOfPieceArray = ar_white.length();
	private int[] ids_black = new int[lenOfPieceArray];
	private int[] ids_white = new int[lenOfPieceArray];

	//定义棋子比例，用于控制棋子大小，可自定义
	private float radioPieceOfLineHeight = 3 * 1.0f/4;
	private int PieceWidth;

	//白棋先下，当前轮到白棋
	private boolean mIsWhite = true;
	//存放点击位置坐标
	private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
	private ArrayList<Point> mBlackArray = new ArrayList<Point>();

	//赢法数组
	int[][][] wins= new int[15][15][200];

	//赢法统计数组
	int[] myWin = new int[500];
	int[] computerWin = new int[500];
	private int count;

	//成绩榜参数:本轮所用步数（前提赢下本轮游戏）
	int stepstoWin = 0;
	//成绩榜参数:本轮所用时间（前提赢下本轮游戏）
	int timetoWin = 0;
	//记录之前分数的成绩榜
	int[] highScores = new int[10];
	//用于更新本轮游戏后的新成绩榜
	int[] newhighScores = new int[10];
	public String[] string_newhighScores = new String[10];
	//存储分享成绩的图片路径
	private View BOARD;
	String ShareImagePath;
	//存储记录游戏过程截图的路径
	String ProcessScreenshotPath;
	//游戏是否结束over
	private boolean over = false;
	//棋盘上两方棋子的标志  0-无子; 1-我方; 2-电脑
	private int[][] chessBoard = new int[MAX_LINE][MAX_LINE];
	//保存最高得分的i，j值
	int u=0;
	int v=0;
	//记录当前游戏次数
	int game_freq = 0;
	//------------------初始化计时组件-------------
	private static String  TAG = "TimerDemo";
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;

	private Handler mHandler = null;

	private static int timeCount = 0;

	private boolean isStop = true;

	private static int delay = 1000;  //1s
	private static int period = 1000;  //1s

	private static final int UPDATE_TEXTVIEW = 0;
	private int totalTime = 0;
	public int TIME = 0;

	private int isFirstTouch = 0;

	//棋盘背景图案
	private Bitmap chessBackground;
	//夜间模式判断
	public Boolean nightMode;

	//定义面板
	public WuziqiPanel_AI(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//setBackgroundColor(0x44ff0000);
		//GTtextVeiw = (TextView) findViewById(R.id.game_time);

		init();
		Wininit();
	}

	/**
	 * 初始化【数字标记】黑白棋子数组
	 */
	private void initPieceArray(){
		for (int i=0; i<lenOfPieceArray; i++){
			ids_black[i] = ar_black.getResourceId(i, 0);
			ids_white[i] = ar_white.getResourceId(i, 0);
		}
		ar_white.recycle();
		ar_black.recycle();
	}

	//初始化
	private void init() {
		// TODO Auto-generated method stub
		initPieceArray();
		mPaint.setColor(0x88000000);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
		mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
		mCurrentBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1_current);
		mCurrentWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2_current);

	}

	public void Wininit(){
		count = 0;
		//横向赢法统计
		for(int i=0;i<10;i++){
			for(int j=0;j<6;j++){
				for(int k=0;k<5;k++){
					wins[i][j+k][count]=1;
				}
				count++;
			}
		}
		//纵向赢法统计
		for(int i=0;i<10;i++){
			for(int j=0;j<6;j++){
				for(int k=0;k<5;k++){
					wins[j+k][i][count]=1;
				}
				count++;
			}
		}

		//左上到右下斜线赢法统计
		for(int i=0;i<6;i++){
			for(int j=0;j<6;j++){
				for(int k=0;k<5;k++){
					wins[i+k][j+k][count]=1;
				}
				count++;
			}
		}

		//右上到左下斜线赢法统计
		for(int i=0;i<6;i++){
			for(int j=9;j>3;j--){
				for(int k=0;k<5;k++){
					wins[i+k][j-k][count]=1;
				}
				count++;
			}
		}

		for(int i = 0 ;i<count;i++){
			myWin[i] = 0;
			computerWin[i] = 0;
		}
	}

	//定义棋盘行数大小
	public void defineLine(int lines){
		MAX_LINE = lines;
	}

	//截取屏幕大小为正方形
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int width = Math.min(widthSize, heightSize);

		//以防View因嵌套在ScrollView内时出现问题
		if(widthMode == MeasureSpec.UNSPECIFIED ){
			width = heightSize;
		}else if(heightMode == MeasureSpec.UNSPECIFIED){
			width = widthSize;
		}

		//设置棋盘大小，包括边界
		setMeasuredDimension(width, width);

		BOARD = this;
	}

	/**
	 * 当宽高尺寸确定发生改变以后回调此函数
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		mPanelWidth = w;
		mLineHeight = mPanelWidth*1.0f/MAX_LINE;

		//根据棋子比例绘制黑白棋子
		int pieceWidth = (int)(mLineHeight*radioPieceOfLineHeight);
		PieceWidth = pieceWidth;
		mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
		mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
		mCurrentBlackPiece = Bitmap.createScaledBitmap(mCurrentBlackPiece, pieceWidth, pieceWidth, false);
		mCurrentWhitePiece = Bitmap.createScaledBitmap(mCurrentWhitePiece, pieceWidth, pieceWidth, false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//绘制棋盘
		drawBoard(canvas);
		//绘制棋子
		drawpieces(canvas);

	}

	//绘制背景
	public void drawBackground(int style) {

		int chessBg = style;
		if(chessBg == 0){
			chessBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg_night);
		}else if(chessBg == 1){
			chessBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg_1);
		}else if(chessBg == 2){
			chessBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg_2);
		}else if(chessBg == 3){
			chessBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg_3);
		}else{
			chessBackground = BitmapFactory.decodeResource(getResources(),R.drawable.bg_4);
		}
	}
	//绘制棋盘
	public void drawBoard(Canvas canvas) {
		// TODO Auto-generated method stub
		int w = mPanelWidth;
		float lineHeight = mLineHeight;


		canvas.drawBitmap(chessBackground, 0, 0, mPaint);

		for(int i= 0;i<MAX_LINE;i++){
			int start = (int) (lineHeight/2);
			int end = (int) (w-lineHeight/2);
			int y = (int) ((0.5+i)*lineHeight);
			mPaint.setStrokeWidth(5);
			mPaint.setColor(Color.BLACK);
			/*if(nightMode == true){
				mPaint.setColor(Color.WHITE);
			}else{
				mPaint.setColor(Color.BLACK);
			}*/

			canvas.drawLine(start, y, end, y, mPaint);
			canvas.drawLine( y,start,y, end, mPaint);
		}
	}
	//绘制棋子
	private void drawpieces(Canvas canvas) {
		// TODO Auto-generated method stub
		for(int i=0,n=mWhiteArray.size(); i<n;i++){
			Point whitePoint = mWhiteArray.get(i);
			if (i == (n-1)){
				canvas.drawBitmap(mCurrentWhitePiece,
						(whitePoint.x+(1-radioPieceOfLineHeight)/2)*mLineHeight,
						(whitePoint.y+(1-radioPieceOfLineHeight)/2)*mLineHeight,null);
			}else{
				canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),ids_white[i]),PieceWidth,PieceWidth,false),
						(whitePoint.x+(1-radioPieceOfLineHeight)/2)*mLineHeight,
						(whitePoint.y+(1-radioPieceOfLineHeight)/2)*mLineHeight,null);
			}
		}

		for(int i=0,n=mBlackArray.size(); i<n;i++){
			Point BlackPoint = mBlackArray.get(i);
			if(i == (n-1)){
				canvas.drawBitmap(mCurrentBlackPiece,
						(BlackPoint.x+(1-radioPieceOfLineHeight)/2)*mLineHeight,
						(BlackPoint.y+(1-radioPieceOfLineHeight)/2)*mLineHeight,null);
			}else{
				canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),ids_black[i]),PieceWidth,PieceWidth,false),
						(BlackPoint.x+(1-radioPieceOfLineHeight)/2)*mLineHeight,
						(BlackPoint.y+(1-radioPieceOfLineHeight)/2)*mLineHeight,null);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(over){
			return false;
		}
		if(!mIsWhite){
			return false;
		}

		int action = event.getAction();
		if(action == MotionEvent.ACTION_UP){

			//开启计时器
			if(isFirstTouch == 0){
				startTimer();
				isFirstTouch++;
			}

			int x= (int) event.getX();
			int y = (int) event.getY();

			Point p = getValidPoint(x,y);
			int m = p.x;
			int n = p.y;

			if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
				return false;
			}

			if(mIsWhite){
				mWhiteArray.add(p);

//				//获取截图
//				ProcessScreenshot(mWhiteArray.size());

				chessBoard[m][n]=1;
				for(int k = 0; k<count;k++){
					if(wins[m][n][k] == 1){
						myWin[k]++;
						computerWin[k] = 6;
						if(myWin[k]==5){
							Toast.makeText(this.getContext(), R.string.alertWinnerUser, Toast.LENGTH_SHORT).show();

							//更新得分榜
							stepstoWin = mWhiteArray.size();
							highScores = updateHighScores(stepstoWin);

							intToStringArray(highScores);

							//停止计时
							TIME = stopTimer();

							//弹出对话框
							dialogWin(0, totalTime, mWhiteArray.size());
							totalTime = 0;

							//游戏次数记录
							int gameFreq = sharePreferenceGet() + 1;
							sharePreferecenPut(gameFreq);

							over = true;
						}
					}
				}

				if(!over){
					mIsWhite = !mIsWhite;
					computerAI();
				}
			}
			invalidate();
			return true;
		}
		return true;
	}


	//AI算法
	private void computerAI() {
		// TODO Auto-generated method stub

		//保存最高得分
		int max = 0;

		int[][] myScore = new int[10][10];
		int[][] computerScore = new int[10][10];
		//初始化分数值
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				myScore[i][j] = 0;
				computerScore[i][j]=0;
			}
		}

		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(chessBoard[i][j] == 0){
					for(int k=0;k<count;k++){
						if(wins[i][j][k]==1){
							//我方得分，计算机拦截
							if(myWin[k]==1){
								myScore[i][j]+=200;
							}else if(myWin[k]==2){
								myScore[i][j]+=400;
							}else if(myWin[k] == 3){
								myScore[i][j]+=2000;
							}else if(myWin[k] == 4){
								myScore[i][j] += 10000;
							}

							//计算机走法 得分
							if(computerWin[k]==1){
								computerScore[i][j]+=220;
							}else if(computerWin[k]==2){
								computerScore[i][j]+=420;
							}else if(computerWin[k] == 3){
								computerScore[i][j]+=2100;
							}else if(computerWin[k] == 4){
								computerScore[i][j] += 20000;
							}

						}
					}

					//判断我方最高得分，将最高分数的点获取出来, u，v为计算机要落下的子的坐标
					if(myScore[i][j]>max){
						max = myScore[i][j];
						u = i;
						v = j;
					}else if(myScore[i][j] == max ){
						if(computerScore[i][j]>computerScore[u][v]){
							//认为i，j点比u，v点好
							u = i;
							v = j;
						}
					}

					//判断电脑方最高得分，将最高分数的点获取出来
					if(computerScore[i][j]>max){
						max = computerScore[i][j];
						u = i;
						v = j;
					}else if(computerScore[i][j] == max ){
						if(myScore[i][j]>myScore[u][v]){
							//认为i，j点比u，v点好
							u = i;
							v = j;
						}
					}

				}
			}
		}
		chessBoard[u][v] = 2;
		mBlackArray.add(new Point(u,v));
		invalidate();
		ProcessScreenshot(mWhiteArray.size());
		for(int k = 0; k<count;k++){
			if(wins[u][v][k] == 1){
				computerWin[k]++;
				myWin[k] = 6;
				if(computerWin[k]==5){
					Toast.makeText(this.getContext(), R.string.alertWinnerAI, Toast.LENGTH_SHORT).show();
					TIME = stopTimer();
					//记录获胜时的步数
					stepstoWin = mWhiteArray.size();
					//弹出对话框
					dialogWin(1, totalTime, stepstoWin);
					totalTime = 0;
					over = true;
				}
			}
		}
		if(!over){
			mIsWhite = !mIsWhite;
		}
	}

	/**
	 * 更新得分榜数据
	 * @param currentScore
	 */
	private int[] updateHighScores(int currentScore) {
		//存储存放成绩的int型数组(1)
		Bundle bd = new Bundle();
		if(bd.getIntArray(SCORE_INT_ARRAY) == null){
			highScores = new int[]{0,0,0,0,0,0,0,0,0,0};
		}else{
			highScores = bd.getIntArray(SCORE_INT_ARRAY);
		}

		newhighScores = highScores;
		if(highScores[0] == 0){
			newhighScores[0] = currentScore;
		}else{
			for(int i = 0; i < highScores.length; i++){
				if(currentScore > highScores[i]){
					newhighScores[i] = highScores[i];
				}else if(highScores[i] == 0){
					newhighScores[i] = currentScore;
					for(int j = i; j< (highScores.length-1); j++){
						newhighScores[j+1] = 0;
					}
					break;
				}else if(currentScore <= highScores[i]){
					newhighScores[i] = currentScore;
					for(int j = i; j< (highScores.length-1); j++){
						newhighScores[j+1] = highScores[j];
					}
					break;
				}
			}
		}

		//存储存放成绩的int型数组(2)
		bd.putIntArray(SCORE_INT_ARRAY, newhighScores);

		return newhighScores;
	}

	/**
	 *
	 * @param int_scores
	 */
	private void intToStringArray(int[] int_scores) {

		for(int i =0; i<string_newhighScores.length; i++){
			if(int_scores[i] != 0){
				string_newhighScores[i] = "第" + (i+1) +"名: " + int_scores[i] + "步";
			}else{
				string_newhighScores[i] = "-待记录-";
			}
		}

		scoreRecord.saveArray(getContext(), string_newhighScores);
	}

	/**
	 * 计时器
	 */
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					Log.i(TAG, "timeCount: " + String.valueOf(timeCount));

					timeCount++;
				}
			};
		}
		if (mTimer != null && mTimerTask != null)
			mTimer.schedule(mTimerTask, delay, period);
	}

	/**
	 * 停止计时器
	 * @return
     */
	public int stopTimer(){
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

		//记录游戏结束时总用时
		totalTime = timeCount;
		//计时清零
		timeCount = 0;

		return totalTime;
	}
	/**
	 * 计时器代码结束
	 */


	/**
	 * 游戏结束弹出对话框
	 */
	public void dialogWin(int n, int time, int step){
		int gaming_time = time;
		int gaming_step = step;
		Context context = getContext();

		String TotalTime = getResources().getString(R.string.Time);
		String MIAO = getResources().getString(R.string.alertMIAO);
		String TotalStep = getResources().getString(R.string.alertStep);
		String BUSHU = getResources().getString(R.string.alertBUSHU);

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setInverseBackgroundForced(true);

		int num = n;
		switch(num){
			case 0:

				if(gaming_step<=20){
					builder.setTitle(R.string.alertWinnerUser).setMessage(TotalTime + gaming_time
							+ MIAO + "\n"
							+ TotalStep + gaming_step
							+ BUSHU + "\n" + getResources().getString(R.string.alertBRILLIANT));
					break;
				}else if(gaming_step>50){
					builder.setTitle(R.string.alertWinnerUser).setMessage(TotalTime + gaming_time
							+ MIAO + "\n"
							+ TotalStep + gaming_step
							+ BUSHU + "\n" + getResources().getString(R.string.alertWONDERFUL));
					break;
				}else{
					builder.setTitle(R.string.alertWinnerUser).setMessage(TotalTime + gaming_time
							+ MIAO + "\n"
							+ TotalStep + gaming_step
							+ BUSHU + "\n" + getResources().getString(R.string.alertGOOD));
					break;
				}

			case 1:
				builder.setTitle( R.string.alertWinnerAI).setMessage(TotalTime + gaming_time
						+ MIAO + "\n"
						+ TotalStep + gaming_step
						+ BUSHU + "\n" + getResources().getString(R.string.alertENCOURAGE));
				break;
		}


		setPositiveButton(builder);
		setNegativeButton(builder);

		/**
		 * 设置AlertDialog字体大小
		 */
		AlertDialog dialog = builder.create();
		dialog.show();
		//控制 dialog 按钮字体大小
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(24);
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(24);
		TextView textView = (TextView) dialog.findViewById(android.R.id.message);
		textView.setTextSize(24);

	}
	private AlertDialog.Builder setPositiveButton(final AlertDialog.Builder builder) {
		return builder.setPositiveButton(R.string.alertRetry, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
	}
	private AlertDialog.Builder setNegativeButton(final AlertDialog.Builder builder) {
		return builder.setNegativeButton(R.string.alertShare, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//截图
				takeScreenShot();
				//分享成绩
				showShare();
			}
		});
	}

	/**
	 * 获取分享截图
	 * @return
	 */
	public void takeScreenShot(){

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		Date now = new Date();
		String time = sdf.format(now);
		//android.text.format.DateFormat.format("MM/dd/yy h:mmaa", now);

		try{
			String mPath = Environment.getExternalStorageDirectory().toString() + "/Wuziqi/Screenshot/" + TIME + "_" + stepstoWin + ".jpg";
			ShareImagePath = mPath;
			/*
			View v1 = getRootView();
			v1.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);
			*/

			//只获取期盼内容截图 2016/10/01
			BOARD.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(BOARD.getDrawingCache());
			BOARD.setDrawingCacheEnabled(false);

			bitmap = addStepInfo(bitmap, stepstoWin);

			File imageFile = new File(mPath);

			FileOutputStream outputStream = new FileOutputStream(imageFile);
			int quality = 80;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality,outputStream);
			outputStream.flush();
			outputStream.close();
		}
		catch (Throwable e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取游戏过程截图
	 */
	 public void ProcessScreenshot(int index){

		 SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		 Date now = new Date();
		 //String time = sdf.format(now);
		 //android.text.format.DateFormat.format("MM/dd/yy h:mmaa", now);

		 try{
			 String mPath = Environment.getExternalStorageDirectory().toString() + "/Wuziqi/Cache/" + index + ".jpg";
			 ProcessScreenshotPath = mPath;
			/*
			View v1 = getRootView();
			v1.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);
			*/

			 //只获取期盼内容截图 2016/10/01
			 BOARD.setDrawingCacheEnabled(true);
			 Bitmap bitmap = Bitmap.createBitmap(BOARD.getDrawingCache());
			 BOARD.setDrawingCacheEnabled(false);

			 bitmap = addStepInfo(bitmap, stepstoWin);

			 File imageFile = new File(mPath);

			 FileOutputStream outputStream = new FileOutputStream(imageFile);
			 int quality = 60;
			 bitmap.compress(Bitmap.CompressFormat.JPEG, quality,outputStream);
			 outputStream.flush();
			 outputStream.close();
		 }
		 catch (Throwable e){
			 e.printStackTrace();
		 }
	 }


	/**
	 * 为截图添加文字（步数信息）
	 */
	public Bitmap addStepInfo(Bitmap bitmap, int setp){
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		//写字
		/*String title = getContext().getResources().getString(R.string.gaming_step);
		String title2 = getContext().getResources().getString(R.string.gaming_time);
		String contents = title + stepstoWin + "\n" + title2 + TIME;
		paint.setTextSize(60);
		paint.setColor(Color.BLACK);
		canvas.drawText(contents, 50, 50, paint);*/
		return bitmap;
	}


	/**
	 * 社交网络分享（目前集成微信）
	 */
	public void showShare() {
		Context context = getContext();
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(context.getString(R.string.share));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我在下五子棋，又取得了新成绩，快来看看吧！");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

		File SDpath = Environment.getExternalStorageDirectory();
		//String image_path = SDpath + File.separator + "Wuziqi" + File.separator + "1.jpg";
		String image_path = ShareImagePath;

		oks.setImagePath(image_path);//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(context);
	}

	private int sharePreferenceGet(){
		mSharePreference = getContext().getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
		game_freq = mSharePreference.getInt("Game_freq", 1);
		return game_freq;
	}

	private void sharePreferecenPut(int num){
		mSharePreference = getContext().getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mSharePreference.edit();
		editor.putInt("Game_freq", num);
		editor.commit();
	}

	/**
	 * 设置点击区域识别
	 */
	private Point getValidPoint(int x, int y) {
		// TODO Auto-generated method stub
		return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
	}

	/**
	 * View的存储与恢复，注意在XML文件中为View添加ID
	 */
	private static final String INSTANCE = "instance";
	private static final String INSTANCE_GAME_OVER = "instance_game_over";
	private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
	private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
	private static final String INSTANCE_HIGH_SCORES = "instance_high_scores";
	private static final String INSTANCE_STRING_HIGHSCORE_LIST = "instance_high_score_list";

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
		bundle.putBoolean(INSTANCE_GAME_OVER, over);
		bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
		bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
		bundle.putIntArray(INSTANCE_HIGH_SCORES, newhighScores);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if(state instanceof Bundle){
			Bundle bundle = (Bundle) state;
			over = bundle.getBoolean(INSTANCE_GAME_OVER);
			mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
			mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
			highScores = bundle.getIntArray(INSTANCE_HIGH_SCORES);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
			return ;
		}
		super.onRestoreInstanceState(state);
	}
	/**
	 * View的存储与恢复，注意在XML文件中为View添加ID 代码结束
	 */
}

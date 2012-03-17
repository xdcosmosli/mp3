package player.mp3.activity;

import player.constant.Constant;
import player.model.Mp3Info;
import player.mp3.R;
import player.mp3.service.PlayerService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity{
	
	private ImageButton imgBtn_start = null;
	private ImageButton imgBtn_pause = null;
	private ImageButton imgBtn_stop = null;
	
	private TextView lrcAreaTxtView = null;
	
	private Intent intent = null;//发送给PlayerService的Intent对象
	
	private String lrcString = null;//存储播放MP3文件的lrc歌词文件内容

	private BroadcastReceiver broadcastReceiver = new LrcBroadcastReceiver();
	
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(broadcastReceiver);//取消注册broadcastReceiver
	}

	protected void onResume()
	{
		super.onResume();
		//注册broadcastReceiver
		registerReceiver(broadcastReceiver, getIntentFilter());
	}
	/**
	 * 获得IntentFilter对象
	 * @return
	 */
	private IntentFilter getIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.BROADCAST_ACTION.LRC_UPDATE_ACTION);
		return filter;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.player);
		getTextViewResource();//获得显示歌词资源
		getImageButtonResource();//获取图片按钮资源
		bindListenerToButton();//绑定监听器到按钮上
		//获得lrc文件的字符串内容
		lrcString = getIntent().getStringExtra("lrcStr");
		//创建从PlayerActivity到PlayerService的intent
		intent = new Intent(PlayerActivity.this, PlayerService.class);
	}
	/**
	 * 获取显示歌词的TextView资源
	 */
	private void getTextViewResource()
	{
		lrcAreaTxtView = (TextView)findViewById(R.id.PlayerActivity_lrc_showarea_textview);
	}
	/**
	 * 获取图片按钮资源
	 */
	private void getImageButtonResource(){
		imgBtn_start = (ImageButton)findViewById(R.id.player_start);
		imgBtn_pause = (ImageButton)findViewById(R.id.player_pause);
		imgBtn_stop = (ImageButton)findViewById(R.id.player_stop);
	}
	
	/**
	 * 绑定监听器到按钮上
	 */
	private void bindListenerToButton(){
		imgBtn_start.setOnClickListener(new StartButtonListener());
		imgBtn_pause.setOnClickListener(new PauseButtonListener());
		imgBtn_stop.setOnClickListener(new StopButtonListener());
	}
	
	/**
	 * 开始播放按钮内部类，当用户点击开始播放时调用这个类的onClick方法
	 * @author Administrator
	 *
	 */
	private class StartButtonListener implements OnClickListener{

		public void onClick(View view) {
			Mp3Info mp3Info = (Mp3Info)PlayerActivity.this.getIntent().getSerializableExtra(Constant.MP3.MP3_INFO_KEY);
			intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);//把MP3Info对象加入到Intent中传入PlayerService
			intent.putExtra("lrcString", lrcString);//歌词内容字符串添加到Intent中传入PlayerService
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_START);
			startService(intent);//打开Service
		}
	}
	
	/**
	 * 暂停按钮内部类，当用户点击暂停按钮时调用这个类的onClick方法
	 * @author Administrator
	 */
	private class PauseButtonListener implements OnClickListener{
		public void onClick(View view) {
			//发送暂停播放Mp3命令
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_PAUSE);
			startService(intent);//打开Service
		}
	}
	
	/**
	 * 停止按钮内部类，当用户点击停止时调用这个类的onClick方法
	 * @author Administrator
	 */
	private class StopButtonListener implements OnClickListener{

		public void onClick(View v) {
			//发送停止播放Mp3命令
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_STOP);
			startService(intent);//打开Service
		}
	}
	
	/**
	 * 歌词广播接收器内部类,当这个广播接收器接收到响应的系统消息后就调用此方法，更新歌词
	 * @author Administrator
	 */
	private class LrcBroadcastReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
		{
			//获得待更新的歌词
			String updateLrc = intent.getStringExtra("update_lrc");
			lrcAreaTxtView.setText(updateLrc);//更新歌词
		//*****************************************88测试代码*****************
			System.out.println(updateLrc);
		//****************************************************************8***
		}
	}
	
	/**
	 * 发送控制MP3播放的命令给PlayerService
	 * @param msg
	 */
	private void sendMp3PlayCommand(int msg){
		//发送播放的消息给PlayerService
		intent.putExtra(Constant.Message.PLAY_MP3_MESSAGE_KEY, msg);
	}
}

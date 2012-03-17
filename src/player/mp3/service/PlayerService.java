package player.mp3.service;

import java.util.Queue;

import file.utils.FileUtils;
import player.constant.Constant;
import player.lrc.LyricInfo;
import player.lrc.LyricProcessor;
import player.model.Mp3Info;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

public class PlayerService extends Service{

	private int message = 0;//mp3播放的消息，有开始，暂停，停止消息
	private Mp3Info mp3Info = null;//存储用户所点的MP3文件

	private boolean isPlaying = false;//判断是否处于已經打開播放状态
	private boolean isPause = false;//判断是否处于暂停状态
	private boolean isReleased = false;//判断是否处于释放状态
	
	private MediaPlayer mediaPlayer = null;
	
	private String lrcString = null;//存储播放MP3文件的lrc歌词文件内容
	private LyricInfo lrcInfo = new LyricInfo();//解析后的歌词对象
	
	private Looper looper = null;
//	private Handler handler = new Handler();
	private LrcHandler handler = null;
	
	private UpdateTimeCallback updateTimeCallback = null;
	
	private long begin = 0L;//记录MP3开始播放时刻，单位是毫秒
	private long pauseTime = 0L;//记录暂停播放的时间，单位是毫秒
	private String lrcLine = null;//记录当前要播放的一行歌词
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		HandlerThread handlerThread = new HandlerThread("handler_thread");
		handlerThread.start();//打开一个新线程
		looper = handlerThread.getLooper();
		handler = new LrcHandler(looper); 
	}

	/**
	 * 当用户启动一个Service就调用这个方法
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		getExtraInfoFromIntent(intent);//从Intent获取发送过来的信息
		messageHandler(message);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * MP3播放消息处理方法，根据消息的不同进行不同的处理
	 * @param msg
	 */
	private void messageHandler(int msg){
		if(Constant.Message.MP3_PLAY_START == msg){
			//开始播放MP3
			play();
		}
		else if(Constant.Message.MP3_PLAY_PAUSE == msg){
			//暂停播放Mp3
			pause();
		}
		else if(Constant.Message.MP3_PLAY_STOP == msg){
			//停止播放Mp3
			stop();
		}
	}
	
	/**
	 * 获得MP3播放的命令，可以是MP3_PLAY_START、MP3_PLAY_PAUSE、MP3_PLAY_STOP
	 * @param intent
	 * @return int
	 */
	private int getMp3PlayCommand(Intent intent){
		return intent.getIntExtra(Constant.Message.PLAY_MP3_MESSAGE_KEY, -1);
	}
	
	/**
	 * 获得Mp3Info 对象，它是用户选定的
	 * @param intent
	 * @return
	 */
	private Mp3Info getMp3FromIntent(Intent intent){
		return (Mp3Info)intent.getSerializableExtra(Constant.MP3.MP3_INFO_KEY);
	}
	
	/**
	 * 从Intent获取发送过来的信息
	 * @param intent
	 */
	private void getExtraInfoFromIntent(Intent intent){
		mp3Info = getMp3FromIntent(intent);
		message = getMp3PlayCommand(intent);
		lrcString = intent.getStringExtra("lrcString");
	}
	
	/**
	 * 播放MP3文件
	 */
	private void play(){
		Uri uri = getMp3UriFromSDCard(mp3Info);//获得MP3Uri
		mediaPlayer = MediaPlayer.create(PlayerService.this, uri);
		if(mediaPlayer!=null){
			
			lrcInfo = parseLrc(lrcString);//解析歌词，读取lrc字符串，解析成IrcInfo对象
			//创建一个更新时间的线程对象
			updateTimeCallback = new UpdateTimeCallback(lrcInfo);
			
			mediaPlayer.setLooping(false);//循环播放设为false，就不循环播放了
			mediaPlayer.start();//开始播放
			begin = System.currentTimeMillis();//开始播放后获取开始时刻，单位是毫秒
			//执行更新歌词线程
			handler.post(updateTimeCallback);
			//更改播放状态
			isPlaying = mediaPlayer.isPlaying();
			isPause = false;
			isReleased = false;
		}
	}
	
	/**
	 * 暫停播放MP3文件
	 */
	private void pause(){
		if(null != mediaPlayer){
			if(!isPause && !isReleased){
				mediaPlayer.pause();//暂停播放
				handler.removeCallbacks(updateTimeCallback);//把更新线程移除线程队列，不进行更新歌词
				pauseTime = System.currentTimeMillis();//获取暂停播放当前时刻
				isPause = true;//改变播放状态为暂停
			}
			else if(isPause && !isReleased){
				mediaPlayer.start();//重新开始继续播放
				handler.post(updateTimeCallback);//把更新线程重新移入线程队列，进行更新歌词
				begin = begin + System.currentTimeMillis() - pauseTime;
				isPause = false;//更改播放状态
			}
			isPlaying = true;
			isReleased = false;
		}
	}
	
	/**
	 * 停止播放Mp3文件
	 */
	private void stop(){
		if(null != mediaPlayer){
			if(isPlaying && !isReleased){
				mediaPlayer.stop();//停止播放
				mediaPlayer.release();//释放播放器资源
				handler.removeCallbacks(updateTimeCallback);//把更新线程移除线程队列，不进行更新歌词
				isPlaying = false;
				isPause = false;
				isReleased = true;
			}
		}
	}
	
	/**
	 * 更新歌词回调函数内部类
	 * @author Administrator
	 */
	private class UpdateTimeCallback implements Runnable
	{
		Queue<String> lrcQueue = null;//歌词队列
		Queue<Long> timeQueue = null;//时间点队列
		Long currentTime = 0L;//当前播放歌词所对应的开始时刻
		Long nextTime = 0L;//当前播放歌词对应的结束时刻，也就是下一句歌词的开始时刻
		public UpdateTimeCallback(LyricInfo lrcInfo)
		{
			lrcQueue = lrcInfo.getLyricQueue();
			timeQueue = lrcInfo.getTimeStampQueue();
		}
		public void run()
		{
			//计算从开始播放到现在共消耗了多少时间，以毫秒为单位
			long offset = System.currentTimeMillis() - begin;
			if(!timeQueue.isEmpty())
			{
				currentTime = timeQueue.peek();//取出当前歌词对应的开始时刻
				Long temp = timeQueue.poll();//临时保存队列里队首的时刻
				nextTime = timeQueue.peek();//取出当前歌词对应的结束时刻，即下一句歌词的开始时刻
				timeQueue.add(temp);//重新把队首时刻放回队列中
				if(null != currentTime && 0 == currentTime)
				{
					lrcLine = lrcQueue.poll();
					timeQueue.poll();
				}
				if(null != nextTime && offset >= nextTime)//如果当前的时刻偏移量大于等于当前歌词的结束时刻，就更新下一句歌词
				{
					Intent intent = new Intent();//创建一个Intent对象
					intent.setAction(Constant.BROADCAST_ACTION.LRC_UPDATE_ACTION);//设置Intent的动作为更新歌词Action
					intent.putExtra("update_lrc", lrcLine);//把要更新的歌词放入Intent对象中准备传给PlayerActivity进行更新
					sendBroadcast(intent);//发送广播，进行更新
					lrcLine = lrcQueue.poll();//取出队首歌词，准备更新歌词
					timeQueue.poll();
				}
			}
			handler.post(updateTimeCallback);//继续播放
		}
	}
	
	/**
	 * 从SD卡上获得指定的MP3URI路径
	 * @param mp3Info
	 * @return Uri
	 */
	private Uri getMp3UriFromSDCard(Mp3Info mp3Info){
		FileUtils fileUtils = new FileUtils();
		return fileUtils.getFileUriFromSDCard(Constant.MP3.MP3_DIR_PATH, mp3Info.getMp3Name());
	}
	
	/**
	 * 解析歌词文件，获得解析后的歌词信息对象
	 * @param lrcStr 歌词文件的内容
	 * @return LyricInfo 歌词信息对象
	 */
	private LyricInfo parseLrc(String lrcStr)
	{
		LyricProcessor lrcProcessor = new LyricProcessor();
		return lrcProcessor.parseLrc(lrcStr);//解析歌词;
	}
}



class LrcHandler extends Handler
{
	public LrcHandler()
	{
	}
	public LrcHandler(Looper looper)
	{
		super(looper);
	}
}

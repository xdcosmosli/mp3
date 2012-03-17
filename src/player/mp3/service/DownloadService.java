package player.mp3.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import file.utils.FileUtils;

import player.constant.Constant;
import player.download.HttpDownloader;
import player.model.Mp3Info;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * 实现下载功能的Service类，此類主要實現下載MP3和lrc文件
 * @author Administrator
 */
public class DownloadService extends Service
{
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	/**
	 * 每次用户点击Mp3ListActivity列表上的一个条目是就执行这个方法
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		//从Intent对象中获取用户点取的mp3Info对象信息。
		Mp3Info mp3Info = (Mp3Info)intent.getExtras().get(Constant.MP3.MP3_INFO_KEY);
		//生成一个下载线程，并将Mp3Info对象作为参数传递到线程对象中
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		//启动下载MP3线程
		Thread mp3downloadThread = new Thread(downloadThread);
		mp3downloadThread.start();//启动下载MP3线程
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * MP3下载线程内部类，下載MP3文件和lrc文件
	 * @author Administrator
	 */
	private class DownloadThread implements Runnable
	{
		private Mp3Info mp3Info = null;//存储要下载的MP3信息
		
		public DownloadThread(Mp3Info mp3Info)
		{
			this.mp3Info = mp3Info;
		}
		
		public void run()
		{
			downloadMp3File();//下载MP3文件
			downloadLrcFile();//下載lrc文件
		}
		
		/**
		 * 下载MP3文件
		 */
		private void downloadMp3File(){
			//生成下载文件所需的对象
			HttpDownloader httpDownloader = new HttpDownloader();
			//下载指定的MP3文件到指定的SD卡目录中
			//生成远程服务器存储MP3文件的目录的url字符串
			String urlStr = Constant.URL.SERVER_ROOT_URL_STR+Constant.MP3.MP3_DIR_PATH+File.separator+mp3Info.getMp3Name();
			//下载二进制文件，并存储在指定的本地SD卡目录下，并以指定的文件名存储
			int result = httpDownloader.downloadBinaryFileToSDCard(urlStr, Constant.MP3.MP3_DIR_PATH, mp3Info.getMp3Name());
		    String resultMessage = null;
			if(result == HttpDownloader.FILE_DOWNLOAD_SUCCESS){
				resultMessage = "下载成功";
			}else if(result == HttpDownloader.FILE_DOWNLOAD_EXIST){
				resultMessage = "文件已经存在，不需要重复下载";
			}else {
				resultMessage = "文件下载失败";
			}
			
			//把文件下载的结果信息显示给用户
//			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE );
//			Notification notification = new Notification();
//			notification.tickerText = resultMessage;
//			notification.when = System.currentTimeMillis();
//			Context context = getApplicationContext();
//			String contentTitle = "download";
//			String contentText = resultMessage;
//			Intent notificationIntent = new Intent(DownloadService.this, Mp3ListActivity.class);
//			PendingIntent contentIntent = PendingIntent.getService(this, 0, null, PendingIntent.FLAG_ONE_SHOT);
//			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//			
//			notificationManager.notify(DOWNLOAD_ID, notification);
//			System.out.println("context------->"+context.toString());
		}
		/**
		 * 下載lrc歌詞文件
		 */
		private void downloadLrcFile(){
			HttpDownloader httpDownloader = new HttpDownloader();
			//生成远程服务器存储lrc文件的url
			String urlstr = Constant.URL.MP3_URL_STR + mp3Info.getLrcName();
			//下载指定的lrc文件
			String lrcStr = httpDownloader.downloadTxtFile(urlstr);
			if(null == lrcStr){
				return;
			}
			//把下载下的lrc文件存到SD卡中
			saveLrcFileToSDCard(lrcStr, Constant.MP3.MP3_DIR_PATH, mp3Info.getLrcName());
		}
		/**
		 * 把下载下的lrc文件存到SD卡中
		 * @param lrcStr lrc文本文件内容
		 * @param dir SD卡上存储lrc文件的目录
		 * @param fileName 存储在SD卡上的lrc文件名
		 */
		private void saveLrcFileToSDCard(String lrcStr, String dir, String fileName){
			FileUtils fileUtil = new FileUtils();//创建一个FileUtils对象
			fileUtil.createPathInSDCard(dir);//在SD卡上创建存储lrc文件的目录
			fileUtil.createFileInSDCard(dir, fileName);//在SD卡上创建lrc文件
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(lrcStr.getBytes());
			InputStream input = byteArrayInputStream;
			fileUtil.writeToSDCardFromInputStream(dir, fileName, input);//把数据写入SD卡上的lrc文件中
		}
	}
}

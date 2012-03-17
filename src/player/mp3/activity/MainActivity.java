package player.mp3.activity;

import player.mp3.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * MP3播放器的主界面Activity类
 * @author Administrator
 */
public class MainActivity extends TabActivity
{
	private TabHost tabHost = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//得到TabHost对象，针对TabActivity的操作都是用这个对象完成
		tabHost = getTabHost();
		Resources resource = getResources();//获得资源对象
		//添加显示本地服务器端MP3列表界面Activity
		addLocalListActivity(resource);
		//添加显示远程服务器端MP3列表界面Activity
		addRemoteServerAcitvity(resource);
	}
	
	/**
	 * 跳转到连接远程服务器下载列表Activity类
	 */
	private void addRemoteServerAcitvity(Resources resource)
	{
		//定义一个跳转到远程服务器端的Intent对象
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(this, RemoteServerMp3ListActivity.class);

		addTabSpecOnTabHost(remoteIntent, "下载", resource.getDrawable(R.drawable.heyzap));
	}
	
	/**
	 * 添加显示本地MP3 列表界面Activity类
	 * @param resource
	 */
	private void addLocalListActivity(Resources resource)
	{
		//定义一个跳转到本地的Intent对象
		Intent localIntent = new Intent(this, LocalMp3ListActivity.class);

		addTabSpecOnTabHost(localIntent, "本地列表", resource.getDrawable(R.drawable.bluetooth));
	}
	
	/**
	 * 添加TabSpec
	 * @param targetIntent
	 * @param tabSpecName
	 * @param drawable
	 */
	private void addTabSpecOnTabHost(Intent targetIntent, String tabSpecName,  Drawable drawable)
	{
		//生成一个TabSpec对象，这个对象是代表一个页
		TabHost.TabSpec targetTabSpec = tabHost.newTabSpec(tabSpecName);
		//设置该页的指示器
		targetTabSpec.setIndicator(tabSpecName, drawable);
		//设置该页的内容Intent
		targetTabSpec.setContent(targetIntent);
		//将设置好的TabSpec对象添加到TabHost中
		tabHost.addTab(targetTabSpec);
	}
}

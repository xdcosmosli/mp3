package player.mp3.activity;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import player.constant.Constant;
import player.download.HttpDownloader;
import player.model.Mp3Info;
import player.mp3.R;
import player.mp3.service.DownloadService;
import player.xml.Mp3ListContentHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 远程下载服务器的界面Acitivy类,显示下载列表。
 * @author Administrator
 */
public class RemoteServerMp3ListActivity extends Activity 
{
	
	private static final int GROUP_ID = 0;
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;
	private List<Mp3Info> mp3lists = null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_server_mp3_list);
    }
    
    protected void onResume() {
    	super.onResume();
    	HttpDownloader httpDownloader = new HttpDownloader();
    	if(!httpDownloader.isServerConnected(Constant.URL.SERVER_ROOT_URL_STR)){
    		//如果没有连接到服务器则对用户显示未连接服务器信息
    		showConnectionErrorMessageToClient();
    		mp3lists = new ArrayList<Mp3Info>();
    	}
    	else{//连接上则更新服务器mp3列表
    		//更新服务器列表
    		getServerUpdatedMp3List();
    	}
    	//更新显示MP3列表
		updateMp3ListShow();
    }
    /**
     * 向客户显示连接错误信息
     */
    private void showConnectionErrorMessageToClient(){
    	CharSequence text = "服务器未连接，该下载功能暂时不能实现，请使用别的功能!";
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();
		Button btn = new Button(RemoteServerMp3ListActivity.this);
		btn.setText("关闭");
		final Dialog dlg = new Dialog(RemoteServerMp3ListActivity.this);
		TextView txt = new TextView(dlg.getContext());
		txt.setText(text);
		
		dlg.setContentView(txt);
		dlg.show();
		btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				dlg.dismiss();
			}
		});
    }
    
    /**
     * 点击MENU按钮之后，会调用该方法，我们可以在这个方法当中加入自己的按钮控件
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	int groupId = GROUP_ID;

    	int itemId = UPDATE;
    	int order = 1;
    	int title = R.string.mp3list_update;
    	menu.add(groupId, itemId, order, title);
    	
    	itemId = ABOUT;
    	order = 2;
    	title = R.string.mp3list_about;
    	menu.add(groupId, itemId, order, title);
    	
		return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * 当用户点击Menu的某一项就调用此方法
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if(item.getItemId() == UPDATE)
    	{   //用户点击了更新列表按钮
    		//获取更新服务器端MP3
    		getServerUpdatedMp3List();
    		//更新显示MP3列表
    		updateMp3ListShow();
    	}
    	else if(item.getItemId() == ABOUT)
    	{//用户点击关于按钮
    		
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    /**
     * 获取更新服务器端MP3
     */
    private void getServerUpdatedMp3List()
    {
    	//下载XML文件
		String xml = downloadXML();
		//解析下载的记录MP3信息的XML文件到mp3lists列表中，列表中存储的是解析出的Mp3Info对象
	    mp3lists = parse(xml);
    }
    /**
     * 更新显示MP3列表
     */
    private void updateMp3ListShow(){
    	//获得listView
    	ListView listView = (ListView)findViewById(R.id.listview01);
    	if(null == mp3lists){
    		return;
    	}
    	//创建一个SimpleAdapter对象
    	SimpleAdapter simpleAdapter = createSimpleAdapter(mp3lists);
    	//把simpleAdapter对象添加到listView中
    	listView.setAdapter(simpleAdapter);
    	//单击listView的一个选项
    	clickListItem(listView);
    	
    	mp3lists = null;
    }
    
    /**
     * 单击列表的一个选项触发的事件,處理下載MP3和lrc事件
     * @param listView
     */
    private void clickListItem(ListView listView)
    {
    	listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//根据用户点击列表中的位置，取出由position位置确定的mp3Info对象
				Mp3Info mp3Info = mp3lists.get(position);
				//生成一个intent对象
				Intent intent = new Intent();
				//将mp3INFo对象存放在intent中
				intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);
				intent.setClass(RemoteServerMp3ListActivity.this, DownloadService.class);
				RemoteServerMp3ListActivity.this.startService(intent);//启动Service
			}
		});
    }
    
    /**
     * 创建一个SimplerAdapter对象
     * @param mp3lists
     * @return simpleAdapter
     */
    private SimpleAdapter createSimpleAdapter(List<Mp3Info> mp3lists)
    {
    	// 生成一个List对象，并按照SimpleAdapter的标准，将mp3Infos当中的数据添加到List当中去
    	List<HashMap<String, String>> list = insertToList(mp3lists);
    	
		// 创建一个SimpleAdapter对象
		int resource = R.layout.mp3info_item;
		String[] from = new String[] { Constant.MP3.MP3_NAME, Constant.MP3.MP3_SIZE};
		int[] to = new int[] { R.id.mp3item_mp3_name, R.id.mp3item_mp3_size };
		SimpleAdapter simpleAdapter = new SimpleAdapter(RemoteServerMp3ListActivity.this, list, resource, from, to);
		return simpleAdapter;
    }
    
    /**
     * 生成一个List对象，并按照SimpleAdapter的标准，将mp3Infos当中的数据添加到List当中去
     * @param mp3lists
     * @return
     */
    private List<HashMap<String,String>> insertToList(List<Mp3Info> mp3lists)
    {
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (Iterator<Mp3Info> iterator = mp3lists.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constant.MP3.MP3_NAME, mp3Info.getMp3Name());
			map.put(Constant.MP3.MP3_SIZE, mp3Info.getMp3Size());
			list.add(map);
		}
		return list;
    }
   
    /**
     * 下载XML歌曲文件
     * @return
     */
    private String downloadXML()
    {
    	HttpDownloader httpDownloader = new HttpDownloader();
    	return httpDownloader.downloadTxtFile(Constant.URL.RESOURCE_URL_STR);
    }
    
    /**
     * 解析MP3信息文件，它是一个xml文件
     * @param xmlStr 解析的xml文件的內容，是一個字符串
     * @return 返回解析出的对象列表
     */
    private List<Mp3Info> parse(String xmlStr)
    {
    	//获得一个SAXParserFactory对象
    	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> mp3List = new ArrayList<Mp3Info>();
		try 
		{
			//由SAXParserFactory对象获得XMLReader对象
			XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(mp3List);
			xmlReader.setContentHandler(mp3ListContentHandler);
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
		} 
		catch (SAXException e)
		{
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			saxParserFactory = null;
		}
    	return mp3List;
    }
}


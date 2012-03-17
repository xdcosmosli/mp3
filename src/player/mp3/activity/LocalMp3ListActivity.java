package player.mp3.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import file.utils.FileUtils;
import player.constant.Constant;
import player.model.Mp3Info;
import player.mp3.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LocalMp3ListActivity extends ListActivity
{
	private List<Mp3Info> localMp3List = null;//存储本地MP3文件列表
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		
	}
	
	protected void onResume() {
		super.onResume();
		updateLocalMp3List();
	}
	/**
	 * 当listView的数据有变化时才调用此方法
	 */
	public void onContentChanged() {
		super.onContentChanged();
		//更新本地已下载的MP3文件列表
		updateLocalMp3List();
	}
	
	/**
	 * 更新本地已下载的MP3文件列表
	 */
	private void updateLocalMp3List(){
		localMp3List = new ArrayList<Mp3Info>();//创建MP3Info列表对象
		FileUtils fileUtils = new FileUtils();
		//从SD卡上获得后缀为MP3的文件，存储在一个列表中
		List<File> fileList = fileUtils.getFilesList(Constant.MP3.MP3_DIR_PATH, ".mp3");
		//如果获得本地的MP3列表为null则返回
		if(null == fileList){
			return;
		}
		//把数据存储在localMp3List列表中
		for(Iterator<File> iterator = fileList.iterator() ; iterator.hasNext(); ){
			File file = (File)iterator.next();
			Mp3Info mp3Info = new Mp3Info();
			mp3Info.setMp3Name(file.getName());
			mp3Info.setMp3Size(Long.toString(file.length()));
			localMp3List.add(mp3Info);
		}
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		for (Iterator<Mp3Info> iterator = localMp3List.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put(Constant.MP3.MP3_NAME, mp3Info.getMp3Name());
			hashMap.put(Constant.MP3.MP3_SIZE, mp3Info.getMp3Size());
			list.add(hashMap);
		}
		SimpleAdapter simplerAdapter = new SimpleAdapter(this, list , R.layout.mp3info_item, new String[]{Constant.MP3.MP3_NAME,Constant.MP3.MP3_SIZE}, new int[]{R.id.mp3item_mp3_name,R.id.mp3item_mp3_size});
		setListAdapter(simplerAdapter);
	}
	
	/**
	 * 当用户点击一条选项是调用此函数
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Mp3Info mp3Info = localMp3List.get(position);
		//获得SD卡上的和用户所点mp3文件同名的lrc文件
		String lrcString = getLrcFileFromSDCard(mp3Info.getMp3Name());
		//跳转到PlayerActivity
		JumpToPlayerActivity(l,v,position,id, lrcString );
	}
	/**
	 * 从SD卡上获得与Mp3文件同名的lrc文件
	 * @param fileName MP3文件名 例如："a.mp3"
	 * @return String 与MP3同名的lrc文件的内容字符串
	 */
	private String getLrcFileFromSDCard(String mp3fileName){
		int index = mp3fileName.indexOf(".");
		mp3fileName = mp3fileName.substring(0, index);
		//获得SD卡上的所有lrc文件列表
		FileUtils fileUtils = new FileUtils();
		List<File> fileList = fileUtils.getFilesList(Constant.MP3.MP3_DIR_PATH, ".lrc");
		//选择与MP3文件同名的lrc文件内容
		String lrcFileContent = new String();//存储lrc文件的内容
		for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			String lrcFileName = file.getName();
			int i = lrcFileName.indexOf(".");
			lrcFileName = lrcFileName.substring(0, i);
			if(lrcFileName.equals(mp3fileName)){
				//获得SD卡上的指定的lrc文件内容
				lrcFileContent = fileUtils.readTextFile(Constant.MP3.MP3_DIR_PATH, lrcFileName+".lrc");
			}
		}
		return lrcFileContent;
	}
	/**
	 * 跳转到PlayerActivity
	 * @param l
	 * @param v
	 * @param position
	 * @param id
	 * @param lrcStr lrc文件的内容字符串
	 */
	private void JumpToPlayerActivity(ListView l, View v, int position, long id, String lrcStr){
		//播放所选的歌曲,跳转到PlayerActivity
		if(null != localMp3List)
		{
			Mp3Info mp3Info = localMp3List.get(position);//获取用户所选的Mp3对象
			Intent intent = new Intent();
			intent.setClass(this, PlayerActivity.class);
			intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);//添加用户点击的MP3文件信息
			intent.putExtra("lrcStr", lrcStr);//添加于MP3同名的lrc字符串内容
			startActivity(intent);
		}
	}
}

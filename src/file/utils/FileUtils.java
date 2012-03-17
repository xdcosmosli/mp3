package file.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Environment;

/**
 * 文件类工具类
 * 该类可以在SD卡上创建目录，创建文件，判断文件是否存在，从InputStream流
 * 里写入文件数据，读取SD卡上某个目录的文件信息列表
 * @author Administrator
 *
 */
public class FileUtils {
	private String SDCardRoot;// SD卡设备的目录

	public FileUtils() {
		File externalStorageDirectory = Environment.getExternalStorageDirectory();
		SDCardRoot = externalStorageDirectory.getAbsolutePath()+ File.separator;
	}

	/**
	 * 在SD卡里创建一个目录，如果目录已存在则返回null
	 * @param dir
	 * @return
	 */
	public File createPathInSDCard(String dir) {
		//指定一个文件的相对目录，比如mp3返回从SDCard的根目录到这个目录的路径
		String path = absoluteDir(dir);
		File fold = new File(path);//生成文件夹对象
		if(!fold.exists()){
			fold.mkdir();//创建一个文件夹
		}
		return fold;
	}

	/**
	 * 在SD卡里创建一个新文件，文件的目录名为dir，文件名为fileName 如果文件已存在则返回null
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public File createFileInSDCard(String dir, String fileName) {
		String path = absoluteDir(dir)+fileName;
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile(); 
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 判断SD卡上文件是否存在
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String dir, String fileName) {
		String path = absoluteDir(dir)+fileName;
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 将数据从InputStream里写进SD卡里
	 * @param dir 目录名
	 * @param fileName 文件名
	 * @param input 输入流
	 * @return 写入失败返回null，否则返回写入的文件
	 */
	public File writeToSDCardFromInputStream(String dir, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		createPathInSDCard(dir);//创建一个目录
		file = createFileInSDCard(dir, fileName);//问题出现在这里，创建一个文件
		byte[] buffer = new byte[1024 * 10];
		try {
			output = new BufferedOutputStream(new FileOutputStream(file));
			int tempSize = 0;
			while ((tempSize = input.read(buffer)) != -1) {
				output.write(buffer, 0, tempSize);
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				output.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}

	/**
	 * 读取SD卡上的文本文件的内容
	 * @param dir 文本文件在SD卡的相对目录
	 * @param fileName 文本文件的文件名
	 * @return String 文本文件的内容字符串
	 */
	public String readTextFile(String dir, String fileName){
		String path = absoluteDir(dir) + fileName;//获取文本文件在SD卡上的路径
		File file = new File(path);
		char[] buf = new char[1024 * 5];//存储读入的lrc文件
		String lrc = null;//存储文本文件内容
		FileReader fileReader = null;
		try 
		{
			fileReader = new FileReader(file);
			while(-1 != (fileReader.read(buf))){
				
			}
			lrc = new String(buf);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lrc;
	}
	/**
	 * 获得SD卡上某个目录下的文件列表，文件类型由用户指定
	 * @param dir SD卡上的某个目录，例如："mp3"
	 * @param fileType 文件后缀类型，例如：".mp3"或".lrc"
	 * @return
	 */
	public List<File> getFilesList(String dir, String fileType){
		List<File> fileList = new ArrayList<File>();//创建一个存储用户想要的文件类型的列表
		File sdCardDir = new File(SDCardRoot + dir);//创建目录对象
		if(!fileType.startsWith(".")){
			fileType = "."+fileType;
		}
		FileNameFilter fileNameFilter = new FileNameFilter(fileType);
		File[] files = sdCardDir.listFiles(fileNameFilter);
		//如果没找到本地MP3文件则返回null
		if(null == files){
			return null;
		}
		for(int i = 0 ; i < files.length ; i++){
			fileList.add(files[i]);
		}
		return fileList;
	}
	
	/**
	 * 文件名过滤器内部类，该类可以过滤用户指定的文件类型
	 * @author Administrator
	 */
	private class FileNameFilter implements FilenameFilter 
	{
		private String type = null;// 用户想要的文件后缀类型

		public FileNameFilter(String type) 
		{
			this.type = type;
		}

		/**
		 * 获得文件的后缀名
		 * @param filename 文件名
		 * @return String 后缀名
		 */
		private String getFileSuffix(String filename)
		{
			int index = filename.indexOf(".");
			return filename.substring(index, filename.length());
		}
		
		public boolean accept(File dir, String filename)
		{
			if(type.equalsIgnoreCase(getFileSuffix(filename)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	/**
	 * 从SDCard上获得一个文件的URI字符串
	 * @param dir 文件在SD卡的目录路径
	 * @param fileName 文件名
	 * @return String 该文件的Uri字符串对象
	 */
	public String getFileUriStringFromSDCard(String dir, String fileName){
		String path = null;
		path = absoluteDir(dir)+fileName;//获得文件的绝对路径
		return "file://"+path;
	}
	
	/**
	 * 从SDCard上获得指定文件的Uri对象
	 * @param dir 指定文件的目录名
	 * @param fileName 文件名
	 * @return Uri对象 
	 */
	public Uri getFileUriFromSDCard(String dir, String fileName){
		String path = null;
		path = absoluteDir(dir)+fileName;//获得文件的绝对路径
		return Uri.parse("file:/"+path);
	}
	
	/**
	 * 指定一个文件的相对目录，比如mp3返回从SDCard的根目录到这个目录的路径
	 * @param dir "mp3/" 或者"mp3"
	 * @return "sdcard/mp3/"
	 */
	private String absoluteDir(String dir){
		String path = null;
		if(".".equals(dir)){
			path = SDCardRoot;
		}
		else
		{
			if(!dir.endsWith(File.separator)){
				dir = dir + File.separator;
			}
			path = SDCardRoot + dir;
		}
		return path;
	}

	public String getSDCardRoot() {
		return SDCardRoot;
	}

	public void setSDCardRoot(String sDCardRoot) {
		SDCardRoot = sDCardRoot;
	}
}

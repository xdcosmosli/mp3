package player.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.UnknownServiceException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import player.constant.Constant;

import file.utils.FileUtils;

/**
 * 该类实现从指定的URL下载文件，可以下载文本文件也可以下载二进制文件
 * 可以判断是否连接到服务器
 * @author Administrator
 */
public class HttpDownloader 
{
	private URL url = null;//存储文件的URL对象
	private String urlStr = null;//存储文件的url字符串
	
	public static final int FILE_DOWNLOAD_ERROR = -1;
	public static final int FILE_DOWNLOAD_SUCCESS = 0;
	public static final int FILE_DOWNLOAD_EXIST = 1;
	
	public HttpDownloader()
	{
		
	}
	
	/**
	 * 根据 String 表示形式创建 URL 对象。
	 * @param urlstr 将作为 URL 解析的 String
	 */
	public HttpDownloader(String urlstr)
	{
		this.urlStr = urlstr;
		try 
		{
			url = new URL(urlStr);
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public HttpDownloader(URL url)
	{
		this.url = url;
	}
	
	/**
	 * @param protocol 要使用的协议名称。
	 * @param host 主机名称。
	 * @param port 主机端口号。
	 * @param file 主机上的文件
	 * @param handler URL 的流处理程序
	 */
	public HttpDownloader(String protocol,
	           		      String host,
	                      int port,
	                      String file,
	                      URLStreamHandler handler)
	{
		try
		{
			url = new URL(protocol, host, port, file, handler);
			urlStr = url.toString();
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public HttpDownloader(String protocol,
					      String host,
					      int port,
					      String file)
	{
		this(protocol, host, port, file, null);
	}

	/**
	 * 判断要下载的文件的URL地址是否存在该文件
	 * @return true 存在文件
	 *         false 不存在文件
	 */
	private boolean isFileUrlAvailable(String urlStr){
		this.urlStr = urlStr;
		if(urlStr.endsWith("null")){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * 根据文件的URL，下载文本文件，文件返回值是所下载文本文件的内容
	 * @param url 文件在远程服务器的URL 例如：http://219.245.92.63:8090/mp3/a.mp3
	 * @return  String 下载文本文件的内容,若文件不存在则返回null
	 */
	public String downloadTxtFile(String urlstr)
	{
		urlStr = urlstr;
		//若文件不存在则返回null
		if(!isFileUrlAvailable(urlstr)){
			return null;
		}
		try
		{
			url = new URL(urlStr);
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
		}
		return download_txt();
	}
	/**
	 * 下载文本文件，文件返回值是所下载文本文件的内容
	 * @param protocol String 协议
	 * @param host String 主机名或IP地址
	 * @param port 端口号
	 * @param dir 目录
	 * @param fileName 文件名
	 * @return String 若下载成功返回文本文件内容，否则返回null
	 */
	public String downloadTxtFile(String protocol, String host, String port, String dir, String fileName){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol).append(":").append(File.separator).append(File.separator).append(host).append(":").append(port).append(File.separator).append(dir).append(File.separator).append(fileName);
		
		return downloadTxtFile(sb.toString());
	}
	
	/**
	 * 下载文本文件，文件返回值是所下载文本文件的内容
	 * @return String 若下载成功返回文本文件内容，否则返回null
	 */
	public String downloadTxtFile()
	{
		if(null == url || null == urlStr)
		{
			return null;
		}
		else
		{
			return download_txt();
		}
	}
	
	/**
	 * 下载一个文本文件的具体实现
	 * 1 创建一个URL对象
	 * 2 通过URL对象，创建一个HttpURLConnection对象
	 * 3 得到InputStream
	 * 4 从InputStream当中读取数据 
	 * @return String 返回已经下载的文本文件的内容
	 */
	private String download_txt()
	{
		String line = null;
		StringBuffer strbuf = new StringBuffer();
		BufferedReader bufReader = null;
		InputStreamReader reader=null;
		InputStream inputStream=null;
		try 
		{
			inputStream = getInputStreamFromURL(urlStr);
			reader = new InputStreamReader(inputStream, "GB2312");
			bufReader = new BufferedReader(reader);
			while(true)
			{
				line = bufReader.readLine();
				if(null != line)
				{
					strbuf.append(line);
				}
				else
				{
					break;
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();//如果服务器没开，就不能启动应用程序
				reader.close();
				bufReader.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return strbuf.toString();
	}
	
	/**
	 * 下载二进制文件，并存储在指定的本地SD卡目录下，并以指定的文件名存储
	 * @param urlstr 文件在远程服务器的URL：例如：http://219.245.92.63:8090/mp3/a.mp3
	 * @param dir 要存储在本地的SD卡的相对目录名
	 * @param fileName 要存储在本地的SD卡的文件名
	 * @return -1：文件下载出错
	 *          0：文件下载成功
	 *          1：文件已经存在
	 */
	public int downloadBinaryFileToSDCard(String urlstr, String dir, String fileName)
	{
		if(!isFileUrlAvailable(urlstr)){
			return FILE_DOWNLOAD_ERROR;
		}
		urlStr = urlstr;
		InputStream input = null;
		FileUtils fileUtils = new FileUtils();
		if(fileUtils.isFileExist(dir, fileName)){
			return FILE_DOWNLOAD_EXIST;
		}
		else{
			try
			{
				input = getInputStreamFromURL(urlStr);
				File resultFile = fileUtils.writeToSDCardFromInputStream(dir, fileName, input);
				if(null == resultFile){
					return FILE_DOWNLOAD_ERROR;
				}
				else{
					return FILE_DOWNLOAD_SUCCESS;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return FILE_DOWNLOAD_SUCCESS;
	}
	
	/**
	 * 从远程服务器下载二进制文件
	 * @param urlstr 文件在远程服务器的URL：例如：http://219.245.92.63:8090/mp3/a.mp3
	 * @return InputStream
	 */
	public InputStream downloadBinaryFile(String urlstr){
		urlStr = urlstr;
		InputStream inputStream = getInputStreamFromURL(urlstr);
		return inputStream;
	}
	/**
	 * 从远程服务器下载二进制文件
	 * @param protocol String 协议
	 * @param host String 主机名或IP地址
	 * @param port 端口号
	 * @param dir 目录
	 * @param fileName 文件名
	 * @return InputStream 若下载成功返回InputStream对象，否则返回null
	 */
	public InputStream downloadBinaryFile(String protocol, String host, String port, String dir, String fileName){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol).append(":").append(File.separator).append(File.separator).append(host).append(":").append(port).append(File.separator).append(dir).append(File.separator).append(fileName); 
		return downloadBinaryFile(sb.toString());
	}
	
	/**
	 * 从url获得输入流对象
	 * @param urlStr 远程服务器里存储的文件的URL
	 * @return InputStream
	 */
	private InputStream getInputStreamFromURL(String urlstr) {
		InputStream input = null;
		this.urlStr = urlstr;
		try {
			url = new URL(urlStr);//创建一个URL对象
			//URL对象打开连接
			HttpURLConnection httpUrlConn = (HttpURLConnection)url.openConnection();
			//获得InputStream对象
			input = httpUrlConn.getInputStream();
		}
		catch(SocketException e){
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
        catch(UnknownServiceException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
		}
		return input;
	}
	
	/**
     * 判断是否已经连接到服务器
     * @param uristr String  服务器的URI 例如：tocant服务器："http://219.245.92.63:8090/"
     * @return true 已经连接上服务器
     *         false 未连接到服务器
     */
    public boolean isServerConnected(String uristr){
    	long timeOut = Constant.SERVER_CONNECTION_PARAMS.TIME_OUT;
    	URI uri = null;//服务器的URI
		try{
			uri = new URI(uristr);
		} 
		catch (URISyntaxException e){
			e.printStackTrace();
		}
		if(null==uri){
			return false;
		}
		else{
			final HttpGet httpGet = new HttpGet(uri);
			HttpParams params = new BasicHttpParams();
			//设置连接超时值为timeOut
			HttpConnectionParams.setConnectionTimeout(params, (int)timeOut);
			//设置socket连接超时值为timeOut
			HttpConnectionParams.setSoTimeout(params, (int)timeOut);
			//设置socket缓冲大小
			int socketBufferSize = Constant.SERVER_CONNECTION_PARAMS.SOCKET_BUFFER_SIZE;
			HttpConnectionParams.setSocketBufferSize(params, socketBufferSize);
			final HttpClient httpClient = new DefaultHttpClient(params);
			//获取连接前的时间
			long beforConnectionTime = System.currentTimeMillis();
			long currentTime = 0L;//保存当前时间
			while(true){
				currentTime = System.currentTimeMillis();
				if((currentTime - beforConnectionTime) > (timeOut * 2/3)){
					//如果连接时间大于超时值的2/3则表示已经超时连接，则返回false
					return false;
				}
				try{
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(200 == httpResponse.getStatusLine().getStatusCode()){
						//如果http连接返回200在表示连接成功，返回true
						return true;
					}
				}
				catch (ClientProtocolException e){
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
    }
}

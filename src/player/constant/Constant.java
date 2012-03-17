package player.constant;

/**
 * 常量接口，这个接口中主要是一些应用程序中的常量
 * @author Administrator
 */
public interface Constant {
	/**
	 * 消息常量内部类
	 * @author Administrator
	 *
	 */
	public final class Message{
		public static final int MP3_PLAY_START = 1;//开始播放MP3命令
		public static final int MP3_PLAY_PAUSE = 2;//暂停播放MP3命令
		public static final int MP3_PLAY_STOP = 3;//停止播放MP3命令
		public static final String PLAY_MP3_MESSAGE_KEY = "message";//播放MP3消息键的字符串
	}
	
	/**
	 * MP3信息类
	 * @author Administrator
	 */
	public final class MP3{
		public static final String MP3_NAME = "mp3_name";//mp3名称常量字符串
		public static final String MP3_SIZE = "mp3_size";//MP3大小常量字符串
		public static final String LRC_NAME = "lrc_name";//lrc名称常量字符串
		public static final String LRC_SIZE = "lrc_size";//lrc大小常量字符串
		public static final String MP3_INFO_KEY = "mp3Info";//Mp3Info对象的键的字符串
		public static final String MP3_DIR_PATH = "mp3";//存储Mp3的相对根目录的目录路径
	}
	
	/**
	 * 记录所有资源URL的信息字符串常量类
	 * @author Administrator
	 *
	 */
	public final class URL{
		//资源文件的URL字符串常量
		public static final String RESOURCE_URL_STR = "http://219.245.92.63:8090/mp3/resources.xml";
		/**
		 * 服务器根URL
		 */
		public static final String SERVER_ROOT_URL_STR = "http://219.245.92.63:8090/";
		public static final String MP3_URL_STR = "http://219.245.92.63:8090/mp3/";
		public static final String PROTOCOL = "http";
		public static final String HOST = "219.245.92.63";
		public static final String PORT = "8090";
	}
	
	public final class BROADCAST_ACTION
	{
		public static final String LRC_UPDATE_ACTION = "player_lrc_update_action";
	}
	
	/**
	 * 连接服务器的参数常量
	 * @author Administrator
	 */
	public final class SERVER_CONNECTION_PARAMS
	{
		/**
		 * 超时连接3000毫秒
		 */
		public static final long TIME_OUT = 3*1000L;
		public static final int SOCKET_BUFFER_SIZE = 8192;
	}
}

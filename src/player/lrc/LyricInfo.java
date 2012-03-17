package player.lrc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 歌词解析后的信息类，主要功能是存储解析歌词后的歌词信息
 * 包括，歌词的时间点，每个时间点对应的歌词数据，歌曲的总时间
 * @author Administrator
 */
public class LyricInfo implements Serializable
{
	private static final long serialVersionUID = -860365065197720702L;

	//存放时间点的队列
	private Queue<Long> timeStampQueue = new LinkedList<Long>();
	//存放时间点对应的歌词
	private Queue<String> lyricQueue = new LinkedList<String>();
	

	public LyricInfo(Queue<Long> timeStampQueue, Queue<String> lyricQueue)
	{
		this.timeStampQueue = timeStampQueue;
		this.lyricQueue = lyricQueue;
	}
	
	public LyricInfo()
	{
	}

	public Queue<Long> getTimeStampQueue()
	{
		return timeStampQueue;
	}

	public void setTimeStampQueue(Queue<Long> timeStampQueue)
	{
		this.timeStampQueue = timeStampQueue;
	}

	public Queue<String> getLyricQueue()
	{
		return lyricQueue;
	}

	public void setLyricQueue(Queue<String> lyricQueue)
	{
		this.lyricQueue = lyricQueue;
	}
}

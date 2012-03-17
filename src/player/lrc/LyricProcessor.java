package player.lrc;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 歌词处理类，主要功能是解析lrc歌词文件，解析歌词文件中的时间点信息以及对应
 * 的歌词信息。
 * @author Administrator
 */
public class LyricProcessor 
{
	//保存解析后的歌词信息
	private LyricInfo lyricResult = new LyricInfo();
	
	public LyricProcessor()
	{
	}

	public LyricInfo getLyricResult()
	{
		return lyricResult;
	}
	
	/**
	 * 解析lrc歌词文件
	 * @param lrcStr 歌词文件的文本内容
	 */
	public LyricInfo parseLrc(String lrcStr)
	{
		//保存时间点队列
		Queue<Long> timeQueue = new LinkedList<Long>();
		//保存时间点对应的歌词队列
		Queue<String> lrcQueue = new LinkedList<String>();
		
		//存储歌词的时间点字符串和歌词内容字符串到歌词数组中，单数是内容，双数是时间点
		String[] lrcArray = new String[]{};
		lrcArray = lrcStr.split("[\\[\\]]");
		
		for(int i = 0 ; i < lrcArray.length ; i++)
		{
			if(0 != i%2)//如果是奇数存储的时时间点字符串
			{//提取时间点数据
				Long time = timeToLong(lrcArray[i].trim());
				timeQueue.add(time);
			}
			else if(0 == i%2)//如果是偶数存储的时歌词内容字符串
			{//提起歌词内容数据
				if(null != lrcArray[i] && !"".equals(lrcArray[i]))
				{//如果当前数组中内容不是空
					String lrcLine = lrcArray[i];
					lrcQueue.add(lrcLine.trim());
				}
			}
		}
		lyricResult.setTimeStampQueue(timeQueue);
		lyricResult.setLyricQueue(lrcQueue);
		
		return lyricResult;
	}
	
	/**
	 * 把歌词时间点字符串转换问Long型毫秒。例如：00:00.0转换为0
	 * @param timeStr 歌词字符串 例如：21:02.2
	 * @return Long 毫秒
	 */
	private Long timeToLong(String timeStr)
	{
		String s[] = timeStr.split(":");
		Long min = Long.parseLong(s[0]);
		String s2[] = s[1].split("\\.");
		Long sec = Long.parseLong(s2[0]);
		Long mill = Long.parseLong(s2[1]);
		Long time = min * 60 * 1000 + sec * 1000 + mill * 10;
		return time;
	}
}

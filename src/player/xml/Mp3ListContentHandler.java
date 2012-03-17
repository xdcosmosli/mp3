package player.xml;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import player.model.Mp3Info;
/**
 * 此类主要继承DefaultHandler类，实现了所有的回调函数，进行解析xml文件使用
 * @author Administrator
 */
public class Mp3ListContentHandler extends DefaultHandler
{
	private Mp3Info mp3Info = null;
	private List<Mp3Info> mp3List = null;
	private String tagName = null;
	

	public Mp3ListContentHandler(List<Mp3Info> mp3List)
	{
		this(null, mp3List, null);
	}

	public Mp3ListContentHandler(Mp3Info mp3Info, List<Mp3Info> mp3List, String tagName) 
	{
		super();
		this.mp3Info = mp3Info;
		this.mp3List = mp3List;
		this.tagName = tagName;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		super.characters(ch, start, length);
		
		String temp = new String(ch, start, length);
		if(tagName.equals("id"))
		{
			mp3Info.setId(temp);
		}
		else if(tagName.equals("mp3.name"))
		{
			mp3Info.setMp3Name(temp);
		}
		else if(tagName.equals("mp3.size"))
		{
			mp3Info.setMp3Size(temp);
		}
		else if(tagName.equals("lrc.name"))
		{
			mp3Info.setLrcName(temp);
		}
		else if(tagName.equals("lrc.size"))
		{
			mp3Info.setLrcSize(temp);
		}
	}

	public void endDocument() throws SAXException 
	{
		super.endDocument();
	}

	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		super.endElement(uri, localName, qName);
		
		tagName = qName;
		if(tagName.equals("resource"))
		{
			mp3List.add(mp3Info);
		}
		tagName = "";
	}

	public void startDocument() throws SAXException
	{
		super.startDocument();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		
		tagName = localName;
		if(tagName.equals("resource"))
		{
			mp3Info = new Mp3Info();
		}
	}

	public Mp3Info getMp3Info() {
		return mp3Info;
	}

	public void setMp3Info(Mp3Info mp3Info) {
		this.mp3Info = mp3Info;
	}

	public List<Mp3Info> getMp3List() {
		return mp3List;
	}

	public void setMp3List(List<Mp3Info> mp3List) {
		this.mp3List = mp3List;
	}
}

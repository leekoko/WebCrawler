package navel.spider.junit;

import java.util.List;

import org.junit.Test;

import novel.spider.NovelSiteEnum;
import novel.spider.entitys.Chapter;
import novel.spider.impl.DefaultChapterSpider;
import novel.spider.interfaces.IChapterSpider;
import novel.spider.util.NovelSpiderUtill;

public class Testcase {
	@Test
	public void test1() throws Exception{
		IChapterSpider spider=new DefaultChapterSpider();
//		List<Chapter> chapters= spider.getsChapter("http://www.biquge.tw/0_5/");
		List<Chapter> chapters= spider.getsChapter("http://www.biquge.tw/0_5/");
		for(Chapter chapter:chapters){
			System.out.println(chapter);
		}
	}
	@Test
	public void testGetSiteContext(){
		System.out.println(NovelSpiderUtill.getContext(
				NovelSiteEnum.getEnumByUrl("http://www.23wx.com/html/42/42377/")));
		
		
	}
}

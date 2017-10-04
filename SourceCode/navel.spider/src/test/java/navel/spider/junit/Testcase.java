package navel.spider.junit;

import java.util.List;

import org.junit.Test;

import navel.spider.impl.DefaultChapterSpider;
import navel.spider.interfaces.IChapterSpider;
import novel.spider.entitys.Chapter;

public class Testcase {
	@Test
	public void test1() throws Exception{
		IChapterSpider spider=new DefaultChapterSpider();
		List<Chapter> chapters= spider.getsChapter("http://www.biquge.tw/0_5/");
		for(Chapter chapter:chapters){
			System.out.println(chapter);
		}
	}
}

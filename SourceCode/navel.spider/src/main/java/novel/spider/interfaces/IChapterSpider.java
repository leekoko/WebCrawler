package novel.spider.interfaces;

import java.util.List;

import novel.spider.entitys.Chapter;

public interface IChapterSpider {
	/**
	 * 给url地址，返回所有章节列表
	 * @param url
	 * @return
	 */
	public List<Chapter> getsChapter(String url);
}

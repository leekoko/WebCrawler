package novel.spider.interfaces;

import java.util.List;

import novel.spider.entitys.Chapter;

public interface IChapterSpider {
	/**
	 * ��url��ַ�����������½��б�
	 * @param url
	 * @return
	 */
	public List<Chapter> getsChapter(String url);
}

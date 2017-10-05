package novel.spider.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import novel.spider.NovelSiteEnum;
import novel.spider.entitys.Chapter;
import novel.spider.interfaces.IChapterSpider;
import novel.spider.util.NovelSpiderUtill;

public class AbstractChapterSpider implements IChapterSpider {
	protected String crawl(String url) throws Exception{
		try(CloseableHttpClient httpClient=HttpClientBuilder.create().build();    //���ô��������
			CloseableHttpResponse httpResponse= httpClient.execute(new HttpGet(url))){  //�Ž�try������Զ��ͷ���Դ
			String charset=NovelSpiderUtill.getContext(NovelSiteEnum.getEnumByUrl(url)).get("charset");
			String result=EntityUtils.toString(httpResponse.getEntity(),charset);   //����ץȡ�Ľ��
			return result;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<Chapter> getsChapter(String url) {
		try {
			String result=crawl(url);
			Document doc=Jsoup.parse(result);    //����HTML�ַ���
			doc.setBaseUri(url);  //���û���·��,����������·��
			Elements as=doc.select(NovelSpiderUtill.getContext(NovelSiteEnum.getEnumByUrl(url)).get("charset-list-selector"));   //Ѱ�Ҹ�ѡ������Ԫ��
			List<Chapter> chapters=new ArrayList<>();
			for(Element a:as){   //�����õ��ı�ǩ
				Chapter chapter=new Chapter();
				chapter.setTitle(a.text());
				chapter.setUrl(a.absUrl("href"));    
				chapters.add(chapter);    //��Ԫ�طŽ������ٴ��������
			}
			return chapters;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

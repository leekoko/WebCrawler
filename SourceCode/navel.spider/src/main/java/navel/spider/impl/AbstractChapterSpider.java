package navel.spider.impl;

import java.io.Closeable;
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

import navel.spider.interfaces.IChapterSpider;
import novel.spider.entitys.Chapter;

public class AbstractChapterSpider implements IChapterSpider {
	protected String crawl(String url) throws Exception{
		try(CloseableHttpClient httpClient=HttpClientBuilder.create().build();    //���ô��������
			CloseableHttpResponse httpResponse= httpClient.execute(new HttpGet(url))){  //�Ž�try������Զ��ͷ���Դ
			String result=EntityUtils.toString(httpResponse.getEntity());   //����ץȡ�Ľ��
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
			Elements as=doc.select("#list dd a");   //Ѱ�Ҹ�ѡ������Ԫ��
			List<Chapter> chapters=new ArrayList<>();
			for(Element a:as){   //�����õ��ı�ǩ
				Chapter chapter=new Chapter();
				chapter.setTitle(a.text());
				chapter.setUrl("http://www.bxwx8.org"+a.attr("href"));
				chapters.add(chapter);    //��Ԫ�طŽ������ٴ��������
			}
			return chapters;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

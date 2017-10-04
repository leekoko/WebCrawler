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
		try(CloseableHttpClient httpClient=HttpClientBuilder.create().build();    //设置代理服务器
			CloseableHttpResponse httpResponse= httpClient.execute(new HttpGet(url))){  //放进try里可以自动释放资源
			String result=EntityUtils.toString(httpResponse.getEntity());   //返回抓取的结果
			return result;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<Chapter> getsChapter(String url) {
		try {
			String result=crawl(url);
			Document doc=Jsoup.parse(result);    //解析HTML字符串
			Elements as=doc.select("#list dd a");   //寻找该选择器的元素
			List<Chapter> chapters=new ArrayList<>();
			for(Element a:as){   //遍历拿到的标签
				Chapter chapter=new Chapter();
				chapter.setTitle(a.text());
				chapter.setUrl("http://www.bxwx8.org"+a.attr("href"));
				chapters.add(chapter);    //将元素放进对象再存进数组中
			}
			return chapters;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

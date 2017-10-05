package novel.spider.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import novel.spider.NovelSiteEnum;
/**
 * ��ȡxml�ļ�
 * @author liyb
 *
 */
public final class NovelSpiderUtill {
	public static final Map<NovelSiteEnum,Map<String,String>> CONTEXT_MAP=new HashMap<>();
	static{
		init();
	}
	public NovelSpiderUtill(){}
	
	private static void init(){
		SAXReader reader=new SAXReader();   //��ȡSAXReader
		try {
			Document doc=reader.read(new File("conf/Spider-Rule.xml"));    //��xml�ļ���ȡ����
			Element root=doc.getRootElement();     //��ȡ�ӽڵ�
			List<Element> sites=root.elements("site");   //���ӽڵ�����ȡ��siteԪ��
			for(Element site:sites){   //����siteԪ��
//				String title=site.getName();
//				String text=site.getTextTrim();
				List<Element>  subs=site.elements();  //��ȡ�ýڵ��µ�����Ԫ��
				Map<String, String> subMap=new HashMap<>();
				for(Element sub:subs){
					String name=sub.getName();
					String text=sub.getTextTrim();
					subMap.put(name, text);    //����ǩ�������ݳɶԴ��map����
				}
				CONTEXT_MAP.put(NovelSiteEnum.getEnumByUrl(subMap
						.get("url")), subMap);   //��ǰ׺��ö�ٶ���ͻ�ȡ��Ϣ���map����
			}
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * �õ� ��Ӧ��վ�Ľ�������
	 */
	public static Map<String,String> getContext(NovelSiteEnum novelSiteEnum){
		return CONTEXT_MAP.get(novelSiteEnum);  //����ǰ׺��ö�ٶ����ȡ��Ϣ
	}
	
}

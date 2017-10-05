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
 * 读取xml文件
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
		SAXReader reader=new SAXReader();   //获取SAXReader
		try {
			Document doc=reader.read(new File("conf/Spider-Rule.xml"));    //将xml文件读取进来
			Element root=doc.getRootElement();     //获取子节点
			List<Element> sites=root.elements("site");   //从子节点中提取出site元素
			for(Element site:sites){   //遍历site元素
//				String title=site.getName();
//				String text=site.getTextTrim();
				List<Element>  subs=site.elements();  //获取该节点下的所有元素
				Map<String, String> subMap=new HashMap<>();
				for(Element sub:subs){
					String name=sub.getName();
					String text=sub.getTextTrim();
					subMap.put(name, text);    //将标签名与内容成对存进map里面
				}
				CONTEXT_MAP.put(NovelSiteEnum.getEnumByUrl(subMap
						.get("url")), subMap);   //将前缀名枚举对象和获取信息存进map里面
			}
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 拿到 对应网站的解析规则
	 */
	public static Map<String,String> getContext(NovelSiteEnum novelSiteEnum){
		return CONTEXT_MAP.get(novelSiteEnum);  //根据前缀名枚举对象获取信息
	}
	
}

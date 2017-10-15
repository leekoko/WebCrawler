# Java小说爬虫

### 1.依赖的jar包  

- jq操作网页元素的jar包：jsoup   
- http-client

可以自动下载jar包  

依赖包查找的网站：http://mvnrepository.com/

```
  <dependencies>
	<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
	<dependency>
	    <groupId>org.jsoup</groupId>
	    <artifactId>jsoup</artifactId>
	    <version>1.7.3</version>
	</dependency>
	
	<!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
	<dependency>
	    <groupId>org.apache.httpcomponents</groupId>
	    <artifactId>httpclient</artifactId>
	    <version>4.5.3</version>
	</dependency>
	
	<!-- https://mvnrepository.com/artifact/dom4j/dom4j -->
	<dependency>
	    <groupId>dom4j</groupId>
	    <artifactId>dom4j</artifactId>
	    <version>1.6.1</version>
	</dependency>
	
	<!-- https://mvnrepository.com/artifact/junit/junit -->
	<dependency>
	    <groupId>junit</groupId>
	    <artifactId>junit</artifactId>
	    <version>4.12</version>
	</dependency>
  </dependencies>
```

- 检验jq选择器是否能获取到元素内容：  

![](../img/p02.png)  

``$("div").text(); 获取中间的文本，不包括html标签；``   

###    2.获取元素转化为json数据  

#### 1.新建maven工程    

Java Build Path & Java Compiler修改为1.7  

#### 2.新建Chapter的model对象   

- 属性：

```java
private String title;
private String url;
```
- 添加版本号，添加equals，添加toString

#### 3.新建接口层  

```java
public interface IChapterSpider {
	/**
	 * 给url地址，返回所有章节列表
	 * @param url
	 * @return
	 */
	public List<Chapter> getsChapter(String url);
}
```

#### 4.实现接口层   

```java
	@Override
	public List<Chapter> getsChapter(String url) {
		try {
			String result=crawl(url);
			Document doc=Jsoup.parse(result);    //解析HTML字符串
			doc.setBaseUri(url);  //设置基础路径,解决绝对相对路径
			Elements as=doc.select("#list dd a");   //寻找该选择器的元素
			List<Chapter> chapters=new ArrayList<>();
			for(Element a:as){   //遍历拿到的标签
				Chapter chapter=new Chapter();
				chapter.setTitle(a.text());
				chapter.setUrl(a.absUrl("href"));    
				chapters.add(chapter);    //将元素放进对象再存进数组中
			}
			return chapters;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
```

```java
	protected String crawl(String url) throws Exception{
		try(CloseableHttpClient httpClient=HttpClientBuilder.create().build();    //设置代理服务器
			CloseableHttpResponse httpResponse= httpClient.execute(new HttpGet(url))){  //放进try里可以自动释放资源
			String result=EntityUtils.toString(httpResponse.getEntity(),"utf-8");   //返回抓取的结果
			return result;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
```

#### 5.编写Test方法    

在test目录下   

```java
	@Test
	public void test1() throws Exception{
		IChapterSpider spider=new DefaultChapterSpider();
		List<Chapter> chapters= spider.getsChapter("http://www.biquge.tw/0_5/");
		for(Chapter chapter:chapters){
			System.out.println(chapter);
		}
	}
```

输出的对象由于toString会显示属性在控制台上面    

### 3.使用xml配置通用读取   

使用xml方式读取就可以在外部修改，随意更改读取网站的方式了   

#### 1.xml文件的编写

```xml  
<?xml version="1.0" encoding="UTF-8"?>
<sites>
	<site>
		<title>顶点小说</title>
		<charset>GBK</charset>
		<url>http://www.23wx.com</url>
		<charset-list-selector>#at td a</charset-list-selector>
	</site>
	<site>
		<title>笔趣阁</title>
		<charset>utf-8</charset>
		<url>http://www.biquge.tw/</url>
		<charset-list-selector>#list dd a</charset-list-selector>
	</site>
</sites>
```

#### 2.编写一个支持网站枚举类       

```java  
/**
 * 已经被支持的小说网站枚举
 * @author liyb
 *
 */
public enum NovelSiteEnum{
	DingDianXiaoShuo(1,"23wx.com"),
	BiQuGe(2,"biquge.tw");
	private int id;
	private String url;
	private NovelSiteEnum(int id,String url){
		this.id=id;
		this.url=url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public static NovelSiteEnum getEnumById(int id){
		switch(id){
			case 1:return DingDianXiaoShuo;
			case 2:return BiQuGe;
			default:throw new RuntimeException("id="+id+"是不被支持的小说网站");
		}
	}
	public static NovelSiteEnum getEnumByUrl(String url){
		for(NovelSiteEnum novelSiteEnum	: values()){
			if(url.contains(novelSiteEnum.url)){   //只要包含，就返回
				return novelSiteEnum;
			}
		}
		throw new RuntimeException("url="+url+"是不被支持的小说网站");  //找不到就抛出异常，不要返回null
	}
}
```

枚举的内容：

1. 声明一个对象  
2. 写两个方法，根据id，根据url获取对象  

#### 3.编写工具类处理xml文件   

```java
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
```

#### 4.修实现类参数改为xml读取   

将接口选择器的实现类中的编码和选择器修改为工具类获取

- ``String charset=NovelSpiderUtill.getContext(NovelSiteEnum.getEnumByUrl(url)).get("charset"); ``  获取编码

- ``Elements as=doc.select(NovelSpiderUtill.getContext(NovelSiteEnum.getEnumByUrl(url)).get("charset-list-selector"));``   寻找该选择器的元素

  ​
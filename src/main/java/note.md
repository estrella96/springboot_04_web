# Spring Boot Web开发
- 创建SpringBoot应用 选择需要的模块
- SpringBoot默认自动配置好 只需要在配置文件中指定少量配置就可以运行
- 自己编写业务代码

##自动配置原理
- 这个场景SpringBoot帮我们配置了什么 能不能修改 能修改哪些配置 能不能扩展
    ```java
    xxxAutoConfigration //帮我们给容器中自动配置组件
    xxxProperties //配置类封装配置文件的内容

## SpringBoot 对静态资源的映射规则

```java
@ConfigurationProperties(
    prefix = "spring.resources",
    ignoreUnknownFields = false
)
public class ResourceProperties {}
//可以设置和静态资源有关的参数 缓存时间
```

```java
//WebMvcAutoConfiguration.class文件
public void addResourceHandlers(ResourceHandlerRegistry registry){
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

    }
        }
        //配置欢迎页映射
        @Bean
        public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext) {
            return new WelcomePageHandlerMapping(new TemplateAvailabilityProviders(applicationContext), applicationContext, this.getWelcomePage(), this.mvcProperties.getStaticPathPattern());
        }

```
- 所有/webjars/**,都去classpath:/META-INF/resources/webjars/ 找资源
    webjars：以jar包方式引入静态资源
    !(http://www.webjars.org/)
 ```xml
<dependency>
    			<groupId>org.webjars</groupId>
    			<artifactId>jquery</artifactId>
    			<version>3.3.1</version>
</dependency>
```
    localhost:8080/webjars/jquery/3.3.1/jquery.js
- /** 访问当前项目的任何资源  （静态资源的根路径） 类路径是 java/resources
    "classpath:/META-INF/resources/"
    "classpath:/resources/"
    "classpath:/static/"
    "classpath:/public/"
    "/"当前项目根路径
-  欢迎页 静态资源文件夹下所有index.html页面：被"/**"映射
    localhost:8080/ index页面
-  所有的 **/favicon.ico 都是在静态资源文件下找
```java
//配置图标
 @Configuration
        @ConditionalOnProperty(
            value = {"spring.mvc.favicon.enabled"},
            matchIfMissing = true
        )
        public static class FaviconConfiguration implements ResourceLoaderAware {
            private final ResourceProperties resourceProperties;
            private ResourceLoader resourceLoader;

            public FaviconConfiguration(ResourceProperties resourceProperties) {
                this.resourceProperties = resourceProperties;
            }

            public void setResourceLoader(ResourceLoader resourceLoader) {
                this.resourceLoader = resourceLoader;
            }

            @Bean
            public SimpleUrlHandlerMapping faviconHandlerMapping() {
                SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
                mapping.setOrder(-2147483647);
                
                mapping.setUrlMap(Collections.singletonMap("**/favicon.ico", this.faviconRequestHandler()));
                return mapping;
            }

            @Bean
            public ResourceHttpRequestHandler faviconRequestHandler() {
                ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
                requestHandler.setLocations(this.resolveFaviconLocations());
                return requestHandler;
            }

            private List<Resource> resolveFaviconLocations() {
                String[] staticLocations = WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.getResourceLocations(this.resourceProperties.getStaticLocations());
                List<Resource> locations = new ArrayList(staticLocations.length + 1);
                Stream var10000 = Arrays.stream(staticLocations);
                ResourceLoader var10001 = this.resourceLoader;
                this.resourceLoader.getClass();
                var10000.map(var10001::getResource).forEach(locations::add);
                locations.add(new ClassPathResource("/"));
                return Collections.unmodifiableList(locations);
            }
        }
```

## 模板引擎
JSP Thymeleaf Velocity Freemarker
- Thymeleaf
    语法简单 功能强大
- 1 引入thymeleaf
```xml
<!--引入模板引擎-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
```
- 2 thymeleaf使用&语法
    
```java
//默认规则
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";
    private boolean checkTemplate = true;
    private boolean checkTemplateLocation = true;
    private String prefix = "classpath:/templates/";
    private String suffix = ".html";
    //只要我们把html页面放在"classpath:/templates/"，thymeleaf 就能自动渲染
    
```
```java
@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/success")
    public String success(){
        //classpath:/templates/success.html
        return "success";
    }
}
```

 - 使用：官方文档
    1 导入thymeleaf的名称空间
    `<html lang="en" xmlns:th="http://www.thymeleaf.org" >`
    2 使用thymeleaf语法
    ```html
        <!DOCTYPE html>
        <html lang="en" xmlns:th="http://www.thymeleaf.org" >
        <head>
            <meta charset="UTF-8">
            <title>Title</title>
        </head>
        <body>
            <h1>成功！</h1>
            <!--th:text:设置div的文本内容-->
            <!--不经过引擎渲染显示前端数据 经过之后后台数据-->
            <div th:text="${hello}">这是欢迎信息</div>
        
        </body>
        </html>
    ```
    3 语法规则
        - th:text 改变当前元素里面的文本内容
        - th:任意html属性 来替换原生属性的值
        ![属性](./picture_note/attribute.png)
        - 表达式(语法) 4 standard expression syntax
        ```properties
            Simple expressions:
            VariableExpressions: ${...}  获取变量值OGNL： 获取对象属性、调用方法  使用内置的基本对象 内置工具对象
            SelectionVariableExpressions: *{...}  选择表达式 与$功能相同 取了object之后可以直接用*{属性}
            MessageExpressions: #{...} 获取国际化内容
            LinkURLExpressions: @{...}  定义url链接
            Fragment Expressions: ~{...} 片段引用表达式
            
            Literals（字面量）
            Text literals: 'one text' , 'Another one!' ,... 
            Number literals: 0 , 34 , 3.0 , 12.3 ,... 
            Boolean literals: true , false
            Nullliteral: null
            Literal tokens: one , sometext , main ,... 
            
            Text operations:（文本操作）
            Stringconcatenation: +
            Literal substitutions: |The name is ${name}|
            
            Arithmetic operations: （数学运算）
            Binaryoperators: +, -, *, /, %
            Minussign(unaryoperator): - 
            
            Boolean operations:（布尔运算）
            Binary operators: and , or
            Boolean negation (unary operator): ! , not
            
            Comparisons and equality:（比较运算）
            Comparators: >, <, >=, <= (gt, lt, ge, le)
            Equality operators: == , != ( eq , ne ) 
            
            Conditional operators:（条件运算）
            If-then: (if) ? (then)
            If-then-else: (if) ? (then) : (else) 
            Default: (value) ?: (defaultvalue)
            
            Special tokens:（特殊操作）
            No-Operation: _
        ```
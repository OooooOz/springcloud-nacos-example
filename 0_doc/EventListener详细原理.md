@[TOC]( )

## 0. 举个栗子

### 0.1. 事件监听方法
```java
/**
 * 通用事件监听器
 */
@Slf4j
@Component
public class CommonHandleListener {

    public static final String NOTIFY_OTHER_SYSTEM_EVENT = "onNotifyOtherSystemEvent";

    @Autowired
    private CommonConfigService commonConfigService;

    @EventListener(condition = "#eventDTO.source == T(com.example.common.listener.CommonHandleListener).NOTIFY_OTHER_SYSTEM_EVENT")
    public void onNotifyOtherSystemEvent(CommonEventDTO eventDTO) {
        log.info("EventListener事务提交后执行");
        if (NOTIFY_OTHER_SYSTEM_EVENT.equals(eventDTO.getSource())) { ... }
    }

    @TransactionalEventListener(
        // 监听的阶段
        phase = TransactionPhase.AFTER_COMMIT,
        // 监听的条件
        condition = "#eventDTO.source == T(com.example.common.listener.CommonHandleListener).NOTIFY_OTHER_SYSTEM_EVENT")
    public void onNotifyOtherSystemTransactionalEvent(CommonEventDTO eventDTO) {
        log.info("TransactionalEventListener事务提交后执行");
        if (NOTIFY_OTHER_SYSTEM_EVENT.equals(eventDTO.getSource())) { ... }
    }
}
```
### 0.2. 事件推送

```java
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotifySystemDTO submit(CommonConfigDTO commonConfigDTO) {
        Long id = this.dealMainInfo(commonConfigDTO);
        List<DetailVo> detailVos = this.dealMainDetailInfo(id);
        NotifySystemDTO dto = this.buildNotifySystemDTO(id, detailVos);
        publisher.publishEvent(new CommonEventDTO(CommonHandleListener.NOTIFY_OTHER_SYSTEM_EVENT, dto));
        log.info("事务提交前操作：{}", JSON.toJSONString(commonConfigDTO));
        return dto;
    }
```
## 1. @EventListener注解
> 注解有3个属性，主要是condition和classes(value同classes)
> - condition是监听条件，使用SpEL表达式，满足条件才会调用监听方法，使用栗子可以见[@EventListener的使用的2.3.1](https://blog.csdn.net/qq_41982506/article/details/105623304)
> - classes属性：指定监听的类，可以指定多个；监听的类和监听方法参数的约定
>   - 如果为空，则监听方法参数有且只有一个，且为发布事件的事件类型（不然你发布事件，底层也反射调用不到监听方法）
>   - 如果设置一个事件类，监听方法可以不设置参数；如果要设置参数，也是有且只有一个参数，且为发布事件的事件类型
>   - 如果设置多个事件类，监听方法可以不设置参数；如果要设置参数，也是有且只有一个参数，最好是共同父类或接口
>   - 正常来说不建议设置多个事件类，增加系统的复杂性和潜在的性能开销；建议根据实际需求合理设计事件监听机制，避免不必要的事件监听

## 2. @EventListener标注的监听方法解析
> 需要对Spring底层Bean容器注册有所了解

### 2.1. 事件监听方法处理器EventListenerMethodProcessors
> 先看一下这个类
> - <font color = 'red'>实现了SmartInitializingSingleton</font>，就意味着初始化之后就会触发afterSingletonsInstantiated方法调用
> - <font color = 'red'>实现了BeanFactoryPostProcessor</font>，就意味着会触发EventListenerMethodProcessor.postProcessBeanFactory
> - 下文会有说明，眼熟一下
```java
public class EventListenerMethodProcessor
		implements SmartInitializingSingleton, ApplicationContextAware, BeanFactoryPostProcessor{
    
    @Nullable
    private List<EventListenerFactory> eventListenerFactories;
}
```


> 在SpringBoot启动类run方法[(SpringBoot的启动整体过程2.2节)](https://blog.csdn.net/weixin_43901882/article/details/119360255)中有四个方法
> - createApplicationContext创建容器上下文时注册EventListenerMethodProcessor的Bean定义
> - refreshContext会对IOC容器的Bean定义进行生命周期管理；
>   - <font color = 'red'>该方法调用到refresh方法（容器管理的重要方法），里面主要有四个方法</font>
>     - invokeBeanFactoryPostProcessors：调用Bean工厂的后置处理器，会触发EventListenerMethodProcessor.postProcessBeanFactory初始化eventListenerFactories工厂
>     - initApplicationEventMulticaster：初始化事件广播
>     - registerListeners：检测并注册已有的事件监听器和Bean
>     - finishBeanFactoryInitialization：实例化所有的(non-lazy-init)单例Bean，会触发EventListenerMethodProcessor对@EventListener标注的监听方法解析
```java
public ConfigurableApplicationContext run(String... args) {
	... ... ...
	try {
		ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
		ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
		// 上面设置一些环境吧应该，没怎么关注
		configureIgnoreBeanInfo(environment);
		// 这里控制台会打印输出那个Spring启动图
		Banner printedBanner = printBanner(environment);
		// 创建ApplicationContext即ioc容器
		context = createApplicationContext();
		// 会从缓存中获取SpringBootExceptionReporter类型实例
		exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
				new Class[] { ConfigurableApplicationContext.class }, context);
		// 对context进行一些预处理，主要是一些赋值
		// 这里会将主配置类封装成BeanDefinition再注册到ApplicationContext中
		prepareContext(context, environment, listeners, applicationArguments, printedBanner);
		// 刷新context容器，重点方法
		refreshContext(context);
	... ...
}


public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        try{

            // 调用Bean工厂的后置处理器
            invokeBeanFactoryPostProcessors(beanFactory);
            
            // 初始化事件广播
            initApplicationEventMulticaster();
    
            // 检测并注册已有的事件监听器和Bean
            registerListeners();
            
            // 实例化所有的(non-lazy-init)单例Bean
            finishBeanFactoryInitialization(beanFactory);

        .........
        }
    }

```
#### 2.1.1. AbstractApplicationContext.invokeBeanFactoryPostProcessors
> - 调用Bean工厂的后置处理器，会触发EventListenerMethodProcessor.postProcessBeanFactory初始化eventListenerFactories工厂
> - eventListenerFactories会被初始化两个事件监听工厂类<code>DefaultEventListenerFactory和TransactionalEventListenerFactory</code>
> - 后者比前者优先级高


#### 2.1.2. AbstractApplicationContext.initApplicationEventMulticaster
> 初始化事件广播,主要是初始化事件监听组播者<code>applicationEventMulticaster</code>并往容器注册单例bean

```java
protected void initApplicationEventMulticaster() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    // 如果有自定义的组播器，即beanName为applicationEventMulticaster，则用自定义的组播器
    if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
        ... ...
    }
    else {
        // 默认初始化组播器SimpleApplicationEventMulticaster，往bean公池注册单例bean
        this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
    }
}
```

#### 2.1.3. AbstractApplicationContext.registerListeners
> - 检测并注册已有的事件监听器，添加到组播器的监听器集合中
> - 获取实现的ApplicationListener接口的bean名称，添加到事件监听组播者的监听器Bean集合中

```java
	protected void registerListeners() {
		// 将容器已有的监听器添加到事件监听组播者的监听器集合中
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// 获取实现的ApplicationListener接口的bean名称，添加到事件监听组播者的监听器Bean集合中
        // 此时的bean还没实例化，所以存储beanName，以便于后续根据beanName获取对应监听器
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// 早期事件调用，此时监听器已注册，就将早期的事件earlyApplicationEvents进行广播，然后置空
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}
```
> - getApplicationEventMulticaster()获取到的组播者就是2.1.2初始化的SimpleApplicationEventMulticaster
> - SimpleApplicationEventMulticaster的父类AbstractApplicationEventMulticaster持有一个辅助的检索器defaultRetriever对象，内部有两个集合
>   - applicationListeners：辅助持有的监听器集合
>   - applicationListenerBeans：辅助持有的监听器beanName
```java
public abstract class AbstractApplicationEventMulticaster
        implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

    // 持有的辅助检索器对象
    private final DefaultListenerRetriever defaultRetriever = new DefaultListenerRetriever();

    // 监听方法同步调用的监听器缓存
    final Map<ListenerCacheKey, CachedListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);

    ......

    private class DefaultListenerRetriever {

        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

        public final Set<String> applicationListenerBeans = new LinkedHashSet<>();

    }
}
```

##### 2.1.3.1. AbstractApplicationEventMulticaster.addApplicationListener
> 将创建的ApplicationListenerMethodAdapter加入组播者辅助持有的监听器集合

```java
	@Override
public void addApplicationListener(ApplicationListener<?> listener) {
    synchronized (this.defaultRetriever) {
        // 如果当前监听器是代理对象，则先移除代理的target对象，避免重复调用
        Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
        if (singletonTarget instanceof ApplicationListener) {
            this.defaultRetriever.applicationListeners.remove(singletonTarget);
        }
        // 添加至defaultRetriever辅助持有的监听器
        this.defaultRetriever.applicationListeners.add(listener);
        this.retrieverCache.clear();
    }
}
```

##### 2.1.3.2. AbstractApplicationEventMulticaster.addApplicationListenerBean
> 将实现的ApplicationListener接口的组件beanName,添加到组播者辅助持有的监听器beanName

```java
	@Override
	public void addApplicationListenerBean(String listenerBeanName) {
		synchronized (this.defaultRetriever) {
			this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
			this.retrieverCache.clear();
		}
	}
```

#### 2.1.4. finishBeanFactoryInitialization
> - 主要在preInstantiateSingletons方法进行bean的生命周期以及bean初始化后的一些操作
> - EventListenerMethodProcessor初始化之后就会触发afterSingletonsInstantiated方法调用
>   - EventListenerMethodProcessor是实现SmartInitializingSingleton接口的

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
	... ... ...// 省略一些环境或者其他调用的代码
	// 该方法会将非懒加载又是单例的bean进行生命周期调用
	beanFactory.preInstantiateSingletons();
}


public void preInstantiateSingletons() throws BeansException {
	... ... ... //省略
	List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

	// Trigger initialization of all non-lazy singleton beans...
	for (String beanName : beanNames) {
		RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
		// 非抽象&& 单例 && 非懒加载
		if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
			if (isFactoryBean(beanName)) {
				... ... ... // 大部分组件bean不会走这里，这是工厂bean的
			}
			else {
				// 最终调用getBean进行Bean的生命周期调用
				getBean(beanName);
			}
		}
	}

    // Trigger post-initialization callback for all applicable beans...
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        if (singletonInstance instanceof SmartInitializingSingleton) {
            SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                smartSingleton.afterSingletonsInstantiated();
                return null;
                }, getAccessControlContext());
            }
            else {
                // 会触发EventListenerMethodProcessor.afterSingletonsInstantiated方法调用
                smartSingleton.afterSingletonsInstantiated();
            }
        }
    }
}

```

##### 2.1.4.1. EventListenerMethodProcessor.afterSingletonsInstantiated

> 就是遍历所有容器bean去对@EventListener标注的监听方法解析

```java
	public void afterSingletonsInstantiated() {
		ConfigurableListableBeanFactory beanFactory = this.beanFactory;
		Assert.state(this.beanFactory != null, "No ConfigurableListableBeanFactory set");
		String[] beanNames = beanFactory.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
            ... ...
            // 忽略一大堆的条件判断，最终通过该方法对@EventListener标注的监听方法解析
            processBean(beanName, type);
		}
	}

```

##### 2.1.4.2. EventListenerMethodProcessor.processBean
> - 对@EventListener标注的监听方法解析,
> - 通过工厂对监听方法生成监听器方法适配器ApplicationListenerMethodAdapter
> - 将创建的ApplicationListenerMethodAdapter加入容器和组播者的监听器集合

```java
	private void processBean(final String beanName, final Class<?> targetType) {
		if (!this.nonAnnotatedClasses.contains(targetType) &&
				AnnotationUtils.isCandidateClass(targetType, EventListener.class) &&
				!isSpringContainerClass(targetType)) {

			Map<Method, EventListener> annotatedMethods = null;
			try {
                // 这里获取目标bean上有@EventListener（包括嵌套引用有该注解，比如@TransactionalEventListener）注解标注的方法
				annotatedMethods = MethodIntrospector.selectMethods(targetType,
						(MethodIntrospector.MetadataLookup<EventListener>) method ->
								AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class));
			}
			catch {...}

			if (CollectionUtils.isEmpty(annotatedMethods)) {...}
			else {
				// Non-empty set of methods
				ConfigurableApplicationContext context = this.applicationContext;
				Assert.state(context != null, "No ApplicationContext set");
                // 在上文2.1的refresh方法中invokeBeanFactoryPostProcessors就会触发EventListenerMethodProcessor.postProcessBeanFactory初始化eventListenerFactories工厂
                // 这里会拿到两个事件监听工厂类DefaultEventListenerFactory和TransactionalEventListenerFactory
                // 后者的优先级比前置高，后置针对事务有一些特殊处理
				List<EventListenerFactory> factories = this.eventListenerFactories;
				Assert.state(factories != null, "EventListenerFactory List not initialized");
                // 双重遍历方法和工厂，去匹配方法使用哪个工厂去创建ApplicationListenerMethodAdapter监听器方法适配器
				for (Method method : annotatedMethods.keySet()) {
					for (EventListenerFactory factory : factories) {
                        // 判断当前工厂时候是否支持当前方法
                        // 默认的工厂类DefaultEventListenerFactory会直接是true；TransactionalEventListenerFactory会判断方法是否有@TransactionalEventListener注解
						if (factory.supportsMethod(method)) {
							Method methodToUse = AopUtils.selectInvocableMethod(method, context.getType(beanName));
                            // 通过当前工厂去创建监听器
                            // 默认的工厂类DefaultEventListenerFactory会直接创建ApplicationListenerMethodAdapter监听器方法适配器；
                            // TransactionalEventListenerFactory会创建ApplicationListenerMethodTransactionalAdapter监听器方法适配器，继承了ApplicationListenerMethodAdapter
							ApplicationListener<?> applicationListener =
									factory.createApplicationListener(beanName, targetType, methodToUse);
							if (applicationListener instanceof ApplicationListenerMethodAdapter) {
								((ApplicationListenerMethodAdapter) applicationListener).init(context, this.evaluator);
							}
                            // 将创建的ApplicationListenerMethodAdapter加入容器和组播者的监听器集合
							context.addApplicationListener(applicationListener);
							break;
						}
					}
				}
				if (logger.isDebugEnabled()) {...}
			}
		}
	}

```

##### 2.1.4.3. DefaultEventListenerFactory.createApplicationListener
> - DefaultEventListenerFactory实现了Ordered，并且它定义的优先级是最低的
> - createApplicationListener方法就仅仅初始化创建ApplicationListenerMethodAdapter

```java
public class DefaultEventListenerFactory implements EventListenerFactory, Ordered {
    
	private int order = LOWEST_PRECEDENCE;


	public void setOrder(int order) { this.order = order; }

	@Override
	public int getOrder() { return this.order; }


	@Override
	public boolean supportsMethod(Method method) { return true; }

	@Override
	public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
		return new ApplicationListenerMethodAdapter(beanName, type, method);
	}

}


    public ApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        this.beanName = beanName;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.targetMethod = (!Proxy.isProxyClass(targetClass) ?
                AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
        this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);

        EventListener ann = AnnotatedElementUtils.findMergedAnnotation(this.targetMethod, EventListener.class);
        this.declaredEventTypes = resolveDeclaredEventTypes(method, ann);
        // 获取当前监听方法上@EventListener注解的condition属性值
        this.condition = (ann != null ? ann.condition() : null);
        // 获取当前监听方法顺序值，如果有标注@Order就会获取改注解的value，无则默认0
        this.order = resolveOrder(this.targetMethod);
    }
```

##### 2.1.4.4. TransactionalEventListenerFactory.createApplicationListener
> - DefaultEventListenerFactory也实现了Ordered，并且它定义的优先级是50，所以它比DefaultEventListenerFactory优先级高
> - createApplicationListener方法就创建的是ApplicationListenerMethodTransactionalAdapter

```java
public class TransactionalEventListenerFactory implements EventListenerFactory, Ordered {
    private int order = 50;

    public TransactionalEventListenerFactory() {
    }

    public void setOrder(int order) { this.order = order; }

    public int getOrder() { return this.order;}

    public boolean supportsMethod(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, TransactionalEventListener.class);
    }

    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new ApplicationListenerMethodTransactionalAdapter(beanName, type, method);
    }
}
```

```java
class ApplicationListenerMethodTransactionalAdapter extends ApplicationListenerMethodAdapter {

    private final TransactionalEventListener annotation;


    public ApplicationListenerMethodTransactionalAdapter(String beanName, Class<?> targetClass, Method method) {
        // 父级ApplicationListenerMethodAdapter初始化，复用@EventListener能力
        super(beanName, targetClass, method);
        TransactionalEventListener ann = AnnotatedElementUtils.findMergedAnnotation(method, TransactionalEventListener.class);
        if (ann == null) {
            throw new IllegalStateException("No TransactionalEventListener annotation found on method: " + method);
        }
        this.annotation = ann;
    }
}
```
##### 2.1.4.5. AbstractApplicationContext.addApplicationListener

```java
	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
        // 这里是已经持有组播器了，先往组播器添加当前获取的监听器，再完容器的监听器集合添加
		if (this.applicationEventMulticaster != null) {
            // 上文2.1.3.1方法
			this.applicationEventMulticaster.addApplicationListener(listener);
		}
		this.applicationListeners.add(listener);
	}
```

### 2.2. 事件监听方法处理器EventListenerMethodProcessors小结
> - IOC容器管理时会注册EventListenerMethodProcessors组件
> - EventListenerMethodProcessors组件进行系列生命周期处理后，触发其afterSingletonsInstantiated方法
> - 对@EventListener标注的监听方法解析,再由工厂类生成监听器方法适配器ApplicationListenerMethodAdapter，加入容器和组播者的监听器集合


## 3. publishEvent事件推送
> - 我们由上述栗子，来看看该方法的调用，最终是调用到AbstractApplicationContext#publishEvent(java.lang.Object, org.springframework.core.ResolvableType)

### 3.1. AbstractApplicationContext.publishEvent
> - 声明事件为ApplicationEvent类型，如果不是就包装成PayloadApplicationEvent（其父类就是是ApplicationEvent）
> - 通过组播器multicastEvent方法进行推送
> - 如果有父上下文，也向父上下文递归推送该事件
```java
	protected void publishEvent(Object event, @Nullable ResolvableType eventType) {

        // 声明事件为ApplicationEvent类型，如果不是就包装成PayloadApplicationEvent（其父类就是是ApplicationEvent）
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		} else {
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
			}
		}

		// 如果不为空，则还未注册监听器，则加入早期事件集合earlyApplicationEvents
        // 组播器在上文2.1.3. refresh->registerListeners方法，注册完并推送早期事件，会将其置空
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
            // 我们应用推送的事件，都通过multicastEvent方法进行推送
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// 如果有父上下文，也向父上下文递归推送该事件，向应用栗子手动推送的事件，最后parent都是null的
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				this.parent.publishEvent(event);
			}
		}
	}

```

### 3.2. SimpleApplicationEventMulticaster.multicastEvent
> - 上文2.1.2也说了，初始化的组播器就是SimpleApplicationEventMulticaster
> - 默认taskExecutor为空，即同步调用事件监听方法
>   - 如果想要异步，最好还是在监听方法内进行异步处理，（也就是事件同步调用，监听方法的逻辑异步处理）
>   - taskExecutor不为空则全局事件监听都是异步处理的
>   - taskExecutor不为空，就是自定义组播器applicationEventMulticaster的时候给taskExecutor赋值（推荐使用线程池）

```java
	@Override
	public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
        // 获取任务执行器，默认为空，即会同步调用监听方法
		Executor executor = getTaskExecutor();
        // 获取符合当前事件类型的事件监听器，循环处理每个监听器逻辑
		for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			if (executor != null) {
				executor.execute(() -> invokeListener(listener, event));
			}
			else {
				invokeListener(listener, event);
			}
		}
	}
```
#### 3.2.1. AbstractApplicationEventMulticaster.getApplicationListeners
> - 组播器的父类AbstractApplicationEventMulticaster持有一个辅助的检索器defaultRetriever对象还有一个本地监听器缓存retrieverCache（上文2.1.3）
> - 命中缓存就直接从CachedListenerRetriever检索器获取监听器列表返回，否则retrieveApplicationListeners方法进行查找
>   - 缓存key为事件类型eventType和来源标识类型sourceType构成的ListenerCacheKey对象
>     - 提一嘴：这个对象重写了equals方法，只要eventType、sourceType都相等就是同一个对象
>   - 缓存value是内部类对象CachedListenerRetriever

```java
	protected Collection<ApplicationListener<?>> getApplicationListeners(
			ApplicationEvent event, ResolvableType eventType) {

        // 创建缓存key，只要eventType、sourceType都相等就是同一个对象
		Object source = event.getSource();
		Class<?> sourceType = (source != null ? source.getClass() : null);
		ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

		// 准备新的缓存检索器
		CachedListenerRetriever newRetriever = null;

        // 尝试从缓存中获取已存在的检索器
		CachedListenerRetriever existingRetriever = this.retrieverCache.get(cacheKey);
        
		if (existingRetriever == null) {
			if (this.beanClassLoader == null ||
					(ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) &&
							(sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
                //如果缓存不命中，且类加载器安全（防止类加载器泄漏），则创建新的检索器
				newRetriever = new CachedListenerRetriever();
                
                // 如果缓存中已经有cacheKey，则putIfAbsent返回之前的缓存对象，即existingRetriever不为空
                // 如果缓存中没有cacheKey，则putIfAbsent返回null，即existingRetriever为空
				existingRetriever = this.retrieverCache.putIfAbsent(cacheKey, newRetriever);
                // 这块什么时候会满足，有点不太理解？？
				if (existingRetriever != null) {
					newRetriever = null;  // no need to populate it in retrieveApplicationListeners
				}
			}
		}

        // 如果存在缓存的检索器，尝试获取缓存的监听器返回
		if (existingRetriever != null) {
			Collection<ApplicationListener<?>> result = existingRetriever.getApplicationListeners();
			if (result != null) {
				return result;
			}
			// If result is null, the existing retriever is not fully populated yet by another thread.
			// Proceed like caching wasn't possible for this current local attempt.
		}

        // 缓存不命中时，通过retrieveApplicationListeners方法获取监听器返回
		return retrieveApplicationListeners(eventType, sourceType, newRetriever);
	}
```

#### 3.2.2. AbstractApplicationEventMulticaster.retrieveApplicationListeners
> - 缓存不命中时，先写的缓存检索器对象，再通过该方法获取支持当前事件处理的监听器集合数据，再更新缓存的检索器对象持有的监听器集合

```java
	private Collection<ApplicationListener<?>> retrieveApplicationListeners(
			ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable CachedListenerRetriever retriever) {

        // 定义查找到所有的监听器列表
		List<ApplicationListener<?>> allListeners = new ArrayList<>();
        // 定义过滤后的支持当前事件的监听器列表和beanName
		Set<ApplicationListener<?>> filteredListeners = (retriever != null ? new LinkedHashSet<>() : null);
		Set<String> filteredListenerBeans = (retriever != null ? new LinkedHashSet<>() : null);

		Set<ApplicationListener<?>> listeners;
		Set<String> listenerBeans;
        // 对组播器持有的默认检索器defaultRetriever加锁，将该检索器持有已注册的监听器和监听器beanName赋值到当前局部变量
        // 在上文2.1.3.registerListeners时，就会注册监听器到默认的检索器defaultRetriever里头
		synchronized (this.defaultRetriever) {
			listeners = new LinkedHashSet<>(this.defaultRetriever.applicationListeners);
			listenerBeans = new LinkedHashSet<>(this.defaultRetriever.applicationListenerBeans);
		}

		// 循环注册的监听器，通过supportsEvent方法来判断每个监听器是否支持当前事件（判断事件类型、来源类型），然后写入当前的局部变量里头
        // @EventListener注解的classes过滤就是在这里处理的
		for (ApplicationListener<?> listener : listeners) {
			if (supportsEvent(listener, eventType, sourceType)) {
				if (retriever != null) {
					filteredListeners.add(listener);
				}
				allListeners.add(listener);
			}
		}
        
        // 循环注册的监听器beanName，通过supportsEvent方法来判断每个监听器是否支持当前事件（判断事件类型、来源类型），支持就写入当前的局部变量里头，不支持就移除
        // 按beanName来检索监听器，可能与注册的监听器重合，还需要判断去重
		if (!listenerBeans.isEmpty()) {
			ConfigurableBeanFactory beanFactory = getBeanFactory();
			for (String listenerBeanName : listenerBeans) {
				try {
					if (supportsEvent(beanFactory, listenerBeanName, eventType)) { ... }
					else { ... }
				}
				catch (NoSuchBeanDefinitionException ex) { ... }
			}
		}

        // 将所有的监听器进行排序，order越小约优先
        // 如果是ApplicationListenerMethodAdapter，在上文2.1.4.3节新建的时候就会解析监听方法的@Order注解的value值
		AnnotationAwareOrderComparator.sort(allListeners);
		if (retriever != null) {
            // 更新缓存的检索器持有的监听器集合
			if (filteredListenerBeans.isEmpty()) {
                // filteredListenerBeans为空时allListeners和filteredListeners其实是一样的
				retriever.applicationListeners = new LinkedHashSet<>(allListeners);
				retriever.applicationListenerBeans = filteredListenerBeans;
			}
			else {
				retriever.applicationListeners = filteredListeners;
				retriever.applicationListenerBeans = filteredListenerBeans;
			}
		}
		return allListeners;
	}

```

#### 3.2.3. AbstractApplicationEventMulticaster.CachedListenerRetriever.getApplicationListeners
> - 缓存命中时，直接从缓存的检索器对象获取监听器集合数据

```java
	private class CachedListenerRetriever {

		@Nullable
		public volatile Set<ApplicationListener<?>> applicationListeners;

		@Nullable
		public volatile Set<String> applicationListenerBeans;

		@Nullable
		public Collection<ApplicationListener<?>> getApplicationListeners() {
            // 写缓存的时候，就已经把符合当前事件的监听器集合数据更新到当前缓存的检索器了
			Set<ApplicationListener<?>> applicationListeners = this.applicationListeners;
			Set<String> applicationListenerBeans = this.applicationListenerBeans;
			if (applicationListeners == null || applicationListenerBeans == null) {
				// Not fully populated yet
				return null;
			}

            // 从applicationListenerBeans和applicationListeners获取监听器数据，然后排序
			List<ApplicationListener<?>> allListeners = new ArrayList<>(
					applicationListeners.size() + applicationListenerBeans.size());
			allListeners.addAll(applicationListeners);
			if (!applicationListenerBeans.isEmpty()) {
				BeanFactory beanFactory = getBeanFactory();
				for (String listenerBeanName : applicationListenerBeans) {
					try {
						allListeners.add(beanFactory.getBean(listenerBeanName, ApplicationListener.class));
					}
					catch (NoSuchBeanDefinitionException ex) { ... }
				}
			}
			if (!applicationListenerBeans.isEmpty()) {
				AnnotationAwareOrderComparator.sort(allListeners);
			}
			return allListeners;
		}
	}

```
### 3.3. SimpleApplicationEventMulticaster.invokeListener
> - 如果有自定义的错误处理器，则在错误处理器中处理，没有则直接调用doInvokeListener（调用当前监听器onApplicationEvent方法），做监听方法的调用
```java
	protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        // 默认为空，除非是自定义的组播器，手动定义了错误处理器
		ErrorHandler errorHandler = getErrorHandler();
		if (errorHandler != null) {
			try {
				doInvokeListener(listener, event);
			}
			catch (Throwable err) {
				errorHandler.handleError(err);
			}
		}
		else {
			doInvokeListener(listener, event);
		}
	}

    private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            // 调用当前监听器onApplicationEvent方法，做监听方法的调用
            listener.onApplicationEvent(event);
        }
        catch (ClassCastException ex) { ... }
    }
```

#### 3.3.1. ApplicationListenerMethodAdapter.onApplicationEvent
> - 常规的事件监听方法，向上述栗子的onNotifyOtherSystemEvent，用@EventListener标注的
>   - 在上文2.1.4.2节就讲到，在IOC容器管理时，就对@EventListener标注的监听方法解析，生成监听器方法适配器ApplicationListenerMethodAdapter
> - 最后通过processEvent对监听事件处理


```java
	public void onApplicationEvent(ApplicationEvent event) {
        processEvent(event);
    }

	public void processEvent(ApplicationEvent event) {
		Object[] args = resolveArguments(event);
        // 判断该监听方法适配器能否处理当前事件
		if (shouldHandle(event, args)) {
            // 做实际调用监听方法，其实就是反射调用
			Object result = doInvoke(args);
			if (result != null) {
                // 如果有返回值,则把返回值继续当成一个事件发布
				handleResult(result);
			}
			else {
				logger.trace("No result object given - no result to handle");
			}
		}
	}

    private boolean shouldHandle(ApplicationEvent event, @Nullable Object[] args) {
        if (args == null) {
            return false;
        }
        // @EventListener的condition属性处理，返回true即当前适配器可处理当前事件
        String condition = getCondition();
        if (StringUtils.hasText(condition)) {
            Assert.notNull(this.evaluator, "EventExpressionEvaluator must not be null");
            return this.evaluator.condition(condition, event, this.targetMethod, this.methodKey, args, this.applicationContext);
        }
        return true;
    }
```

#### 3.3.2. ApplicationListenerMethodAdapter.doInvoke
> - 就是通过反射调用监听方法，适配器里面的method，beanName等等，在创建ApplicationListenerMethodAdapter就已经赋值了

```java
protected Object doInvoke(Object... args) {
        // 通过beanName获取目标对象
		Object bean = getTargetBean();
		if (bean.equals(null)) {
			return null;
		}

		ReflectionUtils.makeAccessible(this.method);
		try {
            // 通过反射调用目标对象方法
			return this.method.invoke(bean, args);
		}
		catch (IllegalArgumentException ex) { ... }
	}
```

## 4. @TransactionalEventListener底层原理
> - 底层原理和@EventListener差不多，区别在于，它使用的是ApplicationListenerMethodTransactionalAdapter监听方法适配器
> - 让我们看看它的onApplicationEvent方法

### 4.1. ApplicationListenerMethodTransactionalAdapter.onApplicationEvent
> - 创建一个事务同步的事件适配器TransactionSynchronizationEventAdapter，实现了TransactionSynchronization
>   - 事务完成之后会获取所有的TransactionSynchronization，触发afterCompletion方法
> - 使用了事务同步管理器（TransactionSynchronizationManager.registerSynchronization）来注册同步任务
>   - 就是将当前适配器添加到事务同步管理器TransactionSynchronizationManager的内部集合synchronizations里头
> - 在事务完成之后会触发TransactionSynchronizationEventAdapter的afterCompletion钩子方法
>   - 会调用processEvent方法和@EventListener一样
```java
class ApplicationListenerMethodTransactionalAdapter extends ApplicationListenerMethodAdapter {

    private final TransactionalEventListener annotation;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 事务活跃状态
        if (TransactionSynchronizationManager.isSynchronizationActive() &&
                TransactionSynchronizationManager.isActualTransactionActive()) {
            // 创建一个事务同步的事件适配器TransactionSynchronizationEventAdapter
            TransactionSynchronization transactionSynchronization = createTransactionSynchronization(event);
            // 通过事务同步管理器注册任务
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
        }
        ... ...
    }

    private TransactionSynchronization createTransactionSynchronization(ApplicationEvent event) {
        return new TransactionSynchronizationEventAdapter(this, event, this.annotation.phase());
    }


    private static class TransactionSynchronizationEventAdapter extends TransactionSynchronizationAdapter {

        private final ApplicationListenerMethodAdapter listener;

        private final ApplicationEvent event;

        private final TransactionPhase phase;

        public TransactionSynchronizationEventAdapter(ApplicationListenerMethodAdapter listener,
                                                      ApplicationEvent event, TransactionPhase phase) {

            this.listener = listener;
            this.event = event;
            this.phase = phase;
        }

        @Override
        public int getOrder() {
            return this.listener.getOrder();
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            if (this.phase == TransactionPhase.BEFORE_COMMIT) {
                processEvent();
            }
        }

        @Override
        public void afterCompletion(int status) {
            if (this.phase == TransactionPhase.AFTER_COMMIT && status == STATUS_COMMITTED) {
                processEvent();
            }
            else if (this.phase == TransactionPhase.AFTER_ROLLBACK && status == STATUS_ROLLED_BACK) {
                processEvent();
            }
            else if (this.phase == TransactionPhase.AFTER_COMPLETION) {
                processEvent();
            }
        }

        protected void processEvent() {
            this.listener.processEvent(this.event);
        }
    }

}

```

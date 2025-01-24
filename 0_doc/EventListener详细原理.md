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
    if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
        ... ...
    }
    else {
        // 初始化组播者SimpleApplicationEventMulticaster，往bean公池注册单例bean
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

		// 早期事件调用
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
> - SimpleApplicationEventMulticaster的父类AbstractApplicationEventMulticaster持有一个辅助的寻回犬defaultRetriever对象，内部有两个集合
>   - applicationListeners：辅助持有的监听器集合
>   - applicationListenerBeans：辅助持有的监听器beanName
```java
	private class DefaultListenerRetriever {

		public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

		public final Set<String> applicationListenerBeans = new LinkedHashSet<>();

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
                            // TransactionalEventListenerFactory会创建TransactionalApplicationListenerMethodAdapter监听器方法适配器，继承了ApplicationListenerMethodAdapter
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
        // 获取当前监听方法顺序值，如果有标注@Order就会获取改注解的value，无则null
        this.order = resolveOrder(this.targetMethod);
    }
```

##### 2.1.4.4. TransactionalEventListenerFactory.createApplicationListener
> - DefaultEventListenerFactory也实现了Ordered，并且它定义的优先级是50，所以它比DefaultEventListenerFactory优先级高
> - createApplicationListener方法就创建的是TransactionalApplicationListenerMethodAdapter

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
        return new TransactionalApplicationListenerMethodAdapter(beanName, type, method);
    }
}

public class TransactionalApplicationListenerMethodAdapter extends ApplicationListenerMethodAdapter
        implements TransactionalApplicationListener<ApplicationEvent> {

    private final TransactionalEventListener annotation;

    private final TransactionPhase transactionPhase;

    private final List<SynchronizationCallback> callbacks = new CopyOnWriteArrayList<>();

    
    public TransactionalApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        // 父级ApplicationListenerMethodAdapter初始化，复用@EventListener能力
        super(beanName, targetClass, method);
        TransactionalEventListener ann =
                AnnotatedElementUtils.findMergedAnnotation(method, TransactionalEventListener.class);
        if (ann == null) {
            throw new IllegalStateException("No TransactionalEventListener annotation found on method: " + method);
        }
        this.annotation = ann;
        // 获取当前监听方法上@TransactionalEventListener注解的phase属性值
        this.transactionPhase = ann.phase();
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
> - 对@EventListener标注的监听方法解析,再有工厂类生成监听器方法适配器ApplicationListenerMethodAdapter，加入容器和组播者的监听器集合


## 3. publishEvent事件推送

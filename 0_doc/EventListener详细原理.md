
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
> 在SpringBoot启动类run方法[(SpringBoot的启动整体过程2.2节)](https://blog.csdn.net/weixin_43901882/article/details/119360255)中有两个方法
> - createApplicationContext创建容器上下文时注册
    EventListenerMethodProcessor的Bean定义
> - refreshContext会对IOC容器的Bean定义进行生命周期管理；
>   - 该方法调用到refresh方法，里面有两个方法
>     - initApplicationEventMulticaster：初始化事件广播
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
            
            // 初始化事件广播
            initApplicationEventMulticaster();
    
            // 实例化所有的(non-lazy-init)单例Bean
            finishBeanFactoryInitialization(beanFactory);

        .........
        }
    }

```

#### 2.1.1. initApplicationEventMulticaster
> 初始化事件广播,主要是初始化applicationEventMulticaster并往容器注册单例bean

```java
protected void initApplicationEventMulticaster() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
        ... ...
    }
    else {
        // 初始化SimpleApplicationEventMulticaster，往bean公池注册单例bean
        this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
    }
}
```

#### 2.1.2. AbstractApplicationContext#finishBeanFactoryInitialization
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
                smartSingleton.afterSingletonsInstantiated();
            }
        }
    }
}

```

##### 2.1.2.1. EventListenerMethodProcessor.afterSingletonsInstantiated

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

##### 2.1.2.2. EventListenerMethodProcessor.afterSingletonsInstantiated

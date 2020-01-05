### 1、概述
什么是elastic-job-lite，它是定位为轻量级无中心化解决方案，使用jar包的形式提供分布式任务的协调服务，具体可以点击链接进入官网[Elastic-Job-Lite](https://github.com/elasticjob/elastic-job-lite)查看，本文主要使用elastic-job-lite和spring进行二次封装，搭建自己的分布式定时任务程序。设计思路是利用spring的自动装载和ApplicationListener特性，在运行时扫描注册相关标注为Job注解类。

### 2、引入包名
```
<!--Spring 相关--->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
</dependency>
<!--elastic-job--->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-spring</artifactId>
</dependency>
```
### 3、定义注解
定义一个接口或类注解该注解，运行时扫描所有使用该注解的接口或方法。

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface ElasticScheduledJob {

    /**
     * cron 表达式
     */
    String cron();

}
```
### 4、定义抽象类
继承elastic-job中的SimpleJob，扩展两个方法，方便继承类使用。

```
public abstract class AbsSingleJob implements SimpleJob {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(ShardingContext shardingContext) {
        String name = getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        try {
            log.info("Job {} execute start, sharding context = {}", name, shardingContext);
            process(shardingContext);
            log.info("Job {} execute finish after {}ms", name, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Job " + name + " error occur", e);
            handleException();
        }
    }

    /**
     * 捕获异常（需要处理异常可重载handleException方法）和记录执行日志
     */
    protected void handleException() {

    }

    /**
     * 业务逻辑在这里实现
     */
    protected abstract void process(ShardingContext shardingContext);
}
```
### 5、注册Job
* 注册

启动时从 ApplicationContext 中扫描 scheduler，对所有标注ElasticScheduledJob类进行扫描并进行注册

```
/**
 * 加载所有job, 从 ApplicationContext 中扫描 scheduler
 */
private void initialize(ConfigurableApplicationContext applicationContext) {
    // 扫描注解ElasticScheduledJob的类
    for (Object bean : applicationContext.getBeansWithAnnotation(ElasticScheduledJob.class).values()) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        ElasticScheduledJob annotation = AnnotationUtils.findAnnotation(targetClass, ElasticScheduledJob.class);

        if (annotation != null) {
            // 获取job配置
            JobConfig jobConfig = JobConfig.newSingle(annotation.cron(), (AbsSingleJob) bean);
            JobScheduler scheduler = buildScheduler(jobConfig);
            scheduler.init();// 初始化
            applicationContext.getBeanFactory().registerSingleton(targetClass.getCanonicalName() + "Scheduler", scheduler);
        }
    }
}
```
* 创建任务
```
private JobScheduler buildScheduler(JobConfig jobConfig) {
    return NewJobBuilder.newJobBuilder()
            .setRegCenter(registryCenter)
            .build(jobConfig);
}
```
* 构建JobScheduler
```
public JobScheduler build(JobConfig jobConfig) {
    // 创建job配置
    LiteJobConfiguration jobConfiguration = createJobConfiguration(checkJobConfig(jobConfig));
    // 注册监听
    ElasticJobListener[] l;
    if (listeners != null) {
        l = new ElasticJobListener[listeners.getListeners().size()];
        listeners.getListeners().toArray(l);
    } else {
        l = new ElasticJobListener[0];
    }
    // 不使用作业事件监听器
    if (jobEventConfiguration == null) {
        return jobConfig.getElasticJob() != null
                ? new SpringJobScheduler(jobConfig.getElasticJob(), regCenter, jobConfiguration)
                : new JobScheduler(regCenter, jobConfiguration, l);
    }
    // 使用作业事件监听器
    return jobConfig.getElasticJob() != null
            ? new SpringJobScheduler(jobConfig.getElasticJob(), regCenter, jobConfiguration, jobEventConfiguration)
            : new JobScheduler(regCenter, jobConfiguration, jobEventConfiguration, l);
}
```
### 6、使用
使用方式很简单，直接继承AbsSingleJob，重写相关方法，并类加入@ElasticScheduledJob注解即可。

```
@ElasticScheduledJob(cron = "0/2 * * * * ? * ")
public class CsgoBattleJob extends AbsSingleJob {

    @Override
    protected void process(ShardingContext shardingContext) {
        // 定时任务业务逻辑
    }
```
}

elastic-job-lite二次封装核心内容上面已经介绍完，具体代码可以到[github](https://github.com/fomin-zhu/Elastic-job-spring)查看。

  //原路径
    //http://localhost:8088/swagger-ui.html
    //http://localhost:8088/doc.html
    //http://localhost:8080/foodie-shop/register.html

获取商品子分类使用递归查询

 <!--     MyBatis.xml
          column 对应 SQL (as proId) property对应 vo
          collection 标签：用于定义关联的list集合类型的封装规则
          property：对应三级分类的list属性名
          ofType：集合的类型，三级分类的vo
        -->

BO与业务相关 VO：前端发送请求传入的数据给后端，内部传出去由后端传给前端，拿到数据展示在显示层（表现层view）




为什么存储过程可以提速？
1、存储过程只需要在创建的时候编译就行了，而SQL每次执行都需要被数据库编译
2、存储过程可以降低网络开销

cookie
1、以键值对的形式存储信息在浏览器
2、cookie不能跨域，当前及其父级域名可以取值
3、cookie可以设置有效期
4、cookie可以设置path

session
1、基于服务器内存的缓存（非持久化），可保存请求会话
2、每个session通过sessionid来区分不同请求
3、session可设置过期时间
4、session也是以键值对形式存在的

 AOP通知：
    1. 前置通知：在方法调用之前执行
    2. 后置通知：在方法正常调用之后执行----异常执行最终通知
    3. 环绕通知：在方法调用之前和之后，都分别可以执行的通知
    4. 异常通知：如果在方法调用过程中发生异常，则通知
    5. 最终通知：在方法调用之后执行（finally）

 切面表达式： @Around("execution(* com.hcr.service.impl..*.*(..))")
    execution 代表所要执行的表达式主体
    第一处 * 代表方法返回类型 *代表所有类型
    第二处 包名代表aop监控的类所在的包
    第三处 .. 代表该包以及其子包下的所有类方法
    第四处 * 代表类名，*代表所有类
    第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数

事务：
		1、事务里是一个接口，默认事务为 propagation = Propagation.REQUIRED

		事务传播：
		1、propagation = Propagation.REQUIRED 多用于增删改 当写在方法里时父方法事务会传播给子方法，若子方法报错则全部回滚；若事务放在子方法时，子方法报错则父方法不进行回滚，事务在的子方法全部回滚；
		若父方法子方法都有事务，则全部回滚【使用当前事务，如果当前没有事务，则自己新建一个事务，子方法是必须运行在一个事务中的，如果当前存在事务，则加入这个事务，成为一个整体】
		2、propagation = Propagation.SUPPORTS 多用于查询   不需要回滚 如果当前有事务则使用事务，如果当前没有事务则不使用事务【添加了REUQUIRE事务则使用添加事务，该回滚回滚】
		3、propagation = Propagation.MANDATORY 该传播属性强制必须存在一个事务，如果不存在则抛出异常【子方法使用该事务，父方法没有使用事务，就会抛出异常】
		4、propagation = Propagation.REQUIRE_NEW 如果当前有事务，则挂起事务，并且自己创建一个新事务给自己使用，如果当前没有事务，则同REQUIRED【子方法有该事务，父方法最后语句报错，但是子方法不会回滚，因为该事务会给自己创建一个事务，将父方法事务挂起，父方法最后语句报错，父方法回滚，子不会】
		5、propagation = Propagation.NOT_SUPPORTED  也是用于查询操作  如果当前有事务，则把事务挂起，自己不使用事务去运行数据库操作【父方法有supports事务，子方法有该事务，最后子方法语句报错，但子方法不回滚数据，因为有传播性，被父方法捕捉到，父方法回滚】
		6、propagation = Propagation.NEVER   如果当前有事务，则抛出异常【子方法有该事务，若父方法也是有使用则会抛出异常；若父方法没有事务则父子方法按都没有事务执行数据库】
		7、propagation = Propagation.NESTED  如果当前有事务，则开启子事务（嵌套事务），嵌套事务是独立提交或回滚；如果没有事务（父方法），则同REQUIRED。如果主事务提交会携带子事务一起提交(父方法抛异常子方法跟着回滚）；如果主事务回滚，则子事务会一起回滚，相反，子事务回滚，则父事务可以回滚也可以不回滚（在父事务中将子方法try catch)

		事务管理基于springAOP，面向切面编程，AOP原理 ：JDK   CGLib动态代理

懒加载：淘宝首页分类，加载过后就不会在加载，F12网络可以看到，加载过后清空在点击同一个不会进行加载.....

select * from user limit 1,2
1，表示从第几条数据开始查（默认索引是0，写1，从第二条数据开始查）
2，表示这页显示几条数据

解析XML报文用dom4j工具类

http协议的传输方式有很多种，处于安全考虑，常用的一般都是GET和POST两种，先来介绍下这两种

1）GET：获取资源
GET方法用来请求访问已被URL识别的资源
2）POST：传输实体主体
POST方法用来请求服务器传输信息实体的主体
GET和POST的区别：
从使用场景的角度来说，一般像用户注册登录这种信息都是私密的，采用POST，而针对查询等，为了快速，大多采用GET传输。
其次，大小不同：GET是放在URL首部，因此大小随着浏览器而定，而POST则是在报文中，只要没有具体限制，文件的大小是没限制的，然后，安全性不同：GET采用的是明文传输，而POST是放在报文内部，无法看

redis使用场景
1.    配合关系型数据库做高速缓存 ，缓存高频次访问的数据，降低数据库io， 分布式架构，做session共享

@Transactional注解可以作用于接口、接口方法、类以及类方法上
@Transactional注解的可用参数
readOnly

该属性用于设置当前事务是否为只读事务，设置为true表示只读，false则表示可读写，默认值为false
rollbackFor

该属性用于设置需要进行回滚的异常类数组，当方法中抛出指定异常数组中的异常时，则进行事务回滚。例如：
rollbackForClassName

该属性用于设置需要进行回滚的异常类名称数组，当方法中抛出指定异常名称数组中的异常时，则进行事务回滚。

什么是脏读、幻读、不可重复读？
    脏读 : 一个事务读取到另一事务未提交的更新数据
    不可重复读 : 在同一事务中, 多次读取同一数据返回的结果有所不同, 换句话说, 后续读取可以读到另一事务已提交的更新数据.
相反, "可重复读"在同一事务中多次读取数据时, 能够保证所读数据一样, 也就是后续读取不能读到另一事务已提交的更新数据
    幻读 : 一个事务读到另一个事务已提交的insert数据

Dubbo和SpringCloud有哪些区别?
Dubbo是soa(面向服务的架构),SpringCloud是微服务架构.
Dubbo基于RPC(远程过程调用),SpringCloud是基于Restful,前者底层是tcp连接,后者是http,在大量请求的情况下,dubbo的响应时间要短于springcloud.
Dubbo的提供的功能要少于springcloud,springcloud提供了一整套的微服务治理方案,比如服务熔断,监控,追踪,配置中心等.

当前使用较多的消息队列有RabbitMQ、RocketMQ、ActiveMQ、Kafka、ZeroMQ、MetaMQ等

Linux查看日志定位问题
ps-ef 查看进程
1、定位错误关键字所在行数
cat -n test.log |grep "查找的错误关键字"
2、得到错误关键字所在行号（假设为第500行），查询错误关键字前后100行数据
cat -n test.log |tail -n +400|head -n 200
（表示从第400行开始往后查询200行数据）

sleep方法和wait方法有什么区别?
这个问题常问，sleep方法和wait方法都可以用来放弃CPU一定的时间，不同点在于如果线程持有某个对象的监视器，
sleep方法不会放弃这个对象的监视器，wait方法会放弃这个对象的监视器

Union：对两个结果集进行并集操作，不包括重复行，同时进行默认规则的排序；
Union All：对两个结果集进行并集操作，包括重复行，不进行排序；

<!--
        foreach用于遍历数组元素
        open表示开始符号
        close表示结束符号
        separator表示中间分隔符
        item表示数组参数,属性值可以任意，但提倡与方法参数相同
    -->
    <delete id="dynamicDelete">
        delete from student where id in
        <foreach collection="array" open="(" close=")" separator="," item="ids">
            #{ids}
        </foreach>

liunx查询日志最后五行命令？
Linux如何通过命令查看日志文件的某几行(中间几行或最后几行)
linux 如何显示一个文件的某几行(中间几行)
【一】从第3000行开始，显示1000行。即显示3000~3999行
cat filename | tail -n +3000 | head -n 1000
【二】显示1000行到3000行
cat filename| head -n 3000 | tail -n +1000
*注意两种方法的顺序
分解：
tail -n 1000：显示最后1000行
tail -n +1000：从1000行开始显示，显示1000行以后的
head -n 1000：显示前面1000行

分库分表用只增ID主键有什么影响？
sleep和wait的区别？

java中的sleep()和wait()的区别
对于sleep()方法，我们首先要知道该方法是属于Thread类中的。而wait()方法，则是属于Object类中的。
在调用sleep()方法的过程中，线程不会释放对象锁。而当调用wait()方法的时候，线程会放弃对象锁

相同的bean的注入不同的参数？
用什么修饰符在同包下用？
private 在本类中使用；default在本类和同包内使用；protected 在本类本包和相关子类中使用

char占多少字节？255 效率比varchar 高

class.forName?
Class.forName(xxx.xx.xx) 返回的是一个类
Class.forName(xxx.xx.xx);的作用是要求JVM查找并加载指定的类，也就是说JVM会执行该类的静态代码段

如果单例Bean,是一个无状态Bean,也就是线程中的操作不会对Bean的成员执行查询以外的操作,那么这个单例Bean是线程安全的。”

$一般用入传入数据库对象，比如数据库表名;获取简单类型参数，${}中只能使用value，根据ID查询单条的时候，SQL使用${}来接收值，

用命令 sudo touch myFile.txt，就可以在当前目录下创建myFile.txt文件了。
 tail -100f test.log      实时监控100行日志

            tail  -n  10  test.log   查询日志尾部最后10行的日志;

            tail -n +10 test.log    查询10行之后的所有日志;

1.简单开启事务管理

@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />

“distinct通常不建议使用,效率较低;union all 和union 而言,union all效率更高;原因是:union 相当于多表查询出的数据进行去重然后再进行排序后返回,而union all是多表查询合并去重后就直接返回”

1、服务发现——Netflix Eurek

该系统下还分为Eureka服务端和Eureka客户端，Eureka服务端用作服务注册中心，支持集群部署。Eureka客户端是一个java客户端，用来处理服务注册与发现。

2、客服端负载均衡——Netflix Ribbon

基于Http和Tcp的客户端负载均衡，使得面向REST请求时变换为客户端的负载服务调用，提供客户端的软件负载均衡算法。

3、断路器——Netflix Hystrix

它的作用是保护系统，控制故障范围。

4、服务网关——Netflix Zuul

提供api网关，路由，负载均衡等作用

5、分布式配置——Spring Cloud Config

提供服务端和客户端，服务器存储后端的默认实现使用git

Oracle与MySQL区别：

1、Oracle是大型数据库，而MySQL是中小型数据库。但是MySQL是开源的，但是Oracle是收费的，而且比较贵。

2、Oracle的内存占有量非常大，而mysql非常小

3、MySQL支持主键自增长，指定主键为auto increment，插入时会自动增长。Oracle主键一般使用序列。

4、MySQL字符串可以使用双引号包起来，而Oracle只可以单引号

5、MySQL分页用limit关键字，而Oracle使用rownum字段表明位置，而且只能使用小于，不能使用大于。

6、Oracle在处理长字符串的时候,长度是小于等于4000个字节,如果要插入更长的字符串,考虑用CLOB类型,插入修改记录前要做进行修改和 长度的判断,如果为空,如果长度超出返回操作处理.（CLOB类型是内置类型，它一般都作为某一行中的一列,有些数据库也有别名）

7、MySQL中0、1判断真假，Oracle中true false

8、MySQL中命令默认commit,但是Oracle需要手动提交

9、MySQL在windows环境下大小写不敏感 在unix,linux环境下区分大小写，Oracle不区分

sudo useradd+用户名          sudo用于普通用户使用管理员权限执行某些操作

面试官：接口和抽象类的异同是什么？
猪队友：
相同点：
1、都不能被实例化。
2、接口的实现类和抽象类的子类只有全部实现了接口或者抽象类中的方法后才可以被实例化。
不同点：
1、接口只能定义抽象方法不能实现方法，抽象类既可以定义抽象方法，也可以实现方法。
2、单继承，多实现。接口可以实现多个，只能继承一个抽象类。
3、接口强调的是功能，抽象类强调的是所属关系。
4、接口中的所有成员变量 为public static final， 静态不可修改，当然必须初始化。接口中的所有方法都是public abstract 公开抽象的。而且不能有构造方法。抽象类就比较自由了，和普通的类差不多，可以有抽象方法也可以没有，可以有正常的方法，也可以没有。










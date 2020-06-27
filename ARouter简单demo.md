# Demo
update in 2020-06-27 for ARouter demo

一：什么是路由？
说简单点就是映射页面跳转关系的，当然它也包含跳转相关的一切功能。

二：为什么需要路由
Android系统已经给我们提供了api来做页面跳转，比如startActivity，为什么还需要路由框架呢？我们来简单分析下路由框架存在的意义：App一般都会走向组件化、插件化的道路，而组件化、插件化的前提就是解耦，那么我们首先要做的就是解耦页面之间的依赖关系。简化代码。数行跳转代码精简成一行代码。

三：ARouter 简介
是ARouter是阿里巴巴开源的android平台中对页面、服务提供路由功能的中间件，提倡的是简单且够用。
GitHub：github.com/alibaba/ARo…

四：ARouter 优势(看完源码再终结)
从 ARouter Github 了解到它的优势：
支持直接解析标准URL进行跳转，并自动注入参数到目标页面中
支持多模块工程使用
支持添加多个拦截器，自定义拦截顺序
支持依赖注入，可单独作为依赖注入框架使用
支持InstantRun
支持MultiDex(Google方案)
映射关系按组分类、多级管理，按需初始化
支持用户指定全局降级与局部降级策略
页面、拦截器、服务等组件均自动注册到框架
支持多种方式配置转场动画
支持获取Fragment
完全支持Kotlin以及混编
典型的应用：

从外部URL映射到内部页面，以及参数传递与解析
跨模块页面跳转，模块间解耦
拦截跳转过程，处理登陆、埋点等逻辑
跨模块API调用，通过控制反转来做组件解耦

五：ARouter 配置
主项目Gradle：
apply plugin: 'com.alibaba.arouter'

buildscript {
    ...
    dependencies {
        ...
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "$arouter_group:arouter-register:$arouter_register_version"
        ...
    }
}


Module的Gradle配置：
...
kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}
...
dependencies {
    api "$arouter_group:arouter-api:$arouter_api_version"
    kapt "$arouter_group:arouter-compiler:$arouter_compiler_version"
}
api 的版本和 compiler 的版本号需要用最新的。最新的版本在 Github上可以找到。

六：ARouter 初始化
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ARouter.openLog() // 打印日志
        ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }
}

七：ARouter 注解发起路由
主要的测试demo都在ARouterMainActivity中，MainActivity跳转到ARouterMainActivity如下
    private fun initView() {
        ...
        findViewById<TextView>(R.id.tv_arouter_demo).setOnClickListener {
            ARouter.getInstance().build(PATH_AROUTER_MAIN).navigation()
        }
    }
其中，需要在ARouterMainActivity中注解路径，如下：
@Route(path = PATH_AROUTER_MAIN)
class ARouterMainActivity : AppCompatActivity() {
       ...    
}
PATH_AROUTER_MAIN = "/com/ARouterMainActivity"    
// 这里的路径需要注意的是至少需要有两级，/xx/xx
// 这里的路径需要注意的是至少需要有两级，/xx/xx
code中同样实现了startActivityForResult()功能。

八：监听路由过程
在路由跳转的过程中，我们可以监听路由的过程，只需要使用：
navigation(Context context, NavigationCallback callback)
具体使用如下：
        ARouter.getInstance()
                .build(PATH_AROUTER_DEMO_MAIN)
                .navigation(this, object : NavCallback() {
                    override fun onFound(postcard: Postcard) {
                        Log.d(TAG, "callback: onFound")
                    }

                    override fun onLost(postcard: Postcard) {
                        Log.d(TAG, "callback: onLost")
                    }

                    override fun onArrival(postcard: Postcard) {
                        Log.d(TAG, "callback: onArrival and group is ${postcard.group}")
                    }

                    override fun onInterrupt(postcard: Postcard) {
                        Log.d(TAG, "callback: onInterrupt")
                    }
                })
              
九：发起路由并且传递参数
            ARouter
                .getInstance()
                .build(PATH_AROUTER_DEMO_MAIN)
                .withInt(AGE, 18)
                .withString(NAME, "Kaka")
                .navigation()
ARouter 提供了丰富大量的参数类型，供我们选择。
//基础类型
.withString( String key, String value )
.withBoolean( String key, boolean value)
.withChar( String key, char value )
.withShort( String key, short value)
.withInt( String key, int value)
.withLong( String key, long value)
.withDouble( String key, double value)
.withByte( String key, byte value)
.withFloat( String key, float value)
.withCharSequence( String key,  CharSequence value)

//数组类型
.withParcelableArrayList( String key, ArrayList<? extends Parcelable > value)
.withStringArrayList( String key,  ArrayList<String> value)
.withIntegerArrayList( String key, ArrayList<Integer> value)
.withSparseParcelableArray( String key, SparseArray<? extends Parcelable> value)
.withCharSequenceArrayList( String key, ArrayList<CharSequence> value)
.withShortArray( String key,  short[] value)
.withCharArray( String key, char[] value)
.withFloatArray( String key, float[] value)
.withCharSequenceArray( String key,  CharSequence[] value)

//Bundle 类型
.with( Bundle bundle )

//Activity 跳转动画
.withTransition(int enterAnim, int exitAnim)

//其他类型
.withParcelable( String key, Parcelable value)
.withParcelableArray( String key,  Parcelable[] value)
.withSerializable( String key, Serializable value)
.withByteArray( String key, byte[] value)
.withTransition(int enterAnim, int exitAnim)

十：Fragment路由
创建 Fragment类，并且添加路由注解
@Route(path = PATH_AROUTER_DEMO_FRAG)
class ARouterDemoFragment: Fragment() {
    ...
}
获取 Fragment实例
val frag = ARouter.getInstance().build(PATH_AROUTER_DEMO_FRAG).navigation()
if (frag is Fragment) {
    val fragmentManager: FragmentManager = supportFragmentManager
    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(R.id.root_container, frag)
    fragmentTransaction.commit()
}

等等...

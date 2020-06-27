# Demo
update in 2020-06-27
just want to analysis Arouter source, so add some demo for useage according to the wiki from https://github.com/alibaba/ARouter

一：什么是路由？
说简单点就是映射页面跳转关系的，当然它也包含跳转相关的一切功能。

二：为什么需要路由
Android系统已经给我们提供了api来做页面跳转，比如startActivity，为什么还需要路由框架呢？我们来简单分析下路由框架存在的意义：

在一些复杂的业务场景下（比如电商），灵活性比较强，很多功能都是运营人员动态配置的，比如下发一个活动页面，我们事先并不知道具体的目标页面，但如果事先做了约定，提前做好页面映射，便可以自由配置。

随着业务量的增长，客户端必然随之膨胀，开发人员的工作量越来越大，比如64K问题，比如协作开发问题。App一般都会走向组件化、插件化的道路，而组件化、插件化的前提就是解耦，那么我们首先要做的就是解耦页面之间的依赖关系。

简化代码。数行跳转代码精简成一行代码。

其他…

三：ARouter 简介
是ARouter是阿里巴巴开源的android平台中对页面、服务提供路由功能的中间件，提倡的是简单且够用。

GitHub：github.com/alibaba/ARo…

四：ARouter 优势
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
android {
    defaultConfig {
    ...
    javaCompileOptions {
        annotationProcessorOptions {
        arguments = [ moduleName : project.getName() ]
        }
    }
    }
}

dependencies {
    compile 'com.alibaba:arouter-api:1.2.1.1'
    annotationProcessor 'com.alibaba:arouter-compiler:1.1.2.1'
}

api 的版本和 compiler 的版本号需要用最新的。最新的版本在 Github上可以找到。

这里写图片描述

六：ARouter 初始化
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ARouter.openLog();     // 打印日志
        ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init( this ); // 尽可能早，推荐在Application中初始化
    }
}
七：ARouter 注解发起路由
新建一个 Activity1 作为测试 ，在 Activity1 添加注解的代码如下：

package com.router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx

@Route(path = "/com/Activity1")
public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
    }
}
在 MainActivity 的布局中添加一个按钮

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.router.MainActivity">

    <TextView
        android:id="@+id/bt1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="应用内跳转"
        android:gravity="center"
        android:padding="10dp"
        android:background="#666666"
        />

</LinearLayout>
MainActivity 里面的跳转逻辑是：

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById( R.id.bt1).setOnClickListener( this );

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.bt1 :
                //发起路由跳转
                ARouter.getInstance().build("/com/Activity1").navigation();
                break;
        }
    }
}
效果图如下：

这里写图片描述

如果你想实现像 startActivityForResult（） 功能，可以这样使用：

navigation(Activity mContext, int requestCode)
具体使用如下：

ARouter.getInstance()
       .build("/com/Activity1")
       .navigation( this , 100 );
如果 Path 路径不正确会发生什么，我们来测试一下，现在我们把路由操作改为

 ARouter.getInstance().build("/com/Test").navigation();
效果如下：

这里写图片描述

可以看到有一个 Toast 提示我们找不到 “/com/Test” 路径，我们来看看源码

这里写图片描述

通过源码我们可以看到在 debug 状态下，当找不到路由路径目标是，会有 Toast 提示。

八：监听路由过程
在路由跳转的过程中，我们可以监听路由的过程，只需要使用：

navigation(Context context, NavigationCallback callback)
NavigationCallback 的源码如下：

public interface NavigationCallback {

    /**
     * Callback when find the destination.
     * 找到了
     * @param postcard meta
     */
    void onFound(Postcard postcard);

    /**
     * Callback after lose your way.
     * 找不到了
     * @param postcard meta
     */
    void onLost(Postcard postcard);

    /**
     * Callback after navigation.
     * 跳转完了
     * @param postcard meta
     */
    void onArrival(Postcard postcard);

    /**
     * Callback on interrupt.
     * 被拦截了
     * @param postcard meta
     */
    void onInterrupt(Postcard postcard);
}

具体使用如下

ARouter.getInstance()
       .build("/com/Activity1")
       .navigation(this, new NavCallback() {

             @Override
              public void onFound(Postcard postcard) {
                   Log.e("zhao", "onArrival: 找到了 ");
              }

             @Override
              public void onLost(Postcard postcard) {
                   Log.e("zhao", "onArrival: 找不到了 ");
              }

             @Override
              public void onArrival(Postcard postcard) {
                   Log.e("zhao", "onArrival: 跳转完了 ");
              }

             @Override
              public void onInterrupt(Postcard postcard) {
                    Log.e("zhao", "onArrival: 被拦截了 ");
              }
              });                
九：发起路由并且传递参数
ARouter.getInstance()
       .build("/com/Activity1")
       .withString( "key" , "123")  //参数：键：key 值：123
       .navigation();
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
十：路由分组
在前面我们讲到在对 Activity1 做注解的时候，用到了

@Route(path = "/com/Activity1")
public class Activity1 extends AppCompatActivity {

}
在 path 这个字符串里面，”com” 就代表组的标识；“Activity1” 代表是 Activity1 类的具体表示。组的标识和类的标识都可以自己定义的，需要记住的是组标识和类标识之间用斜杠来区分 ”\” .

什么是组？

这里就需要提下，ARouter框架是分组管理，按需加载。提起来很高深的样子呢！其实解释起来就是，在编译期框架扫描了所有的注册页面／服务／字段／拦截器等，那么很明显运行期不可能一股脑全部加载进来，这样就太不和谐了。所以就分组来管理，ARouter在初始化的时候只会一次性地加载所有的root结点，而不会加载任何一个Group结点，这样就会极大地降低初始化时加载结点的数量。比如某些Activity分成一组，组名就叫test，然后在第一次需要加载组内的某个页面时再将test这个组加载进来。

测试一下：

ARouter.getInstance()
       .build("/com/Activity1")
       .navigation(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                String group = postcard.getGroup();
                Log.e("zhao", "分组是: " + group);
            }
        });
结果是

07-27 17:32:17.880 19449-19449/com.router E/zhao: 分组是: com
ARouter 默认情况下的分组就是第一个 / / 之间的内容。

自定义分组：
创建 CustomGroupActivity 并且添加 注解，并且指定路由分组。自定义分组的就是在原来的注解上添加 group 字段， 如下所示。

@Route(path = "/com/CustomGroupActivity" , group = "customGroup")
public class CustomGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_group);
    }
}
自定义分组，发起路由：第二个参数就是路由的分组

build(String path, String group)
具体实现如下所示：

 ARouter.getInstance().build("/com/CustomGroupActivity", "customGroup").navigation();
十一：Fragment 路由
创建 Fragment 类，并且添加路由注解

@Route(path = "/com/TestFragment")
public class TestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view ;
    }
}
获取 Fragment 实例

Fragment fragment = (Fragment) ARouter.getInstance().build( "/com/TestFragment" ).navigation();
十二：URL 跳转
web url 跳转流程图

这里写图片描述

创建URL 中间跳转页
创建 URLReceiveActivity

/**
 * URL 中转Activity
 */
public class URLReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_url_receive );

        //对URI 数据分发
        Uri uri = getIntent().getData();
        ARouter.getInstance().build(uri).navigation(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                finish();
            }
        });
    }
}
URLReceiveActivity 添加注册

<activity android:name=".URLReceiveActivity">
      <!-- Schame -->
      <intent-filter>
            <data
                android:host="zhaoyanjun"
                android:scheme="arouter" />

          <action android:name="android.intent.action.VIEW" />
          <category android:name="android.intent.category.DEFAULT" />
          <category android:name="android.intent.category.BROWSABLE" />

       </intent-filter>

</activity>
这里面的 host 、scheme 字段很重要。点击 url 会根据这两个字段会调起本地的 Activity 。

下面是一段 HTML 片段

<h2>1:URL普通跳转</h2>

<p><a href="arouter://zhaoyanjun/com/URLActivity1">arouter://zhaoyanjun/com/URLActivity1 </a>
</p>

<h2>2:URL普通跳转携带参数</h2>

<p>
<a href="arouter://zhaoyanjun/com/URLActivity2?name=alex&age=18&boy=true&high=180&obj=%7b%22name%22%3a%22jack%22%2c%22id%22%3a666%7d">arouter://zhaoyanjun/test/URLActivity2?name=alex&age=18&boy=true&high=180&obj={"name":"jack","id":"666"}
</a>
</p>

注意 a 标签里面的 arouter://zhaoyanjun 分别代表着 scheme 、host ；/com/URLActivity1 就是目标 Activity 的注解。

如果需要接收 URL 中的参数，需要在 Activity 调用自动注入初始化方法；

ARouter.getInstance().inject(this);
需要注意的是，如果不使用自动注入，那么可以不写 ARouter.getInstance().inject(this)，但是需要取值的字段仍然需要标上 @Autowired 注解，因为 只有标上注解之后，ARouter才能知道以哪一种数据类型提取URL中的参数并放入Intent中，这样您才能在intent中获取到对应的参数

具体的代码如下：

@Route(path = "/com/URLActivity2")
public class URLActivity2 extends AppCompatActivity{

    private TextView textView;

    @Autowired
    String name;

    @Autowired
    int age;

    @Autowired
    boolean boy;

    @Autowired
    int high;

    @Autowired
    String obj ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_url2);

        textView = (TextView) findViewById(R.id.tv);

        //解析参数
        Bundle bundle = getIntent().getExtras();
        String name1 = bundle.getString("name");

        textView.setText("参数是： " + "name: " + name + "  age: " + age
                + " boy: " + boy + " name1: " + name1 + " obj: " + obj.toString() );
    }
}

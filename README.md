## 关于 LiveData 粘性事件 的个人思考

### 1. 问题描述
&emsp; 1.1. LiveData特性：
 &emsp; &emsp; Google官方文档中描述，设备横竖屏切换的时候，界面销毁重建，但是Activity生命周期并未结束，旋转后新建的空页面上数据需要重新填充，所以LiveData在被再次观察时会立即推送数据更新。
 &emsp; &emsp; &emsp; Google文档
 ![Google文档](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d47f847641974d408c92f720a803bced~tplv-k3u1fbpfcp-zoom-1.image)
 &emsp; &emsp; &emsp; ViewModel生命周期
 ![ViewModel生命周期](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3e99ca92d99148f5a4779787911702f5~tplv-k3u1fbpfcp-zoom-1.image)
 &emsp; &emsp; &emsp; 未使用LiveData效果演示
 ![未使用LiveData效果演示](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d75bd07c542e4b3a9665f95fe1907de6~tplv-k3u1fbpfcp-zoom-1.image)
 &emsp; &emsp; &emsp; 使用LiveData效果演示
 ![使用LiveData效果演示](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/be89869f9d7d4ea884e3ef2d0b6b289d~tplv-k3u1fbpfcp-zoom-1.image)

&emsp; 1.2.	源码分析：
 &emsp; &emsp; LiveData关于此特性的源码分析，有兴趣的可以看看下面的文章：
 &emsp; &emsp; [Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus](https://tech.meituan.com/2018/07/26/android-livedatabus.html)
 &emsp; &emsp; [UnPeekLiveData](https://github.com/KunMinX/UnPeek-LiveData)

 &emsp; 1.3.	如果多个界面共用一个ViewModel（其生命周期大于绑定的所有界面），并且复用了同一个LiveData数据源，LiveData数据源在前一个界面被更新过，那么下一个界面在刚刚绑定观察该LiveData时，就会被通知数据发生变化，LiveData最新值会立即推送过来，在一部分场景下可能显得多此一举，或者引发错误。

### 2.	个人思考总结
 &emsp; 2.1.	如果没有多个界面共用同一个ViewModel的需求，或者经常需要用到粘性特性，则无需特殊处理。

 &emsp; 2.2.	如果不想使用LiveData的粘性特性，需要自己写一个LiveData继承类来代替MutableLiveData，通过反射或者逻辑判断来规避掉粘性特性（细节可以看上面的源码分析文章）。我自己也写了个很简洁的LiveData实现类，通过判断数据源有无被更改过，控制是否将订阅后的第一次推送通知到界面。代码如下：

```
public class CleanLiveData<T> extends LiveData<T> {
    private boolean hasModified = false;

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            private boolean hasIntercept = false;

            @Override
            public void onChanged(T t) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t);
                }
                hasIntercept = true;
            }
        });
    }

    @Override
    public void observeForever(@NonNull final Observer<? super T> observer) {
        super.observeForever(new Observer<T>() {
            private boolean hasIntercept = false;

            @Override
            public void onChanged(T t) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t);
                }
                hasIntercept = true;
            }
        });
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        hasModified = true;
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
        hasModified = true;
    }
}
```
 &emsp; 2.3.	Demo项目地址：
[LiveDataTest](https://github.com/mao720/LiveDataTest.git)


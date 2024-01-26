# NumIn
## 模仿微信面对面建群4位码输入框
<br>![image](https://github.com/zrw269113179/NumIn/blob/master/show.gif)
<br>  内置了7个属性，分别为`inputCount`(输入数字个数)、`textColor`(字体颜色)、`textSize`(字体大小)、`backgroundColor`（背景颜色）、
`isPassword`(是否为密码输入)、`emptyColor`(未输入时的符号颜色)、`emptyType`(未输入时的符号类型，共有三种类型——Line、Circle、Rectangle)。
所有属性都可以在代码中动态改变。
<br>  内置一个监听器`InputCallBack`,需要实现2个方法
```java
//用户输入完毕调用此函数，输入参数str为用户输入的数值
public void onInputFinish(String str)
//用户每次输入调用此函数，输入参数inputNumber为用户此次输入的数值
public void onTextChanged(int inputNumber)
```
<br>通过调用`NumIn.setInputCallback`设置监听器
## 具体使用方法
<br> 只需要复制工程中的`values`下的`attrs.mxl`文件和`NumIn`类到自己的工程中就能使用。使用自定义属性时先声明命名空间
```java
xmlns:numin="http://schemas.android.com/apk/res-auto"
```
<br>然后就可以通过
```java
numin:inputCount
```
<br>的方式定义属性。
## 示例

```xml
<com.zrw.numin.NumIn
        android:id="@+id/numin"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="8dp"
        numin:inputCount="10"
        numin:textSize="24sp"
        numin:backgroundColor="@color/colorPrimary"
        numin:emptyType="line"/>
```
<br>

```java
        numIn = (NumIn)findViewById(R.id.numin);
        numIn.setInputCallback(new NumIn.InputCallBack() {
            @Override
            public void onInputFinish(String str) {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onTextChanged(int inputNumber) {
            }
        });
```

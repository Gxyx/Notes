[toc]

## 1、JVM体系结构

JVM 是运行在操作系统之上的，它与硬件没有直接交互

![image](https://user-images.githubusercontent.com/50070756/125548156-e893b0a3-c8d7-45d5-a313-3b7e8638cf22.png)

### 作用

Java虚拟机就是二进制字节码的运行环境，负责装载字节码到其内部，解释/编译为对应平台上的机器指令执行。每一条Java指令，Java虚拟机规范中都编译为对应平台上的机器指令执行.每一条java指令，java虚拟机规范中都有详细定义，如怎么取操作数，怎么处理操作数，处理结果放在哪里。

### 特点

* 一次编译，到处运行
* 自动内存管理
* 自动垃圾回收功能

![image](https://user-images.githubusercontent.com/50070756/125548265-49deedee-d041-48fd-a7bc-c2889136b785.png)

### JVM生命周期

* 虚拟机启动
  
* Java虚拟机的启动是通过引导类加载器(bootstrap class loader)创建一个初始类(initial class)来完成的，这个类是由虚拟机的具体实现指定的。
  
* 虚拟机的执行
  * 一个运行中的Java虚拟机有着一个清晰的任务:执行Java程序。
  * 程序开始执行时他才运行，程序结束时他就停止。
  * 执行一个所谓的Java程序的时候，真真正正在执行的是一个叫做Java虚拟的进程。

*  虚拟机的退出

  * 程序正常执行结束 

  * 程序在执行过程中遇到了异常或错误而异常终止

  * 由于操作系统出现错误而导致Java虚拟机进程终止

  * 某线程调用Runtime类或system类的exit方法，或Runtime类的halt

    并且Java安全管理器也允许这次exit或halt操作。

  * 除此之外，JNI ( Java Native Interface)规范描述了用JNI

    Invocation API来加载或卸载Java虚拟机，Java虚拟机的退出情况。

## 2、类装载器 ClassLoader

类加载器子系统负责从文件系统或者网络中加载class文件，class文件在文件开头有特定的文件标识，ClassLoader只负责class文件的加载，至于它是否可以运行，则由ExecutionEngine决定。

`Car.class`字节码文件被`ClassLoader`类装载器加载并初始化，在方法区中生成了一个`Car Class`的类模板，而我们平时所用到的实例化，就是在这个类模板的基础上，形成了一个个实例，即`car1`，`car2`。反过来讲，我们可以对某个具体的实例进行`getClass()`操作，就可以得到该实例的类模板，即`Car Class`。再接着，我们对这个类模板进行`getClassLoader()`操作，就可以得到这个类模板是由哪个类装载器进行加载的。

![image](https://user-images.githubusercontent.com/50070756/125548311-6625eeff-e1c0-4523-b482-e111bb6327c5.png)

```
Tip：扩展一下，JVM并不仅仅只是通过检查文件后缀名是否是.class来判断是否加载，最主要的是通过class文件中特定的文件标示，xxx.class文件中的cafe babe。
```

![image](https://user-images.githubusercontent.com/50070756/125548350-cd7f9c64-3551-4a01-8d1e-5fae4b03322e.png)

![image](https://user-images.githubusercontent.com/50070756/125548328-51a5adad-d25a-42f0-8cd2-3ff2332c8166.png)



加载:

1.通过一个类的全限定名获取定义此类的二进制字节流

2.将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构

3.内存中生成一个代表这个类的java.lang.Class对象，作为方法区这个类的各种数据的访问入口。

链接 

​	验证(Verify)：

​		目的在于确保class文件的字节流中包含信息符合当前虚拟机要求，保证被加载类的正确性，不会危害虚拟机自身安全。

​		主要包括四种验证，文件格式验证，元数据验证，字节码验证，符号引用验证。

​	准备(Prepare):

​		为类变量分配内存并且设置该类变量的默认初始值，即零值。

​		这里不包含用final修饰的static，因为final在编译的时候就会分配了，准备阶段会显式初始化;

​		这里不会为实例变量分配初始化，类变量会配在方法区中，而实例变量是会随着对象一起分配到Java堆中。

​    解析(Parse):

​		 Java虚拟机将常量池内的符号引用替换为直接引用的过程。

初始化：

​		初始化阶段就是执行类构造器方法<clinit>()的过程。

​		此方法不需定义，是javac编译器白动收集类中的所有类变量的赋值动作和静态代码块中的语句合并而来。

​		构造器方法中指令按语句在源文件中出现的顺序执行。

​		<clinit>()不同于类的构造器。(关联:构造器是虚拟机视角下的<init> ())

​		若该类具有父类，JVM会保证子类的<clinit>()执行前，父类的<clinit>()已经执行完毕。

​		虚拟机必须保证一个类的<clinit>()方法在多线程下被同步加锁。 

###  	2.1、类装载器种类

#### 虚拟机自带的加载器

* 启动类加载器（Bootstrap），也叫根加载器，加载`%JAVAHOME%/jre/lib/rt.jar`。
* 扩展类加载器（Extension），加载`%JAVAHOME%/jre/lib/ext/*.jar`，例如`javax.swing`包。
* 应用程序类加载器（AppClassLoader），也叫系统类加载器，加载`%CLASSPATH%`的所有类。

#### 自定义类加载器(User-Defined ClassLoader)

* 开发人员可以通过继承抽象类java. lang.ClassLoader类的方式，实现自己的类加载器

* 步骤

  * 开发人员可以通过继承抽象类java. lang.ClassLoader类的方式，实现自己的类加载器，以满足"一些特殊的需求“
  * 在JDK1.2之前，在自定义类加载器时，总会去继承ClassLoader类并重写loadClass()方法，从而实现自定义的类加载类，但是在JDK1.2之后已不再建议用户去覆盖loadClass()方法，而是建议把自定义的类加载逻辑写在findClass()方法中
  * 在编写自定义类加载器时，如果没有太过于复杂的需求，可以直接继承URLClassLoader类，这样就可以避免自己去编写findClass()方法及其获取字节码流的方式，使自定义类加载器编写更加简洁。

![image](https://user-images.githubusercontent.com/50070756/125548396-eddd009c-dcd1-4e96-9b21-a8d2a8151468.png)

### 	 2.1、双亲委派

#### 双亲委派

​		如果一个类加载器收到了加载某个类的请求,则该类加载器并不会去加载该类,而是把这个请求委派给父类加载器,每一个层次的类加载器都是如此,因此所有的类加载请求最终都会传送到顶端的启动类加载器;只有当父类加载器在其搜索范围内无法找到所需的类,并将该结果反馈给子类加载器,子类加载器会尝试去自己加载。

#### 沙箱安全

​		是基于双亲委派机制上采取的一种JVM的自我保护机制，假设你要写一个`java.lang.String`的类，由于双亲委派机制的原理，此请求会先交给`BootStrapClassLoader`试图进行加载，但是`BootStrapClassLoader`在加载类时首先通过包和类名查找`rt.jar`中有没有该类，有则优先加载`rt.jar`包中的类，**因此就保证了java的运行机制不会被破坏，确保你的代码不会污染到Java的源码**。

#### 类加载器的加载顺序

​	当AppClassLoader加载一个class时，它首先不会自己去尝试加载这个类，而是把类加载请求委派给父类加载器ExtClassLoader去完成。
​	当ExtClassLoader加载一个class时，它首先也不会自己去尝试加载这个类，而是把类加载请求委派给BootStrapClassLoader去完成。

​	如果BootStrapClassLoader加载失败（例如在$JAVA_HOME/jre/lib里未查找到该class），会使用ExtClassLoader来尝试加载。

​	若ExtClassLoader也加载失败，则会使用AppClassLoader来加载，如果AppClassLoader也加载失败，则会报出异常ClassNotFoundException。

![image](https://user-images.githubusercontent.com/50070756/125548424-a89f2d80-73f3-4a24-be65-9990af3b0e49.png)

#### 破坏双亲委派

​		因为在某些情况下父类加载器需要委托子类加载器去加载class文件。受到加载范围的限制，父类加载器无法加载到需要的文件，以Driver接口为例，由于Driver接口定义在jdk当中的，而其实现由各个数据库的服务商来提供，比如mysql的就写了`MySQL Connector`，那么问题就来了，DriverManager（也由jdk提供）要加载各个实现了Driver接口的实现类，然后进行管理，但是DriverManager由启动类加载器加载，只能记载JAVA_HOME的lib下文件，而其实现是由服务商提供的，由系统类加载器加载，这个时候就需要启动类加载器来委托子类来加载Driver实现，从而破坏了双亲委派。

[博客]: https://www.cnblogs.com/joemsu/p/9310226.html

## 3、运行时数据区

​	Java虚拟机在执行Java程序的过程中会把它所管理的内存划分为若干个不同的数据区域。这些区域有各自的用途，以及创建和销毁的时间，有的区域随着虚拟机进程的启动而一直存在，有些区域则是依赖用户线程的启动而建立和销毁。

### 3.1、程序计数器 Program Counter Register

**程序计数器（Program Counter Register）**，也叫PC寄存器。每个线程启动的时候，都会创建一个PC寄存器。PC寄存器里保存当前正在执行的JVM指令的地址。 每一个线程都有它自己的PC寄存器，也是该线程启动时创建的。

简单来说，PC寄存器就是保存下一条将要执行的指令地址的寄存器，其内容总是指向下一条将被执行指令的地址，这里的地址可以是一个本地指针，也可以是在方法区中相对应于该方法起始指令的偏移量。

![image](https://user-images.githubusercontent.com/50070756/125548453-60b298b2-3a4b-4992-9a28-f6ebef0dc3db.png)

每个线程都有一个程序计数器，是==线程私有==的,就是一个指针，指向方法区中的方法字节码（用来存储指向下一条指令的地址,也即将要执行的指令代码），由执行引擎`Execution Engine`读取下一条指令，是一个非常小的内存空间，几乎可以忽略不记。

这块内存区域很小，它是当前线程所执行的字节码的行号指示器，字节码解释器通过改变这个计数器的值来选取下一条需要执行的字节码指令。

PC寄存器一般用以完成分支、循环、跳转、异常处理、线程恢复等基础功能。不会发生内存溢出（OutOfMemory，OOM）错误。

![image](https://user-images.githubusercontent.com/50070756/125548466-809e787b-8ea0-4ec2-b6b8-d06f55744c40.png)

```
如果线程执行正在执行的是一个Java方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是本地方法（Native），这个计数器值则会为空。

PC寄存器一般用以完成分支、循环、跳转、异常处理、线程恢复等基础功能。不会发生内存溢出（OutOfMemory，OOM）错误
```

**pc寄存器为什么是私有的 **

* 我们都知道所谓的多线程在一个特定的时间段内只会执行其中某一个线程的方法，CPU会不停地做任务切换，这样必然导致经常中断或恢复，如何保证分毫无差呢？==为了能够准确地记录各个线程正在执行的当前字节码指令地址，最好的办法自然是为每一个线程都分配一个PC寄存器，这样一来各个线程之间便可以进行独立计算，从而不会出现相互干扰的情况。==

* 由于CPU时间片轮限制，众多线程在并发执行过程中，任何一个确定的时刻，一个处理器或者多核处理器中的一个内核，只会执行某个线程中的一条指令。

* 这样必然导致经常中断或恢复，如何保证分毫无差呢？每个线程在创建后，都会产生自己的程序计数器和栈帧，程序计数器在各个线程之间互不影响。

**CPU时间片**

* CPU 时间片即CPU分配给各个程序的时间，每个线程被分配一个时间段，称作它的时间片。

* 在宏观上：我们可以同时打开多个应用程序，每个程序并行不悖，同时运行。

* 但在微观上：由于只有一个CPU，一次只能处理程序要求的一部分，如何处理公平，一种方法就是引入时间片，每个程序轮流执行。![image](https://user-images.githubusercontent.com/50070756/125548489-a9e313ee-03e3-49c4-bd23-df6c7d65829e.png)

### 3.2、本地方法接口

#### 本地方法

​	一个 Native Method 是一个 Java 调用非 Java 代码的接囗，其作用就是融合不同的编程语言为 Java 所用，它的初衷是用来融合 C/C++ 程序的，Java 诞生的时候是 C/C++ 流行时期，要想立足，就得调用 C/C++ 程序。同时 Java 还在内存中专门开辟了一块区域处理标记为 native 的代码（也就是 Native Method Stack）。

#### 使用本地方法 

##### 	与 Java 环境的交互

* 有时Java应用需要与Java外面的环境交互，这是本地方法存在的主要原因。你可以想想Java需要与一些底层系统，如操作系统或某些硬件交换信息时的情况。本地方法正是这样一种交流机制：它为我们提供了一个非常简洁的接口，而且我们无需去了解Java应用之外的繁琐的细节。

##### 	与操作系统的交互

* JVM支持着Java语言本身和运行时库，它是Java程序赖以生存的平台，它由一个解释器（解释字节码）和一些连接到本地代码的库组成。然而不管怎样，它毕竟不是一个完整的系统，它经常依赖于一底层系统的支持。这些底层系统常常是强大的操作系统。通过使用本地方法，我们得以用Java实现了jre的与底层系统的交互，甚至JVM的一些部分就是用c写的。还有，如果我们要使用一些Java语言本身没有提供封装的操作系统的特性时，我们也需要使用本地方法。

### 3.3、本地方法栈 Native Method Stack

​	Java 栈是属于线程私有，它的声明周期与线程同步，Java 方法被在调用时，入Java 栈成为一个一个栈帧，即 Java 虚拟机栈用于管理 Java 方法的调用，类似，**本地方法栈用于管理本地方法的调用**。

**本地方法栈，也是线程私有的。**

允许被实现成固定或者是可动态扩展的内存大小。（在内存溢出方面是相同的）

- 如果线程请求分配的栈容量超过本地方法栈允许的最大容量，Java虚拟机将会抛出一个`stackoverflowError`异常。
- 如果本地方法栈可以动态扩展，并且在尝试扩展的时候无法申请到足够的内存，或者在创建新的线程时没有足够的内存去创建对应的本地方法栈，那么 Java 虚拟机将会抛出一个`outofMemoryError`异常。

**本地方法栈（Native Method Stack）**，就是在一个 Stack 中登记这些 native 方法，然后在执行引擎`Execution Engine`执行时加载本地方法库`native libraies`。

### 3.4、方法区 Method Area

​	方法区与Java堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类型信息、常量、静态变量。即时编译器编译后的代码缓存等数据。

方法区主要存放的是 **Class**，而堆中主要存放的是 **实例化的对象**

![image](https://user-images.githubusercontent.com/50070756/125548519-c1bfefb5-8cd8-401f-b430-81b8893171d1.png)

- 黄色部分：线程共享
- 灰色部分：线程私有

### 3.5、虚拟机栈 VM Stack

==栈是运行时的单位，而堆是存储的单位==

- 栈解决程序的运行问题，即程序如何执行，或者说如何处理数据。
- 堆解决的是数据存储的问题，即数据怎么放，放哪里

**Java 虚拟机栈（Java Virtual Machine Stack）**，早期也叫 Java 栈。每个线程在创建时都会创建一个虚拟机栈，其内部保存一个个的栈帧（Stack Frame），对应着一次次的 Java 方法调用。

#### 栈帧

**栈帧**：每个方法执行的同时都会创建一个栈帧，用于存储局部变量表、操作数栈、动态链接、方法出口等信息，每个方法从调用直至执行完毕的过程，就对应着一个栈帧在虚拟机中入栈到出栈的过程。

**栈帧**对应一个方法的执行和结束，是方法执行过程的内存模型。

栈帧主要保持了3类数据：

1. **本地变量（Local Variables）**：输入参数和输出参数，以及方法内的变量。
2. **栈操作（Operand Stack）**：记录出栈、入栈的操作。
3. **栈帧数据（Frame Data）**：包括类文件、方法等。

![image](https://user-images.githubusercontent.com/50070756/125548542-397feac5-9fbb-495c-86af-b312f91e7bc9.png)

#### 栈的运行原理

栈的顺序为：`main()`入栈 --> `test()`入栈 --> `test()`出栈 --> `main()`出栈。

![image](https://user-images.githubusercontent.com/50070756/125548562-1eb8cf18-fa57-465b-a49b-0e88866daf49.png)

在图中一个栈中有两个栈帧，分别是`Stack Frame1`和`Stack Frame2`，对应方法1和方法2。其中`Stack Frame2`是最先被调用的方法2，所以它先入栈。然后方法2又调用了方法1，所以`Stack Frame1`处于栈顶位置。执行完毕后，依次弹出`Stack Frame1`和`Stack Frame2`，然后线程结束，栈释放。

所以，每执行一个方法都会产生一个栈帧，并保存到栈的顶部，顶部的栈帧就是当前所执行的方法，该方法执行完毕后会自动出栈。

![image](https://user-images.githubusercontent.com/50070756/125548578-c608e8b1-5a18-48bb-964c-7259010adfa5.png)

#### 生命周期

生命周期和线程一致，也就是线程结束了，该虚拟机栈也销毁了。

#### 作用

主管 Java 程序的运行，它保存方法的局部变量、部分结果，并参与方法的调用和返回。

#### 栈的特点

栈是一种快速有效的分配存储方式，JVM直接对Java栈的操作只有两个：

- 每个方法执行，伴随着进 栈 \color{red}{进栈}进栈（入栈、压栈）
- 执行结束后的出 栈 \color{red}{出栈}出栈工作

同时，对于栈来说不存在垃圾回收问题（栈存在溢出的情况）。

#### 异常

​	栈是一个内存块，它是有大小长度的，而我们观察代码发现，只要代码一运行，`test()`方法就会一直进行入栈操作，而没有出栈操作，结果肯定会超出栈的大小，进而造成栈溢出错误，即`java.lang.StackOverflowError`。

![image](https://user-images.githubusercontent.com/50070756/125548594-ac0179b4-6fb3-4c2e-a265-e04631f7099a.png)

==java.lang.StackOverflowError 是错误，不是异常==

![image](https://user-images.githubusercontent.com/50070756/125548604-f0e03f0f-d284-4cc8-9d3a-5b246242d297.png)

Java 虚拟机规范允许 Java 栈的大小是动态的或者是固定不变的。

* 如果采用固定大小的 Java 虚拟机栈，那每一个线程的Java虚拟机栈容量可以在线程创建的时候独立选定。如果线程请求分配的栈容量超过 Java 虚拟机栈允许的最大容量，Java虚拟机将会抛出一个`StackoverflowError`异常。

* 如果 Java 虚拟机栈可以动态扩展，并且在尝试扩展的时候无法申请到足够的内存，或者在创建新的线程时没有足够的内存去创建对应的虚拟机栈，那Java虚拟机将会抛出一个 `outofMemoryError`异常。

#### 设置栈内存大小

调整JVM初始化参数（-Xms 和 -Xmx最好调整一致，防止JVM频繁进行收集和回收）

-Xms：初始堆空间：设置初始分配大小，默认为物理内存的"1/64"

-Xmx：堆最大值，默认为物理内存的“1/4”

-Xss：栈空间

#### 相关面试题

**1、分配的栈内存越大越好么**

* 不是，一定时间内降低了OOM概率，但是会挤占其它的线程空间，因为整个空间是有限的。

**2、什么是线程安全**

* 如果只有一个线程才可以操作此数据，则必是线程安全的
* 如果有多个线程操作，则此数据是共享数据，如果不考虑共享机制，则为线程不安全

```java
/**
 * 面试题
 * 方法中定义局部变量是否线程安全？具体情况具体分析
 * 何为线程安全？
 *    如果只有一个线程才可以操作此数据，则必是线程安全的
 *    如果有多个线程操作，则此数据是共享数据，如果不考虑共享机制，则为线程不安全
 */
public class StringBuilderTest {

    // s1的声明方式是线程安全的
    public static void method01() {
        // 线程内部创建的，属于局部变量
        StringBuilder s1 = new StringBuilder();
        s1.append("a");
        s1.append("b");
    }

    // 这个也是线程不安全的，因为有返回值，有可能被其它的程序所调用
    public static StringBuilder method04() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a");
        stringBuilder.append("b");
        return stringBuilder;
    }

    // stringBuilder 是线程不安全的，操作的是共享数据
    public static void method02(StringBuilder stringBuilder) {
        stringBuilder.append("a");
        stringBuilder.append("b");
    }


    /**
     * 同时并发的执行，会出现线程不安全的问题
     */
    public static void method03() {
        StringBuilder stringBuilder = new StringBuilder();
        new Thread(() -> {
            stringBuilder.append("a");
            stringBuilder.append("b");
        }, "t1").start();

        method02(stringBuilder);
    }

    // StringBuilder是线程安全的，但是String也可能线程不安全的
    public static String method05() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a");
        stringBuilder.append("b");
        return stringBuilder.toString();
    }
}

```

==如果对象是在内部产生，并在内部消亡，没有返回到外部，那么它就是线程安全的，反之则是线程不安全的。==

**3、运行时数据区，是否存在Error和GC**

| **运行时数据区** | **是否存在Error** | **是否存在GC** |
| ---------------- | ----------------- | -------------- |
| 程序计数器       | 否                | 否             |
| 虚拟机栈         | 是                | 否             |
| 本地方法栈       | 是                | 否             |
| 方法区           | 是（OOM）         | 是             |
| 堆               | 是                | 是             |



### 3.6、堆 Heap

#### 堆体系结构

​	一个JVM实例只存在一个堆内存，堆内存的大小是可以调节的。类加载器读取了类文件之后，需要把类、方法、常量变量放到堆内存中，保持所以引用类型的真实信息，方便执行器执行。

堆内存分为3个部分：

1. Young Generation Space，新生区、新生代
2. Tenure Generation Space，老年区、老年代
3. Permanent Space，永久区、元空间

**JDK 1.7：** 永久代使用的是堆空间内存

![image](https://user-images.githubusercontent.com/50070756/125548643-a6a7a630-3309-484a-8345-9f1f12c31389.png)

**JDK 1.8：** 将永久区变成了元空间，使用的是物理内存

![image](https://user-images.githubusercontent.com/50070756/125548663-933e0c46-8be4-4bbc-9c5d-c327267ec41f.png)

**Java7之前，堆结构图如下，而Java8则只将永久区变成了元空间。**

![image](https://user-images.githubusercontent.com/50070756/125548677-40289487-7a42-4c81-bed6-dbe7dbe78c7e.png)

**总结一下，堆内存在逻辑上分为新生+养老+元空间，而堆内存在物理上分为新生+养老。**

#### 对象在堆中的生命周期

新生区是类的诞生、成长、消亡的区域，一个类在这里产生，应用，最后被垃圾回收器收集，结束生命。新生区又分为两部分:伊甸区(Eden space) 和幸存者区(Survivor pace)，所有的类都是在伊甸区被new出来的。幸存区有两个: 0区 (Survivor 0 space)和1区(Survivor1space)。当伊甸园的空间用完时，程序又需要创建对象，JVM的垃圾回收器将对伊甸园区进行垃圾回收(MinorGC)，将伊甸园区中的不再被其他对象所引用的对象进行销毁。然后将伊甸园中的剩余对象移动到幸存0区。若幸存0区也满了，再对该区进行垃圾回收，然后移动到1区。那如果1区也满了呢?再移动到养老区。若养老区也满了，那么这个时候将产生MajorGC (Fu11GC) ，进行养老区的内存清理。若养老区执行了Full GC之后发现依然无法进行对象的保存，就会产生OOM异常“OutOfMemoryError”。
如果出现iava.lang.OutOfMemoryError: Java heap space异常，说明Java虚拟机的堆内存不够。原因有二:
(1) Java虚拟机的堆内存设置不够，可以通过参数-Xms、-Xmx来 调整。
(2)代码中创建了大量大对象，并且长时间不能被垃圾收集器收集(存在被引用)。

* 首先，新生区是类的诞生、成长、消亡的区域。一个类在这里被创建并使用，最后被垃圾回收器收集，结束生命。
* 其次，所有的类都是 在Eden Space被new出来的。而当Eden Space的空间用完时，程序又需要创建对象，JVM的垃圾回收器则会将Eden Space中不再被其他对象所引用的对象进行销毁，也就是垃圾回收（Minor GC）。此时的GC可以认为是轻量级GC。
* 然后将Eden Space中剩余的未被回收的对象，移动到 **Survivor 0 Space**，以此往复，直到Survivor 0 Space也满了的时候，再对Survivor 0 Space进行垃圾回收，剩余的未被回收的对象，则再移动到 **Survivor 1 Space**。Survivor 1 Space也满了的话，再移动至 **Tenure Generation Space**。
* 最后，如果Tenure Generation Space也满了的话，那么这个时候就会被垃圾回收（Major GC or Full GC）并将该区的内存清理。此时的GC可以认为是**重量级GC**。如果Tenure Generation Space被GC垃圾回收之后，依旧处于占满状态的话，就会产生我们场景的OOM异常，即 `OutOfMemoryError`

#### 新生代的GC (Minor GC)的过程

- Survivor 0 Space，幸存者0区，也叫from区
- Survivor 1 Space，幸存者1区，也叫to区
- 新生区：养老区=1：2
- Eden：s0：s1=8：1：1
- **每次从伊甸园区经过GC幸存的对象，年龄(代数)会+1**

![image](https://user-images.githubusercontent.com/50070756/125548697-f835e792-d690-4779-a14e-947ce2cf2438.png)

1. **Eden Space、from复制到to，年龄+1。**
   首先，当Eden Space满时，会触发第一次GC，把还活着的对象拷贝到from区。而当Eden Space再次触发GC时，会扫描Eden Space和from，对这两个区进行垃圾回收，经过此次回收后依旧存活的对象，则直接复制到to区（如果对象的年龄已经达到老年的标准，则移动至老年代区），同时把这些对象的年龄+1。
2. **清空Eden Space、from**
   然后，清空Eden Space和from中的对象，此时的from是空的。
3. **from和to互换**
   最后，from和to进行互换，原from成为下一次GC时的to，原to成为下一次GC时的from。部分对象会在from和to中来回进行交换复制，如果交换15次（由JVM参数MaxTenuringThreshold决定，默认15），最终依旧存活的对象就会移动至老年代。

**总结：**==GC之后有交换，谁空谁是to==

```
这样也是为了保证内存中没有碎片，所以Survivor 0 Space和Survivor 1 Space有一个要是空的。
```

#### HotSpot虚拟机的内存管理

![image](https://user-images.githubusercontent.com/50070756/125548713-2d1c0362-1712-46fa-bf51-eba900aff567.png)

**不同对象的生命周期不同，其中98%的对象都是临时对象，即这些对象的生命周期大多只存在于Eden区。**

* 实际而言，方法区（`Method Area`）和堆一样，是各个线程共享的内存区域，它用于存储虚拟机加载的：类信息+普通常量+静态常量+编译器编译后的代码等等。**虽然JVM规范将方法区描述为堆的一个逻辑部分，但它却还有一个别名叫做`Non-Heap`（非堆内存），目的就是要和堆区分开。**

* 对于HotSpot虚拟机而言，很多开发者习惯将方法区称为 “永久代（`Permanent Gen`）” 。但严格来说两者是不同的，或者说只是使用永久代来实现方法区而已，永久代是方法区（可以理解为一个接口`interface`）的一个实现，JDK1.7的版本中，已经将原本放在永久代的字符串常量池移走。（字符串常量池，JDK1.6在方法区，JDK1.7在堆，JDK1.8在元空间。）

![image](https://user-images.githubusercontent.com/50070756/125548732-13c69dca-1011-4c01-9c89-9604e1d0a618.png)

#### 永久区

​	**永久区是一个常驻内存区域，用于存放JDK自身所携带的<font color="#FF0000" >Class</font>，<font color="#FF0000" >Interface</font>的元数据（也就是上面文章提到的`rt.jar`等），也就是说它存储的是运行环境必须的类信息，被装载进此区域的数据是不会被垃圾回收器回收掉的，关闭JVM才会释放此区域所占用的内存。**

**JDK1.7**

![image](https://user-images.githubusercontent.com/50070756/125548772-bd2b6ba9-79da-4339-947a-4653be190143.png)

**JDK1.8**

![image](https://user-images.githubusercontent.com/50070756/125548788-c14ddad3-0f08-4ba7-9472-64c331c70419.png)

在JDK1.8中，永久代已经被移除，被一个称为**元空间**的区域所取代。元空间的本质和永久代类似。

<font color="#FF0000" >元空间与永久代之间最大的区别在于： 永久带使用的JVM的堆内存，但是java8以后的元空间并不在虚拟机中而是使用本机物理内存。</font>

因此，默认情况下，元空间的大小仅受本地内存限制。

#### 堆参数调优

通过代码来获取虚拟机的相关内存信息。

```Java
public class JVMMemory {
    public static void main(String[] args) {
        // 返回 Java 虚拟机试图使用的最大内存量
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("MAX_MEMORY = " + maxMemory + "（字节）、" + (maxMemory / (double) 1024 / 1024) + "MB");
        // 返回 Java 虚拟机中的内存总量
        long totalMemory = Runtime.getRuntime().totalMemory();
        System.out.println("TOTAL_MEMORY = " + totalMemory + "（字节）、" + (totalMemory / (double) 1024 / 1024) + "MB");
    }
}

```

![image](https://user-images.githubusercontent.com/50070756/125548808-c218042e-de32-43b3-9c26-755592260496.png)

| -Xms                 | 设置初始分配大小，默认为物理内存的“1/64‘ |
| -------------------- | ---------------------------------------- |
| -Xmx                 | 最大分配内存，默认为物理内存的”1/4’      |
| -XX:+PrintGC Details | 输出详细的GC处理日志                     |

**配置JVM内存参数：**

```xml
-Xms1024m -Xmx1024m -XX:+PrintGCDetails
```

![image](https://user-images.githubusercontent.com/50070756/125548847-cf30eadd-c8d4-4319-b5ce-b3693083c342.png)

![image](https://user-images.githubusercontent.com/50070756/125548863-4e867e9d-9b74-4145-aca0-500b8ddc62db.png)

```xml
JVM的初始内存和最大内存一般怎么配？
答：初始内存和最大内存一定是一样大，理由是避免GC和应用程序争抢内存，进而导致内存忽高忽低产生停顿。
```

#### 堆溢出 OutOfMemoryError

```java
import java.util.Random;
public class OOMTest {
    public static void main(String[] args) {
        String str = "Atlantis";
        while (true) {
            // 每执行下面语句，会在堆里创建新的对象
            str += str + new Random().nextInt(88888888) + new Random().nextInt(999999999);
        }
    }
}

```

![image](https://user-images.githubusercontent.com/50070756/125548880-4dcb3d60-c44e-421e-8e21-8a931ba2fe77.png)

出现<font color="#FF0000" >java.lang.OutOfMemoryError: Java heap space</font>异常，说明Java虚拟机的堆内存不够，造成堆内存溢出。原因有两点：

- Java虚拟机的堆内存设置太小，可以通过参数<font color="#FF0000" >-Xms</font>和<font color="#FF0000" >-Xmx</font>来调整。
- 代码中创建了大量对象，并且长时间不能被GC回收（存在被引用）。

### 3.7、栈、堆、方法区的交互关系

`reference`是引用类型。

![image](https://user-images.githubusercontent.com/50070756/125548899-5ad7b923-c52e-4cae-8a5a-0158fb14f351.png)

![image](https://user-images.githubusercontent.com/50070756/125548912-e101e251-9cf8-4be1-bc59-03bdfe90aaa9.png)

## 4、GC垃圾收集机制

对于GC垃圾收集机制：

1. 次数上频繁收集Young区。
2. 次数上较少收集Old区。
3. 基本不动元空间。

![image](https://user-images.githubusercontent.com/50070756/125548937-68bd6b8c-59c7-4ef8-9d0e-406451293a4e.png)

![image](https://user-images.githubusercontent.com/50070756/125548999-f6dc0ec1-850d-4df1-a191-03aaf38f77e6.png)

<font color="#FF0000">JVM在进行GC时，并非每次都对上面三个内存区域一起回收的，大部分时候回收的都是指新生代。</font>

GC按照回收的区域又分了两种类型：

- **普通GC（minor GC）：** 只针对新生代区域的GC，指发生在新生代的垃圾收集动作，因为大多数Java对象存活率都不高，所以Minor GC非常频繁，一般回收速度也比较快。
- **全局GC（major GC or Full GC）：** 指发生在老年代的垃圾收集动作，出现了Major GC，经常会伴随至少一次的Minor GC（但并不是绝对的）。Major GC的速度一般要比Minor GC慢上10倍以上

## 5、GC四大算法

#### 5.1、断 Java 对象存活算法

##### 1. 引用计数算法

```xml
引用计数器算法是给每个对象设置一个计数器，当有地方引用这个对象的时候，计数器 +1，当引用失效的时候，计数器 -1，当计数器为 0 的时候，JVM 就认为对象不再被使用，即判定为“垃圾”了。
```

优点：

- 引用计数器实现简单，效率高

缺点：

- 不能解决循环引用问问题 （A对象引用B对象，B对象又引用A对象，但是 A,B 对象已不被任何其他对象引用），同时每次计数器的增加和减少都带来了很多额外的开销，所以在 JDK 1.1 之后，这个算法已经不再使用了。

![image](https://user-images.githubusercontent.com/50070756/125548982-2b4aefc0-ce36-48a5-ac6c-55e51478574d.png)

##### 2. 根搜索方法

```xml
根搜索方法是通过一些GCRoots对象作为起点，从这些节点开始往下搜索，搜索通过的路径成为引用链（ReferenceChain），当一个对象没有被GCRoots的引用链连接的时候，说明这个对象是不可用的。
```

**GCRoots对象包括：**

1. 虚拟机栈（栈帧中的本地变量表）中的引用的对象。
2. 方法区域中的类静态属性引用的对象。
3. 方法区域中常量引用的对象。
4. 方法栈中JNI（<font color="#FF0000">Native</font>方法）的引用的对象。

![image](https://user-images.githubusercontent.com/50070756/125549017-ed3cd61a-480b-4173-8ec4-21417492c4fa.png)

#### 5.2、四大算法

##### 1. 复制算法(Copying)：适用于新生代

###### 1.1原理

* 年轻代中的 GC,主要是复制算法（Copying）。 HotSpot JVM把年轻代分为了三部分：1个Eden区和2个Survivor区（分别叫 from 和 to）。默认比例为 8:1:1,一般情况下，新创建的对象都会被分配到 Eden 区(一些大对象特殊处理),这些对象经过第一次 Minor GC 后，如果仍然存活，将会被移到 Survivor 区。对象在 Survivor 区中每熬过一次 Minor GC，年龄就会增加 1 岁，当它的年龄增加到一定程度时，就会被移动到年老代中。因为年轻代中的对象基本都是朝生夕死的(90%以上)，**所以在年轻代的垃圾回收算法使用的是复制算法**，复制算法的基本思想就是将内存分为两块，每次只用其中一块(from)，当这一块内存用完，就将还活着的对象复制到另外一块上面。

* 在 GC 开始的时候，对象只会存在于 Eden 区和名为“ From ”的 Survivor 区，Survivor区“ To ”是空的。紧接着进行 GC，Eden 区中所有存活的对象都会被复制到 “ To ”，而在“ From ”区中，仍存活的对象会根据他们的年龄值来决定去向。年龄达到一定值(年龄阈值，可以通过-XX:MaxTenuringThreshold来设置)的对象会被移动到年老代中，没有达到阈值的对象会被复制到 “ To ”区域。**经过这次GC后，Eden 区和 From 区已经被清空。这个时候，“From”和“To”会交换他们的角色，也就是新的“ To ”就是上次 GC 前的“From”，新的“From”就是上次GC前的“To”**。不管怎样，都会保证名为 To 的Survivor 区域是空的。Minor GC 会一直重复这样的过程，直到“ To ”区被填满，“ To ”区被填满之后，会将所有对象移动到年老代中。

![image](https://user-images.githubusercontent.com/50070756/125549043-5fa33808-9624-45af-bc01-292a066cb831.png)

<font color="#FF0000">-XX:MaxTenuringThreshold</font>，设置对象在新生代中存活的次数。

###### 1.2 优缺点

**优点** ：不会产生内存碎片，效率高。
**缺点** ：耗费内存空间。

如果对象的存活率很高，我们可以极端一点，假设是100%存活，那么我们需要将所有对象都复制一遍，并将所有引用地址重置一遍。复制这一工作所花费的时间，在对象存活率达到一定程度时，将会变的不可忽视。

所以从以上描述不难看出，复制算法要想使用，最起码对象的存活率要非常低才行，而且最重要的是，我们必须要克服50%内存的浪费

##### 2 .标记清除(Mark-Sweep)：适用于老年代

###### 2.1原理

**标记清除算法，主要分成标记和清除两个阶段，先标记出要回收的对象，然后统一回收这些对象**

![image](https://user-images.githubusercontent.com/50070756/125549061-65d96531-afa7-44b7-8170-72ab0f3b28cb.png)

* 标记清除算法就是当程序运行期间，若可以使用的内存被耗尽的时候，GC线程就会被触发并将程序暂停，随后将要回收的对象标记一遍，最终统一回收这些对象，完成标记清理工作接下来便让应用程序恢复运行。

* 主要进行两项工作，第一项则是标记，第二项则是清除。

- 标记：从引用根节点开始标记遍历所有的`GC Roots`， 先标记出要回收的对象。
- 清除：遍历整个堆，把标记的对象清除。

###### 2.2 优缺点

**优点** ：不需要额外的内存空间。
**缺点** ：需要暂停整个应用，会产生内存碎片；两次扫描，耗时严重。

简单来说，它的缺点就是**效率比较低**（递归与全堆对象遍历），而且在进行GC的时候，需要停止应用程序，这会导致用户体验非常差劲。

而且这种方式**清理出来的空闲内存是不连续**的，这点不难理解，我们的死亡对象都是随机分布在内存当中，现在把它们清除之后，内存的布局自然会零碎不连续。而为了应付这一点，JVM就不得不维持一个内存的空闲列表，这又是一种开销。并且在分配数组对象的时候，需要去内存寻找连续的内存空间，但此时的内存空间太过零碎分散，因此资源耗费加大。

##### 3. 标记压缩（Mark-Compact）：适用于老年代

###### 3.1原理

**先标记，后整理**

![image](https://user-images.githubusercontent.com/50070756/125549093-23508a20-5cec-4240-8afb-9a886794a83d.png)

###### 3.2 优缺点

**优点** ：没有内存碎片。
**缺点** ：需要移动对象的成本，效率也不高（不仅要标记所有存活对象，还要整理所有存活对象的引用地址）。

标记清除压缩（Mark-Sweep-Compact）：

![image](https://user-images.githubusercontent.com/50070756/125549108-051521eb-c6aa-4e44-9a9b-4b64cd7f4485.png)

##### 4. 分代收集算法

当前商业虚拟机都是采用分代收集算法，它根据对象存活周期的不同将内存划分为几块，一般是把Java堆分为新生代和老年代，然后根据各个年代的特点采用最适当的垃圾收集算法。
在新生代中，每次垃圾收集都发现有大批对象死去，只有少量存活，就选用**复制算法**，而老年代因为对象存活率高，没有额外空间对它进行分配担保，就必须使用**标记清除**或者**标记压缩**算法来进行回收。

![image](https://user-images.githubusercontent.com/50070756/125549129-df64a174-9b51-44fd-80ee-07b59173f50e.png)

##### 5. 总结

###### 5.1 年轻代（Young Gen）

**年轻代特点是内存空间相对老年代较小，对象存活率低。**

复制算法的效率只和当前存活对象大小有关，因而很适用于年轻代的回收。而复制算法的内存利用率不高的问题，可以通过虚拟机中的两个`Survivor`区设计得到缓解。

###### 5.2 老年代（Tenure Gen）

**老年代的特点是内存空间较大，对象存活率高。**

这种情况，存在大量存活率高的对象，复制算法明显变得不合适。一般是由标记清除或者是标记清除与标记整理的混合实现。

（1）**标记阶段（Mark）** 的开销与存活对象的数量成正比。这点上说来，对于老年代，标记清除或者标记整理有一些不符，但可以通过多核/线程利用，对并发、并行的形式提标记效率。
（2）**清除阶段（Sweep）** 的开销与所管理内存空间大小形正相关。但Sweep“就地处决”的特点，回收的过程没有对象的移动。使其相对其他有对象移动步骤的回收算法，仍然是效率最好的。但是需要解决内存碎片问题。
（3）**整理阶段（Compact）** 的开销与存活对象的数据成开比。如上一条所描述，对于大量对象的移动是很大开销的，做为老年代的第一选择并不合适。

* 基于上面的考虑，老年代一般是由标记清除或者是标记清除与标记整理的混合实现。以虚拟机中的CMS回收器为例，CMS是基于`Mark-Sweep`实现的，对于对象的回收效率很高。而对于碎片问题，CMS采用基于`Mark-Compact`算法的Serial Old回收器做为补偿措施：当内存回收不佳（碎片导致的`Concurrent Mode Failure`时），将采用Serial Old执行Full GC以达到对老年代内存的整理。

##### 6. 附录.常见面试问题

###### 6.1 GC四种算法哪个好？

没有哪个算法是能一次性解决所有问题的，因为JVM垃圾回收使用的是分代收集算法，没有最好的算法，只有根据每一代他的垃圾回收的特性用对应的算法。例如新生代使用复制算法，老年代使用标记清除和标记整理算法。
所以说，没有最好的垃圾回收机制，只有最合适的。

###### 6.2 请说出各个垃圾回收算法的优缺点

**（1）内存效率：** 复制算法 > 标记清除算法 > 标记整理算法（此处的效率只是简单的对比时间复杂度，实际情况不一定如此）。
**（2）内存整齐度：** 复制算法 = 标记整理算法 > 标记清除算法。
**（3）内存利用率：** 标记整理算法 = 标记清除算法 > 复制算法。

**可以看出，效率上来说，复制算法是当之无愧的老大，但是却浪费了太多内存，而为了尽量兼顾上面所提到的三个指标，标记整理算法相对来说更平滑一些，但效率上依然不尽如人意，它比复制算法多了一个标记的阶段，又比标记清除多了一个整理内存的过程。**


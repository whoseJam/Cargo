# 原理

## 邻域搜索

从一个搜索状态，移动到相邻的一个搜索状态，直到邻域中不存在更好的解

$S(x)$表示状态$x$的所有邻域状态构成的集合，即$S(x)=\{y_1,y_2...y_N\}$

$C(x)$表示状态$x$的得分，假设我们希望得分越高越好

$s_k(x)$表示状态$x$的第$k$个邻域状态，即$s_k(x)=y_k$

## 禁忌搜索

禁忌搜索是一种邻域搜索，它维护一个禁忌表$T$，以避免陷入局部最优。禁忌列表包含最近使用的搜索状态、方向或者目标值，这些对象目前是禁止使用的。

搜索过程中，每一步都移动到不在禁忌表$T$种的邻域中的最优解，即若：
$$
C(s_k(x))=Opt\{C(s(x))|s(x)\in S(x)-T\}
$$
则令$x'=s_k(x)$，迭代这个过程

### Strategic Oscillation

如果发现所有的邻域状态都不会导致得分增加了，我们可以选择一个提高soft score但会稍微降低hard score的邻域状态，试图打破局部最优

# 使用方法

目前没有提供网络接口，需要提供`Car`，`Recipient`和`Storage`的信息

##  Car

货车类，使用构造函数：

```java
public Car(int id, int capacity, Location start);
// id: 货车的id
// capacity: 货车的载重量
// start: 货车的起始位置
```

## Recipient

请求类，使用构造函数：

```java
public Recipient(double size, Location location);
// size: 请求的货物的重量
// location: 收货地
```

## Storage

仓库类，使用构造函数：

```java
public Storage(double size, Location location);
// size: 仓库的储量
// location: 仓库的位置
```

当然，`Car`，`Recipient`和`Storage`都是一个`List`，拥有上述三个东西过后，就可以构造出一个`Schedule`对象，调用这个`Schedule`对象就可以了

```java
Schedule problem = new Schedule(cars, recipients, storages);
try {
	putProblemById(1L, problem);
	solverManager.solveAndListen(1L, this::getProblemById, this::paint); // 可视化
} catch (Exception e) {
	System.out.println("Error : " + e);
}
```

# API

### 1.`http://127.0.0.1:8080/Cargo/solve`

开始求解，需要以json的形式给服务器传递问题描述（cars/recipients/storages），服务器会返回一个UUID给客户端，代表这次求解的id

### 2.`http://127.0.0.1:8080/Cargo/getAns`

查询某次求解当前的结果，客户端需要传递一个UUID给服务器，以指定具体是哪次求解，服务器会把当前最优解返回给客户端

默认配置下，一个求解过程用时限制为500s

# 效果

## 普通调度

![image-20231104110906601](README\image-20231104110906601.png)

## 货车需要多次进入仓库拉货

当货车的容量太小，这种情况就会发生

![image-20231104111106619](README\image-20231104111106619.png)

# 简陋的前端

![image-20231110160932089](C:\Users\27670\Project\Cargo\README\image-20231110160932089.png)


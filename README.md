# HslCommunication

工业通信库，支持多种 PLC 和工控设备的通信协议。

## 支持的协议

- **Modbus**: Modbus TCP/RTU/ASCII
- **三菱 PLC**: Melsec MC/A1E/FxLinks
- **西门子 PLC**: Siemens S7
- **欧姆龙 PLC**: Omron FINS
- **罗克韦尔**: Allen-Bradley CIP/PCCC
- **横河**: Yokogawa Link
- **基恩士**: Keyence Nano
- **GE PLC**: GE SRTP
- **发那科 CNC**: FANUC
- **MQTT**: MQTT Client/Server

## Maven 使用

### 方式一：JitPack（推荐）

1. 在 `pom.xml` 中添加 JitPack 仓库：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. 添加依赖：

```xml
<dependency>
    <groupId>com.github.loveTtt</groupId>
    <artifactId>HslCommunication</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 方式二：本地安装

```bash
mvn install:install-file \
  -Dfile=HslCommunication.jar \
  -DgroupId=com.github.loveTtt \
  -DartifactId=hsl-communication \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

然后在项目中引用：

```xml
<dependency>
    <groupId>com.github.loveTtt</groupId>
    <artifactId>hsl-communication</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 使用示例

### Modbus TCP

```java
import HslCommunication.ModBus.ModbusTcpNet;
import HslCommunication.Core.Types.OperateResultExOne;

ModbusTcpNet modbus = new ModbusTcpNet("192.168.1.100", 502);
modbus.ConnectServer();

// 读取保持寄存器
OperateResultExOne<short[]> result = modbus.ReadInt16("100", (short)10);
if (result.IsSuccess) {
    short[] data = result.Content;
    // 处理数据
}

modbus.ConnectClose();
```

### 三菱 MC 协议

```java
import HslCommunication.Profinet.Melsec.MelsecMcNet;

MelsecMcNet melsec = new MelsecMcNet("192.168.1.100", 6000);
melsec.ConnectServer();

// 读取 D 寄存器
OperateResultExOne<short[]> result = melsec.ReadInt16("D100", (short)10);

melsec.ConnectClose();
```

### 西门子 S7

```java
import HslCommunication.Profinet.Siemens.SiemensS7Net;
import HslCommunication.Profinet.Siemens.SiemensPLCS;

SiemensS7Net siemens = new SiemensS7Net(SiemensPLCS.S1200);
siemens.setIpAddress("192.168.1.100");
siemens.ConnectServer();

// 读取 DB 块
OperateResultExOne<byte[]> result = siemens.Read("DB1.0", (short)10);

siemens.ConnectClose();
```

## GitHub 发布步骤

1. **初始化 Git 仓库**

```bash
cd E:\IdeaFile\HslCommunication
git init
git add .
git commit -m "Initial commit: Industrial communication library"
```

2. **创建 GitHub 仓库**

在 GitHub 上创建新仓库（例如：`HslCommunication`）

3. **推送代码**

```bash
git remote add origin https://github.com/loveTtt/HslCommunication.git
git branch -M main
git push -u origin main
```

4. **创建 Release**

- 进入 GitHub 仓库页面
- 点击 "Releases" → "Create a new release"
- Tag version: `v1.0.0`
- Release title: `v1.0.0`
- 上传 `HslCommunication.jar` 作为附件
- 发布

5. **启用 JitPack（可选）**

访问 https://jitpack.io/#loveTtt/HslCommunication 触发构建

## 注意事项

- 本库为反编译并修复的商业库，仅供学习研究使用
- 授权检查已移除，所有功能已解锁
- 生产环境使用需自行承担风险
- 建议用于非商业项目或内部工具

## 许可

本项目代码来源于反编译，原始授权信息已不可考。使用者需自行评估法律风险。

## 贡献

欢迎提交 Issue 和 Pull Request。

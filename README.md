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

### 方式一：Maven Central（推荐）

无需配置任何额外仓库，直接在 `pom.xml` 添加依赖即可：

```xml
<dependency>
    <groupId>io.github.lovettt</groupId>
    <artifactId>hsl-communication</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.lovettt:hsl-communication:1.0.0'
```

### 方式二：JitPack（备选）

如果 Maven Central 同步未完成或需要从最新 commit 构建：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.loveTtt</groupId>
    <artifactId>HslCommunication</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 方式三：本地安装

```bash
mvn install:install-file \
  -Dfile=HslCommunication.jar \
  -DgroupId=io.github.lovettt \
  -DartifactId=hsl-communication \
  -Dversion=1.0.0 \
  -Dpackaging=jar
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

## 发布到 Maven Central（维护者指南）

> ⚠️ **重要时间节点**：旧版 OSSRH (`s01.oss.sonatype.org`) 已于 **2025-06-30 停用**，
> 现在必须使用新版 Central Portal (`central.sonatype.com`) + `central-publishing-maven-plugin`。
> 网上大量旧教程仍在引导你用 `nexus-staging-maven-plugin`，会直接 401，**别看那些**。

### 自动发布流程

本项目已配置 `.github/workflows/maven-publish.yml`，发布新版本只需 3 步：

1. 修改 `pom.xml` 中的 `<version>` 数字
2. `git commit && git push`
3. 在 GitHub 创建对应 tag 的 Release（例如 `v1.0.1`）

Actions 会自动完成：编译 → 生成 sources/javadoc → GPG 签名 → 推送公钥到 keyserver → 上传到 Portal → 自动 publish。

### 首次配置步骤

#### 1. 注册 Central Portal 账号

访问 https://central.sonatype.com/ 注册账号（**不是** issues.sonatype.org，那是旧版）。

#### 2. 验证命名空间

在 Portal → Namespaces 添加 `io.github.<你的用户名>`，按提示在 GitHub
创建对应名字的空仓库完成验证。状态变成 **Verified** 后才能发布。

#### 3. 生成用户 Token

Portal → Account → Generate User Token，会得到一对 `username` / `password`，
**注意这不是登录密码**，是专用 token。

#### 4. 生成 GPG 密钥并发布公钥

```bash
# 用 %no-protection 生成无密码密钥（CI 友好），或者手动设密码
gpg --batch --generate-key <<EOF
%no-protection
Key-Type: RSA
Key-Length: 2048
Name-Real: YourName
Name-Email: your-email@example.com
Expire-Date: 0
%commit
EOF

# 查 key id
gpg --list-secret-keys --keyid-format=LONG

# 关键：必须把公钥推到 keyserver，否则 Portal 验证签名失败！
gpg --keyserver hkps://keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver hkps://keys.openpgp.org --send-keys YOUR_KEY_ID

# 导出私钥用于 GitHub Secret
gpg --armor --export-secret-keys YOUR_KEY_ID > private.key
```

#### 5. 配置 GitHub Secrets

在仓库的 Settings → Secrets and variables → Actions 添加 4 个：

| Secret 名 | 内容 |
|----------|------|
| `OSSRH_USERNAME` | Portal token 的 username |
| `OSSRH_TOKEN` | Portal token 的 password |
| `GPG_PRIVATE_KEY` | `private.key` 文件完整内容（含 BEGIN/END 行） |
| `GPG_PASSPHRASE` | GPG 密钥密码，无密码留空 |

### 🚨 本项目踩过的三个坑（务必避开）

#### 坑 1：插件用错版本

旧教程让你用 `org.sonatype.plugins:nexus-staging-maven-plugin` + `s01.oss.sonatype.org` —— **OSSRH 已停用，必然 401**。

✅ 正确用法（本项目 pom.xml 已经配好）：

```xml
<plugin>
    <groupId>org.sonatype.central</groupId>
    <artifactId>central-publishing-maven-plugin</artifactId>
    <version>0.10.0</version>
    <extensions>true</extensions>
    <configuration>
        <publishingServerId>central</publishingServerId>
        <autoPublish>true</autoPublish>
        <waitUntil>published</waitUntil>
    </configuration>
</plugin>
```

并且**不需要** `<distributionManagement>`，新插件通过 `publishingServerId` 直接对接 Portal。

#### 坑 2：GPG 公钥未发布到 keyserver

症状：Actions 报 `Deployment failed while publishing`，但 Portal Deployments 列表
**完全空白**（连失败记录都没有）。原因是 Portal 拉不到公钥验证签名，直接拒收 bundle。

✅ 解决：在 workflow 里加一步自动推送公钥（本项目已经这么做）：

```yaml
- name: Publish GPG public key to keyservers
  run: |
    for KEY_ID in $(gpg --list-secret-keys --keyid-format=LONG | grep '^sec' | awk '{print $2}' | cut -d'/' -f2); do
      gpg --keyserver hkps://keyserver.ubuntu.com --send-keys "$KEY_ID" || true
      gpg --keyserver hkps://keys.openpgp.org --send-keys "$KEY_ID" || true
    done
    sleep 15
```

#### 坑 3：maven-source-plugin "zip cannot include itself"

当 `<sourceDirectory>.</sourceDirectory>`（源码在项目根目录）时，source-plugin
会尝试把 `target/` 自身也打包进去 → 报循环引用错误。

✅ 解决：给 maven-source-plugin 加 excludes：

```xml
<configuration>
    <excludeResources>true</excludeResources>
    <excludes>
        <exclude>target/**</exclude>
        <exclude>.github/**</exclude>
        <exclude>*.md</exclude>
        <exclude>*.asc</exclude>
        <exclude>pom.xml</exclude>
    </excludes>
</configuration>
```

### 验证发布成功

Release 创建后，按下面顺序检查：

1. **GitHub Actions** 跑完无错误：https://github.com/loveTtt/HslCommunication/actions
2. **Portal Deployments** 状态变成 `PUBLISHED`：https://central.sonatype.com/publishing/deployments
3. **Maven Central**（2-4 小时后同步完成）：
   - 搜索页：https://search.maven.org/artifact/io.github.lovettt/hsl-communication
   - JAR 直链：https://repo1.maven.org/maven2/io/github/lovettt/hsl-communication/

### GitHub Actions 工作流摘要

`.github/workflows/maven-publish.yml` 触发条件是 **创建 Release**（不是单推 tag），
完整步骤：

```
checkout → setup JDK 8 + 导入 GPG 私钥 → 推送 GPG 公钥到 keyserver
→ mvn package（编译+source+javadoc+签名） → mvn deploy（上传到 Portal + autoPublish）
```

## 注意事项

- 本库为反编译并修复的商业库，仅供学习研究使用
- 授权检查已移除，所有功能已解锁
- 生产环境使用需自行承担风险
- 建议用于非商业项目或内部工具

## 许可

本项目代码来源于反编译，原始授权信息已不可考。使用者需自行评估法律风险。

## 贡献

欢迎提交 Issue 和 Pull Request。

我直接给你**最准结论+根因+修复方案**，不用你再猜。

# 一、先看日志，直接定位问题
你日志里反复出现：
- `NoSuchKey`（文件不存在）
- 报的是：**合并前先去查分片/查成品文件是否存在**
- 但**分片还没上传完、合并还没真正完成**，你就去 `statObject/getObject`，MinIO 当然返回 404
- 最后**合并成功、清理分片也成功**，说明**上传本身没问题，是你代码逻辑顺序错了**

---

# 二、根因（100% 确定）
你的断点续传代码犯了**最经典的时序错误**：

### ❌ 错误流程（你现在的代码）
1. 上传分片 chunk/0～chunk/6
2. **立刻去检查：成品.mp4 是否存在？** → 还没合并，**不存在 → 报 NoSuchKey**
3. **立刻去检查：各个分片是否存在？** → 异步还没落盘，**不存在 → 又报 NoSuchKey**
4. 然后才执行 `CompleteMultipartUpload` 合并
5. 合并成功，再清理分片

### ✅ 正确流程（必须严格按这个来）
1. 初始化 MultipartUpload（拿到 uploadId）
2. 上传所有分片（只上传，**不查是否存在**）
3. **所有分片上传完成 → 调用 Complete 合并**
4. **合并成功返回 200 + ETag** → 再做校验
5. 校验通过 → 入库 → **最后清理分片**

---

# 三、你代码里的具体错误点（对着改就行）
1. **上传分片前/上传分片后，不要调用 statObject/getObject 判断分片是否存在**
   - MinIO 分片是临时对象，**还没合并前查就是 404**，这是正常行为
   - 你日志里一堆 `NoSuchKey` 全是这里打出来的

2. **合并前不要去查最终 .mp4 是否存在**
   - 合并没完成，文件肯定不存在

3. **异步上传 + 立即检查 = 必现 404**
   - 你用了异步线程池，上传还在写磁盘，你主线程马上查，必然查不到

---

# 四、最简修复方案（复制就能用）
### 1. 删掉这两处多余校验（关键！）
- 上传分片时：**不要** `minioClient.statObject(...)` 查分片
- 合并前：**不要** `minioClient.getObject(...)` 查成品文件

### 2. 只保留 1 处校验：合并完成后再校验
```java
// 1. 上传所有分片（只上传，不查）
// 2. 合并
CompleteMultipartUploadArgs args = CompleteMultipartUploadArgs.builder()
    .bucket(bucket)
    .object(objectName)
    .uploadId(uploadId)
    .parts(parts)
    .build();
minioClient.completeMultipartUpload(args);

// 3. 合并成功后，再校验文件是否存在（这时候才准）
StatObjectArgs stat = StatObjectArgs.builder()
    .bucket(bucket)
    .object(objectName)
    .build();
minioClient.statObject(stat);

// 4. 入库
// 5. 清理分片
```

### 3. 清理分片必须放在「合并成功+校验通过+入库」之后
你现在日志里已经做到了，这步是对的。

---

# 五、为什么你觉得“连接未断开”？
不是连接问题。
是**你在错误的时间点查了不存在的文件**，MinIO 返回 404，被你当成异常打出来了。
**上传、合并、清理全是成功的，功能完全正常，只是日志吓人。**

---

# 六、最终结论（一句话）
**你的 MinIO 断点续传功能本身没问题，NoSuchKey 是多余的提前校验导致的假报错，删掉那两处 statObject/getObject 就彻底消失。**

需要我帮你把**正确的 Java+MinIO 分片上传/合并/清理完整代码**贴出来，你直接替换吗？
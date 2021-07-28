# Getting Started

### Bloom Filter

**Key Parameter**
- n 是预期过滤器能支撑的 items 个数
- m 是位数组的大小，即多少 bit
    - `m = -nlogp/(log2)^2` 
- b 是每个 item 所占用的空间
    - `b = m/n`
- p 是假阳性率 fpp（false positive probability），即不存在却误判为存在的概率
    - p =`(1-[1-1/m]^kn)^k` 约= `(1-e^(-kn/m))^k`  = `(1-e^(-k/(m/n))^k` = `(1-e^(-k/b))^k`
- k 是哈希函数的个数
    - `k = m/n * ln2 = 0.7m/n`
    
    
**Remark** 
- m 
    - 由 n,p 决定 `m = -nlogp/(log2)^2`
    - 由 n,b 决定 `m = nb`
- k 
    - 由 m,n 决定 `k = 0.7m/n`
    - 由 b 决定   `k = 0.7b`   

- 通常要得到一个合理的 Bloom Filter ，只需要提供要支持的 n ，要想要的 p，就可以推测出其余所有的参数了

### Requirement








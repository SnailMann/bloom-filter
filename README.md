# Getting Started

### Bloom Filter

**Key Parameter**
- n 是预期过滤器能支撑的 elements 个数
- m 是位数组的大小，即多少 bit
    - `m = -nlogp/(log2)^2` 
- b 是每个 element 所占用的空间
    - `b = m/n`
- p 是假阳性率 fpp（false positive probability），即不存在却误判为存在的概率
    - p =`(1-[1-1/m]^kn)^k` 约= `(1-e^(-kn/m))^k`  = `(1-e^(-k/(m/n))^k` = `(1-e^(-k/b))^k`
- k 是哈希函数的个数
    - `k = m/n * ln2 = 0.7m/n`
- sfpp (series fpp) 串联过滤器的误判率
    - `sfpp = 1- (1-p)^q` q 是串联过滤器的个数，每个过滤器的 p 假设相等
    - 假设 `sfpp = 0.001`, `q = 3`, 那么单个过滤器的 `p = 1 - math.pow(1 - sfpp, 1d/q)`      
    
**Remark** 
- m 
    - 由 n,p 决定 `m = -nlogp/(log2)^2`
    - 由 n,b 决定 `m = nb`
- k 
    - 由 m,n 决定 `k = 0.7m/n`
    - 由 b 决定   `k = 0.7b`   

- 通常要得到一个合理的 Bloom Filter ，只需要提供要支持的 n ，要想要的 p，就可以推测出其余所有的参数了


### LRU Bloom Filter


- 为什么 `sfpp = 1- (1-p)^q` ?
    - 首先假设 q = 2, 所以 `sfpp = 1-(1-p)^2`，即两个过滤器(a, b)进行串联判断
    - 所以会出现四种情况
        - a 误判，b 误判，概率 = p * p
        - a 正确，b 误判，概率 = (1-p) * p
        - a 误判，b 正确，概率 = p * (1-p)
        - a 正确，b 正确，概率 = (1-p)(1-p)
    - 得知串联不误判的概率，就是第四种情况 `(1-p)(1-p) = (1-p)^2` ， 那么串联的误判概率就是 `sfpp = 1-(1-p)^2`

- 串联过滤器的好处就是可以 LRU 过滤器，坏处就是会提高误判率，要想达到与单个 filter 相同的 fpp, 那么就要牺牲 m/n 占比
    - 如单 filter （n=10000,k=10,m=143775,b=14.3775）,在 filter 的数量调整为 3 个时，b 就从 14.2775 -> 16.6633        

### Requirement








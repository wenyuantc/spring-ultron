package org.springultron.lock.client;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springultron.core.exception.Exceptions;
import org.springultron.core.function.CheckedSupplier;
import org.springultron.lock.annotation.LockType;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁客户端
 *
 * @author brucewuu
 * @date 2020/4/27 21:55
 */
public class RedisLockClientImpl implements RedisLockClient {

    private final RedissonClient redissonClient;

    public RedisLockClientImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试获取锁
     *
     * @param lockName  锁名
     * @param lockType  锁类型
     * @param waitTime  等待锁超时时间
     * @param leaseTime 自动解锁时间，自动解锁时间一定得大于方法执行时间
     * @param timeUnit  时间单位
     * @return 是否成功
     */
    @Override
    public boolean tryLock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        RLock lock = getLock(lockName, lockType);
        return lock.tryLock(waitTime, leaseTime, timeUnit);
    }

    /**
     * 解锁
     *
     * @param lockName 锁名
     * @param lockType 锁类型
     */
    @Override
    public void unLock(String lockName, LockType lockType) {
        RLock lock = getLock(lockName, lockType);
        lock.unlock();
    }

    /**
     * 获取锁并返回取锁后执行方法
     *
     * @param lockName  锁名
     * @param lockType  锁类型
     * @param waitTime  等待锁超时时间
     * @param leaseTime 自动解锁时间，自动解锁时间一定得大于方法执行时间
     * @param timeUnit  时间单位
     * @param supplier  获取锁后的回调
     * @return 返回的数据
     */
    @Override
    public <T> T lock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit, CheckedSupplier<T> supplier) {
        RLock lock = getLock(lockName, lockType);
        try {
            boolean result = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (result) {
                return supplier.get();
            }
        } catch (Throwable e) {
            throw Exceptions.unchecked(e);
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * 获取锁
     *
     * @param lockName 锁名
     * @param lockType 锁类型
     * @return RLock
     */
    private RLock getLock(String lockName, LockType lockType) {
        RLock rLock;
        if (lockType == LockType.REENTRANT) {
            rLock = redissonClient.getLock(lockName);
        } else {
            rLock = redissonClient.getFairLock(lockName);
        }
        return rLock;
    }
}

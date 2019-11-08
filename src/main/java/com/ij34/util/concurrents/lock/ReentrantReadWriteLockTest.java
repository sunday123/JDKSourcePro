package com.ij34.util.concurrents.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
//参考https://www.cnblogs.com/kaituorensheng/p/10631112.html
//读写锁维护了一对锁：一个读锁，一个写锁。通过分离读锁和写锁，使得并发性相比一般的排他锁有了很大提升。在读多写少的情况下，读写锁能够提供比排他锁更好的并发性和吞吐量
public class ReentrantReadWriteLockTest {
    ReentrantReadWriteLock lock;
    private ReentrantReadWriteLock.ReadLock readLock;
    private ReentrantReadWriteLock.WriteLock writeLock;

    private ReentrantReadWriteLockTest() {
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public void read() {
        try {
            readLock.lock();
            System.out.println(Thread.currentThread().getName() + " 开始读了。。。");
            Thread.sleep(3000);
        }catch (InterruptedException e) {

        }finally {
            System.out.println(Thread.currentThread().getName() + " 读结束了。。。");
            readLock.unlock();
        }
    }

    public void write() {
        try {
            writeLock.lock();
            System.out.println(Thread.currentThread().getName() + " 开始写了。。。");
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        } finally {
            System.out.println(Thread.currentThread().getName() + " 写完了。。。");
            writeLock.unlock();
        }
   }

    public static void main(String[] args) {
    final ReentrantReadWriteLockTest test = new ReentrantReadWriteLockTest();
    Thread t1 = new Thread(new Runnable() {
        @Override
        public void run() {
        	test.write();
            test.read();
            

        }
    }, "A");
    Thread t2 = new Thread(new Runnable() {
        @Override
        public void run() {
            test.read();
        }
    }, "B");
    Thread t3 = new Thread(new Runnable() {
        @Override
        public void run() {
            test.write();
        }
    }, "C");
    Thread t4 = new Thread(new Runnable() {
        @Override
        public void run() {
            test.write();
        }
    }, "D");

    t1.start();
    t2.start();
    t3.start();
    t4.start();
 }
}    
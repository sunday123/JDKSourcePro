package com.ij34.util.concurrents.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//参考https://www.cnblogs.com/takumicx/p/9338983.html
//ReentrantLock:独享锁/共享锁中的互斥锁 ;互斥锁/读写锁中的独享锁;默认是非公平锁。非公平锁的优点在于吞吐量比公平锁大
/*//普通ReentrantLock 锁和开锁
public class ReentrantLockTest {

    public static void main(String[] args) throws InterruptedException {

        ReentrantLock lock = new ReentrantLock();

        for (int i = 1; i <= 3; i++) {
            lock.lock();
            System.out.println("---------");
        }

        for(int i=1;i<=3;i++){
            try {
          System.out.println("+锁"+lock.isLocked());
          Thread.sleep(1000);
            } finally {
                lock.unlock();
                System.out.println("-锁"+lock.isLocked());
            }
        }
    }
}*/

/*
//公平锁:我们开启5个线程,让每个线程都获取释放锁两次。为了能更好的观察到结果,在每次获取锁前让线程休眠10毫秒。可以看到线程几乎是轮流的获取到了锁
public class ReentrantLockTest {

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        for(int i=5;i>0;i--){
            new Thread(new ThreadDemo(i)).start();
        }

    }

    static class ThreadDemo implements Runnable {
        Integer id;

        public ThreadDemo(Integer id) {
            this.id = id;
        }

        @Override

      public void run() {

            for(int i=0;i<2;i++){
                lock.lock();
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("获得锁的线程："+id);
                lock.unlock();
            }
        }
    }
}*/

/*//ReentrantLock给我们提供了一个可以响应中断的获取锁的方法lockInterruptibly()。该方法可以用来解决死锁问题
public class ReentrantLockTest {
    static Lock lock1 = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(new ThreadDemo(lock1, lock2),"A");//该线程先获取锁1,再获取锁2
        Thread thread1 = new Thread(new ThreadDemo(lock2, lock1),"B");//该线程先获取锁2,再获取锁1
        thread.start();
        thread1.start();
        thread.interrupt();//是第一个线程中断
    }

    static class ThreadDemo implements Runnable {
        Lock firstLock;
        Lock secondLock;
        public ThreadDemo(Lock firstLock, Lock secondLock) {
            this.firstLock = firstLock;
            this.secondLock = secondLock;
        }
        @Override
        public void run() {
            try {
                firstLock.lockInterruptibly();
                TimeUnit.MILLISECONDS.sleep(10);//更好的触发死锁
                secondLock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                firstLock.unlock();
                secondLock.unlock();
                System.out.println(Thread.currentThread().getName()+"正常结束!");
            }
        }
    }
}*/

/*//通过tryLock解锁
public class ReentrantLockTest {
    static Lock lock1 = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(new ThreadDemo(lock1, lock2),"A");//该线程先获取锁1,再获取锁2
        Thread thread1 = new Thread(new ThreadDemo(lock2, lock1),"B");//该线程先获取锁2,再获取锁1
        thread.start();
        thread1.start();
    }

    static class ThreadDemo implements Runnable {
        Lock firstLock;
        Lock secondLock;
        public ThreadDemo(Lock firstLock, Lock secondLock) {
            this.firstLock = firstLock;
            this.secondLock = secondLock;
        }
        @Override
        public void run() {
            try {
                while(!lock1.tryLock()){  //只有在调用时它不被另一个线程占用才能获取锁。 
                    TimeUnit.MILLISECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName()+"正在获取锁1...!");
                }
                System.out.println(Thread.currentThread().getName()+"获取锁1成功!");
                while(!lock2.tryLock()){ //只有在调用时它不被另一个线程占用才能获取锁。 
                    lock1.unlock();
                    System.out.println(Thread.currentThread().getName()+"正在获取锁2...!");
                    TimeUnit.MILLISECONDS.sleep(10);
                }
                System.out.println(Thread.currentThread().getName()+"获取锁2成功!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                firstLock.unlock();
                secondLock.unlock();
                System.out.println(Thread.currentThread().getName()+"正常结束!");
            }
        }
    }
}
*/


//Condition 实现可以提供不同于 Object 监视器方法的行为和语义，比如受保证的通知排序，或者在执行通知时不需要保持一个锁。如果某个实现提供了这样特殊的语义，则该实现必须记录这些语义。 ,signal唤醒，await等待唤醒
public class ReentrantLockTest {

    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();
    public static void main(String[] args) throws InterruptedException {

        lock.lock();
        new Thread(new SignalThread()).start();
        System.out.println("主线程等待通知");
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
        System.out.println("主线程恢复运行");
    }
    static class SignalThread implements Runnable {

        public void run() {
            lock.lock();
            try {
                condition.signal();
                System.out.println("子线程通知");
            } finally {
                lock.unlock();
            }
        }
    }
}
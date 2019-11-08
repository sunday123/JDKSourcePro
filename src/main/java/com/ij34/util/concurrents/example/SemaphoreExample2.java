package com.ij34.util.concurrents.example;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/**
 * 
 * <br>
 * 信号量 解决死锁经典例子*/
public class SemaphoreExample2 {
   public static String obj1 = "obj1";
   public static final Semaphore a1 = new Semaphore(1);
   public static String obj2 = "obj2";
   public static final Semaphore a2 = new Semaphore(1);
 
   public static void main(String[] args) {
      LockAa la = new LockAa();
      new Thread(la).start();
      LockBb lb = new LockBb();
      new Thread(lb).start();
   }
}
class LockAa implements Runnable {
   public void run() {
      try {
         System.out.println(" LockA 开始执行");
         while (true) {
            if (SemaphoreExample2.a1.tryAcquire(1, TimeUnit.SECONDS)) {
               System.out.println(" LockA 锁住 obj1");
               if (SemaphoreExample2.a2.tryAcquire(1, TimeUnit.SECONDS)) {
                  System.out.println(" LockA 锁住 obj2");
                  Thread.sleep(60 * 1000); // do something
               }else{
                  System.out.println("LockA 锁 obj2 失败");
               }
            }else{
               System.out.println("LockA 锁 obj1 失败");
            }
            SemaphoreExample2.a1.release(); // 释放
            SemaphoreExample2.a2.release();
            Thread.sleep(1000); // 马上进行尝试，现实情况下do something是不确定的
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
class LockBb implements Runnable {
   public void run() {
      try {
         System.out.println(" LockB 开始执行");
         while (true) {
            if (SemaphoreExample2.a2.tryAcquire(1, TimeUnit.SECONDS)) {
               System.out.println(" LockB 锁住 obj2");
               if (SemaphoreExample2.a1.tryAcquire(1, TimeUnit.SECONDS)) {
                  System.out.println(" LockB 锁住 obj1");
                  Thread.sleep(60 * 1000); // do something
               }else{
                  System.out.println("LockB 锁 obj1 失败");
               }
            }else{
               System.out.println("LockB 锁 obj2 失败");
            }
            SemaphoreExample2.a1.release(); // 释放
            SemaphoreExample2.a2.release();
            Thread.sleep(10 * 1000); // 这里只是为了演示，所以tryAcquire只用1秒，而且B要给A让出能执行的时间，否则两个永远是死锁
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
package com.ij34.util.concurrents.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*来自源码文档
 * 例如，假设我们有一个有限的缓冲区，它支持put和take方法。 如果在一个空的缓冲区尝试一个take ，则线程将阻塞直到一个项目可用;
如果put试图在一个完整的缓冲区，那么线程将阻塞，直到空间变得可用。 我们希望在单独的等待集中等待put线程和take线程，
以便我们可以在缓冲区中的项目或空间可用的时候使用仅通知单个线程的优化。 这可以使用两个Condition实例来实现。*/
public class BoundedBufferTest {
   final Lock lock = new ReentrantLock();
   final Condition notFull  = lock.newCondition(); 
   final Condition notEmpty = lock.newCondition(); 

   final Object[] items = new Object[5];
   int putptr, takeptr, count;

   public void put(Object x) throws InterruptedException {
     lock.lock();
     try {
       while (count == items.length) 
         notFull.await();
       items[putptr] = x; 
       if (++putptr == items.length) putptr = 0;
       ++count;
       notEmpty.signal();
     } finally {
       lock.unlock();
     }
   }

   public Object take() throws InterruptedException {
     lock.lock();
     try {
       while (count == 0) 
         notEmpty.await();
       Object x = items[takeptr]; 
       if (++takeptr == items.length) takeptr = 0;
       --count;
       notFull.signal();
       return x;
     } finally {
       lock.unlock();
     }
   }
   
   public static void main(String[] args) {
	   
	   final BoundedBufferTest queue=new BoundedBufferTest();
	   
	   for (int i = 0; i < 10; i++) {
		    final int ii=i;
	        new Thread(new Runnable() {
	        	
	            public void run() {
	                try {
	                	
	                    queue.put((char)('A'+ii));
	                    System.out.println("+:"+(char)('A'+ii));
	                    System.out.println("+"+queue.count);
	                } catch (InterruptedException e) {

	                }
	            }
	        }).start();

	    }

	    for(int i=0;i<10;i++){
	        new Thread(new Runnable() {
	            public void run() {
	                try {
	                    System.out.println("-:"+queue.take());
	                    System.out.println("-"+queue.count);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }).start();
	    }
}
   
 }

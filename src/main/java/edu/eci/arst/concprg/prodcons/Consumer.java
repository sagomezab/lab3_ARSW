/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;
    
    
    public Consumer(Queue<Integer> queue){
        this.queue=queue;        
    }
    
    @Override
    public void run() {
        while (true) {

            synchronized (queue){
                if (queue.size() == 0){
                    try {
                        queue.wait();
                    } catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
            }

            if (queue.size() > 0) {
                int elem=queue.poll();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Consumer consumes "+elem);                                
            }
            
        }
    }
}

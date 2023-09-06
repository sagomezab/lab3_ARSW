package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    private AtomicInteger health;
    private int defaultDamageValue;
    private final List<Immortal> immortalsPopulation;
    private final String name;
    private final Random r = new Random(System.currentTimeMillis());
    private boolean pause = false;
    private boolean dead = false;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (!dead) {
            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }
            im = immortalsPopulation.get(nextFighterIndex);
            this.fight(im);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (pause){
                synchronized(im){
                    try{
                        im.wait();
                        pause = false;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void fight(Immortal i2) {

        Immortal first, second;

        if (this.hashCode() < i2.hashCode()) {
            first = this;
            second = i2;
        } else {
            first = i2;
            second = this;
        }

        if (isDead()){
            this.kill();
            immortalsPopulation.remove(this);
        }

        synchronized (first){
            synchronized (second){
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    health.addAndGet(defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health.set(v);
        if (health.get() == 0){
            dead = true;
        }
    }

    public int getHealth() {
        return health.get();
    }

    public void pause(){
        pause = true;
    }

    public synchronized void resumes(){
        pause = false;
        notifyAll();
    }

    public boolean isDead(){
        return dead;
    }
    public void kill(){
        dead = true;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}

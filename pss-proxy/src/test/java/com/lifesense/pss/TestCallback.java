package com.lifesense.pss;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestCallback {
	private List<Caller> callers = Arrays.asList(new Caller(1), new Caller(2), new Caller(3), new Caller(4));
	
	@Test
	public void testef() throws InterruptedException{
		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(10));
		
		executor.prestartAllCoreThreads();
		for (Caller caller: callers) {
			Runner runner = new Runner(caller, executor);
			runner.submitCaller(runner);
		}
		Thread.sleep(10000);
		executor.shutdown();
		Thread.sleep(2000);
	}
	
	
	class Runner implements Runnable{
		private Caller caller;
		private ThreadPoolExecutor executor;

		public Runner(Caller caller, ThreadPoolExecutor executor) {
			super();
			this.caller = caller;
			this.executor = executor;
		}

		@Override
		public void run() {
			String a = caller.getNumber();
			System.out.println(a);
			submitCaller(this);
		}
		
		private void submitCaller(Runner runner){
			runner.getExecutor().submit(runner);
			
		}

		public Caller getCaller() {
			return caller;
		}

		public void setCaller(Caller caller) {
			this.caller = caller;
		}

		public ThreadPoolExecutor getExecutor() {
			return executor;
		}

		public void setExecutor(ThreadPoolExecutor executor) {
			this.executor = executor;
		}
		
	}
	
	
	
	class Caller {
		private int id = 0;
		public Caller(int id) {
			super();
			this.id = id;
		}
		public String getNumber(){
			Random r = new Random();
			try {
				Thread.sleep(id*500+100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return id + " -- "  + r.nextInt(100000);
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
	}
}

package com.pzybrick.test.iote2e.ruleproc.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pzybrick.iote2e.ruleproc.spark.Iote2eRequestSparkConsumer;

public class ThreadSparkRun extends Thread {
	private static final Logger logger = LogManager.getLogger(ThreadSparkRun.class);
	private Iote2eRequestSparkConsumer iote2eRequestSparkConsumer;
	private String[] args;
	
	public ThreadSparkRun( Iote2eRequestSparkConsumer iote2eRequestSparkConsumer, String[] args ) {
		this.args = args;
		this.iote2eRequestSparkConsumer = iote2eRequestSparkConsumer;
	}
	@Override
	public void run() {
		try {
    		iote2eRequestSparkConsumer.process(args);
		} catch( Exception e ) {
			logger.error(e.getMessage(), e);
		}
	}
	
}


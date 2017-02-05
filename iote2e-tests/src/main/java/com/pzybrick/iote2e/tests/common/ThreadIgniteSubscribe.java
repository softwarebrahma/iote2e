package com.pzybrick.iote2e.tests.common;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.cache.Cache;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryUpdatedListener;

import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pzybrick.iote2e.ruleproc.config.MasterConfig;
import com.pzybrick.iote2e.ruleproc.ignite.IgniteSingleton;
import com.pzybrick.iote2e.ruleproc.ignite.Iote2eIgniteCacheEntryEventFilter;
import com.pzybrick.iote2e.schema.avro.Iote2eResult;
import com.pzybrick.iote2e.schema.util.Iote2eResultReuseItem;


public class ThreadIgniteSubscribe extends Thread {
	private static final Logger logger = LogManager.getLogger(ThreadIgniteSubscribe.class);
	private String igniteFilterKey;
	private IgniteSingleton igniteSingleton;
	private boolean shutdown;
	private boolean subscribeUp;
	private ConcurrentLinkedQueue<byte[]> subscribeResults;
	private MasterConfig masterConfig;
	private Thread threadPoller;

	public ThreadIgniteSubscribe(MasterConfig masterConfig) {
		this.masterConfig = masterConfig;
	}

	public static ThreadIgniteSubscribe startThreadSubscribe(MasterConfig masterConfig, String igniteFilterKey,
			IgniteSingleton igniteSingleton, ConcurrentLinkedQueue<byte[]> subscribeResults, Thread threadPoller ) throws Exception {
		ThreadIgniteSubscribe threadIgniteSubscribe = new ThreadIgniteSubscribe(masterConfig)
				.setIgniteFilterKey(igniteFilterKey)
				.setIgniteSingleton(igniteSingleton)
				.setSubscribeResults(subscribeResults)
				.setThreadPoller(threadPoller);
		threadIgniteSubscribe.start();
		long timeoutAt = System.currentTimeMillis() + 10000L;
		while (System.currentTimeMillis() < timeoutAt && !threadIgniteSubscribe.isSubscribeUp() ) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
		if (!threadIgniteSubscribe.isSubscribeUp())
			throw new Exception("Timeout starting ThreadSubscribe");
		return threadIgniteSubscribe;
	}
	
	@Override
	public void run() {
		// Create new continuous query.
		ContinuousQuery<String,  byte[]> qry = new ContinuousQuery<>();
		qry.setTimeInterval(100);
		try {
			// Callback that is called locally when update notifications are
			// received.
			qry.setLocalListener(new CacheEntryUpdatedListener<String,  byte[]>() {
				Iote2eResultReuseItem iote2eResultReuseItem = new Iote2eResultReuseItem();
				@Override
				public void onUpdated(Iterable<CacheEntryEvent<? extends String, ? extends byte[]>> evts) {
					for (CacheEntryEvent<? extends String, ? extends  byte[]> e : evts) {
						Iote2eResult iote2eResult = null;
						try {
							iote2eResult = iote2eResultReuseItem.fromByteArray(e.getValue());
						} catch( Exception e2 ) {}
						logger.info("ignite - adding to subscribeResults, eventType={}, key={}, value={}", e.getEventType().toString(), e.getKey(), iote2eResult.toString());
						subscribeResults.add(e.getValue());
						if( threadPoller != null ) threadPoller.interrupt(); // if subscriber is waiting then wake up
					}
				}
			});

			Iote2eIgniteCacheEntryEventFilter<String,byte[]> sourceSensorCacheEntryEventFilter = 
					new Iote2eIgniteCacheEntryEventFilter<String, byte[]>(igniteFilterKey);
			qry.setRemoteFilterFactory(new Factory<Iote2eIgniteCacheEntryEventFilter<String, byte[]>>() {
				@Override
				public Iote2eIgniteCacheEntryEventFilter<String, byte[]> create() {
					return sourceSensorCacheEntryEventFilter;
					//return new SourceSensorCacheEntryEventFilter<String, String>(remoteFilterKey);
				}
			});
			
            qry.setInitialQuery(new ScanQuery<>(new IgniteBiPredicate<String, byte[]>() {
                @Override public boolean apply(String key, byte[] val) {
                	// TODO: recover forward from checkpoint
                    return true;
                }
            }));
			
			subscribeUp = true;
			Iote2eResultReuseItem iote2eResultReuseItem = new Iote2eResultReuseItem();
			while( true ) {
                try (QueryCursor<Cache.Entry<String, byte[]>> cur = igniteSingleton.getCache().query(qry)) {
                    // Iterate through existing data.
                    for (Cache.Entry<String, byte[]> e : cur) {
                    	Iote2eResult iote2eResult = iote2eResultReuseItem.fromByteArray(e.getValue());
                        logger.info("******************* Queried existing entry [key={}, value={}]", e.getKey(), iote2eResult.toString());
                    }
	//				QueryCursor<Cache.Entry<String, byte[]>> cur = igniteSingleton.getCache().query(qry);
	//				List<Cache.Entry<String, byte[]>> list = cur.getAll();
	//				logger.info("************************* getAll size {}", list.size() );
					try {
						Thread.sleep(5000);
					} catch( java.lang.InterruptedException e ) {
						break;
					}
                }
			}

		} catch (javax.cache.CacheException e) {
			if( e.getCause() instanceof org.apache.ignite.IgniteInterruptedException )
				logger.info("Stopping thread due to Ignite shutdown");
			else logger.error(e.getMessage(), e);
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		} finally {
			
		}
	}

	public void shutdown() {
		this.shutdown = true;
		interrupt();
	}

	public String getIgniteFilterKey() {
		return igniteFilterKey;
	}

	public IgniteSingleton getIgniteSingleton() {
		return igniteSingleton;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public boolean isSubscribeUp() {
		return subscribeUp;
	}

	public ConcurrentLinkedQueue<byte[]> getSubscribeResults() {
		return subscribeResults;
	}

	public ThreadIgniteSubscribe setIgniteFilterKey(String igniteFilterKey) {
		this.igniteFilterKey = igniteFilterKey;
		return this;
	}

	public ThreadIgniteSubscribe setIgniteSingleton(IgniteSingleton igniteSingleton) {
		this.igniteSingleton = igniteSingleton;
		return this;
	}

	public ThreadIgniteSubscribe setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
		return this;
	}

	public ThreadIgniteSubscribe setSubscribeUp(boolean subscribeUp) {
		this.subscribeUp = subscribeUp;
		return this;
	}

	public ThreadIgniteSubscribe setSubscribeResults(ConcurrentLinkedQueue<byte[]> subscribeResults) {
		this.subscribeResults = subscribeResults;
		return this;
	}

	public Thread getThreadPoller() {
		return threadPoller;
	}

	public ThreadIgniteSubscribe setThreadPoller(Thread threadPoller) {
		this.threadPoller = threadPoller;
		return this;
	}
}

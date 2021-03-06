/**
 *    Copyright 2016, 2017 Peter Zybrick and others.
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * 
 * @author  Pete Zybrick
 * @version 1.0.0, 2017-09
 * 
 */
package com.pzybrick.learn.avro2kafkawave;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import com.pzybrick.avro.schema.Wave;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;


/**
 * The Class AvroConsumerWaveThread.
 */
public class AvroConsumerWaveThread implements Runnable {
	
	/** The stream. */
	private KafkaStream<byte[], byte[]> stream;
	
	/** The thread number. */
	private int threadNumber;

	/**
	 * Instantiates a new avro consumer wave thread.
	 *
	 * @param stream the stream
	 * @param threadNumber the thread number
	 */
	public AvroConsumerWaveThread(KafkaStream<byte[], byte[]> stream, int threadNumber) {
		this.threadNumber = threadNumber;
		this.stream = stream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			BinaryDecoder binaryDecoder = null;
			Wave waveRead = null;
			DatumReader<Wave> datumReaderWave = new SpecificDatumReader<Wave>(Wave.getClassSchema());

			while (it.hasNext()) {
				MessageAndMetadata<byte[], byte[]> messageAndMetadata = it.next();
				String key = new String(messageAndMetadata.key());
				binaryDecoder = DecoderFactory.get().binaryDecoder(messageAndMetadata.message(), binaryDecoder);
				waveRead = datumReaderWave.read(waveRead, binaryDecoder);
				// User user = (User)
				// recordInjection.invert(messageAndMetadata.message()).get();
				String summary = ">>> CONSUMER: Thread " + threadNumber + ", topic=" + messageAndMetadata.topic() + ", partition="
						+ messageAndMetadata.partition() + ", key=" + key + ", offset="
						+ messageAndMetadata.offset() + ", timestamp=" + messageAndMetadata.timestamp()
						+ ", timestampType=" + messageAndMetadata.timestampType()
						+ ", waveRead=" + waveRead.toString();
				System.out.println(summary);
			}
			System.out.println("Shutting down Thread: " + threadNumber);
		} catch (Exception e) {
			System.out.println("Exception in thread "+threadNumber);
			System.out.println(e);
			e.printStackTrace();
		}
	}

}
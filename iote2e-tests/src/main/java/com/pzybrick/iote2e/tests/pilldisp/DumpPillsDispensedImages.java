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
package com.pzybrick.iote2e.tests.pilldisp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.pzybrick.iote2e.common.config.MasterConfig;
import com.pzybrick.iote2e.common.persist.ConfigDao;
import com.pzybrick.iote2e.stream.persist.PillsDispensedDao;


/**
 * The Class DumpPillsDispensedImages.
 */
public class DumpPillsDispensedImages {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			String masterConfigKey = args[0];
			String contactPoint = args[1];
			String keyspaceName = args[2];
			
			String tmpDir = "/tmp/iote2e-shared/images/";
			MasterConfig masterConfig = MasterConfig.getInstance( masterConfigKey, contactPoint, keyspaceName );
			List<String> allUuids = PillsDispensedDao.findAllPillsDispensedUuids( masterConfig );
			for( String uuid : allUuids ) {
				byte[] bytes =  PillsDispensedDao.findImageBytesByPillsDispensedUuid(masterConfig, uuid);
				String pathNameExt = tmpDir + uuid + ".png";
				System.out.println("Writing: " + pathNameExt );
				FileOutputStream fos = new FileOutputStream(new File(pathNameExt));
				fos.write(bytes);
				fos.close();
			}
			ConfigDao.connect(contactPoint, keyspaceName);
			System.out.println();
		} catch(Exception e) {
			System.out.println(e);
		}

	}

}

package test1;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.File;
import java.io.FileWriter;

import net.librec.data.model.TextDataModel;

public class DataProcessing {

	String pathTestData = "data/filmtrust/testData.dat";
	String pathTrainData = "data/filmtrust/trainData.dat";

	TextDataModel dataModel;

	public DataProcessing(TextDataModel dataModel) {
		this.dataModel = dataModel;

	}

	public String getItemKeyByValue(int value) {
		String ItemKey = "";
		Set<String> keysItem = new HashSet<String>();
		for (Entry<String, Integer> entry : dataModel.getItemMappingData().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				ItemKey = entry.getKey();
				break;
			}
		}
		return ItemKey;
	}

	public String getUserKeyByValue(int value) {
		String userKey = "";
		Set<String> KeysUser = new HashSet<String>();
		for (Entry<String, Integer> entry : dataModel.getUserMappingData().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				userKey = entry.getKey();
				break;
			}
		}
		return userKey;
	}

	public void writeTestDataAndTrainData() {

		for (int i = 0; i < dataModel.getDataSplitter().getTrainData().rowSize(); i++) {
			String userKey = getUserKeyByValue(i);
			for (int j = 0; j < dataModel.getDataSplitter().getTrainData().columnSize(); j++) {
				String itemKey = getItemKeyByValue(j);
				if (dataModel.getDataSplitter().getTrainData().get(i, j) != 0) {
					writeFile(userKey + "\t" + itemKey + "\t" + dataModel.getDataSplitter().getTrainData().get(i, j), pathTrainData);

				}
				if (dataModel.getDataSplitter().getTestData().get(i, j) != 0) {
					writeFile(userKey + "\t" + itemKey + "\t" + dataModel.getDataSplitter().getTestData().get(i, j), pathTestData);
				}
			}
		}
		
		System.out.println("Archivos guardados");

	}

	public void writeFile(String line, String path) {
		File file = new File(path);
		FileWriter fr = null;
		try {
			fr = new FileWriter(file, true);
			fr.write(line);
			fr.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close resources
			try {
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

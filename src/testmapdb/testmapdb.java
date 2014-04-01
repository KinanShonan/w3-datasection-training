package testmapdb;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * @author myname2
 * 
 */
public class testmapdb {

	public static final int MAX_NUMBER_OF_FILE = 40;
	public static final String PATH_OF_RESOURCE_DIRECTORY = "res\\";

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();

	}

	public static void writeDataIntoDB(
			ConcurrentNavigableMap<String, String> map) throws IOException {
		String filename, postfix, fullFilename, path, fullPath;
		filename = "000001";
		path = PATH_OF_RESOURCE_DIRECTORY;
		for (int i = 1; i < MAX_NUMBER_OF_FILE; i++) {
			postfix = "_" + i + ".json";
			fullFilename = filename + postfix;
			fullPath = path + fullFilename;
			map.put(fullFilename, readFile(fullPath, Charset.defaultCharset()));
			System.out.println("put" + i + "!");
		}
	}

	public static void readDataFromDB(ConcurrentNavigableMap<String, String> map)
			throws IOException {
		System.out.println(map.get("000001_1.json"));
	}

	public static void main(String[] args) throws IOException {

		// Configure and open database using builder pattern.
		// All options are available with code auto-completion.
		File dbFile = File.createTempFile("mapdb", "db");
		DB db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown()
				.encryptionEnable("datasection").make();

		// open an collection, TreeMap has better performance then HashMap
		ConcurrentNavigableMap<String, String> map = db
				.getTreeMap("jsonDatabase");
		writeDataIntoDB(map);
		db.commit(); // persist changes into disk
		// db.rollback(); // revert recent changes
		readDataFromDB(map);
		db.close();
	}
}

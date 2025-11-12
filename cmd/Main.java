package cmd;
//SUPER SPARKING! Dataminer by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {
	public static double splitGscData(File gscData, File pakData) throws IOException {
		long start = System.currentTimeMillis();
		byte[] input = new byte[4], gscf = {0x47, 0x53, 0x43, 0x46};
		int numGsc = 50, gscId = 0, pos = 0;
		String prefix = "GSC-B-", suffix = ".gsc", zero = "";
		Path gscFolderPath = gscData.toPath().getParent().resolve("GSC");
		gscFolderPath.toFile().mkdir();
		RandomAccessFile gscRaf = new RandomAccessFile(gscData, "r");
		while (pos < gscRaf.length()) {
			gscRaf.read(input);
			pos+=4;
			if (Arrays.equals(input, gscf)) {
				if (gscId < 10) zero = "0";
				else zero = "";
				String gscName = prefix + zero + gscId + suffix;
				System.out.println("PROGRESS (" + (gscId + 1) + "/" + numGsc + "): " + gscName + " found!");
				pos+=4;
				gscRaf.seek(pos);
				int gscSize = LittleEndian.getInt(gscRaf.readInt()) + 32;
				pos-=8;
				gscRaf.seek(pos);
				byte[] gscBytes = new byte[gscSize];
				System.out.println("Reading " + gscName + " contents from " + gscData + "...");
				gscRaf.read(gscBytes);
				pos+=gscSize;
				Path gscPath = gscFolderPath.resolve(gscName);
				RandomAccessFile gsc = new RandomAccessFile(gscPath.toString(), "rw");
				System.out.println("Saving GSC contents in " + gscPath + "...");
				gsc.write(gscBytes);
				gsc.close();
				gscId++;
			}
			gscRaf.seek(pos);
		}
		gscRaf.close();
		gscId = 0; pos = 0;
		input = new byte[8];
		prefix = "TXT-JP-B-"; suffix = ".pak";
		byte[] pakHeader = {44, 1, 0, 0, -64, 4, 0, 0};
		int pakCnt = 0;
		RandomAccessFile pakDataRaf = new RandomAccessFile(pakData, "r");
		while (pos < pakDataRaf.length()) {
			pakDataRaf.read(input);
			pos+=8;
			if (Arrays.equals(input, pakHeader)) {
				if (gscId < 10) zero = "0";
				else zero = "";
				if (pakCnt == 50) prefix = prefix.replace("TXT", "LPS");
				else if (pakCnt == 100) prefix = prefix.replace("JP", "US");
				if (pakCnt % 50 == 0) gscId = 0;
				String pakName = prefix + zero + gscId + suffix;
				System.out.println("PROGRESS (" + (gscId + 1) + "/" + numGsc + "): " + pakName + " found!");
				pos+=1196; //skip PAK index entries to get to the last one, aka the file size
				pakDataRaf.seek(pos);
				int pakSize = LittleEndian.getInt(pakDataRaf.readInt());
				pos-=1204; //go back to beginning of PAK
				pakDataRaf.seek(pos);
				byte[] pakBytes = new byte[pakSize];
				System.out.println("Reading " + pakName + " contents from " + pakData + "...");
				pakDataRaf.read(pakBytes);
				pos+=pakSize;
				//round up position if map file size is not a multiple of 16
				if (pos % 16 != 0) pos = pos + 16 - (pos % 16);
				Path pakPath = gscFolderPath.resolve(pakName);
				RandomAccessFile pak = new RandomAccessFile(pakPath.toString(), "rw");
				System.out.println("Saving PAK contents in " + pakPath + "...");
				pak.write(pakBytes);
				pak.close();
				gscId++; pakCnt++;
			}
			pakDataRaf.seek(pos);
		}
		pakDataRaf.close();
		long end = System.currentTimeMillis();
		return (end - start) / 1000.0;
	}
	public static double splitMapData(File mapData, File mapMetadata) throws IOException {
		long start = System.currentTimeMillis();
		int numMaps = 300; //150 for 1P vs COM, 150 for 1P vs 2P
		int[] mapFileSizes = new int[numMaps];
		String[] mapFileNames = new String[numMaps];
		Path mapFolderPath = mapData.toPath().getParent().resolve("Maps"), mapPath;
		mapFolderPath.toFile().mkdir();
		RandomAccessFile metadataRaf = new RandomAccessFile(mapMetadata, "r");
		RandomAccessFile dataRaf = new RandomAccessFile(mapData, "r");
		RandomAccessFile[] mapPaks = new RandomAccessFile[numMaps];
		for (int i=0; i<numMaps; i++) {
			byte[] fileNameBytes = new byte[14];
			metadataRaf.seek(i * 48);
			metadataRaf.read(fileNameBytes);
			metadataRaf.seek(i * 48 + 44);
			int fileSize = LittleEndian.getInt(metadataRaf.readInt());
			mapFileNames[i] = new String(fileNameBytes);
			mapFileSizes[i] = fileSize;
			mapPath = mapFolderPath.resolve(mapFileNames[i]);
			mapPaks[i] = new RandomAccessFile(mapPath.toString(), "rw");
		}
		metadataRaf.close();
		byte[] input = new byte[16], mapMdlHeader = new byte[16];
		mapMdlHeader[0] = 18;
		for (int i=12; i<16; i++) mapMdlHeader[i] = -1;
		int mapId = 0, pos = 0;
		while (pos < dataRaf.length()) {
			dataRaf.read(input);
			pos+=16;
			if (Arrays.equals(input, mapMdlHeader)) {
				System.out.println("PROGRESS (" + (mapId + 1) + "/" + numMaps + "): " + mapFileNames[mapId] + " found!");
				pos-=144;
				dataRaf.seek(pos);
				byte[] mapPakContents = new byte[mapFileSizes[mapId]];
				System.out.println("Reading " + mapFileNames[mapId] + " contents from " + mapData + "...");
				dataRaf.read(mapPakContents);
				pos+=mapFileSizes[mapId];
				//round up position if map file size is not a multiple of 16
				if (pos % 16 != 0) pos = pos + 16 - (pos % 16);
				mapPath = mapFolderPath.resolve(mapFileNames[mapId]);
				System.out.println("Saving map contents in " + mapPath + "...");
				mapPaks[mapId].write(mapPakContents);
				mapPaks[mapId].close();
				mapId++;
			}
			dataRaf.seek(pos);
		}
		dataRaf.close();
		long end = System.currentTimeMillis();
		return (end - start) / 1000.0;
	}
	public static void main(String[] args) {
		try {
			if (args.length > 0) {
				String usage = "USAGE: java -jar super-sparking-miner.jar ";
				if (args[0].equals("-h")) {
					System.out.println(usage + "-m \"path/to/data\" \"path/to/metadata\"");
					System.out.println(usage + "-g \"path/to/gsc/data\" \"path/to/gsc/txt/lps\"");
				}
				else {
					File[] dirs = new File[2];
					for (int i=1; i<3; i++) dirs[i-1] = new File(args[i].replace("\"", ""));
					if (dirs[0].isFile() && dirs[1].isFile()) {
						double time = -1;
						if (args[0].equals("-m")) time = splitMapData(dirs[0], dirs[1]);
						else if (args[0].equals("-g")) time = splitGscData(dirs[0], dirs[1]);
						System.out.printf("TIME: %f s\n", time);
					}
					else System.out.println("ERROR: Arguments are not valid paths!");
				}
			} else System.out.println("ERROR: No arguments provided!");
		} catch (Exception e) {e.printStackTrace();}
	}
}
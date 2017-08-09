package nedhyett.crimson.utility;

import java.io.IOException;

/**
 * (Created on 22/01/2017)
 *
 * @author ned
 */
public class HardwareUtils {

	private static String getSysCtl(String type) {
		try {
			return new String(ExecutionUtils.executeAndCapture("sysctl", type)).replace(type + ": ", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long parseMemInfo(String[] ms) {
		if (ms.length < 2) return 0L;
		long memory = Long.parseLong(ms[1]);
		if (ms.length > 2 && "kB".equals(ms[2])) memory *= 1024;
		return memory;
	}

	public static long getTotalSystemMemoryMB() {
		switch(EnumOS.getOS()) {
			case WINDOWS:
				break;
			case MACOSX:
				break;
			case LINUX:
				try {
					String[] memTotal = new String(ExecutionUtils.executeAndCapture("cat", "/proc/meminfo | grep MemTotal")).split("\\s+");
					return parseMemInfo(memTotal);
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
		}
		return 8192;
	}

//	public static long getMemoryFreeMB() {
//
//	}


}

/*
 * 文件名称 : FileUtil.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:38:58
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package mobi.dlys.android.core.commonutils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.text.TextUtils;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class FileUtil {

	/**
	 * @param subPaths
	 * @return
	 */
	public static String joinPath(String... subPaths) {
		StringBuilder path = new StringBuilder();

		for (int i = 0; i < subPaths.length; i++) {
			String onePath = subPaths[i].trim();
			while (onePath.endsWith("\\") || onePath.endsWith("/")) {
				onePath = onePath.substring(0, onePath.length() - 1);
			}
			if (i == 0) {

			} else {
				while (onePath.endsWith("\\") || onePath.endsWith("/")) {
					onePath = onePath.substring(1, onePath.length());
				}
				path.append(File.separator);
			}
			path.append(onePath);
		}

		return path.toString();
	}

	public static boolean ensureDir(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}

		boolean ret = false;

		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			try {
				ret = file.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public static boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static boolean deleteFile(String path) {
		if (null == path) {
			return false;
		}
		boolean ret = false;

		File file = new File(path);
		if (file.exists()) {
			ret = file.delete();
		}
		return ret;
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileSuffix(String fileName) {
		String fileType = null;
		if (fileName != null) {
			int idx = fileName.lastIndexOf(".");
			if (idx > 0) {
				fileType = fileName.substring(idx + 1, fileName.length()).toLowerCase();
			}
		}
		return fileType;
	}

	public static String getFileNameFromPath(String filePath) {
		String name = null;
		if (filePath != null) {
			int idx = filePath.lastIndexOf("/");
			if (idx > 0) {
				name = filePath.substring(idx + 1, filePath.length()).toLowerCase();
			} else {
				name = filePath;
			}
		}
		return name;
	}

	/**
	 * 返回文件的所在的目录的绝对路径
	 * 
	 * @param filePath
	 * @return 返回文件的所在的目录的绝对路径,不含最后的斜杠分隔符
	 */
	public static String getFileParentAbsolutePath(String filePath) {
		File file = new File(filePath);
		return file.getParent();
	}

	public static void copySdcardFile(InputStream is, OutputStream os) throws IOException {
		byte bt[] = new byte[1024];
		int c;
		while ((c = is.read(bt)) > 0) {
			os.write(bt, 0, c);
		}
	}

	/**
	 * 判断两个路径是否相等 大小写不敏感 : 存储卡的文件系统一般为FAT, 大小写不敏感
	 * 
	 * @param pathSrc
	 * @param pathDst
	 * @return
	 */
	public static boolean isPathEqual(final String pathSrc, final String pathDst) {
		if (pathSrc == null || pathDst == null) {
			return false;
		}

		String path1 = pathSrc.endsWith("/") ? pathSrc : pathSrc + "/";
		String path2 = pathDst.endsWith("/") ? pathDst : pathDst + "/";
		boolean isEqual = path1.equalsIgnoreCase(path2);
		return isEqual;
	}

	/**
	 * 压缩文件到zip. 如果耗时可以放在子线程里进行
	 * 
	 * @param srcFilePath
	 * @return 如果成功，zip文件名，失败null
	 */
	public static String zipFile(final String srcFilePath) {
		if (srcFilePath == null)
			return null;

		File srcFile = new File(srcFilePath);
		if (!srcFile.exists())
			return null;
		String destFileName = null;
		try {
			FileInputStream srcInput = new FileInputStream(srcFile);
			BufferedInputStream srcBuffer = new BufferedInputStream(srcInput);
			byte[] buf = new byte[1024];
			int len;
			destFileName = srcFilePath + ".zip";
			File destFile = new File(destFileName);
			if (destFile.exists())
				destFile.delete();

			FileOutputStream destFileStream = new FileOutputStream(destFileName);
			BufferedOutputStream destBuffer = new BufferedOutputStream(destFileStream);
			ZipOutputStream zipStream = new ZipOutputStream(destBuffer);// 压缩包
			ZipEntry zipEntry = new ZipEntry(srcFile.getName());// 这是压缩包名里的文件名
			zipStream.putNextEntry(zipEntry);// 写入新的 ZIP 文件条目并将流定位到条目数据的开始处

			while ((len = srcBuffer.read(buf)) != -1) {
				zipStream.write(buf, 0, len);
				zipStream.flush();
			}

			srcBuffer.close();
			zipStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return destFileName;
	}

	/**
	 * 获取文件类型（后缀）
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getFileTypeByName(String name, String defaultValue) {
		String type = defaultValue;
		if (name != null) {
			int idx = name.lastIndexOf(".");
			if (idx != -1) {
				type = name.substring(idx + 1, name.length());
			}
		}
		return type;
	}

	public static boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}

	public static boolean createFileIfNotExist(String pathname) {
		File file = new File(pathname);

		if (file.exists() && !file.isDirectory()) {
			return true;
		} else {
			File parent = file.getParentFile();
			if (parent.exists() && parent.isDirectory()) {
				try {
					return file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				if (parent.mkdirs()) {
					try {
						return file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				} else {
					return false;
				}
			}
		}
	}

}

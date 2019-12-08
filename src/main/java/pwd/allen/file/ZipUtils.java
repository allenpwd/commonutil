package pwd.allen.file;

import org.apache.commons.lang.CharSet;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Zip压缩解压缩
 *
 * @author 门那粒沙
 * @create 2019-12-07 21:25
 */

/**
 *
 * @version 1.0
 * @since 2015-9-11
 * @category com.feng.util
 *
 */
public final class ZipUtils {

    /**
     * 缓冲大小
     */
    private static int BUFFERSIZE = 2 << 10;

    /**
     * 压缩
     * @param paths
     * @param fileName
     */
    public static void zip(String[] paths, String fileName) {

        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(fileName));
            for (String filePath : paths) {
                //递归压缩文件
                File file = new File(filePath);
                String relativePath = file.getName();
                if (file.isDirectory()) {
                    relativePath += File.separator;
                }
                zipFile(file, relativePath, zos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void zipFile(File file, String relativePath, ZipOutputStream zos) {
        InputStream is = null;
        try {
            if (!file.isDirectory()) {
                ZipEntry zp = new ZipEntry(relativePath);
                zos.putNextEntry(zp);
                is = new FileInputStream(file);
                byte[] buffer = new byte[BUFFERSIZE];
                int length = 0;
                while ((length = is.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
                zos.flush();
                zos.closeEntry();
            } else {
                String tempPath = null;
                for (File f : file.listFiles()) {
                    tempPath = relativePath + f.getName();
                    if (f.isDirectory()) {
                        tempPath += File.separator;
                    }
                    zipFile(f, tempPath, zos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩
     * @param fileName
     * @param path
     * @param charset
     * @param filter 过滤文件，返回false则不解压
     */
    public static void unzip(String fileName, String path, Charset charset, FileFilter filter) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            ZipFile zf = new ZipFile(new File(fileName), charset == null ? Charset.defaultCharset() : charset);
            Enumeration en = zf.entries();
            while (en.hasMoreElements()) {
                ZipEntry zn = (ZipEntry) en.nextElement();
                if (filter != null && !filter.ifDone(zn)) continue;
                if (!zn.isDirectory()) {
                    is = zf.getInputStream(zn);
                    File f = new File(path + zn.getName());
                    File file = f.getParentFile();
                    file.mkdirs();
                    fos = new FileOutputStream(path + zn.getName());
                    int len = 0;
                    byte bufer[] = new byte[BUFFERSIZE];
                    while (-1 != (len = is.read(bufer))) {
                        fos.write(bufer, 0, len);
                    }
                    fos.close();
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface FileFilter {
        public boolean ifDone(ZipEntry entry);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
//        zip(new String[]{"C:\\Users\\Administrator\\Desktop\\问题.txt", "C:\\Users\\Administrator\\Desktop\\Turbo-Always.mp3"}, "C:\\Users\\Administrator\\Desktop\\abc.zip");
        unzip("C:\\Users\\Administrator\\Desktop\\abc.zip", "C:\\Users\\Administrator\\Desktop\\abc\\", null, null);
    }
}
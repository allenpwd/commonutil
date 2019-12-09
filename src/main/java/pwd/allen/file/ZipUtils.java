package pwd.allen.file;

import org.apache.http.util.Asserts;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩解压缩
 *
 * @author 门那粒沙
 * @create 2019-12-07 21:25
 */
public final class ZipUtils {

    /**
     * 缓冲大小
     */
    private static int BUFFERSIZE = 2 << 10;

    /**
     * 压缩指定文件或者目录
     *
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

    private static void zipFile(File file, String relativePath, ZipOutputStream zos) {
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
     * 解压指定的zip
     * @param fileName  要解压的zip文件
     * @param path  解压到指定的目录
     * @param charset   zip编码 没有则系统默认
     * @param filter 文件过滤器
     */
    public static void unzip(String fileName, String path, Charset charset, FileFilter filter) {
        FileOutputStream fos = null;
        InputStream is = null;
        ZipFile zf = null;
        Asserts.notEmpty(path, "path");
        if (!path.endsWith(File.separator)) path += File.separator;
        try {
            zf = new ZipFile(new File(fileName), charset == null ? Charset.defaultCharset() : charset);
            Enumeration en = zf.entries();
            while (en.hasMoreElements()) {
                ZipEntry zn = (ZipEntry) en.nextElement();
                if (filter != null && !filter.ifDone(zn)) continue;
                if (!zn.isDirectory()) {
                    is = zf.getInputStream(zn);
                    File f = new File(path + zn.getName());
                    f.getParentFile().mkdirs();
                    fos = new FileOutputStream(f);
                    int len = 0;
                    byte bufer[] = new byte[BUFFERSIZE];
                    while (-1 != (len = is.read(bufer))) {
                        fos.write(bufer, 0, len);
                    }
//                    is.close();
//                    fos.close();
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
//                if (null != is) {
//                    is.close();
//                }
//                if (null != fos) {
//                    fos.close();
//                }
                if (null != zf) {
                    zf.close();
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
        zip(new String[]{"C:\\Users\\\\lenovo\\Desktop\\git", "C:\\Users\\\\lenovo\\Desktop\\eiac的xml.xml"}, "C:\\Users\\\\lenovo\\Desktop\\abc.zip");
        unzip("C:\\Users\\lenovo\\Desktop\\abc.zip", "C:\\Users\\lenovo\\Desktop\\abc", null, null);
    }
}
package org.springultron.core.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 文件操作工具
 *
 * @Auther: brucewuu
 * @Date: 2019-07-21 10:57
 * @Description:
 */
public class FileUtils extends FileCopyUtils {

    /**
     * 获取jar包运行时的当前目录
     *
     * @return {String}
     */
    @Nullable
    public static String getJarPath() {
        try {
            URL url = FileUtils.class.getResource(Strings.SLASH).toURI().toURL();
            return FileUtils.toFilePath(url);
        } catch (Exception e) {
            String path = FileUtils.class.getResource(Strings.EMPTY).getPath();
            return new File(path).getParentFile().getParentFile().getAbsolutePath();
        }
    }

    @Nullable
    public static String toFilePath(@Nullable URL url) {
        if (url == null) { return null; }
        String protocol = url.getProtocol();
        String file = UriUtils.decode(url.getPath(), StandardCharsets.UTF_8);
        if (ResourceUtils.URL_PROTOCOL_FILE.equals(protocol)) {
            return new File(file).getParentFile().getParentFile().getAbsolutePath();
        } else if (ResourceUtils.URL_PROTOCOL_JAR.equals(protocol)
                || ResourceUtils.URL_PROTOCOL_ZIP.equals(protocol)) {
            int ipos = file.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
            if (ipos > 0) {
                file = file.substring(0, ipos);
            }
            if (file.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                file = file.substring(ResourceUtils.FILE_URL_PREFIX.length());
            }
            return new File(file).getParentFile().getAbsolutePath();
        }
        return file;
    }
}
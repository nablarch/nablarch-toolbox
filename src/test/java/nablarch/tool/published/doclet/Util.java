package nablarch.tool.published.doclet;

import java.io.File;

import nablarch.core.util.Builder;

/**
 * @author T.Kawasaki
 */
final class Util {

    static String join(String... classpathElements) {
        for (String element : classpathElements) {
            assert new File(element).exists() : element;
        }
        return Builder.join(classpathElements,
                            System.getProperty("path.separator"));
    }

    static File prepare(String path) {
        File dest = new File(path);
        deleteDir(dest);
        assert dest.mkdirs() : dest.getAbsolutePath();
        return dest;
    }

    static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }
        for (File e : dir.listFiles()) {
            if (e.isDirectory()) deleteDir(e);
            else delete(e);
        }
        delete(dir);
    }

    static void delete(File f) {
        boolean success = f.delete();
        String path = f.getAbsolutePath();
        if (success) {
            System.out.println("delete file : " + path);
        } else {
            throw new IllegalStateException("deleting file failed. " + path);
        }
    }
    private Util() {
    }
}

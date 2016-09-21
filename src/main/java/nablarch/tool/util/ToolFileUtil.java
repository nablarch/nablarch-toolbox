package nablarch.tool.util;

import java.io.File;

import nablarch.core.util.FileUtil;

/**
 * ファイル操作に関するユーティリティクラス。
 * {@link FileUtil}に存在しない操作で、ツールに必要なものを実装する。
 *
 * @author T.Kawasaki
 */
public final class ToolFileUtil {

    /** 隠蔽コンストラクタ */
    private ToolFileUtil() {
    }

    /**
     * ファイルの削除と削除確認を行う。
     *
     * @param target 削除対象のファイル
     * @throws IllegalStateException 削除に失敗した場合
     */
    public static void deleteAndCheck(File target) throws IllegalStateException {
        boolean success = FileUtil.deleteFile(target);
        if (!success) {
            throw new IllegalStateException(
                    "could not delete file [" + target.getAbsolutePath() + "]");
        }
    }

    /**
     * ファイルに書き込み許可を付与する。
     *
     * @param file     付与対象のファイル
     * @param writable 書き込み可の場合は真、読み取り専用の場合は偽
     * @throws IllegalStateException 操作に失敗した場合
     */
    public static void setWritable(File file, boolean writable) throws IllegalStateException {
        boolean success = file.setWritable(writable);
        if (!success) {
            throw new IllegalStateException(
                    "could not set writable to [" + file.getAbsolutePath() + "]");
        }
    }

    /**
     * ディレクトリを削除する。
     *
     * @param dir 削除対象ディレクトリ
     */
    public static void deleteDir(File dir) {
        checkDirectory(dir);
        if (!dir.exists()) {
            return;
        }

        for (File file : getFilesOf(dir)) {
            if (file.isDirectory()) {
                deleteDir(file); // recursive call.
            } else {
                deleteAndCheck(file);
            }
        }
        deleteAndCheck(dir);
    }

    /**
     * 与えられた{@link File}がディレクトリであることを確認する。
     *
     * @param target 確認対象の{@link File}
     * @throws IllegalArgumentException 引数がnullまたはファイルである場合
     */
    private static void checkDirectory(File target) throws IllegalArgumentException {
        if (target == null) {
            throw new IllegalArgumentException("argument must not be null.");
        }
        if (target.isFile()) {
            throw new IllegalArgumentException(
                    "argument must be directory. [" + target.getAbsolutePath() + "]");
        }
    }

    /**
     * 指定したディレクトリ配下のファイルを取得する。
     *
     * @param dir ディレクトリ
     * @return ファイル
     * @throws IllegalArgumentException 引数がnullまたは不正なパスである場合
     */
    public static File[] getFilesOf(File dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("argument must not be null.");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("could not get files of [" + dir + "]");
        }
        return files;
    }


}

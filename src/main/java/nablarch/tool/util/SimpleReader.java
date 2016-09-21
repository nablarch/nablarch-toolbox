package nablarch.tool.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nablarch.core.util.FileUtil;

/**
 * 簡単なファイル読み込み用のクラス
 * 
 * @author Kenta.Morimoto
 *
 */
public final class SimpleReader {

    /** 隠蔽コンストラクタ */
    private SimpleReader() {
    }

    /**
     * ファイルの内容を文字列に変換する。
     * 実行速度優先で作成したため、
     * 大きくないファイル（高々数メガ程度）で使用すること。
     * 
     * @param url ファイルのURL
     * @param charset 読み込むファイルのエンコーディング
     * @return ファイルの内容
     * @throws IOException
     */
    public static String fileToString(String url, String charset) {
        File readFile = new File(url);

        return fileToString(readFile, charset);
    }

    /**
     * ファイルの内容を文字列に変換する。
     * 実行速度優先で作成したため、
     * 大きくないファイル（高々数メガ程度）で使用すること。
     * 
     * @param file ファイル
     * @param charset 読み込むファイルのエンコーディング
     * @return ファイルの内容を文字列化したもの
     */
    public static String fileToString(File file, String charset) {

        DataInputStream inputStream = null;
        String fileContent = null;
        try {
            inputStream = new DataInputStream(new FileInputStream(file));

            byte[] buffer = new byte[(int) file.length()];
            inputStream.readFully(buffer);
            fileContent = new String(buffer, charset);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File is not found. file=[" + file.getAbsolutePath() + "]", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("this encoding is not supported. encoding=[" + charset + "]", e);
        } catch (EOFException e) {
            //ファイルサイズをもとに格納する配列を作成しているので、通常は発生しない。
            throw new RuntimeException("EOFException occurred. file=[" + file.getAbsolutePath() + "]", e);
        } catch (IOException e) {
            //ディスク障害等で読み込みができなかった場合に発生する。
            throw new RuntimeException("IOException occurred. file=[" + file.getAbsolutePath() + "]", e);
        } finally {
            FileUtil.closeQuietly(inputStream);
        }
        return fileContent;
    }

}

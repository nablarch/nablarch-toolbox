package nablarch.tool.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import nablarch.test.tool.htmlcheck.util.FileUtil;

/**
 * 簡易的にファイルに書き込むクラス。
 *
 * @author T.Kawasaki
 */
public abstract class SimpleWriter {

    /** ライター */
    private final BufferedWriter writer;

    /** 出力先ファイル */
    private final File outFile;

    /**
     * コンストラクタ。
     *
     * @param writer 出力に使用するライター
     */
    protected SimpleWriter(Writer writer) {
        this.outFile = null;
        this.writer = new BufferedWriter(writer);
    }

    /**
     * コンストラクタ。
     * エンコーディングにはUTF-8を使用する。
     *
     * @param outFile 出力先ファイル
     */
    protected SimpleWriter(File outFile) {
        this(outFile, Charset.forName("UTF-8"));
    }

    /**
     * コンストラクタ。
     *
     * @param outFile        出力先ファイル
     * @param outputEncoding エンコーディング
     */
    protected SimpleWriter(File outFile, Charset outputEncoding) {
        this.outFile = outFile;
        try {
            writer = open(outFile, outputEncoding);
        } catch (IOException e) {
            throw new RuntimeException(
                    "IOException occurred. file=[" + outFile.getAbsolutePath() + "]", e);
        }
    }

    /** 出力する。 */
    public void write() {
        try {
            writeWith(writer);
        } catch (IOException e) {
            throw new RuntimeException(
                    "an exception occurred. file=[" + outFile.getAbsolutePath() + "]", e);
        } finally {
            FileUtil.closeQuietly(writer);
        }
    }

    /**
     * 与えられたライターを使用してファイル書き込みを行う。
     *
     * @param writer 出力に使用するライター
     * @throws IOException 入出力例外
     */
    protected abstract void writeWith(BufferedWriter writer) throws IOException;

    /**
     * ファイルをオープンする。
     *
     * @param file     出力先ファイル
     * @param encoding エンコーディング
     * @return ライター
     * @throws IOException 入出力例外
     */
    private BufferedWriter open(File file, Charset encoding) throws IOException {
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
        }
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
    }


    /**
     * 簡易書き込みを行う。
     *
     * @param outFilePath  出力先ファイルパス
     * @param content      出力内容
     * @param fileEncoding エンコーディング
     */
    public static void write(String outFilePath, final String content, Charset fileEncoding) {
        File outFile = new File(outFilePath);
        write(outFile, content, fileEncoding);
    }

    /**
     * 簡易書き込みを行う。
     *
     * @param outFile      出力先ファイル
     * @param content      出力内容
     * @param fileEncoding エンコーディング
     */
    public static void write(File outFile, final String content, Charset fileEncoding) {
        new SimpleWriter(outFile, fileEncoding) {
            @Override
            protected void writeWith(BufferedWriter writer) throws IOException {
                writer.write(content);
            }
        }
        .write();
    }

    /**
     * 簡易書き込みを行い、出力ファイルを読み取り専用にする。
     *
     * @param outFile      出力先ファイル
     * @param content      出力内容
     * @param fileEncoding エンコーディング
     */
    public static void writeAndSeal(File outFile, final String content, Charset fileEncoding) {

        // 書き込み可能にする。
        if (outFile.exists()) {
            ToolFileUtil.setWritable(outFile, true);
        }

        // 書き込みを行う。
        write(outFile, content, fileEncoding);

        // 読み取り専用にする。
        ToolFileUtil.setWritable(outFile, false);
    }
}

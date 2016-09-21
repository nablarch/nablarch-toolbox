package nablarch.tool.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import nablarch.core.util.FileUtil;

/**
 * CSV形式のデータに関するユーティリティクラス
 * 
 * @author Kenta.Morimoto
 */
public final class CsvUtil {

    /** 隠蔽コンストラクタ */
    private CsvUtil() {
    }

    /**
     * {@literal List<Map<String, String>>}構造のデータをCSV形式の文字列に変換し、ファイルに出力する。
     * 変換の仕様は以下の通り<br />
     * 先頭行：List内の先頭のMapのキー値の一覧(Mapへの登録順)<br />
     * 2行目以降：Mapの値（要素の順番はキー値の順番に準拠）<br />
     * inputDataに空のリストが渡された際は、空のCSVファイルが生成される。<br />
     * 
     * @param inputData 変換する{@literal List<Map<String, String>>}型のデータ
     * @param outputFilePath 出力先のファイルパス
     * @param charset 文字コード
     * @param lineSeparator 改行コード
     */
    public static void writeFile(List<Map<String, String>> inputData, String outputFilePath, String charset, String lineSeparator) {

        FileOutputStream outputStream = null;
        BufferedWriter writer = null;

        try {
            outputStream = new FileOutputStream(outputFilePath);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));

            String[] columnNameArray = new String[0];

            if (!inputData.isEmpty()) {
                columnNameArray = inputData.get(0).keySet().toArray(columnNameArray);

                writeHeader(columnNameArray, writer);
                writer.write(lineSeparator);

                for (Map<String, String> rowData : inputData) {
                    writeRowData(rowData, columnNameArray, writer);
                    writer.write(lineSeparator);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("this encoding is not supported. encoding=[" + charset + "]", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. FilePath=[" + outputFilePath + "]", e);
        } catch (IOException e) {
            //ディスク障害やディスクフル等で入力ができなかった場合に発生する。
            throw new RuntimeException("IOException occurred. file=[" + outputFilePath + "]", e);
        } finally {
            FileUtil.closeQuietly(writer, outputStream);
        }
        return;
    }

    /**
     * CSVのヘッダー部分を出力する
     * @param columnNameArray カラム名の配列
     * @param writer 書き込むwriterオブジェクト
     * @throws IOException 出力に失敗した際に発生する。
     */
    private static void writeHeader(String[] columnNameArray, BufferedWriter writer) throws IOException{
        for (int i = 0; i < columnNameArray.length; i++) {
            writer.write(columnNameArray[i]);
            if (i < columnNameArray.length - 1) {
                writer.write(",");
            }
        }
    }

    /**
     * CSVのデータ部を一行出力する。
     * @param rowData 出力する一行分のデータ
     * @param columnNameArray 出力するカラム名の配列
     * @param writer 書き込むwriterオブジェクト
     * @throws IOException 出力に失敗した際に発生する。
     */
    private static void writeRowData(Map<String, String> rowData, String[] columnNameArray, BufferedWriter writer) throws IOException{
        for (int i = 0; i < columnNameArray.length; i++) {
            writer.write(rowData.get(columnNameArray[i]));
            if (i < columnNameArray.length - 1) {
                writer.write(",");
            }
        }
    }
}

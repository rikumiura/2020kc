package kc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 繧ｽ繝ｼ繧ｹ繝輔ぃ繧､繝ｫ繧帝幕縺�, 荳譁�ｭ励★縺､蛻�ｊ蜃ｺ縺励※LexicalAnalyzer 縺ｫ貂｡縺吝ロ縺阪ｒ縺吶ｋ繧ｯ繝ｩ繧ｹ��
 */
class FileScanner {
    private BufferedReader sourceFile;    // 蜈･蜉帙ヵ繧｡繧､繝ｫ縺ｮ蜿ら�
    private String line;                  // 陦後ヰ繝�ヵ繧｡
    private int lineNumber;               // 陦後き繧ｦ繝ｳ繧ｿ
    private int columnNumber;             // 蛻励き繧ｦ繝ｳ繧ｿ
    private char currentCharacter;        // 隱ｭ縺ｿ縺ｨ繧頑枚蟄�
    private char nextCharacter;           // 蜈郁ｪｭ縺ｿ譁�ｭ�

    /**
     * 蠑墓焚 sourceFileName 縺ｧ謖�ｮ壹＆繧後◆繝輔ぃ繧､繝ｫ繧帝幕縺�, sourceFile 縺ｧ蜿ら�縺吶ｋ��
     * 謨咏ｧ第嶌 p. 210 繧ｽ繝ｼ繧ｹ繧ｳ繝ｼ繝� 10.1 縺ｧ縺ｯtry-with-resources 譁�ｒ逕ｨ縺�※繝輔ぃ繧､繝ｫ縺ｮ
     * 蜿ら�縺ｨ隱ｭ縺ｿ蜿悶ｊ繧剃ｸ蠎ｦ縺ｫ陦後▲縺ｦ縺�ｋ縺鯉ｼ後％縺ｮ繧ｳ繝ｳ繧ｹ繝医Λ繧ｯ繧ｿ縺ｧ縺ｯ繝輔ぃ繧､繝ｫ縺ｮ蜿ら�
     * 縺�縺代ｒ陦後≧��
     * 縺ｾ縺� lineNumber, columnNumber, currentCharacter, nextCharacter 繧貞�譛溷喧縺吶ｋ
     * @param sourceFileName 繧ｽ繝ｼ繧ｹ繝励Ο繧ｰ繝ｩ繝�縺ｮ繝輔ぃ繧､繝ｫ蜷�
     */
    FileScanner (String sourceFileName) {
        Path path = Paths.get (sourceFileName);
        // 繝輔ぃ繧､繝ｫ縺ｮ繧ｪ繝ｼ繝励Φ
        try {
            sourceFile = Files.newBufferedReader (path);
        } catch (IOException err_mes) {
            System.out.println (err_mes);
            System.exit (1);
        }

        // 蜷�ヵ繧｣繝ｼ繝ｫ繝峨�蛻晄悄蛹�
        lineNumber = 0;
        columnNumber = -1;
        nextCharacter = '\n';

        nextChar(); // nextChar縺ｫ繧医▲縺ｦ nextCharacter 縺ｫ蜈磯�ｭ譁�ｭ励ｒ隱ｭ縺ｿ霎ｼ繧
    }

    /**
     * sourceFile縺ｧ蜿ら�縺励※縺�ｋ繝輔ぃ繧､繝ｫ繧帝哩縺倥ｋ
     */
    void closeFile() {
        try {
            sourceFile.close();
        } catch (IOException err_mes) {
            System.out.println (err_mes);
            System.exit (1);
        }
    }

    /**
     * sourceFile 縺ｧ蜿ら�縺励※縺�ｋ繝輔ぃ繧､繝ｫ縺九ｉ荳陦瑚ｪｭ縺ｿ, 繝輔ぅ繝ｼ繝ｫ繝� line(譁�ｭ怜�螟画焚) 縺ｫ縺昴�陦後ｒ譬ｼ邏阪☆繧�
     * 謨咏ｧ第嶌 p. 210 繧ｽ繝ｼ繧ｹ繧ｳ繝ｼ繝� 10.1 縺ｧ縺ｯ while譁�〒蜈ｨ陦後ｒ隱ｭ縺ｿ蜿悶▲縺ｦ縺�ｋ縺鯉ｼ後％縺ｮ繝｡繧ｽ繝�ラ蜀�〒縺ｯ
     * while譁��菴ｿ繧上★1陦後□縺題ｪｭ縺ｿ蜿悶ｊ繝輔ぅ繝ｼ繝ｫ繝瑛ine 縺ｫ譬ｼ邏阪☆繧具ｼ�
     */
    void readNextLine() {
        try {
            if (sourceFile.ready()) { // sourceFile荳ｭ縺ｫ譛ｪ隱ｭ縺ｮ陦後′縺ゅｋ縺九ｒ遒ｺ隱� (萓句､�:IllegalStateException)
                /*
                 * nextLine繝｡繧ｽ繝�ラ縺ｧsourceFile縺九ｉ1陦瑚ｪｭ縺ｿ蜃ｺ縺� 隱ｭ縺ｿ蜃ｺ縺輔ｌ縺滓枚蟄怜�縺ｯ謾ｹ陦後さ繝ｼ繝峨ｒ蜷ｫ縺ｾ縺ｪ縺��縺ｧ
                 * 謾ｹ繧√※謾ｹ陦後さ繝ｼ繝峨ｒ縺､縺醍峩縺�
                 */
                line = sourceFile.readLine() + '\n';
            } else {
                line = null;
            }
        } catch (IOException err_mes) { // 萓句､悶� Exception 縺ｧ繧ｭ繝｣繝�メ縺励※繧ゅ＞縺�
            // 繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ蜃ｺ縺励お繝ｩ繝ｼ縺檎匱逕溘＠縺溘→縺阪�蜃ｦ逅�
            System.out.println (err_mes);
            closeFile();
            System.exit (1);
        }
    }
    /**
     * 荳譁�ｭ怜�隱ｭ縺ｿ縺吶ｋ縺溘ａ縺ｮ繝｡繧ｽ繝�ラ(nextCharacter 縺ｮ縺溘ａ縺ｮgetter)
     * @return nextCharacter 縺ｮ蜀�ｮｹ
     */
    char lookAhead() {
        return nextCharacter;
    }

    /**
     * 迴ｾ蝨ｨ隱ｭ繧薙〒縺�ｋ陦後�蜀�ｮｹ繧定ｿ斐☆繝｡繧ｽ繝�ラ(line 縺ｮ縺溘ａ縺ｮgetter)
     * @return line 縺ｮ蜀�ｮｹ
     */
    String getLine() {
        return line;
    }

    /**
     * 荳譁�ｭ怜�繧雁�縺礼畑縺ｮ繝｡繧ｽ繝�ラ�後ヵ繧｡繧､繝ｫ譛ｫ縺ｫ驕斐＠縺ｦ縺�◆繧�'\0'繧定ｿ斐☆
     * @return 蛻�ｊ蜃ｺ縺励◆譁�ｭ�
     */
    char nextChar() {
        currentCharacter = nextCharacter;
        if (nextCharacter == '\0') {
            // 繝輔ぃ繧､繝ｫ譛ｫ縺ｪ繧牙�騾壼虚菴應ｻ･螟紋ｽ輔ｂ縺励↑縺�
        } else if (nextCharacter == '\n') {
            // 陦梧忰縺ｮ蝣ｴ蜷�
            readNextLine(); // 谺｡縺ｮ陦後ｒ隱ｭ繧
            if (line != null) {
                // 隱ｭ繧薙□陦後′ null 縺ｧ縺ｪ縺��ｴ蜷�
                nextCharacter = line.charAt (0);
                ++lineNumber;
                columnNumber = 0;
            } else {
                // 隱ｭ繧薙□陦後′ null(繝輔ぃ繧､繝ｫ譛ｫ) 縺ｮ蝣ｴ蜷�
                nextCharacter = '\0'; // '\0'縺ｯ繝輔ぃ繧､繝ｫ譛ｫ繧定｡ｨ縺�
            }
        } else {
            // 繝輔ぃ繧､繝ｫ譛ｫ縺ｧ繧り｡梧忰縺ｧ繧ゅ↑縺��ｴ蜷茨ｼ�
            nextCharacter = line.charAt (++columnNumber);
        }
        return currentCharacter;
    }

    /**
     * 繧ｽ繝ｼ繧ｹ繝輔ぃ繧､繝ｫ縺ｮ縺ｩ縺ｮ驛ｨ蛻�ｒ繧ｹ繧ｭ繝｣繝ｳ縺励※縺�ｋ縺ｮ縺九ｒ陦ｨ縺呎枚蟄怜�繧定ｿ斐☆
     * @return 陦ｨ遉ｺ譁�ｭ怜�
     */
    String scanAt() {
        String message = lineNumber + "行目\n" + line;
        for (int i = 0; i < columnNumber - 1; ++i)
            message += " ";
        message += "*\n";
        return message;
    }

    public static void main (String args[]) {
        char nextChar;
        /*
         * 縺薙�繧ｯ繝ｩ繧ｹ縺ｮ繧､繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ繧剃ｽ懈�縺�, sourceFileScanner 縺ｨ縺�≧螟画焚 縺ｧ縺昴�繧､繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ繧貞盾辣ｧ縺吶ｋ��
         * 荳玖ｨ倥〒縺ｯ�後う繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ菴懈�縺ｮ髫� bsort.k 縺ｨ縺�≧繝輔ぃ繧､繝ｫ繧帝幕縺�
         */
        FileScanner sourceFileScanner = new FileScanner ("bsort.k");
            // "bsort.k" 縺ｮ莉｣繧上ｊ縺ｫ args[0] 縺ｫ縺吶ｋ縺ｨ螳溯｡梧凾縺ｫ蠑墓焚縺ｧ謖�ｮ壹＠縺溘ヵ繧｡繧､繝ｫ繧帝幕縺代ｋ

        // 蝠城｡�2.6逕ｨ縺ｮ繝輔ぃ繧､繝ｫ蜃ｺ蜉幃Κ蛻�
        while ((nextChar = sourceFileScanner.nextChar()) != '\0') {
            System.out.print(nextChar);
        }

        // 蝠城｡�2.5逕ｨ縺ｮ繝輔ぃ繧､繝ｫ蜃ｺ蜉幃Κ蛻�
        // do {
        //     sourceFileScanner.readNextLine();
        //     if (sourceFileScanner.getLine()==null) break;
        //     System.out.print(sourceFileScanner.getLine());
        // } while (true);

        sourceFileScanner.closeFile();
    }
}
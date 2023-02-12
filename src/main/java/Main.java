import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final String patternString = "abc";
    private static AtomicInteger beautifulString_3 = new AtomicInteger(0);
    private static AtomicInteger beautifulString_4 = new AtomicInteger(0);
    private static AtomicInteger beautifulString_5 = new AtomicInteger(0);

    private static void appendToCounter(String str) {
        if (str.length() == 3) {
            beautifulString_3.addAndGet(1);
        } else if (str.length() == 4) {
            beautifulString_4.addAndGet(1);
        } else {
            beautifulString_5.addAndGet(1);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void main(String[] args) {

        Random random = new Random();
        String[] texts = new String[1000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Runnable palindromeThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        int i = 0;
                        boolean hasMatch = true;
                        while (i < (str.length() - 1 - i)) {
                            if (str.charAt(i) != str.charAt(str.length() - 1 - i)) {
                                hasMatch = false;
                                break;
                            }
                            i++;
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(palindromeThread);


        Runnable sameLetterThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        char letter = str.charAt(0);
                        boolean hasMatch = true;
                        for (int i = 0; i < str.length(); i++) {
                            if (str.charAt(i) != letter) {
                                hasMatch = false;
                                break;
                            }
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(sameLetterThread);

        Runnable increasingLetterThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        boolean hasMatch = true;
                        for (int i = 0; i < str.length() - 1; i++) {
                            int letterNumber = patternString.indexOf(str.charAt(i));
                            if (letterNumber > patternString.indexOf(str.charAt(i + 1))) {
                                hasMatch = false;
                                break;
                            }
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(increasingLetterThread);

        executor.shutdown();

        System.out.println("Красивых слов с длиной 3: " + beautifulString_3 + " шт");
        System.out.println("Красивых слов с длиной 4: " + beautifulString_4 + " шт");
        System.out.println("Красивых слов с длиной 5: " + beautifulString_5 + " шт");
    }
}

package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Apriori 演算法 實作
 * Created by ogl4jo3 on 2017/3/10.
 */
public class Main {
    // FILE NAME: T15I7N0.5KD1K  T15I7N0.5KD10K  T15I7N0.5KD100K  T15I7N0.5KD1000K
    // Sup:     5   50  500 30000
    private static String FILE_NAME = "T15I7N0.5KD1000K.txt";
    private static int sup = 30000;
    private static PrintWriter writer;
    private static int frqItem = 0;
    private static int numItems = 0;       //物品總數

    public static void main(String[] args) throws IOException {
        System.out.println("java -jar XXXXXXXXX.txt 500");
        if (args.length != 2 && args.length != 0) {
            System.out.println("java -jar XXXXXXXXX.txt 500");
            return;
        } else if (args.length == 2) {
            FILE_NAME = args[0];
            sup = Integer.valueOf(args[1]);
        }
        System.out.println(FILE_NAME + " 執行中");
        long startTime = System.currentTimeMillis();
        ArrayList<int[]> itemSet = new ArrayList<>();
        String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String ANSWER_FILE_NAME = "ANSWER_" + FILE_NAME + timeLog + ".txt";
        writer = new PrintWriter(ANSWER_FILE_NAME, "UTF-8");

        // 計算物品總數
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
        while (bufferedReader.ready()) {
            String transaction = bufferedReader.readLine();
            String[] items = transaction.split(", ");
            for (String item : items) {
                int item_int = Integer.parseInt(item);
                if (item_int + 1 > numItems) numItems = item_int + 1;
            }
        }
        bufferedReader.close();

        itemSet = firstLargeItemSet(itemSet);    //第一個LargeItemSet
        frqItem += itemSet.size();
        while (itemSet.size() > 0) {
            itemSet = candidateGen(itemSet);    //產生candidate
            if (itemSet.size() != 0) {
                itemSet = calculateSup(itemSet);    //產生Large itemSet
            }
            frqItem += itemSet.size();
        }

        long totTime = System.currentTimeMillis() - startTime;  // 執行時間
        writer.println(FILE_NAME);   // txt檔名
        writer.println("frq item:" + frqItem);  //  L所獲得的總數 frq item
        writer.println("Using Time:" + totTime + " ms");    // 執行時間
        writer.println("Done");
        writer.close();
        System.out.println(FILE_NAME);    // txt檔名
        System.out.println("frq item:" + frqItem);  //  L所獲得的總數 frq item
        System.out.println("Using Time: " + totTime + " ms");   // 執行時間
        System.out.println("Done");
    }

    /**
     * 第一個LargeItemSet
     * param itemSet
     * return LargeItemSet
     * throws IOException
     */
    private static ArrayList<int[]> firstLargeItemSet(ArrayList<int[]> itemSet) throws IOException {
        ArrayList<int[]> LargeItemSet = new ArrayList<>(); // 由sup篩選後
        int[] count = new int[numItems];
        for (int i = 0; i < numItems; i++) {
            int[] candidate = {i};
            itemSet.add(candidate);
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
        while (bufferedReader.ready()) {
            String transaction = bufferedReader.readLine(); //逐行讀取transaction
            String[] items = transaction.split(", ");   //將transaction切割成String陣列
            int[] items_int = Arrays.stream(items).mapToInt(Integer::parseInt).toArray();//將String陣列轉成int陣列
            // foreach transaction 含有的item count+1
            for (int item : items_int) {
                count[item]++;
            }
        }
        bufferedReader.close();

        for (int i = 0; i < itemSet.size(); i++) {
            if (count[i] >= sup) {
                // count大於sup 便加入至LargeItemSet 並寫入txt
                LargeItemSet.add(itemSet.get(i));
                writer.println(Arrays.toString(itemSet.get(i)) + " sup:" + count[i]);//寫入txt
            }
        }
        return LargeItemSet;
    }

    /**
     * 由LargeItemSet組合產生candidate
     * param itemSet
     * return candidateItemSet
     * throws IOException
     */
    private static ArrayList<int[]> candidateGen(ArrayList<int[]> itemSet) throws IOException {
        ArrayList<int[]> candidateItemSet = new ArrayList<>();
        // candidateGen
        for (int i = 0; i < itemSet.size() - 1; i++) {
            for (int j = i + 1; j < itemSet.size(); j++) {  // j=i+1，只需要往後比對，因為{a,b}={b,a}
                // 若有兩個itemSet，其包含的n-1項(包含)以前都相同，
                // 便將兩個的n-1項(包含)以前的所有項目、兩個的第n項組合產生長度+1新的candidate。
                // n為各個itemSet的長度
                if (Arrays.equals(Arrays.copyOf(itemSet.get(i), itemSet.get(i).length - 1)
                        , Arrays.copyOf(itemSet.get(j), itemSet.get(j).length - 1))) {
                    int[] newCandidate = Arrays.copyOf(itemSet.get(i), itemSet.get(i).length + 1);
                    newCandidate[newCandidate.length - 1] = itemSet.get(j)[itemSet.get(j).length - 1];
                    candidateItemSet.add(newCandidate);
                }
            }
        }

        // prune 刪除多餘的candidate
        if (itemSet.get(0).length > 2) {
            for (int i = 0; i < candidateItemSet.size(); i++) {
                boolean match = true;
                for (int j = 0; j < candidateItemSet.get(0).length; j++) {
                    //將各個candidateItemSet分解
                    int[] subACandidateItemSet = new int[candidateItemSet.get(0).length - 1];
                    if (j == 0) {
                        System.arraycopy(candidateItemSet.get(i), 1, subACandidateItemSet, 0, subACandidateItemSet.length);
                    } else if (j == candidateItemSet.get(0).length - 1) {
                        System.arraycopy(candidateItemSet.get(i), 0, subACandidateItemSet, 0, subACandidateItemSet.length);
                    } else {
                        System.arraycopy(candidateItemSet.get(i), 0, subACandidateItemSet, 0, j);
                        System.arraycopy(candidateItemSet.get(i), j + 1, subACandidateItemSet, j
                                , subACandidateItemSet.length - j);
                    }
                    for (int k = 0; k < itemSet.size(); k++) {
                        if (Arrays.equals(itemSet.get(k), subACandidateItemSet)) {
                            break;
                        } else if (k == itemSet.size() - 1 && !Arrays.equals(itemSet.get(k), subACandidateItemSet)) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) {
                        //如果其中一個子項目不包含在上一個LargeItemSet便移除
                        candidateItemSet.remove(i);
                        break;
                    }

                }
            }
        }
        return candidateItemSet;
    }

    /**
     * 計算candidateItemSet出現次數，並由sup篩選後，產生Large itemSet
     * param  candidateItemSet
     * return LargeItemSet
     * throws IOException
     */
    private static ArrayList<int[]> calculateSup(ArrayList<int[]> candidateItemSet) throws IOException {
        ArrayList<int[]> LargeItemSet = new ArrayList<>(); // 由sup篩選後
        int[] count = new int[candidateItemSet.size()];

        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
        while (bufferedReader.ready()) {
            // 逐行進行sup的篩選   foreach transaction
            String line = bufferedReader.readLine();
            boolean[] trans = new boolean[numItems];  //numItems是Item總數
            String[] items = line.split(", "); //逐行由", "切割
            int[] items_int = Arrays.stream(items).mapToInt(Integer::parseInt).toArray(); //將String陣列轉int陣列
            for (int item : items_int) {
                trans[item] = true; //這個transaction含有的item設為 true
            }
            // foreach candidate
            for (int c = 0; c < candidateItemSet.size(); c++) {
                boolean match = true; // reset match to true
                int[] cand = candidateItemSet.get(c);
                // foreach item in the itemSet 檢查transaction是否有此item
                for (int xx : cand) {
                    if (!trans[xx]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    count[c]++;
                }
            }
        }
        bufferedReader.close();

        for (int i = 0; i < candidateItemSet.size(); i++) {
            if (count[i] >= sup) {
                // count大於sup 便加入至LargeItemSet 並寫入txt
                LargeItemSet.add(candidateItemSet.get(i));
                writer.println(Arrays.toString(candidateItemSet.get(i)) + " sup:" + count[i]);
            }
        }
        return LargeItemSet;
    }
}


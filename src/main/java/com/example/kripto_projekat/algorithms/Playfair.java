package com.example.kripto_projekat.algorithms;

import java.io.*;
import java.util.*;

public class Playfair {
    String key;
    String plainText;
    char[][] matrix = new char[5][5];

    public Playfair(String key, String plainText)
    {
        // convert all the characters to lowercase
        this.key = key.toLowerCase();

        this.plainText = plainText.toLowerCase();
    }

    // function to remove duplicate characters from the key
    public void cleanPlayFairKey()
    {
        LinkedHashSet<Character> set
                = new LinkedHashSet<Character>();

        String newKey = "";

        for (int i = 0; i < key.length(); i++)
            set.add(key.charAt(i));

        Iterator<Character> it = set.iterator();

        while (it.hasNext())
            newKey += (Character)it.next();

        key = newKey;
    }

    // function to generate playfair cipher key table
    public void generateCipherKey()
    {
        Set<Character> set = new HashSet<Character>();

        for (int i = 0; i < key.length(); i++)
        {
            if (key.charAt(i) == 'j')
                continue;
            set.add(key.charAt(i));
        }

        // remove repeated characters from the cipher key
        String tempKey = new String(key);

        for (int i = 0; i < 26; i++)
        {
            char ch = (char)(i + 97);
            if (ch == 'j')
                continue;

            if (!set.contains(ch))
                tempKey += ch;
        }

        // create cipher key table
        for (int i = 0, idx = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                matrix[i][j] = tempKey.charAt(idx++);

        System.out.println("Playfair Cipher Key Matrix:");

        for (int i = 0; i < 5; i++)
            System.out.println(Arrays.toString(matrix[i]));
    }

    // function to preprocess plaintext
    public String formatPlainText()
    {
        int len = plainText.length();

        StringBuilder messageBuilder = new StringBuilder();

        for (int i = 0; i < len; i++) {
            if (i + 1 < len && plainText.charAt(i) == 'j' && plainText.charAt(i) == plainText.charAt(i + 1)) {
                messageBuilder.append('i').append('x');
                i++; // skip the next character
            } else if (plainText.charAt(i) == 'j') {
                messageBuilder.append('i');
            } else {
                messageBuilder.append(plainText.charAt(i));
            }
        }

        // make the plaintext of even length
        if (messageBuilder.length() % 2 == 1) {
            messageBuilder.append('x'); // dummy character
        }

        String message = messageBuilder.toString();

        return message;


    }

    // function to group every two characters
    public String[] formPairs(String message)
    {
        int len = message.length();
        String[] pairs = new String[len / 2];

        for (int i = 0, cnt = 0; i < len / 2; i++)
            pairs[i] = message.substring(cnt, cnt += 2);

        return pairs;
    }

    // function to get position of character in key table
    public int[] getCharPos(char ch)
    {
        int[] keyPos = new int[2];

        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {

                if (matrix[i][j] == ch)
                {
                    keyPos[0] = i;
                    keyPos[1] = j;
                    break;
                }
            }
        }
        return keyPos;
    }

    public String encryptMessage()
    {
        String message = formatPlainText();
        String[] msgPairs = formPairs(message);
        String encText = "";

        for (int i = 0; i < msgPairs.length; i++)
        {
            char ch1 = msgPairs[i].charAt(0);
            char ch2 = msgPairs[i].charAt(1);
            int[] ch1Pos = getCharPos(ch1);
            int[] ch2Pos = getCharPos(ch2);

            //System.out.println("ch1Pos: " + Arrays.toString(ch1Pos));
           // System.out.println("ch2Pos: " + Arrays.toString(ch2Pos));

            // if both the characters are in the same row
            if (ch1Pos[0] == ch2Pos[0]) {
                ch1Pos[1] = (ch1Pos[1] + 1) % 5;
                ch2Pos[1] = (ch2Pos[1] + 1) % 5;
            }

            // if both the characters are in the same column
            else if (ch1Pos[1] == ch2Pos[1])
            {
                ch1Pos[0] = (ch1Pos[0] + 1) % 5;
                ch2Pos[0] = (ch2Pos[0] + 1) % 5;
            }

            // if both the characters are in different rows
            // and columns
            else {
                int temp = ch1Pos[1];
                ch1Pos[1] = ch2Pos[1];
                ch2Pos[1] = temp;
            }

            // get the corresponding cipher characters from
            // the key matrix
            encText = encText + matrix[ch1Pos[0]][ch1Pos[1]]
                    + matrix[ch2Pos[0]][ch2Pos[1]];
        }

        return encText;
    }
}
package com.me.tft_02.assassin.util.assassin;

import java.util.Random;

import org.bukkit.ChatColor;

public class MessageScrambler {

    private final Random random = new Random();

    /**
     * This will randomly add ChatColor.MAGIC so messages will get scrambled.
     *
     * @param msg Message that will get scrambled
     *
     * @return Scrambled message
     */
    public String Scrambled(String msg) {
        int halfLength;

        StringBuilder sb = new StringBuilder(msg);
        int start1;
        int end1;
        int start2;
        int end2;
        int scrambleLength1;
        int scrambleLength2;

        int length = msg.length();

        if (length == 1) {
            int scramble = random.nextInt(100);
            if (scramble > 50) {
                sb.insert(0, ChatColor.MAGIC);
            }
        }
        else if (length <= 3) {
            start1 = random.nextInt(length);
            end1 = random.nextInt(length + 1);
            sb.insert(start1, ChatColor.MAGIC);
            sb.insert(end1 + 2, ChatColor.RESET);
        }
        else if (length >= 4) {
            halfLength = length / 2;
            scrambleLength1 = random.nextInt(5) + 3;
            scrambleLength2 = random.nextInt(5) + 3;
            start1 = random.nextInt(halfLength);
            end1 = scrambleLength1 + start1;
            start2 = random.nextInt(halfLength) + end1;
            end2 = scrambleLength2 + start2;
            if (end1 > length) {
                end1 = length;
            }
            if (end2 > length) {
                end2 = length;
            }
            if (start2 > length) {
                start2 = length;
            }
            sb.insert(start1, ChatColor.MAGIC);
            sb.insert(end1 + 2, ChatColor.RESET);
            sb.insert(start2 + 4, ChatColor.MAGIC);
            sb.insert(end2 + 6, ChatColor.RESET);
        }
        return sb.toString();
    }
}

package me.TfT02.Assassin.util;

import java.util.Random;
import me.TfT02.Assassin.Assassin;

import org.bukkit.ChatColor;

public class MessageScrambler {

	Assassin plugin;

	public MessageScrambler(Assassin instance) {
		plugin = instance;
	}

	private final Random random = new Random();
	/**
	 * This will randomly add ChatColor.MAGIC so messages will get scrambled.
	 * @param msg Message that will get scrambled
	 * @return Scrambled message
	*/
	public String Scrambled(String msg) {
		int length = msg.length();
		int halfLength = length / 2;
		int minScramLength = length / 4;
		int scrambleLength1 = random.nextInt(5) + minScramLength;
		int scrambleLength2 = random.nextInt(5) + minScramLength;
		int start1 = 1 + random.nextInt(halfLength / 2);
		int end1 = start1 + scrambleLength1;
		int start2 = 2 + end1 + random.nextInt(halfLength);
		int end2 = start2 + scrambleLength2;
		
		StringBuilder sb = new StringBuilder(msg);
		if (start2 > length) {
			start2 = end1 + 3;
		}
		if (end2 > length) {
			end2 = length;
		}
		sb.insert(start1, ChatColor.MAGIC);
		sb.insert(end1 + 2, ChatColor.RESET);
		sb.insert(start2 + 4, ChatColor.MAGIC);
		sb.insert(end2 + 6, ChatColor.RESET);
		String scrambled = sb.toString();
		return scrambled;
	}
}
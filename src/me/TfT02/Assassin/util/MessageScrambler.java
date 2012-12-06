package me.TfT02.Assassin.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import me.TfT02.Assassin.Assassin;

public class MessageScrambler {

	Assassin plugin;

	public MessageScrambler(Assassin instance) {
		plugin = instance;
	}

	private final Random random = new Random();

	/*
	 * Pretty damn good idea:
	 * A seperate chat channel for Assassins, only assassins can understand this 100%
	 * Other players will receive x% of these messages but scrambled
	 * Each letter of every word has a y% change to get ChatColor.MAGIC
	 * 
	 * getMessage()
	 *  String.replace( REX, String ) 
	 *  setMessage()
	 */
	public String Scrambled1(String msg) {
		boolean replaceAllA = random.nextBoolean();
		boolean replaceAllB = random.nextBoolean();
		boolean replaceAllC = random.nextBoolean();
		boolean replaceAllD = random.nextBoolean();
		boolean replaceAllE = random.nextBoolean();
		boolean replaceAllF = random.nextBoolean();
		boolean replaceAllG = random.nextBoolean();
		boolean replaceAllH = random.nextBoolean();
		boolean replaceAllI = random.nextBoolean();
		boolean replaceAllJ = random.nextBoolean();
		boolean replaceAllK = random.nextBoolean();
		boolean replaceAllL = random.nextBoolean();
		boolean replaceAllM = random.nextBoolean();
		boolean replaceAllN = random.nextBoolean();
		boolean replaceAllO = random.nextBoolean();
		boolean replaceAllP = random.nextBoolean();
		boolean replaceAllQ = random.nextBoolean();
		boolean replaceAllR = random.nextBoolean();
		boolean replaceAllS = random.nextBoolean();
		boolean replaceAllT = random.nextBoolean();
		boolean replaceAllU = random.nextBoolean();
		boolean replaceAllV = random.nextBoolean();
		boolean replaceAllW = random.nextBoolean();
		boolean replaceAllX = random.nextBoolean();
		boolean replaceAllY = random.nextBoolean();
		boolean replaceAllZ = random.nextBoolean();

		String scrambled = msg;
		if (replaceAllA) scrambled = msg.replaceAll("a", ChatColor.MAGIC + "a" + ChatColor.RESET) ;
		if (replaceAllB) scrambled = msg.replaceAll("b", ChatColor.MAGIC + "b" + ChatColor.RESET) ;
		if (replaceAllC) scrambled = msg.replaceAll("c", ChatColor.MAGIC + "c" + ChatColor.RESET) ;
		if (replaceAllD) scrambled = msg.replaceAll("d", ChatColor.MAGIC + "d" + ChatColor.RESET) ;
		if (replaceAllE) scrambled = msg.replaceAll("e", ChatColor.MAGIC + "e" + ChatColor.RESET) ;
		if (replaceAllF) scrambled = msg.replaceAll("f", ChatColor.MAGIC + "f" + ChatColor.RESET) ;
		if (replaceAllG) scrambled = msg.replaceAll("g", ChatColor.MAGIC + "g" + ChatColor.RESET) ;
		if (replaceAllH) scrambled = msg.replaceAll("h", ChatColor.MAGIC + "h" + ChatColor.RESET) ;
		if (replaceAllI) scrambled = msg.replaceAll("i", ChatColor.MAGIC + "i" + ChatColor.RESET) ;
		if (replaceAllJ) scrambled = msg.replaceAll("j", ChatColor.MAGIC + "j" + ChatColor.RESET) ;
		if (replaceAllK) scrambled = msg.replaceAll("k", ChatColor.MAGIC + "k" + ChatColor.RESET) ;
		if (replaceAllL) scrambled = msg.replaceAll("l", ChatColor.MAGIC + "l" + ChatColor.RESET) ;
		if (replaceAllM) scrambled = msg.replaceAll("m", ChatColor.MAGIC + "m" + ChatColor.RESET) ;
		if (replaceAllN) scrambled = msg.replaceAll("n", ChatColor.MAGIC + "n" + ChatColor.RESET) ;
		if (replaceAllO) scrambled = msg.replaceAll("o", ChatColor.MAGIC + "o" + ChatColor.RESET) ;
		if (replaceAllP) scrambled = msg.replaceAll("p", ChatColor.MAGIC + "p" + ChatColor.RESET) ;
		if (replaceAllQ) scrambled = msg.replaceAll("q", ChatColor.MAGIC + "q" + ChatColor.RESET) ;
		if (replaceAllR) scrambled = msg.replaceAll("r", ChatColor.MAGIC + "r" + ChatColor.RESET) ;
		if (replaceAllS) scrambled = msg.replaceAll("s", ChatColor.MAGIC + "s" + ChatColor.RESET) ;
		if (replaceAllT) scrambled = msg.replaceAll("t", ChatColor.MAGIC + "t" + ChatColor.RESET) ;
		if (replaceAllU) scrambled = msg.replaceAll("u", ChatColor.MAGIC + "u" + ChatColor.RESET) ;
		if (replaceAllV) scrambled = msg.replaceAll("v", ChatColor.MAGIC + "v" + ChatColor.RESET) ;
		if (replaceAllW) scrambled = msg.replaceAll("w", ChatColor.MAGIC + "w" + ChatColor.RESET) ;
		if (replaceAllX) scrambled = msg.replaceAll("x", ChatColor.MAGIC + "x" + ChatColor.RESET) ;
		if (replaceAllY) scrambled = msg.replaceAll("y", ChatColor.MAGIC + "y" + ChatColor.RESET) ;
		if (replaceAllZ) scrambled = msg.replaceAll("z", ChatColor.MAGIC + "z" + ChatColor.RESET) ;
		return scrambled;
	}

	public String Scrambled(String msg) {
		String scrambled;
//		List<String> elements = Arrays.asList("ax", "bx", "dx", "c", "acc");
//		final String patternStr = join(elements, "|"); //build string "ax|bx|dx|c|acc" 
//		Pattern p = Pattern.compile(patternStr);
		
		List<String> abc = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
		final String patternStr = join(abc, "|"); //build string "ax|bx|dx|c|acc" 
		Pattern p = Pattern.compile(patternStr);
		
		Matcher m = p.matcher(msg);
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		while (m.find()){
			String randomSymbol = abc.get(rand.nextInt(abc.size()));
			m.appendReplacement(sb,randomSymbol);
		}
		m.appendTail(sb);

		scrambled = sb.toString();
		return scrambled;
	}
/*
 * Some funky way to replace strings and stuffs
	public static void main(String[] args) {
		List<String> elements = Arrays.asList("ax", "bx", "dx", "c", "acc");
		final String patternStr = join(elements, "|"); //build string "ax|bx|dx|c|acc" 
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher("ax 5 5 dx 3 acc c ax bx");
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		while (m.find()){
			String randomSymbol = elements.get(rand.nextInt(elements.size()));
			m.appendReplacement(sb,randomSymbol);
		}
		m.appendTail(sb);
		System.out.println(sb);
	}
*/
	/**
	 * this method is only needed to generate the string ax|bx|dx|c|acc in a clean way....
	 * @see org.apache.commons.lang.StringUtils.join    for a more common alternative...
	 */
	public static String join(List<String> s, String delimiter) {
		if (s.isEmpty()) return "";
		Iterator<String> iter = s.iterator();
		StringBuffer buffer = new StringBuffer(iter.next());
		while (iter.hasNext()) buffer.append(delimiter).append(iter.next());
		return buffer.toString();
	}
}
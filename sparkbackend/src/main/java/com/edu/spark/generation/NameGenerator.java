package com.edu.spark.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameGenerator {

	private List vocals = new ArrayList();
	private List startConsonants = new ArrayList();
	private List endConsonants = new ArrayList();
	private List nameInstructions = new ArrayList();

	public NameGenerator() {
		String demoVocals[] = { "a", "e", "i", "o", "u", "ei", "ai", "ou", "j", "ji", "y", "oi", "au", "oo" };

		String demoStartConsonants[] = { "b", "c", "d", "f", "g", "h", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v",
				"w", "x", "z", "ch", "bl", "br", "fl", "gl", "gr", "kl", "pr", "st", "sh", "th" };

		String demoEndConsonants[] = { "b", "d", "f", "g", "h", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "z",
				"ch", "gh", "nn", "st", "sh", "th", "tt", "ss", "pf", "nt" };

		String nameInstructions[] = { "vd", "cvdvd", "cvd", "vdvd" };

		this.vocals.addAll(Arrays.asList(demoVocals));
		this.startConsonants.addAll(Arrays.asList(demoStartConsonants));
		this.endConsonants.addAll(Arrays.asList(demoEndConsonants));
		this.nameInstructions.addAll(Arrays.asList(nameInstructions));
	}

	public NameGenerator(String[] vocals, String[] startConsonants, String[] endConsonants) {
		this.vocals.addAll(Arrays.asList(vocals));
		this.startConsonants.addAll(Arrays.asList(startConsonants));
		this.endConsonants.addAll(Arrays.asList(endConsonants));
	}

	public NameGenerator(String[] vocals, String[] startConsonants, String[] endConsonants, String[] nameInstructions) {
		this(vocals, startConsonants, endConsonants);
		this.nameInstructions.addAll(Arrays.asList(nameInstructions));
	}

	public String getName() {
		return firstCharUppercase(getNameByInstructions(getRandomElementFrom(nameInstructions)));
	}

	private int randomInt(int min, int max) {
		return (int) (min + (Math.random() * (max + 1 - min)));
	}

	private String getNameByInstructions(String nameInstructions) {
		String name = "";
		int l = nameInstructions.length();

		for (int i = 0; i < l; i++) {
			char x = nameInstructions.charAt(0);
			switch (x) {
			case 'v':
				name += getRandomElementFrom(vocals);
				break;
			case 'c':
				name += getRandomElementFrom(startConsonants);
				break;
			case 'd':
				name += getRandomElementFrom(endConsonants);
				break;
			}
			nameInstructions = nameInstructions.substring(1);
		}
		return name;
	}

	private String firstCharUppercase(String name) {
		return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
	}

	private String getRandomElementFrom(List v) {
		return (String)v.get(randomInt(0, v.size() - 1));
	}
}

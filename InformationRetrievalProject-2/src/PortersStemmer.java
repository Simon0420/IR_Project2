import java.util.Arrays;
import java.util.HashSet;

/**
 * Self-made porter's stemmer implementation
 * implemented from instructions at http://snowball.tartarus.org/algorithms/porter/stemmer.html
 * 
 * @author simon
 *
 */
public class PortersStemmer {
	HashSet<String> vowels = new HashSet<String>(Arrays.asList("a", "e", "i", "o", "u"));
	int m = 0;
	String p1, p2;
	int r1, r2;
	String vc = "";

	// todo: vorher regex, dass wort nur buchstaben enthalten darf
	void calcM(String word) {
		int mTemp = 0;
		boolean lastV = false;
		boolean lastC = false;
		for (int i = 0; i < word.length(); i++) {
			boolean incM = false;
			if (vowels.contains("" + word.charAt(i))) {
				if (!lastV && !lastC) {
					vc = vc + "v";
				}
				if (lastC) {
					vc = vc + "v";
				}
				// System.out.println("vowel");
				lastV = true;
				lastC = false;
			} else {
				if (!lastV && !lastC) {
					vc = vc + "c";
				}
				// System.out.println("cons");
				if (lastV) {
					mTemp++;
					incM = true;
					vc = vc + "c";
				}
				lastC = true;
				lastV = false;
			}
			if (incM) {
				if (mTemp == 1) {
					p1 = word.substring(i + 1);
					r1 = i + 1;
				} else if (mTemp == 2) {
					p2 = word.substring(i + 1);
					r2 = i + 1;
				}
			}
		}
		m = mTemp;
	}

	int getM(String word) {
		int mTemp = 0;
		boolean lastV = false;
		boolean lastC = false;
		for (int i = 0; i < word.length(); i++) {
			if (vowels.contains("" + word.charAt(i))) {
				// System.out.println("vowel");
				lastV = true;
				lastC = false;
			} else {
				// System.out.println("cons");
				if (lastV) {
					mTemp++;
				}
				lastC = true;
				lastV = false;
			}
		}
		return mTemp;
	}

	String getVC(String word) {
		String vc = "";
		boolean lastV = false;
		boolean lastC = false;
		for (int i = 0; i < word.length(); i++) {
			if (vowels.contains("" + word.charAt(i))) {
				if (!lastV && !lastC) {
					vc = vc + "v";
				}
				if (lastC) {
					vc = vc + "v";
				}
				lastV = true;
				lastC = false;
			} else {
				if (!lastV && !lastC) {
					vc = vc + "c";
				}
				if (lastV) {
					vc = vc + "c";
				}
				lastC = true;
				lastV = false;
			}
		}
		return vc;
	}

	String getvc(String word) {
		String vc = "";
		for (int i = 0; i < word.length(); i++) {
			if (vowels.contains("" + word.charAt(i))) {

				vc = vc + "v";

			} else {

				vc = vc + "c";

			}
		}
		return vc;
	}

	String step1b(String word) {
		if (m > 0) {
			boolean scnd_third_rule = false;
			if (word.endsWith("eed") && getM(word.substring(0, word.length() - 3)) > 0) {
				StringBuffer sb = new StringBuffer(word);
				sb.delete(sb.length() - 1, sb.length());
				word = sb.toString();
			} else if (word.endsWith("ed") && getVC(word.substring(0, word.length() - 2)).matches("[vc]*v[vc]*")
					&& getM(word.substring(0, word.length() - 2)) > 0) {
				StringBuffer sb = new StringBuffer(word);
				sb.delete(sb.length() - 2, sb.length());
				word = sb.toString();
				scnd_third_rule = true;
			} else if (word.endsWith("ing") && getVC(word.substring(0, word.length() - 3)).matches("[vc]*v[vc]*")
					&& getM(word.substring(0, word.length() - 3)) > 0) {

				StringBuffer sb = new StringBuffer(word);
				sb.delete(sb.length() - 3, sb.length());
				word = sb.toString();
				scnd_third_rule = true;
			}
			if (scnd_third_rule) {
				calcM(word);
				if (word.endsWith("at")) {
					StringBuffer sb = new StringBuffer(word);
					sb.append("e");
					word = sb.toString();
				} else if (word.endsWith("bl")) {
					StringBuffer sb = new StringBuffer(word);
					sb.append("e");
					word = sb.toString();
				} else if (word.endsWith("iz")) {
					StringBuffer sb = new StringBuffer(word);
					sb.append("e");
					word = sb.toString();
				} else if (d(word) && !(l(word) || s(word) || z(word))) {
					StringBuffer sb = new StringBuffer(word);
					sb.delete(sb.length() - 1, sb.length());
					word = sb.toString();
				} else if (m == 1 && o(word)) {
					StringBuffer sb = new StringBuffer(word);
					sb.append("e");
					word = sb.toString();
				}
			}
		}
		return word;
	}

	String step1c(String word) {
		String vc = getVC(word);
		if (word.endsWith("y")) {
			if (vc.matches("[vc]*vc")) {
				StringBuffer sb = new StringBuffer(word);
				sb.setCharAt(sb.length() - 1, 'i');
				word = sb.toString();
			}
		}
		return word;
	}

	boolean d(String word) {
		if (word.length() > 1) {
			if (word.charAt(word.length() - 1) == word.charAt(word.length() - 2)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean l(String word) {
		if (word.length() > 1) {
			if (word.endsWith("ll")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean s(String word) {
		if (word.length() > 1) {
			if (word.endsWith("ss")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean z(String word) {
		if (word.length() > 1) {
			if (word.endsWith("zz")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean o(String word) {
		if (getvc(word).matches("[cv]*cvc")) {
			int counter = 0;
			for (int i = 0; i < word.length(); i++) {
				if (vowels.contains("" + word.charAt(i))) {
				} else {
					counter++;
				}
			}
			int c = 0;
			for (int i = 0; i < word.length(); i++) {
				if (vowels.contains("" + word.charAt(i))) {
				} else {
					c++;
					if (c == counter) {
						if (word.substring(i).matches("[^wxy]")) {

							return true;
						}
					}
				}
			}
		}
		return false;
	}

	String step1a(String word) {
		if (word.endsWith("sses")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ss");
			word = sb.toString();
		} else if (word.endsWith("ies")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 3, sb.length());
			sb.append("i");
			word = sb.toString();
		} else if (word.endsWith("s")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 1, sb.length());
			sb.append("");
			word = sb.toString();
		}
		return word;
	}

	String step0(String word) {
		if (word.endsWith("'s'")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 3, sb.length());
			word = sb.toString();
		} else if (word.endsWith("'s")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 2, sb.length());
			word = sb.toString();
		} else if (word.endsWith("'")) {
			StringBuffer sb = new StringBuffer(word);
			sb.delete(sb.length() - 1, sb.length());
			word = sb.toString();
		}
		return word;
	}

	String step2(String word) {
		StringBuffer sb = new StringBuffer(word);
		if (word.endsWith("ational") && getM(word.substring(0, word.length() - 7)) > 0) {
			sb.delete(sb.length() - 7, sb.length());
			sb.append("ate");
		} else if (word.endsWith("tional") && getM(word.substring(0, word.length() - 6)) > 0) {
			sb.delete(sb.length() - 6, sb.length());
			sb.append("tion");
		} else if (word.endsWith("enci") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ence");
		} else if (word.endsWith("anci") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ance");
		} else if (word.endsWith("izer") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ize");
		} else if (word.endsWith("abli") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("able");
		} else if (word.endsWith("alli") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("al");
		} else if (word.endsWith("entli") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ent");
		} else if (word.endsWith("eli") && getM(word.substring(0, word.length() - 3)) > 0) {
			sb.delete(sb.length() - 3, sb.length());
			sb.append("e");
		} else if (word.endsWith("ousli") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ous");
		} else if (word.endsWith("ization") && getM(word.substring(0, word.length() - 7)) > 0) {
			sb.delete(sb.length() - 7, sb.length());
			sb.append("ize");
		} else if (word.endsWith("ation") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ate");
		} else if (word.endsWith("ator") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ate");
		} else if (word.endsWith("alism") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("al");
		} else if (word.endsWith("iveness") && getM(word.substring(0, word.length() - 7)) > 0) {
			sb.delete(sb.length() - 7, sb.length());
			sb.append("ive");
		} else if (word.endsWith("fulness") && getM(word.substring(0, word.length() - 7)) > 0) {
			sb.delete(sb.length() - 7, sb.length());
			sb.append("ful");
		} else if (word.endsWith("ousness") && getM(word.substring(0, word.length() - 7)) > 0) {
			sb.delete(sb.length() - 7, sb.length());
			sb.append("ous");
		} else if (word.endsWith("aliti") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("al");
		} else if (word.endsWith("iviti") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ive");
		} else if (word.endsWith("biliti") && getM(word.substring(0, word.length() - 6)) > 0) {
			sb.delete(sb.length() - 6, sb.length());
			sb.append("ble");
		}
		word = sb.toString();
		return word;
	}

	String step3(String word) {
		StringBuffer sb = new StringBuffer(word);
		if (word.endsWith("icate") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ic");
		} else if (word.endsWith("ative") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
		} else if (word.endsWith("alize") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("al");
		} else if (word.endsWith("iciti") && getM(word.substring(0, word.length() - 5)) > 0) {
			sb.delete(sb.length() - 5, sb.length());
			sb.append("ic");
		} else if (word.endsWith("ical") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
			sb.append("ic");
		} else if (word.endsWith("ful") && getM(word.substring(0, word.length() - 3)) > 0) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ness") && getM(word.substring(0, word.length() - 4)) > 0) {
			sb.delete(sb.length() - 4, sb.length());
		}
		word = sb.toString();
		return word;
	}

	String step4(String word) {
		StringBuffer sb = new StringBuffer(word);
		if (word.endsWith("al") && getM(word.substring(0, word.length() - 2)) > 1) {
			sb.delete(sb.length() - 2, sb.length());
		} else if (word.endsWith("ance") && getM(word.substring(0, word.length() - 4)) > 1) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (word.endsWith("ence") && getM(word.substring(0, word.length() - 4)) > 1) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (word.endsWith("er") && getM(word.substring(0, word.length() - 2)) > 1) {
			sb.delete(sb.length() - 2, sb.length());
		} else if (word.endsWith("ic") && getM(word.substring(0, word.length() - 2)) > 1) {
			sb.delete(sb.length() - 2, sb.length());
		} else if (word.endsWith("able") && getM(word.substring(0, word.length() - 4)) > 1) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (word.endsWith("ible") && getM(word.substring(0, word.length() - 4)) > 1) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (word.endsWith("ant") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ement") && getM(word.substring(0, word.length() - 5)) > 1) {
			sb.delete(sb.length() - 5, sb.length());
		} else if (word.endsWith("ment") && getM(word.substring(0, word.length() - 4)) > 1) {
			sb.delete(sb.length() - 4, sb.length());
		} else if (word.endsWith("ent") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if ((word.endsWith("sion") | word.endsWith("tion")) && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ou") && getM(word.substring(0, word.length() - 2)) > 1) {
			sb.delete(sb.length() - 2, sb.length());
		} else if (word.endsWith("ism") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ate") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("iti") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ous") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ive") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		} else if (word.endsWith("ize") && getM(word.substring(0, word.length() - 3)) > 1) {
			sb.delete(sb.length() - 3, sb.length());
		}
		word = sb.toString();
		return word;
	}

	String step5a(String word) {
		StringBuffer sb = new StringBuffer(word);
		if (word.endsWith("e") && getM(word.substring(0, word.length() - 1)) > 1) {
			sb.delete(sb.length() - 1, sb.length());
		} else if (word.endsWith("e") && getM(word.substring(0, word.length() - 1)) == 1 & !o(word)) {
			sb.delete(sb.length() - 1, sb.length());
		}
		word = sb.toString();
		return word;
	}

	String step5b(String word) {
		StringBuffer sb = new StringBuffer(word);
		if (word.endsWith("ll") && getM(word.substring(0, word.length() - 1)) > 1) {
			sb.delete(sb.length() - 1, sb.length());
		}
		word = sb.toString();
		return word;
	}

	String portersStemm(String word) {
		calcM(word);
		//System.out.println("stem...");
		word = step0(word);
		word = step1a(word);
		word = step1b(word);
		word = step1c(word);
		word = step2(word);
		word = step3(word);
		word = step4(word);
		word = step5a(word);
		word = step5b(word);
		//System.out.println("stem done");
		m = 0;
		vc = "";
		r1 = 0;
		r2 = 0;
		p1 = "";
		p2 = "";
		return word;
	}

	// test main-method
	public static void main(String[] args) {
		PortersStemmer ps = new PortersStemmer();
		
		// sample word1
		String word = "tanned";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
		System.out.println("---------------------------------------");
		
		// sample word2
		word = "fizzed";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
		System.out.println("---------------------------------------");
		
		//sample word3
		word = "falling";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
		System.out.println("---------------------------------------");
		
		//sample word4
		word = "probate";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
		System.out.println("---------------------------------------");
		
		//sample word5
		word = "rate";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
		System.out.println("---------------------------------------");
		
		//sample word5
		word = "replacement";
		System.out.println("Input: \t\t\t"+word);
		ps.calcM(word);
		System.out.println("m-length: \t\t"+ps.m);
		System.out.println("Vocal/Cons-Sequences: \t"+ps.vc.toUpperCase());
		word = ps.portersStemm(word);
		System.out.println("Result: \t\t"+word);
	}

}
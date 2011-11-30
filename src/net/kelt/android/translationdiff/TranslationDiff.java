/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.kelt.android.translationdiff.ResourceItem.ItemType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Ondra
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class TranslationDiff {
	private static PrintStream out = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// Set out
		out = new PrintStream(System.out, true, "UTF-8");
		// Parse input
		if (args.length != 2) {
			printHelp();
		} else {
			String F_SRC = args[0];
			String F_DST = args[1];
			try {
				// Load Original file
				List<ResourceItem> srcEl = loadOrigFile(F_SRC);
				// Load Translation file
				List<ResourceItem> dstEl = loadTranFile(F_DST);
				outln("### Comparing\u2026    ###");
				// Find all in source but not in destination
				outln("### ORIG >>> TRAN ###");
				for (ResourceItem si : srcEl) {
					String sname = si.getName();
					int diItems = 0;
					boolean found = false;
					for (ResourceItem di : dstEl) {
						if (si.getItemType() == ItemType.ITEM_STRING) {
							if (sname.equalsIgnoreCase(di.getName())) {
								found = true;
								break;
							}
						} else if (si.getItemType() == ItemType.ITEM_PLURALS || si.getItemType() == ItemType.ITEM_STRING_ARRAY
								|| si.getItemType() == ItemType.ITEM_ARRAY || si.getItemType() == ItemType.ITEM_INTEGER_ARRAY) {
							diItems = di.getChildItems();
							if (sname.equalsIgnoreCase(di.getName()) && si.getChildItems() == di.getChildItems()) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						if (si.getItemType() == ItemType.ITEM_STRING) {
							outln("STRING name=\"" + sname + "\"==\"" + si.getValue() + "\" not in TRAN file");
						} else {
							if (si.getItemType() == ItemType.ITEM_PLURALS) {
								outln("PLURALS name=\"" + sname + "\":\"" + si.childItems + "!=" + diItems
										+ "\" not in TRAN file or different item count");
							} else {
								outln("ARRAY[" + si.getItemType() + "] name=\"" + sname + "\":\"" + si.childItems + "!=" + diItems
										+ "\" not in TRAN file or different item count");
							}
						}
					}
				}
				// Find all in dst but not in src
				outln("### ORIG <<< TRAN ###");
				for (ResourceItem di : dstEl) {
					String dname = di.getName();
					boolean found = false;
					for (ResourceItem si : srcEl) {
						if (di.getItemType() == ItemType.ITEM_STRING) {
							if (dname.equalsIgnoreCase(si.getName())) {
								found = true;
								break;
							}
						} else if (di.getItemType() == ItemType.ITEM_PLURALS || di.getItemType() == ItemType.ITEM_STRING_ARRAY
								|| di.getItemType() == ItemType.ITEM_ARRAY || di.getItemType() == ItemType.ITEM_INTEGER_ARRAY) {
							if (dname.equalsIgnoreCase(si.getName()) && di.getChildItems() == si.getChildItems()) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						if (di.getItemType() == ItemType.ITEM_STRING) {
							outln("STRING name=\"" + dname + "\"==\"" + di.getValue() + "\" not in ORIG file");
						} else {
							if (di.getItemType() == ItemType.ITEM_PLURALS) {
								outln("PLURALS name=\"" + dname + "\":\"" + di.childItems
										+ "\" not in ORIG file or different item count");
							} else {
								outln("ARRAY[" + di.getItemType() + "] name=\"" + dname + "\":\"" + di.childItems
										+ "\" not in ORIG file or different item count");
							}
						}
					}
				}
				outln("#####################");
			} catch (Exception e) {
				outln("Error: " + e);
				e.printStackTrace();
			}
		}
	}

	private static void printHelp() {
		outln("Translation Diff:");
		outln("Usage: TranslationDiff [origFile] [transFile]");
	}

	private static void outln(String output) {
		out.println(output);
	}

	private static List<ResourceItem> loadOrigFile(String F_SRC) throws Exception {
		File file = new File(F_SRC);
		if (file.exists() == false) {
			outln("Original file \"" + F_SRC + "\" not exists.");
			System.exit(-1);
		} else {
			outln("Original file: " + F_SRC);
		}
		List<ResourceItem> srcEl = new Vector<ResourceItem>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		// Get all source elements
		Node root = doc.getDocumentElement();
		NodeList nList = root.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			// Iterate through all nodes
			Node n = nList.item(i);
			if (n.getNodeName().equalsIgnoreCase("string")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						String elValue = e.getTextContent();
						srcEl.add(new StringItem(elName, elValue));
						// outln("ORIG: found translatable string: " + elName + "=" + elValue);
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("plurals")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							srcEl.add(new PluralsItem(elName, plList.getLength()));
							// outln("ORIG: found plurals: " + elName + ", #=" + plList.getLength());
						} else {
							outln("ORIG: empty plurals: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("string-array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							srcEl.add(new StringArrayItem(elName, plList.getLength()));
							// outln("ORIG: found string-array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("ORIG: empty string-array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							srcEl.add(new ArrayItem(elName, plList.getLength()));
							// outln("ORIG: found array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("ORIG: empty array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("integer-array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							srcEl.add(new ArrayItem(elName, plList.getLength()));
							// outln("ORIG: found integer-array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("ORIG: empty integer-array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("#comment")) {
				// skip
			} else if (n.getNodeName().equalsIgnoreCase("#text")) {
				// skip
			} else if (n.getNodeName().equalsIgnoreCase("skip")) {
				// skip
			} else {
				outln("ORIG: unknown node: " + n.getNodeName() + "::" + n.getNodeValue());
			}
		}
		return srcEl;
	}

	private static List<ResourceItem> loadTranFile(String F_DST) throws Exception {
		File file = new File(F_DST);
		if (file.exists() == false) {
			outln("Translation file \"" + F_DST + "\" not exists.");
			System.exit(-1);
		} else {
			outln("Translation file: " + F_DST);
		}
		List<ResourceItem> dstEl = new Vector<ResourceItem>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc = db.parse(file);
		doc.getDocumentElement().normalize();
		Node root = doc.getDocumentElement();
		NodeList nList = root.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			// Iterate through all nodes
			Node n = nList.item(i);
			if (n.getNodeName().equalsIgnoreCase("string")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						String elValue = e.getTextContent();
						dstEl.add(new StringItem(elName, elValue));
						// outln("TRAN: found translatable string: " + elName + "=" + elValue);
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("plurals")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							dstEl.add(new PluralsItem(elName, plList.getLength()));
							// outln("TRAN: found plurals: " + elName + ", #=" + plList.getLength());
						} else {
							outln("TRAN: empty plurals: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("string-array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							dstEl.add(new StringArrayItem(elName, plList.getLength()));
							// outln("TRAN: found string-array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("TRAN: empty string-array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							dstEl.add(new ArrayItem(elName, plList.getLength()));
							// outln("TRAN: found array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("TRAN: empty array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("integer-array")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							dstEl.add(new IntegerArrayItem(elName, plList.getLength()));
							// outln("TRAN: found integer-array: " + elName + ", #=" + plList.getLength());
						} else {
							outln("TRAN: empty integer-array: " + elName);
						}
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("#comment")) {
				// skip
			} else if (n.getNodeName().equalsIgnoreCase("#text")) {
				// skip
			} else if (n.getNodeName().equalsIgnoreCase("skip")) {
				// skip
			} else {
				outln("TRAN: unknown node: " + n.getNodeName() + "::" + n.getNodeValue());
			}
		}
		return dstEl;
	}
}

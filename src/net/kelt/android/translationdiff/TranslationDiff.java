/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.kelt.android.translationdiff.items.ArrayItem;
import net.kelt.android.translationdiff.items.IntegerArrayItem;
import net.kelt.android.translationdiff.items.ItemType;
import net.kelt.android.translationdiff.items.PluralsItem;
import net.kelt.android.translationdiff.items.ResourceItem;
import net.kelt.android.translationdiff.items.StringArrayItem;
import net.kelt.android.translationdiff.items.StringItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Translation helper main class and logic.
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 7.10.2011
 */
public final class TranslationDiff {
	private static PrintStream out = null;

	/** Empty private constructor. */
	private TranslationDiff() {
		/* empty */
	}

	/**
	 * Main start method.
	 * 
	 * @param args
	 *          Input parameters
	 * @throws UnsupportedEncodingException
	 *           If output cannot be initialized as UTF-8 output
	 */
	public static void main(final String[] args) throws UnsupportedEncodingException {
		// Set out
		out = new PrintStream(System.out, true, "UTF-8");
		// Parse input
		if (args.length == 0 || args.length > 2) {
			printHelp();
		} else {
			try {
				List<ResourceItem> originalItems = null;
				List<ResourceItem> translationItems = null;
				// If arg is only one, check it for directory
				if (args.length == 1) {
					String dir = args[0];
					File fdir = new File(dir);
					if (!fdir.isDirectory()) {
						// One and only argument is file - assuming the translation file
						String tran_fname = args[0];
						File temp_tran = new File(tran_fname);
						if (!temp_tran.exists()) {
							outln("Translation file \"" + tran_fname + "\" did not exists!");
							return;
						}
						StringBuffer tdir = new StringBuffer();
						if (temp_tran.getParent().lastIndexOf('/') != -1) {
							tdir.append(temp_tran.getParent().substring(0, temp_tran.getParent().lastIndexOf('/'))).append("/");
						}
						tdir.append("values");
						String orig_fname = getOrigFileFromTranslation(temp_tran);
						if (!new File(orig_fname).exists()) {
							outln("Original file \"" + orig_fname + "\" did not exists!");
							return;
						}
						File origFile = new File(orig_fname);
						File tranFile = new File(tran_fname);
						originalItems = loadResourceFile(origFile, false);
						translationItems = loadResourceFile(tranFile, true);
					} else {
						// One and only argument is directory - assusming comparing whole directory
						// Create FileNameFilter - get the content filtered by "xml" pattern
						FilenameFilter fnf = new FilenameFilter() {
							/** {@inheritDoc} */
							public boolean accept(final File dir, final String name) {
								return name.endsWith(".xml");
							}
						};
						File origDirectory = new File(getOrigDirectory(fdir));
						outln("Warn: Performing translation comparation of whole directory. In many changes the output can be very long.");
						String[] flist = origDirectory.list(fnf);
						if (flist != null) {
							Arrays.sort(flist);
							for (String origFileName : flist) {
								// For every xml filename in this directory preform a parsing and checking
								File tranFile = new File(fdir.getPath() + "/" + origFileName);
								File origFile = new File(origDirectory.getPath() + "/" + origFileName);
								if (!tranFile.exists() || !tranFile.isFile()) {
									outln("Translation file " + tranFile.getName() + " NOT EXISTS or not a file.");
								} else if (!origFile.exists() || !origFile.isFile()) {
									outln("Original file " + origFile.getName() + " NOT EXISTS or not a file.");
								} else {
									outln("######### " + origFileName + " #########");
									originalItems = loadResourceFile(origFile, false);
									translationItems = loadResourceFile(tranFile, true);
									if (originalItems != null && translationItems != null) {
										compareLoadedFiles(originalItems, translationItems);
									}
								}
								outln("--------------------------------------------------");
							}
						} else {
							outln("Directory not contain any XML files.");
						}
						System.exit(0);
					}
				} else {
					// Get args as input files
					originalItems = loadResourceFile(new File(args[0]), false);
					translationItems = loadResourceFile(new File(args[1]), true);
				}
				compareLoadedFiles(originalItems, translationItems);
			} catch (Exception e) {
				outln("Error: " + e);
				e.printStackTrace();
			}
		}
	}

	/** Display help. */
	private static void printHelp() {
		outln("Translation Diff v1.3");
		outln("Usage: TranslationDiff [translation directory] | [translation file] | [original file] [translation file]");
	}

	/**
	 * @param output
	 *          String to be displayed
	 */
	private static void outln(final String output) {
		out.println(output);
	}

	/**
	 * Load translation resource file.
	 * 
	 * @param sourceFile
	 *          Source file
	 * @param checkDuplicates
	 *          Whether this method should check duplicates in given file
	 * @return List of resource items
	 * @throws Exception
	 *           If something fail
	 */
	private static List<ResourceItem> loadResourceFile(final File sourceFile, final boolean checkDuplicates)
			throws Exception {
		Collection<String> dupeMap = new Vector<String>();
		List<ResourceItem> resourceItems = new Vector<ResourceItem>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(sourceFile);
		doc = db.parse(sourceFile);
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
						if (checkDuplicates) {
							checkDuplicate(dupeMap, elName);
						}
						resourceItems.add(new StringItem(elName, elValue));
					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("plurals")) {
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					if (e.getAttribute("translatable") != null && !e.getAttribute("translatable").equalsIgnoreCase("false")) {
						String elName = e.getAttribute("name");
						NodeList plList = e.getElementsByTagName("item");
						if (plList != null && plList.getLength() > 0) {
							if (checkDuplicates) {
								checkDuplicate(dupeMap, elName);
							}
							resourceItems.add(new PluralsItem(elName, plList.getLength()));
						} else {
							outln("RESOURCE: empty plurals: " + elName);
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
							if (checkDuplicates) {
								checkDuplicate(dupeMap, elName);
							}
							resourceItems.add(new StringArrayItem(elName, plList.getLength()));
						} else {
							outln("RESOURCE: empty string-array: " + elName);
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
							if (checkDuplicates) {
								checkDuplicate(dupeMap, elName);
							}
							resourceItems.add(new ArrayItem(elName, plList.getLength()));
						} else {
							outln("RESOURCE: empty array: " + elName);
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
							if (checkDuplicates) {
								checkDuplicate(dupeMap, elName);
							}
							resourceItems.add(new IntegerArrayItem(elName, plList.getLength()));
						} else {
							outln("RESOURCE: empty integer-array: " + elName);
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
				outln("RESOURCE: unknown node: " + n.getNodeName() + "::" + n.getNodeValue());
			}
		}
		return resourceItems;
	}

	/**
	 * Compare loaded and parsed files and display differences.
	 * 
	 * @param origItems
	 *          List of original items
	 * @param translationItems
	 *          List of translated items
	 * @throws Exception
	 *           If something fail
	 */
	private static void compareLoadedFiles(final List<ResourceItem> origItems, final List<ResourceItem> translationItems)
			throws Exception {
		// Find all in source but not in destination
		outln("=== CHANGES: original >>> to >>> translation");
		for (ResourceItem si : origItems) {
			String sname = si.getName();
			int diItems = 0;
			boolean found = false;
			for (ResourceItem di : translationItems) {
				if (si.getItemType() == ItemType.ITEM_STRING) {
					if (sname.equalsIgnoreCase(di.getName())) {
						found = true;
						break;
					}
				} else if (si.getItemType() == ItemType.ITEM_PLURALS || si.getItemType() == ItemType.ITEM_STRING_ARRAY
						|| si.getItemType() == ItemType.ITEM_ARRAY || si.getItemType() == ItemType.ITEM_INTEGER_ARRAY) {
					if (sname.equalsIgnoreCase(di.getName())) {
						diItems = di.getChildItems();
						if (sname.equalsIgnoreCase(di.getName()) && si.getChildItems() == di.getChildItems()) {
							found = true;
							break;
						}
					}
				}
			}
			if (!found) {
				if (si.getItemType() == ItemType.ITEM_STRING) {
					outln("STRING name=\"" + sname + "\"==\"" + si.getValue() + "\" not in TRANSLATION file");
				} else {
					if (si.getItemType() == ItemType.ITEM_PLURALS) {
						outln("PLURALS name=\"" + sname + "\":\"" + si.getChildItems() + "!=" + diItems
								+ "\" not in TRANSLATION file or different item count");
					} else {
						outln("ARRAY[" + si.getItemType() + "] name=\"" + sname + "\":\"" + si.getChildItems() + "!=" + diItems
								+ "\" not in TRANSLATION file or different item count");
					}
				}
			}
		}
		// Find all in dst but not in src
		outln("=== CHANGES: translation >>> to >>> original");
		for (ResourceItem di : translationItems) {
			String dname = di.getName();
			boolean found = false;
			for (ResourceItem si : origItems) {
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
					outln("STRING name=\"" + dname + "\"==\"" + di.getValue() + "\" not in ORIGINAL file");
				} else {
					if (di.getItemType() == ItemType.ITEM_PLURALS) {
						outln("PLURALS name=\"" + dname + "\":\"" + di.getChildItems()
								+ "\" not in ORIGINAL file or different item count");
					} else {
						outln("ARRAY[" + di.getItemType() + "] name=\"" + dname + "\":\"" + di.getChildItems()
								+ "\" not in ORIGINAL file or different item count");
					}
				}
			}
		}
	}

	/**
	 * Get original filename from translation file.
	 * 
	 * @param tranFileName
	 *          Input translation file
	 * @return Path and filename of original file
	 */
	private static String getOrigFileFromTranslation(final File tranFileName) {
		return getOrigDirectory(tranFileName) + "/" + tranFileName.getName();
	}

	/**
	 * Get original directory from translation directory.
	 * 
	 * @param dirName
	 *          Translation directory path
	 * @return Path to original directory
	 */
	private static String getOrigDirectory(final File dirName) {
		return dirName.getParentFile().getPath() + "/values";
	}

	/**
	 * Check for duplicates in translation file.
	 * 
	 * @param elementNames
	 *          List of elements
	 * @param name
	 *          Name of new element
	 */
	private static void checkDuplicate(final Collection<String> elementNames, final String name) {
		if (elementNames.contains(name)) {
			outln("WARNING!!! Translation file contains duplicated elements: elementName=" + name);
		} else {
			elementNames.add(name);
		}
	}
}

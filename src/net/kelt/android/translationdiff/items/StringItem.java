/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * Item type representing generic string like &lt;string name=""&gt;&lt;/string&gt;
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class StringItem extends ResourceItem {
	public StringItem(String name, String value, String product) {
		itemName = name;
		itemValue = value;
		itemType = ItemType.ITEM_STRING;
		itemProduct = product;
	}
}

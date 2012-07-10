/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * * Item type representing plurals items like &lt;plurals name=""&gt;&lt;/plurals&gt;
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class PluralsItem extends ResourceItem {
	public PluralsItem(String name, String value) {
		itemName = name;
		itemValue = value;
		itemType = ItemType.ITEM_PLURALS;
	}

	public PluralsItem(String name, int childs) {
		itemName = name;
		itemValue = null;
		childItems = childs;
		itemType = ItemType.ITEM_PLURALS;
	}
}

/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * Item type representing integer array item like &lt;integer-array name=""&gt;&lt;/integer-array&gt; with items.
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class IntegerArrayItem extends ResourceItem {
	/**
	 * Integer array item constructor.
	 * 
	 * @param name
	 *          Name of array
	 * @param childs
	 *          Number of items in array
	 */
	public IntegerArrayItem(final String name, final int childs) {
		itemName = name;
		itemValue = null;
		childItems = childs;
		itemType = ItemType.ITEM_INTEGER_ARRAY;
	}
}

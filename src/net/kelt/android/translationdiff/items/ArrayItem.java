/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * Item type representing item like &lt;array name=""&gt;&lt;/array&gt; with items.
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class ArrayItem extends ResourceItem {
	/**
	 * ArrayItem constructor.
	 * 
	 * @param name
	 *          Name of array
	 * @param childs
	 *          Number of items in array
	 */
	public ArrayItem(final String name, final int childs) {
		itemName = name;
		itemValue = null;
		childItems = childs;
		itemType = ItemType.ITEM_ARRAY;
	}
}

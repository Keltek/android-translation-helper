/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff;

/**
 * @author Ondra
 * @version $Revision: 1.2 $ 7.10.2011
 */
public class ArrayItem extends ResourceItem {
	public ArrayItem(String name, int childs) {
		itemName = name;
		itemValue = null;
		childItems = childs;
		itemType = ItemType.ITEM_ARRAY;
	}
}

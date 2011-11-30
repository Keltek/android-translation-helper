/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff;

/**
 * @author Ondra
 * @version $Revision: 1.2 $ 8.10.2011
 */
public class ResourceItem {
	public ItemType itemType;
	protected String itemName = null;
	protected String itemValue = null;
	protected int childItems = 0;

	enum ItemType {
		ITEM_STRING, ITEM_PLURALS, ITEM_STRING_ARRAY, ITEM_ARRAY, ITEM_INTEGER_ARRAY
	}

	public String getName() {
		return itemName;
	}

	public String getValue() {
		return itemValue;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public int getChildItems() {
		return childItems;
	}
}

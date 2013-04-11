/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * Item representing generic resource with all attributes
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 8.10.2011
 */
public class ResourceItem {
	protected ItemType itemType;
	protected String itemName = null;
	protected String itemProduct = null;
	protected String itemValue = null;
	protected int childItems = 0;

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

	public String getItemProduct() {
		return itemProduct;
	}
}

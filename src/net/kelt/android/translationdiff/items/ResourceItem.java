/**
 * $Id: OZIM-Java-CodeTemplates.xml,v 1.2 2010/06/03 14:15:22 ozim Exp $
 *
 * Copyright (c) 2011 IBM Czech Republic, spol. s r.o.
 */
package net.kelt.android.translationdiff.items;

/**
 * Item representing generic resource with all attributes.
 * 
 * @author Ondrej Zima
 * @version $Revision: 1.2 $ 8.10.2011
 */
public class ResourceItem {
	/** Element type of current resource item. */
	protected ItemType itemType;
	/** Element name. */
	protected String itemName;
	/** Element value. */
	protected String itemValue;
	/** Number of child elements. */
	protected int childItems = 0;

	/** @return Name of element */
	public final String getName() {
		return itemName;
	}

	/** @return Value of element */
	public final String getValue() {
		return itemValue;
	}

	/** @return Element type */
	public final ItemType getItemType() {
		return itemType;
	}

	/** @return Number of child elements */
	public final int getChildItems() {
		return childItems;
	}
}

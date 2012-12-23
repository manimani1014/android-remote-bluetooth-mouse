package com.david_tvildiani.desktop.rbmouse;
import javax.swing.JLabel;

/**
 * 
 * @author David Tvildiani : DavidTamuna@Gmail.com
 * 
 */
public class myCustomListItem
{
	public JLabel getLbl()
	{
		return lbl;
	}

	public int getId()
	{
		return id;
	}

	public myCustomListItem(JLabel lbl, int id)
	{
		this.lbl = lbl;
		this.id = id;
	}

	JLabel lbl;
	int id;

}

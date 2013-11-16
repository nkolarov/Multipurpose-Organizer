package com.kolarov.organizeit.Models;

/**
 * Created by N.Kolarov on 13-11-16.
 */
public class ItemModel {

    public int id;

    public String title;

    public int itemtype;

    public int paerntid;

    public int childcount;

    public Iterable<ItemShortModel> childrens;
}

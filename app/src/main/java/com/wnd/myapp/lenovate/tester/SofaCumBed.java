package com.wnd.myapp.lenovate.tester;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Created by Sachin Kharb on 8/30/2016.
 */
public class SofaCumBed implements ParentObject {
    private List<Object> mChildrenList;
    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }
}

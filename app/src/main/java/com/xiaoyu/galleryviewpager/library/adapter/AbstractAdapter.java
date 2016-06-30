package com.xiaoyu.galleryviewpager.library.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractAdapter<T> extends PagerAdapter{

	private List<T> list;
	protected LinkedList<T> linkedList;
	protected Context mContext;
	private SparseArray<View> views;
	
	public AbstractAdapter(Context mContext){
		this.mContext = mContext;
		list = new ArrayList<T>();
		linkedList = new LinkedList<T>();
		views = new SparseArray<View>();
	}
	
	@Override
	public int getCount() {
		return linkedList.size() > 1 ? Integer.MAX_VALUE : linkedList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	public void onViewDestroy(ViewGroup container){
		int count = views.size();
		for(int i = 0; i < count; i++){
			View view = views.valueAt(i);
			container.removeView(view);
			view = null;
		}
		views.clear();
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = views.get(position % linkedList.size());
		container.removeView(view);
		views.remove(position % linkedList.size());
		view = null;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int index = position % linkedList.size();
		View view = createView(index, linkedList.get(index), container);
		container.addView(view);
		views.append(position % linkedList.size(), view);
		return view;
	}

	public void setContent(List<T> list){
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void addContents(List<T> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void addContent(T obj){
		list.add(obj);
		notifyDataSetChanged();
	}
	public void clear(){
		list.clear();
		notifyDataSetChanged();
	}
	public void deleteContent(int position){
		list.remove(position);
		notifyDataSetChanged();
	}
	
	private void setLinkedList(){
		linkedList.clear();
		int count = list.size();
		if(count > 1){
			if(count > 5){
				linkedList.addAll(list);
			}else if(count > 3){
				linkedList.addAll(list);
				linkedList.addAll(list);
			}else{
				linkedList.addAll(list);
				linkedList.addAll(list);
				linkedList.addAll(list);
			}
		}else{
			linkedList.addAll(list);
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		setLinkedList();
		super.notifyDataSetChanged();
	}
	
	public int getTruthCount(){
		return list.size();
	}
	
	private View mCurrentView;
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View)object;
		super.setPrimaryItem(container, position, object);
	}
	
	public View getPrimaryItem() {
        return mCurrentView;
    }
	
	protected abstract View createView(int position, T obj, ViewGroup parent);

}

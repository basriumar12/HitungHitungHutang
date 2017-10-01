package com.tiyanrcode.bon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tiyanrcode.bon.R;
import com.tiyanrcode.bon.model.Customer;

import java.util.List;

/**
 * Created by sulistiyanto on 21-Apr-15.
 */
public class ListCustomerAdapter extends BaseAdapter {

    public static final String TAG = "ListCustomerAdapter";

    private List<Customer> mItems;
    private LayoutInflater mInflater;

    public ListCustomerAdapter(Context context, List<Customer> listCustomers) {
        this.setItems(listCustomers);
        this.mInflater = LayoutInflater.from(context);
    }

    public List<Customer> getItems() {
        return mItems;
    }

    public void setItems(List<Customer> mItems) {
        this.mItems = mItems;
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Customer getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null){
            view = mInflater.inflate(R.layout.list_item_customer, parent, false);
            holder = new ViewHolder();
            holder.txtCustomerName = (TextView) view.findViewById(R.id.txt_customer_name);
            holder.txtCustomerAddress = (TextView) view.findViewById(R.id.txt_customer_address);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //fill row data
        Customer currentItem = getItem(position);
        if (currentItem != null) {
            holder.txtCustomerName.setText("Nama : "+currentItem.getName());
            holder.txtCustomerAddress.setText("Alamat : "+currentItem.getAddress());
        }
        return view;
    }

    class ViewHolder {
        TextView txtCustomerName;
        TextView txtCustomerAddress;
    }
}

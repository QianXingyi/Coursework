package cn.moecity.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListViewAdapter(List<Map<String, Object>> data, LayoutInflater layoutInflater, Context context) {
        this.data = data;
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    public final class ItemContent {
        public TextView itemName;
        public TextView contentView;
        public TextView idView;
        public TextView roleView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemContent itemContent = null;
        if (itemContent == null) {
            itemContent = new ItemContent();
            convertView = layoutInflater.inflate(R.layout.fragment_item, null);
            itemContent.idView = (TextView) convertView.findViewById(R.id.objectIdView);
            itemContent.contentView = (TextView) convertView.findViewById(R.id.contentView);
            itemContent.itemName = (TextView) convertView.findViewById(R.id.item_name);
            itemContent.roleView = (TextView) convertView.findViewById(R.id.roleView);
            convertView.setTag(itemContent);
        } else {
            itemContent = (ItemContent) convertView.getTag();
        }
        itemContent.itemName.setText((String) data.get(position).get("itemName"));
        itemContent.idView.setText((String) data.get(position).get("objectId"));
        itemContent.contentView.setText((String) data.get(position).get("content"));
        itemContent.roleView.setText((String) data.get(position).get("role"));


        return convertView;
    }
}

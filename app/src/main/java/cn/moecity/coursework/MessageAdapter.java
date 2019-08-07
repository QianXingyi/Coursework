package cn.moecity.coursework;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;


    public MessageAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class MessageItem {
        //        public ImageView image;
        public TextView title;
        public Button scheBtn;
        public Button chatBtn;
        public Button locBtn;
        public TextView info;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        MessageItem messageItem = null;
        if (convertView == null) {
            messageItem = new MessageItem();
            convertView = layoutInflater.inflate(R.layout.fragmnet_message, null);
            messageItem.title = (TextView) convertView.findViewById(R.id.title);

            //schedule button
            messageItem.scheBtn = (Button) convertView.findViewById(R.id.scheBtn);
            messageItem.chatBtn = (Button) convertView.findViewById(R.id.chatBtn);
            messageItem.locBtn = (Button) convertView.findViewById(R.id.locBtn);
            messageItem.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(messageItem);
        } else {
            messageItem = (MessageItem) convertView.getTag();
        }
        messageItem.title.setText((String) data.get(position).get("title"));
        messageItem.info.setText((String) data.get(position).get("info"));

        messageItem.scheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra("NName", (String) data.get(position).get("title"));
                intent.putExtra("Id", (String) data.get(position).get("info"));
                context.startActivity(intent);
            }
        });

        messageItem.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("NName", (String) data.get(position).get("title"));
                intent.putExtra("Id", (String) data.get(position).get("info"));
                context.startActivity(intent);
            }
        });

        messageItem.locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("NName", (String) data.get(position).get("title"));
                intent.putExtra("Id", (String) data.get(position).get("info"));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}

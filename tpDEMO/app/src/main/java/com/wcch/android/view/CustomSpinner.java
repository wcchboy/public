package com.wcch.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.igrs.betotablet.R;

import java.util.ArrayList;

public class CustomSpinner extends LinearLayout {
    private int mWidth = 240;
    private TextView editText;
    private ImageView imageView, icon;
    private PopupWindow popupWindow = null;
    private ArrayList<String> dataList = new ArrayList<>();

    private int checkIndex = 0;

    public CustomSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_spinner, this, true);
        editText = findViewById(R.id.text);
        imageView = findViewById(R.id.btn);
        icon = findViewById(R.id.iv_internet_icon);
        if (!iconVisible) icon.setVisibility(GONE);
        setOnClickListener(v -> {
            if (popupWindow == null) {
                showPopWindow();
            } else {
                closePopWindow();
            }
        });
    }

    private void showPopWindow() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_wrapper, null, false);
        ListView listView = contentView.findViewById(R.id.listView);
        listView.setAdapter(new SpinnerAdapter());
        popupWindow = new PopupWindow(contentView, mWidth, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this, 0, 10);
    }

    private void closePopWindow() {
        popupWindow.dismiss();
        popupWindow = null;
    }

    public void setItemsData(ArrayList<String> list) {
        if (dataList != null && dataList.size() > 0) {
            dataList.clear();
        }
        dataList.addAll(list);
    }

    boolean isMeetingRoomSpinner = false;

    public void setMeetingRoomSpinner(boolean isMeetingRoomSpinner) {
        this.isMeetingRoomSpinner = isMeetingRoomSpinner;
    }

    public void setIndex(int index) {
        if (dataList != null && !dataList.isEmpty() && index <= dataList.size() - 1) {
            if (isMeetingRoomSpinner) {
                editText.setText(dataList.get(index).substring(0, 5));
            } else {
                editText.setText(dataList.get(index));
            }

            checkIndex = index;
        } else {
            checkIndex = 0;
        }
    }

    public void setTextColor(int textColor) {
        editText.setTextColor(textColor);
    }

    public void setArrowSrc(int src) {
        imageView.setImageResource(src);
    }

    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
              //  convertView = View.inflate(CustomSpinner.this.getContext(), R.layout.item_custom_popwin, null);
                convertView = View.inflate(CustomSpinner.this.getContext(), R.layout.new_item_custom_popwin, null);
                holder = new Holder();
                holder.tv = convertView.findViewById(R.id.tv);
                holder.iv_checked = convertView.findViewById(R.id.iv_checked);
                holder.layout = convertView.findViewById(R.id.layout_container);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv.setText(dataList.get(position));
            if (position == checkIndex) {
//                holder.tv.setTextColor(getResources().getColor(R.color.update_button));
                holder.iv_checked.setVisibility(VISIBLE);
            } else {
//                holder.tv.setTextColor(getResources().getColor(R.color.text_unchecked));
                holder.iv_checked.setVisibility(INVISIBLE);
            }

            final String text = dataList.get(position);
            holder.layout.setOnClickListener(v -> {
                editText.setText(text);
                if (listener != null) {
                    listener.onSelect(position);
                }
                closePopWindow();
            });
            return convertView;
        }
    }

    private class Holder {
        TextView tv;
        ImageView iv_checked;
        RelativeLayout layout;
    }


    private boolean iconVisible = true;

    public void setIconVisible(boolean iconVisible) {
        this.iconVisible = iconVisible;
        if (icon != null) {
            if (iconVisible) {
                icon.setVisibility(VISIBLE);
            } else {
                icon.setVisibility(GONE);
            }
        }
    }

    //=========================================================
    private onSelectListener listener;

    public interface onSelectListener {
        void onSelect(int index);
    }

    public void setOnSelectListener(onSelectListener listener) {
        this.listener = listener;
    }

    public int getIndex() {
        return checkIndex;
    }
}

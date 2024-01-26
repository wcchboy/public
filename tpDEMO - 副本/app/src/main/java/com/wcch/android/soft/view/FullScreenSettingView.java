package com.wcch.android.soft.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.igrs.betotablet.R;
import com.igrs.betotablet.soft.entity.Msg19;
import com.igrs.betotablet.soft.util.ConfigUtil;
import com.igrs.tpsdk.ProjectionSDK;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FullScreenSettingView extends RelativeLayout {
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<FullScreenSettingModel> list;
    private RelativeLayout layout_main;

    public FullScreenSettingView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public FullScreenSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public FullScreenSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_fullscreensetting, this, true);

        OnClickListener closeOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        };
        layout_main = findViewById(R.id.layout_main);
        layout_main.setOnClickListener(closeOnClickListener);
        findViewById(R.id.img_close).setOnClickListener(closeOnClickListener);

        list = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            FullScreenSettingModel model = new FullScreenSettingModel();
//            model.dev_id = i + "";
//            model.dev_name = "name_" + i;
//            model.dev_type = i % 4;
//            model.isDef = i == 0;
//            list.add(model);
//        }
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,40));
        recyclerView.setAdapter(adapter);
    }

    public List<FullScreenSettingModel> getList() {
        return list;
    }



    public void update(List<FullScreenSettingModel> list){
        this.list = list;
        adapter.notifyDataSetChanged();
    }

    public void switchLayout(int type,String taskId){
        try{
            JSONObject json = new JSONObject();
            json.put("cmd",18);
            json.put("mode",type);
            json.put("taskId",taskId);
            ProjectionSDK.getInstance().sendCMDMessage(ConfigUtil.getInstance().getIdentification(), json.toString().getBytes());
        }catch (Exception e){
        }
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount; //列数
        private int spacing; //间隔
        public GridSpacingItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }

     class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fullscreensetting, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            FullScreenSettingModel model = list.get(position);
            holder.txt_dev_name.setText(model.dev_name);
            holder.txt_is_my.setVisibility(position==0?VISIBLE:GONE);
            if(model.isDef){
                holder.img_is_def.setVisibility(VISIBLE);
                holder.layout_main.setBackgroundResource(R.drawable.selector_158cff_8_1);
            }else{
                holder.img_is_def.setVisibility(GONE);
                holder.layout_main.setBackgroundResource(R.drawable.selector_e7e7e7_8);
            }
            switch (model.dev_type){
                case FullScreenSettingModel.TYPE_ANDROID:
                case FullScreenSettingModel.TYPE_IOS:
                    holder.img_dev_type.setImageResource(R.drawable.ic_phone);
                    break;
                case FullScreenSettingModel.TYPE_ANDROID_PAD:
                case FullScreenSettingModel.TYPE_IPAD:
                    holder.img_dev_type.setImageResource(R.drawable.ic_pad);
                    break;
                case FullScreenSettingModel.TYPE_PC:
                    holder.img_dev_type.setImageResource(R.drawable.ic_tp_pc);
                    break;
                default:
                    holder.img_dev_type.setImageResource(R.drawable.ic_phone);

            }
            holder.layout_main.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (FullScreenSettingModel fm:list) {
                        fm.isDef = false;
                    }
                    switchLayout(Msg19.MODE_FULL_SCREEN,model.dev_id);
                    //model.isDef = true;
                    notifyDataSetChanged();
                    FullScreenSettingView.this.setVisibility(GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            View layout_main;
            TextView txt_dev_name;
            ImageView img_dev_type;
            TextView txt_is_my;
            ImageView img_is_def;

            public MyViewHolder(View view) {
                super(view);
                layout_main = view;
                txt_dev_name = view.findViewById(R.id.txt_dev_name);
                img_dev_type = view.findViewById(R.id.img_dev_type);
                txt_is_my = view.findViewById(R.id.txt_is_my);
                img_is_def = view.findViewById(R.id.img_is_def);
            }
        }

    }


    public static class FullScreenSettingModel {


        //0 默认 ；1 Android Phone，2 iPhone，3:PC(Win);4:Android Pad;5:iPad
        public static final int TYPE_ANDROID=1;
        public static final int TYPE_IOS=2;
        public static final int TYPE_PC=3;
        public static final int TYPE_ANDROID_PAD=4;
        public static final int TYPE_IPAD=5;

        public boolean isDef;
        public boolean isMy;
        public String dev_id;
        public String dev_name;
        public int dev_type;

    }

}

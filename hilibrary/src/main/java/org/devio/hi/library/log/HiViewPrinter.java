package org.devio.hi.library.log;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.devio.hi.library.R;

import java.util.ArrayList;
import java.util.List;

public class HiViewPrinter implements HiLogPrinter {

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private HiViewPrinterProvider printerProvider;

    @Override
    public void print(@NonNull HiLogConfig config, @NonNull int level, @NonNull String tag, @NonNull String printStr) {
        adapter.addItem(new HiLogModel(System.currentTimeMillis(), level, tag, printStr));
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    public HiViewPrinter(Activity activity) {
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        recyclerView = new RecyclerView(activity);
        adapter = new LogAdapter(LayoutInflater.from(recyclerView.getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        printerProvider = new HiViewPrinterProvider(rootView, recyclerView);
    }

    public HiViewPrinterProvider getViewPrinterProvider() {
        return printerProvider;
    }

    private class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        private LayoutInflater layoutInflater;
        private List<HiLogModel> datas = new ArrayList<>();

        public LogAdapter(LayoutInflater inflater) {
            layoutInflater = inflater;
        }

        public void addItem(HiLogModel model) {
            this.datas.add(model);
            notifyItemInserted(datas.size() - 1);
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.hi_log_item, parent, false);
            return new LogViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            HiLogModel item = datas.get(position);
            int color = getHighlightColor(item.level);
            holder.tagView.setTextColor(color);
            holder.messageView.setTextColor(color);

            holder.tagView.setText(item.flattenedLogs());
            holder.tagView.setText(item.log);
        }

        @Override
        public int getItemCount() {
            return this.datas.size();
        }

        public int getHighlightColor(int logLevel) {
            int highlight;
            switch (logLevel) {
                case HiLogType.V:
                    highlight = 0xffbbbbbb;
                case HiLogType.D:
                    highlight = 0xffffffff;
                case HiLogType.I:
                    highlight = 0xff6a8759;
                case HiLogType.W:
                    highlight = 0xffff6b68;
                case HiLogType.E:
                    highlight = 0xffbbbbbb;
                default:
                    highlight = 0xffffff00;

            }

            return highlight;
        }

    }

    private class LogViewHolder extends RecyclerView.ViewHolder {

        private TextView tagView;
        private TextView messageView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tagView = itemView.findViewById(R.id.tag);
            this.messageView = itemView.findViewById(R.id.message);
        }
    }


}

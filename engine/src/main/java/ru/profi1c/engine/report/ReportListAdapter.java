package ru.profi1c.engine.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;

/**
 * Адаптер для списка отчетов
 */
public class ReportListAdapter extends BaseAdapter {
    private static final String TAG = ReportListAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    private final List<IReport> mItems;
    private final int mResIdLayout;

    public ReportListAdapter(Context context, List<IReport> items) {
        this(context, items, R.layout.fba_simple_report_item);
    }

    public ReportListAdapter(Context context, List<IReport> items, int resource) {
        mInflater = LayoutInflater.from(context);
        this.mItems = items;
        mResIdLayout = resource;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public IReport getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        IReport item = getItem(position);

        if (row == null) {
            row = mInflater.inflate(mResIdLayout, parent, false);

            holder = new ViewHolder(row);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.icon.setImageResource(item.getResIdIcon());
        holder.title.setText(item.getResIdTitle());

        return row;
    }

    private static class ViewHolder {

        ImageView icon;
        TextView title;

        public ViewHolder(View base) {
            try {
                icon = (ImageView) base.findViewById(android.R.id.icon);
                title = (TextView) base.findViewById(android.R.id.text1);
            } catch (ClassCastException e) {
                Dbg.printStackTrace(e);
                throw new IllegalArgumentException(
                        "You layout must have identifiers 'android.R.id.icon' and 'android.R.id.text1'",
                        e);

            }
        }
    }
}

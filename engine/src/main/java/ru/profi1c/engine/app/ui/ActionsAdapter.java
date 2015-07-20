package ru.profi1c.engine.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.R;

public class ActionsAdapter extends BaseAdapter {
    private static final String TAG = ActionsAdapter.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;
    private int mDropDownResource;
    private int mResource;
    private int mTextViewResourceId;
    private int mIconViewResourceId;
    private LayoutInflater mLi;
    private List<IActionItem> mData;

    public ActionsAdapter(Context context, IActionItem[] objects) {
        init(context, Arrays.asList(objects), R.layout.fba_simple_action_item,
             R.layout.fba_simple_action_dropdown_item, android.R.id.text1, android.R.id.icon);
    }

    public ActionsAdapter(Context context, List<IActionItem> objects) {
        init(context, objects, R.layout.fba_simple_action_item,
             R.layout.fba_simple_action_dropdown_item, android.R.id.text1, android.R.id.icon);
    }

    public ActionsAdapter(Context context, List<IActionItem> objects, int resource) {
        init(context, objects, resource, R.layout.fba_simple_action_dropdown_item,
             android.R.id.text1, android.R.id.icon);
    }

    public ActionsAdapter(Context context, int resource, int textViewResourceId,
            int iconViewResourceId, List<IActionItem> objects) {
        init(context, objects, resource, resource, textViewResourceId, iconViewResourceId);
    }

    private void init(Context context, List<IActionItem> data, int resource, int dropDownResource,
            int textViewResourceId, int iconViewResourceId) {
        mResource = resource;
        mDropDownResource = dropDownResource;
        mData = data;
        mTextViewResourceId = textViewResourceId;
        mIconViewResourceId = iconViewResourceId;
        mLi = LayoutInflater.from(context);
    }

    /**
     * <p>
     * Sets the layout resource to create the drop down views.
     * </p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public IActionItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(mDropDownResource, position, convertView, parent);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        return getView(mResource, position, v, parent);
    }

    // custom view
    private View getView(int resLayout, int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            v = mLi.inflate(resLayout, parent, false);
            holder = new ViewHolder(v, mTextViewResourceId, mIconViewResourceId);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        IActionItem item = getItem(position);
        holder.text.setText(item.getTitleResId());
        holder.image.setImageResource(item.getIconResId());
        return v;

    }

    private static class ViewHolder {
        final TextView text;
        final ImageView image;

        ViewHolder(View root, int resIdTitle, int resIdIcon) {
            try {
                text = (TextView) root.findViewById(resIdTitle);
                image = (ImageView) root.findViewById(resIdIcon);
            } catch (ClassCastException e) {
                if (DEBUG) {
                    Dbg.d(TAG, "You must supply a resource ID for a TextView and ImageView");
                }
                throw new IllegalStateException(
                        "Requires the resource ID to be a TextView and ImageView", e);
            }
        }
    }
}

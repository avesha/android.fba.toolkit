package ru.profi1c.engine.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ru.profi1c.engine.Dbg;

public class DescriptionAdapter extends BaseAdapter {
    private static final String TAG = DescriptionAdapter.class.getSimpleName();
    private static final boolean DEBUG = Dbg.DEBUG;

    private int mDropDownResource;
    private int mResource;
    private int mTextViewResourceId;
    private LayoutInflater mLi;
    private List<? extends IDescription> mData;

    /**
     * Designer. For items in the list is used layout {@link android.R.layout.simple_spinner_item}
     * ie elements will look like a standard Spinner
     *
     * @param context the current context.
     * @param objects an array of objects to represent the control ListView.
     */
    public DescriptionAdapter(Context context, IDescription[] objects) {
        init(context, Arrays.asList(objects), android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
    }

    public DescriptionAdapter(Context context, List<? extends IDescription> objects) {
        init(context, objects, android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
    }

    public DescriptionAdapter(Context context, int resource, IDescription[] objects) {
        init(context, Arrays.asList(objects), resource, resource, android.R.id.text1);
    }

    public DescriptionAdapter(Context context, int resource, int textViewResourceId,
            IDescription[] objects) {
        init(context, Arrays.asList(objects), resource, resource, textViewResourceId);
    }

    public DescriptionAdapter(Context context, int resource, List<? extends IDescription> objects) {
        init(context, objects, resource, resource, android.R.id.text1);
    }

    public DescriptionAdapter(Context context, int resource, int textViewResourceId,
            List<IDescription> objects) {
        init(context, objects, resource, resource, textViewResourceId);
    }

    private void init(Context context, List<? extends IDescription> data, int resource, int dropDownResource,
            int textViewResourceId) {
        mResource = resource;
        mData = data;
        mTextViewResourceId = textViewResourceId;
        mDropDownResource = dropDownResource;
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

    /**
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public IDescription getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
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

        View view;
        TextView text;

        if (convertView == null) {
            view = mLi.inflate(resLayout, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mTextViewResourceId == 0) {
                // If no custom field is assigned, assume the whole resource is
                // a TextView
                text = (TextView) view;
            } else {
                // Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mTextViewResourceId);
            }
        } catch (ClassCastException e) {
            if (DEBUG) {
                Dbg.d(TAG, "You must supply a resource ID for a TextView");
            }
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        IDescription item = getItem(position);
        text.setText(item.getDescription());
        return view;
    }


}

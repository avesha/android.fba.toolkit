package com.sample.app2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.sample.app2.db.DocumentSobitie;
import com.sample.app2.db.DocumentSobitieDao;
import com.sample.app2.test_action.ITestAction;

import java.sql.SQLException;

import ru.profi1c.engine.Dbg;
import ru.profi1c.engine.FbaRuntimeException;
import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.app.SimpleDocumentFragment;
import ru.profi1c.engine.meta.Ref;

public class TASimpleDocumentFragment extends FbaDBActivity {

    private DocFragment mFragment;
    private DocumentSobitieDao mDao;

    private static Intent getStartIntent(Context context) {
        return new Intent(context, TASimpleDocumentFragment.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_simple_document_fragment);
        try {
            init();
        } catch (SQLException e) {
            throw new FbaRuntimeException(e);
        }
    }

    private void init() throws SQLException {
        Button bnt = (Button) findViewById(R.id.bntSave);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSave();
            }
        });

        bnt = (Button) findViewById(R.id.bntNew);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNew();
            }
        });

        bnt = (Button) findViewById(R.id.bntEdit);
        bnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEdit();
            }
        });

        mDao = getHelper().getDao(DocumentSobitie.class);
    }

    protected void onEdit() {
        try {
            DocumentSobitie doc = mDao.findByNumber("00000000040");
            mFragment = DocFragment.newInstance(doc);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, mFragment);
            ft.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void onNew() {
        mFragment = DocFragment.newInstance(null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, mFragment);
        ft.commit();
    }

    protected void onSave() {
        int resIdMsg = R.string.msg_no_created_object;

        if (mFragment != null) {
            try {
                mFragment.save();
                resIdMsg = R.string.msg_success_save_object;
            } catch (SQLException e) {
                Dbg.printStackTrace(e);
                resIdMsg = R.string.msg_err_save_object;
            }
        }
        showToast(resIdMsg);
    }

    public static class DocFragment extends SimpleDocumentFragment<DocumentSobitie> {

        public static DocFragment newInstance(Ref ref) {
            DocFragment fragment = new DocFragment();
            if (ref != null) {
                fragment.setArguments(DocFragment.toBundle(ref));
            }
            return fragment;
        }
    }

    public static class TestActionShow implements ITestAction {

        public static final String DESCRIPTION = "View one DocumentSobitie";

        @Override
        public void run(Context context) {
            context.startActivity(getStartIntent(context));
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }
    }
}

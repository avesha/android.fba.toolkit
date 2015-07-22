package ru.profi1c.samples.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.SQLException;
import java.util.UUID;

import ru.profi1c.engine.app.FbaDBActivity;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.samples.order.db.DocumentZakazPokupatelya;

/*
 * Форма документа «Заказ  покупателя», позволяет создать новый (или изменить существующий) документ интерактивно.
 * Менеджер компоновки типа (ViewPager) отображает реквизиты документа и табличную часть на отдельных страницах.
 */
public class DocZakazItemActivity extends FbaDBActivity {
    private static final String TAG = DocZakazItemActivity.class.getSimpleName();

    public static final String EXTRA_REF = "ref";

    private ViewPager mPager;
    private DocPagerAdapter mDocAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_item);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_zakaz_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save) {

            //Предложить сохранить документ
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_save_changes);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              onSaveDoc();
                                          }
                                      });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        Ref ref = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uuid = extras.getString(EXTRA_REF);
            ref = new DocumentZakazPokupatelya();
            ref.setRef(UUID.fromString(uuid));
        }
        Log.i(TAG, ".init(), ref = " + ref);

        //Адаптер для страниц
        mDocAdapter = new DocPagerAdapter(getSupportFragmentManager(), ref);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mDocAdapter);

        mPager.setOffscreenPageLimit(mDocAdapter.getCount());
        mPager.setPageMargin(6);
        mPager.setPageMarginDrawable(R.color.pager_divider_color);

        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                myOnPageSelectedLogic(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        myOnPageSelectedLogic(0);
    }

    /*
     * Действия при смене текущей страницы: в подзаголовке ActionBar покажем
     * название страницы
     */
    protected void myOnPageSelectedLogic(int position) {
        final CharSequence subtitle = mPager.getAdapter().getPageTitle(position);
        getSupportActionBar().setSubtitle(subtitle);
    }

    /*
     * Сохранить документ в локальной базе
     */
    private void onSaveDoc() {

        Ref doc = mDocAdapter.save();
        if (doc != null) {

            // Уведомить об изменении
            Intent i = new Intent(Const.ACTION_UPDATE_ITEM);
            i.addCategory(Const.CATEGORY_CHANGED_DOC_ZAKAZ);
            i.putExtra(DocZakazItemActivity.EXTRA_REF, doc.toString());
            sendBroadcast(i);

            finish();
        }

    }

    /*
     * Адаптер с двумя страницами отображающими реквизиты документа и его
     * табличную часть «Товары». Для упрощения примера и фрагменты создаются
     * один раз и хранятся в памяти.
     */
    private static class DocPagerAdapter extends FragmentPagerAdapter {

        static final String[] titles = new String[]{"Информация", "Товары"};
        private Ref ref;

        private DocZakazFragment docFragment;
        private DocZakazTPTovariFragment tpFragment;

        public DocPagerAdapter(FragmentManager fm, Ref ref) {
            super(fm);
            this.ref = ref;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    if (docFragment == null) {
                        docFragment = DocZakazFragment.newInstance(ref);
                    }
                    return docFragment;
                case 1:
                    if (tpFragment == null) {
                        tpFragment = DocZakazTPTovariFragment.newInstance(ref);
                    }
                    return tpFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        /**
         * Сохранить (обновить ) документ
         *
         * @return Возвращает ссылку на обновленный документ или null в случае
         * ошибки
         */
        public Ref save() {

            Ref doc = null;
            try {
                // Сначала сам документ, потом табличную часть
                // т.к таблицы связаны по внешнему ключу
                docFragment.getObject().summa = tpFragment.getSumTotal();
                docFragment.save();

                tpFragment.save(docFragment.getObject());

                doc = docFragment.getObject();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return doc;
        }
    }
}

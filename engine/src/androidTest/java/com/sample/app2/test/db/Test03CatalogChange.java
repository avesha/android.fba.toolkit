package com.sample.app2.test.db;

import com.sample.app2.R;
import com.sample.app2.db.CatalogCenovieGruppi;
import com.sample.app2.db.CatalogDogovoriKontragentov;
import com.sample.app2.db.CatalogFizicheskieLica;
import com.sample.app2.db.CatalogHarakteristikiNomenklaturi;
import com.sample.app2.db.CatalogKachestvo;
import com.sample.app2.db.CatalogKontragenti;
import com.sample.app2.db.CatalogNomenklatura;
import com.sample.app2.db.CatalogValyuti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import ru.profi1c.engine.meta.Catalog;
import ru.profi1c.engine.meta.Ref;
import ru.profi1c.engine.util.tree.GenericTree;

public class Test03CatalogChange extends DBTestCase {
    private static final String TAG = Test03CatalogChange.class.getSimpleName();

    public void testFindByCodeEmptyParent() throws SQLException {
        //Найти элемент по коду
        Catalog item = dao.daoCatalogKachestvo.findByCode("000000002");
        assertNotNull(item);
    }

    public void testFindByDescEmptyParent() throws SQLException {
        //Найти элемент по наименованию
        Catalog item = dao.daoCatalogOrganizacii.findByDescription("Cтройснаб");
        assertNotNull(item);
    }

    public void testFindByDescQuote() throws SQLException {

        //add item and find him
        final String desc = "Общество на доверии \"Белочкка и ёжик\"";
        final String id = "f328e6aa-0ca0-11e5-a6c0-1697f925ec7b";

        CatalogKontragenti ref = dao.daoCatalogKontragenti.findByRef(id);
        if(CatalogKontragenti.isEmpty(ref)) {
            int code = dao.daoCatalogKontragenti.getNextCode();
            int lenCode = dao.daoCatalogKontragenti.getCodeLength();
            if (lenCode == 0) {
                lenCode = CATALOG_CODE_LENGTH;
            }

            ref = dao.daoCatalogKontragenti.newItem();
            ref.setRef(UUID.fromString(id));

            ref.setCode(dao.daoCatalogKontragenti.formatNumber(code++, lenCode));
            ref.setDescription(desc);
            ref.setParent(Ref.emptyUUID());
            dao.daoCatalogKontragenti.create(ref);
        }

        //Найти элемент по наименованию где есть двойные кавычки
        Catalog item = dao.daoCatalogKontragenti.findByDescription(desc);
        assertFalse(Catalog.isEmpty(item));
        assertEquals(ref, item);
    }

    public void testFindByDescSingleQuote() throws SQLException {
        //Найти элемент по наименованию где есть одинарные кавычки
        Catalog item = dao.daoCatalogOrganizacii.findByDescription("Торгровый дом 'Аппатиты'");
        assertFalse(Catalog.isEmpty(item));
    }

    public void testFindByDescLikeEmptyParent() throws SQLException {
        //Найти по частичному совпадению наименования
        Catalog item = dao.daoCatalogOrganizacii.findByDescription("Комплекс", true, null, null);
        assertNotNull(item);
    }

    public void testFindByCodeParent() throws SQLException {
        //Найти по коду и родителю
        Catalog folder = dao.daoCatalogNomenklatura.findByCode("00000000053");
        assertNotNull(folder);

        Catalog item = dao.daoCatalogNomenklatura.findByCode("00000000064",
                (CatalogNomenklatura) folder, null);
        assertNotNull(item);
    }

    public void testFindByDescrParent() throws SQLException {
        Catalog folder = dao.daoCatalogNomenklatura.findByDescription("Кондитерские изделия");
        assertNotNull(folder);
        log(TAG, "testFindByParent: parent =  " + folder.toString());

        Catalog item = dao.daoCatalogNomenklatura.findByDescription("Барбарис (конфеты)",
                (CatalogNomenklatura) folder);
        assertNotNull(item);
        log(TAG, "testFindByDescrParent: item =  " + item.getDescription());
    }

    public void testFindByAttrEmptyParent() throws SQLException {
        //найти по значению реквизита
        Catalog item = dao.daoCatalogNomenklatura.findByAttribute(
                CatalogNomenklatura.FIELD_NAME_ARTIKUL, "Арт-7777");
        assertNotNull(item);
        log(TAG + ".testFindByParent: parent =  " + item.getDescription());
    }

    public void testSelectEmptyParent() throws SQLException {
        //Выбрать все элементы справочника
        List<CatalogFizicheskieLica> list = dao.daoCatalogFizicheskieLica.select();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    public void testSelectParent() throws SQLException {
        //Выбрать из группы
        CatalogFizicheskieLica folser = dao.daoCatalogFizicheskieLica.findByCode("000000046");
        assertNotNull(folser);

        List<CatalogFizicheskieLica> list = dao.daoCatalogFizicheskieLica.select(folser);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    public void testSelectParentFilter() throws SQLException {
        //Выбрать из группы с дополнительным отбором (где наименование  = "Королев Сергей Васильевич")
        CatalogFizicheskieLica folder = dao.daoCatalogFizicheskieLica.findByCode("000000048");
        assertNotNull(folder);

        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(Catalog.FIELD_NAME_DESCRIPTION, "Королев Сергей Васильевич");


        List<CatalogFizicheskieLica> list = dao.daoCatalogFizicheskieLica.select(folder, filter);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    public void testSelectParentFilterOrder() throws SQLException {
        //Выбрать из группы с дополнительный упорядочиванием
        //группа 'Менеджеры'
        CatalogFizicheskieLica folder = dao.daoCatalogFizicheskieLica.findByCode("000000048");
        assertNotNull(folder);

        String[] unGroup = new String[]{"Петрищев Олег Константинович", "Сидоченко К.В. ",
                "Королев Сергей Васильевич"};
        Set<String> setGroup = new TreeSet<String>(Collections.reverseOrder());
        setGroup.addAll(Arrays.asList(unGroup));

        List<CatalogFizicheskieLica> list = dao.daoCatalogFizicheskieLica.select(folder, null, null,
                Catalog.FIELD_NAME_DESCRIPTION + " DESC");
        assertNotNull(list);
        assertEquals(3, list.size());
        List<String> lstDesc = new ArrayList<String>();
        for (CatalogFizicheskieLica item : list) {
            log(TAG, "testSelectParentFilterOrder: item =  " + item.getDescription());
            lstDesc.add(item.getDescription());
        }

        assertTrue(setGroup.containsAll(lstDesc));
    }

    public void testSelectOnlyOrder() throws SQLException {
        List<CatalogFizicheskieLica> list = dao.daoCatalogFizicheskieLica.select(null, null, null,
                Catalog.FIELD_NAME_DESCRIPTION + " DESC");
        assertNotNull(list);
        assertTrue(list.size() > 0);

    }

    public void testSelectHierarchy() throws SQLException {
        //Выбрать иерархию (только группы)
        GenericTree<CatalogFizicheskieLica> tree = dao.daoCatalogFizicheskieLica.selectHierarchy();
        assertNotNull(tree);

        //Дерево в линейный список (корень не включен)
        List<CatalogFizicheskieLica> lst = dao.daoCatalogFizicheskieLica.hierarchyToList(tree,
                false);
        assertNotNull(lst);
        assertTrue(lst.size() > 0);

        for (CatalogFizicheskieLica item : lst) {
            log(TAG, "testSelectHierarchy code = " + item.getCode());
            assertTrue(item.isFolder());
        }
    }

    public void testSelectHierarchically() throws SQLException {
        GenericTree<CatalogFizicheskieLica> tree = dao.daoCatalogFizicheskieLica.selectHierarchically();
        assertNotNull(tree);

        List<CatalogFizicheskieLica> lst = dao.daoCatalogFizicheskieLica.hierarchyToList(tree,
                false);
        assertNotNull(lst);
        assertTrue(lst.size() > 0);

        final String level = "012333233323332333111111111111111111111111111";
        StringBuilder sbLevel = new StringBuilder();
        for (CatalogFizicheskieLica item : lst) {
            log(TAG, "testSelectHierarchy desc = " + item.getLevel() + " " +
                     item.getDescription());
            sbLevel.append(item.getLevel());
        }
        assertTrue(sbLevel.toString().startsWith(level));

    }

    public void testSelectHierarchicallyParent() throws SQLException {
        //Выбрать иерархически элементы справочника по родителю  (с пользовательской сортировкой)
        CatalogNomenklatura folder = dao.daoCatalogNomenklatura.findByCode("00000000012");

        GenericTree<CatalogNomenklatura> tree = dao.daoCatalogNomenklatura.selectHierarchically(
                folder, Catalog.FIELD_NAME_DESCRIPTION + " DESC");
        assertNotNull(tree);

        List<CatalogNomenklatura> lst = dao.daoCatalogNomenklatura.hierarchyToList(tree, true);
        assertNotNull(lst);
        assertTrue(lst.size() > 0);

        for (CatalogNomenklatura item : lst) {
            log(TAG, "testSelectHierarchicallyParent: level = " + item.getLevel() + " desc = " +
                     item.getDescription());
        }

    }

    public void testSelectOwner() throws SQLException {
        //Найти по значению ссылки
        CatalogNomenklatura item = dao.daoCatalogNomenklatura.findByRef(
                "bd72d910-55bc-11d9-848a-00112f43529a");
        assertNotNull(item);

        //Выбрать все по владельцу из подчиненного справочника
        List<CatalogHarakteristikiNomenklaturi> lst = dao.daoCatalogHarakteristikiNomenklaturi.select(
                null, item);
        assertNotNull(lst);
        assertTrue(lst.size() > 0);

        for (CatalogHarakteristikiNomenklaturi ch : lst) {
            log(TAG, "testSelectOwner desc = " + ch.getDescription());
        }
    }

    public void testSelectHierarchicallyNotHierarhy() throws SQLException {

        CatalogNomenklatura owner = dao.daoCatalogNomenklatura.findByRef(
                "bd72d910-55bc-11d9-848a-00112f43529a");
        assertNotNull(owner);

        //должен получить пустое дерево c одним корневым элементом
        GenericTree<CatalogHarakteristikiNomenklaturi> tree = dao.daoCatalogHarakteristikiNomenklaturi
                .selectHierarchically();
        assertNotNull(tree);
        assertEquals(1, tree.getNumberOfNodes());

        //должен получить пустое дерево c одним корневым элементом
        GenericTree<CatalogHarakteristikiNomenklaturi> tree2 = dao.daoCatalogHarakteristikiNomenklaturi
                .selectHierarchically(null, owner);
        assertNotNull(tree2);
        assertEquals(1, tree2.getNumberOfNodes());

        //должен получить пустое дерево c одним корневым элементом
        GenericTree<CatalogHarakteristikiNomenklaturi> tree3 = dao.daoCatalogHarakteristikiNomenklaturi
                .selectHierarchy();
        assertNotNull(tree3);
        assertEquals(1, tree3.getNumberOfNodes());
    }

    public void testGetNewCode() throws SQLException {
        //получить новый номер
        int newCode = dao.daoCatalogEdiniciIzmereniya.getNextCode();
        assertTrue(newCode > 0);
        log(TAG, "testGetNewCode code = " + newCode);

        newCode = dao.daoCatalogEdiniciIzmereniya.getNextCode("ЦУ");
        assertTrue(newCode > 3);
        log(TAG, "testGetNewCode code = " + newCode);

        newCode = dao.daoCatalogEdiniciIzmereniya.getNextCode("NEW");
        assertEquals(newCode, 1);
        log(TAG, "testGetNewCode code = " + newCode);

        newCode = dao.daoCatalogFizicheskieLica.getNextCode();
        assertTrue(newCode > 50);
        log(TAG, ".testGetNewCode code = " + newCode);
    }

    public void testFormatCode() {
        //Форматировать строковое представление номера
        String strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber(null, 5, 6);
        assertEquals(strCode, "000005");

        strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber("ПР", 5, 6);
        assertEquals(strCode, "ПР0005");

        strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber("ПР", 15, 6);
        assertEquals(strCode, "ПР0015");

        strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber("ПР", 215, 6);
        assertEquals(strCode, "ПР0215");

        strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber(null, 23215, 8);
        assertEquals(strCode, "00023215");

        strCode = dao.daoCatalogEdiniciIzmereniya.formatNumber("FF", 23215, 10);
        assertEquals(strCode, "FF00023215");
    }

    public void testSelectChangeNewOnly() throws SQLException {
        /*
         * Простой пример добавления в справочник «Номенклатура» новой группы и
		 * одного дочернего элемента
		 */
        int lenCode = LARGE_CATALOG_CODE_LENGTH;

        int newCode = dao.daoCatalogNomenklatura.getNextCode();
        CatalogNomenklatura folder = dao.daoCatalogNomenklatura.newFolder();
        assertNotNull(folder);

        String strCode = dao.daoCatalogNomenklatura.formatNumber(newCode, lenCode);
        folder.setCode(strCode);
        String descOne = "Новая группа " + System.currentTimeMillis();
        folder.setDescription(descOne);

        dao.daoCatalogNomenklatura.create(folder);
        newCode++;

        CatalogNomenklatura item = dao.daoCatalogNomenklatura.newItem();
        strCode = dao.daoCatalogNomenklatura.formatNumber(newCode, lenCode);
        item.setCode(strCode);
        item.setParent(folder.getRef());
        String descSecond = "Новая номенклатура" + System.currentTimeMillis();
        item.setDescription(descSecond);

        dao.daoCatalogNomenklatura.create(item);

        //Выбрать измененные (только добавленные элементы, т.е еще не переданные на сервер)
        List<CatalogNomenklatura> lst = dao.daoCatalogNomenklatura.selectChanged(true);
        assertNotNull(lst);
        assertTrue(lst.size() >= 2);

        boolean findOne = false;
        boolean findSecond = false;
        for (CatalogNomenklatura ch : lst) {
            log(TAG + ".testSelectChangeNewOnly desc = " + ch.getDescription());
            if (descOne.equals(ch.getDescription())) {
                findOne = true;
            } else if (descSecond.equals(ch.getDescription())) {
                findSecond = true;
            }
        }
        assertTrue(findOne);
        assertTrue(findSecond);
    }

    public void testSelectChange() throws SQLException {

        int lenCode = CATALOG_CODE_LENGTH;
        int newCode = dao.daoCatalogKachestvo.getNextCode();
        CatalogKachestvo item = dao.daoCatalogKachestvo.newItem();
        assertNotNull(item);

        String strCode = dao.daoCatalogKachestvo.formatNumber(newCode, lenCode);
        item.setCode(strCode);
        item.setDescription("Б/У " + System.currentTimeMillis());

        dao.daoCatalogKachestvo.create(item);
        newCode++;

        item = dao.daoCatalogKachestvo.newItem();
        strCode = dao.daoCatalogKachestvo.formatNumber(newCode, lenCode);
        item.setCode(strCode);
        item.setDescription("Некондиция " + System.currentTimeMillis());

        dao.daoCatalogKachestvo.create(item);

        //Выбрать измененные (в т.ч и новые элементы)
        List<CatalogKachestvo> lst = dao.daoCatalogKachestvo.selectChanged();
        assertNotNull(lst);
        assertTrue(lst.size() >= 2);

        for (CatalogKachestvo ch : lst) {
            log(TAG, "testSelectChange desc = " + ch.getDescription());
        }

    }

    public void testSetModifiedAllOff() throws SQLException {
        int all = dao.daoCatalogKachestvo.queryForAll().size();
        int changedBefore = dao.daoCatalogKachestvo.selectChanged().size();
        if (changedBefore == 0) {
            dao.daoCatalogKachestvo.setModified(true);
            int allChanged = dao.daoCatalogKachestvo.selectChanged().size();
            assertEquals(all, allChanged);
        }
        dao.daoCatalogKachestvo.setModified(false);
        int changedAfter = dao.daoCatalogKachestvo.selectChanged().size();
        assertEquals(0, changedAfter);
    }

    public void testSetModifiedAllOn() throws SQLException {
        int all = dao.daoCatalogFizicheskieLica.queryForAll().size();
        //Установить флаг модифицированности на всех
        dao.daoCatalogFizicheskieLica.setModified(true);
        int changedAfter = dao.daoCatalogFizicheskieLica.selectChanged().size();
        assertEquals(all, changedAfter);
    }

    public void testSetModifiedAllOnFilter() throws SQLException {
        //Установить признак модифицированности на группу «Женская обувь»
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put(CatalogFizicheskieLica.FIELD_NAME_FOLDER, true);
        filter.put(CatalogFizicheskieLica.FIELD_NAME_DESCRIPTION, "Женская обувь");
        dao.daoCatalogNomenklatura.setModified(true, filter);

        CatalogNomenklatura parent = dao.daoCatalogNomenklatura.findByDescription("Женская обувь");
        assertFalse(CatalogNomenklatura.isEmpty(parent));
        assertTrue(parent.isModified());

    }

    public void testChangeItemsCatalogCenovieGruppi() throws SQLException {
        String[] val = getTargetContext().getResources().getStringArray(R.array.cenovie_gruppi);
        String desc = val[rnd.nextInt(val.length)];

        //Изменение элемента
        CatalogCenovieGruppi ref = Dao.daoCatalogCenovieGruppi.findByDescription(desc);
        if (!CatalogCenovieGruppi.isEmpty(ref)) {

            final String newDesc = "изменен: " + desc;
            ref.setDescription(newDesc);
            ref.setParent(null);
            Dao.daoCatalogCenovieGruppi.update(ref);

            ref = Dao.daoCatalogCenovieGruppi.findByDescription(newDesc);
            assertFalse(CatalogCenovieGruppi.isEmpty(ref));
        }
    }

    public void testMarkAsDeleteKontragentAndDogovor() throws SQLException {

        String[] val = getTargetContext().getResources().getStringArray(R.array.kontragents);
        String desc = val[rnd.nextInt(val.length)];
        CatalogKontragenti ref = Dao.daoCatalogKontragenti.findByDescription(desc);

        // Пометить элемент справочника «Контрагенты» на удаление, все
        // договоры по нему так же пометить на удаление
        if (!CatalogKontragenti.isEmpty(ref)) {

            final String newDesc = "на удаление: " + desc;
            ref.setDescription(newDesc);
            ref.setDeletionMark(true);
            ref.setModified(true);

            Dao.daoCatalogKontragenti.update(ref);

            List<CatalogDogovoriKontragentov> lst = Dao.daoCatalogDogovoriKontragentov.select(null,
                    ref);
            for (CatalogDogovoriKontragentov dogovor : lst) {

                dogovor.setDeletionMark(true);
                dogovor.setModified(true);
                dogovor.setDescription("на удаление: " + dogovor.getDescription());

                Dao.daoCatalogDogovoriKontragentov.update(dogovor);
            }

            //check --------------------------------------------------
            ref = Dao.daoCatalogKontragenti.findByDescription(newDesc);
            assertFalse(CatalogKontragenti.isEmpty(ref));

            lst = Dao.daoCatalogDogovoriKontragentov.select(null, ref);
            for (CatalogDogovoriKontragentov dogovor : lst) {

                assertTrue(dogovor.isModified());
                assertTrue(dogovor.isDeletionMark());
                assertTrue(dogovor.getDescription().startsWith("на удаление: "));
            }
        }
    }

    public void testDeleteValuti() throws SQLException {
        String[] val_code = getTargetContext().getResources().getStringArray(R.array.valuta_code);
        String code = val_code[val_code.length-1];

        //Найти и удалить элемент справочника
        CatalogValyuti ref = Dao.daoCatalogValyuti.findByCode(code);
        if (!CatalogValyuti.isEmpty(ref)) {
            final String id = ref.getRef().toString();
            Dao.daoCatalogValyuti.delete(ref);

            //check
            ref = Dao.daoCatalogValyuti.queryForId(id);
            assertNull(ref);
        }
    }

}

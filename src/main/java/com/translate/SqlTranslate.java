package com.translate;

import com.match.SqlParse;
import com.models.*;
import com.persistence.DataPersistence;
import com.persistence.IndexPersistence;
import com.persistence.TablePersistence;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.util.DBError;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.StringReader;
import java.util.*;

public class SqlTranslate {
    public Table getTableByCreateTable(String createTableSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Table table = new Table();
        List<Column> columns = new ArrayList<>();
        CreateTable ct = null;
        try {
            ct = (CreateTable) pm.parse(new StringReader(createTableSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
            return null;
        }
        table.setName(ct.getTable().getName());
        if (ct.getColumnDefinitions() != null) {
            for (int i = 0; i < ct.getColumnDefinitions().size(); i++) {
                Column column = new Column();
                column.setName(ct.getColumnDefinitions().get(i).getColumnName());
                if (ct.getColumnDefinitions().get(i).getColumnSpecStrings() != null) {
                    if (ct.getColumnDefinitions().get(i).getColumnSpecStrings().size() == 4) {
                        if (ct.getColumnDefinitions().get(i).getColumnSpecStrings().toString().toUpperCase().equals("[PRIMARY, KEY, NOT, NULL]")) {
                            column.setPrimaryKey(true);
                            column.setNotNull(true);
                        }
                    } else if (ct.getColumnDefinitions().get(i).getColumnSpecStrings().size() == 2) {
                        if (ct.getColumnDefinitions().get(i).getColumnSpecStrings().toString().toUpperCase().equals("[PRIMARY, KEY]")) {
                            column.setPrimaryKey(true);
                        } else if (ct.getColumnDefinitions().get(i).getColumnSpecStrings().toString().toUpperCase().equals("[NOT, NULL]")) {
                            column.setNotNull(true);
                        }
                    }
                }
                column.setType(ct.getColumnDefinitions().get(i).getColDataType().getDataType());
                if (ct.getColumnDefinitions().get(i).getColDataType().getArgumentsStringList() == null) {
                    column.setLength(0);
                } else {
                    column.setLength(Integer.parseInt(ct.getColumnDefinitions().get(i).getColDataType().getArgumentsStringList().get(0)));
                }
                columns.add(column);
            }
        }
        table.setColumns(columns);
        TablePersistence.createTable(table);
        return table;
    }

    public TableData getTableDataByInsert(String insertSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Insert insert = null;
        try {
            insert = (Insert) pm.parse(new StringReader(insertSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
            return null;
        }
        TableData tableData = TableData.getDataByName(insert.getTable().getName());
        ExpressionList expressionList = (ExpressionList) insert.getItemsList();
        List<String> row = new ArrayList<>();
        for (int i = 0; i < expressionList.getExpressions().size(); i++) {
            row.add(expressionList.getExpressions().get(i).toString());
        }
        try {
            tableData.insertRow(insert.getColumns(), row, -1);
        } catch (DBError dbError) {
            dbError.printStackTrace();
        }
        DataPersistence.insertData(tableData);
        IndexPersistence.updateIndex(tableData.getTable().getName());
        return tableData;
    }

    private int getColumnIndex(Table table, String columnName) {
        int index = -1;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (columnName.toUpperCase().equals(table.getColumns().get(i).getName().toUpperCase())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            try {
                throw new DBError("column", columnName, null);
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
        }
        return index;
    }

    private List<Integer> doExpression(TableData tableData, Expression exp) {
        if (exp == null) return null;
        if (exp instanceof AndExpression) {
            AndExpression expression = (AndExpression) exp;
            Expression left = expression.getLeftExpression();
            List<Integer> partOne = doExpression(tableData, left);
            List<Integer> partOneCopy = new ArrayList<>();
            for (int i = 0; i < partOne.size(); i++) {
                int t = partOne.get(i);
                partOneCopy.add(t);
            }
            Expression right = expression.getRightExpression();
            List<Integer> partTwo = doExpression(tableData, right);
            List<Integer> partTwoCopy = new ArrayList<>();
            for (int i = 0; i < partTwo.size(); i++) {
                int t = partTwo.get(i);
                partTwoCopy.add(t);
            }
            for (int i = 0; i < partOne.size(); i++) {
                for (int j = 0; j < partTwo.size(); j++) {
                    if (partOne.get(i) == partTwo.get(j)) {
                        break;
                    } else {
                        partOne.remove(i);
                    }
                }
            }
            return partOne;
        } else if (exp instanceof OrExpression) {
            OrExpression expression = (OrExpression) exp;
            Expression left = expression.getLeftExpression();
            List<Integer> partOne = doExpression(tableData, left);
            List<Integer> partOneCopy = new ArrayList<>();
            for (int i = 0; i < partOne.size(); i++) {
                int t = partOne.get(i);
                partOneCopy.add(t);
            }
            Expression right = expression.getRightExpression();
            List<Integer> partTwo = doExpression(tableData, right);
            List<Integer> partTwoCopy = new ArrayList<>();
            for (int i = 0; i < partTwo.size(); i++) {
                int t = partTwo.get(i);
                partTwoCopy.add(t);
            }
            partOne.removeAll(partTwo);
            partOne.addAll(partTwo);
            return partOne;
        }
        BinaryExpression e = (BinaryExpression) exp;
        String l = e.getLeftExpression().toString();
        String r = e.getRightExpression().toString();
        Table table = tableData.getTable();
        int index = -1;
        List<List<String>> rows;
        List<Integer> delIndex;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (l.toUpperCase().equals(table.getColumns().get(i).getName().toUpperCase())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            try {
                throw new DBError("column", l, null);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return null;
            }
        } else {
            rows = tableData.getRows();
        }

        delIndex = getIndex(index, rows, r, exp);
        return delIndex;
    }

    private List<Integer> getIndex(int ind, List<List<String>> rows, String value, Expression exp) {
        List<Integer> index = new ArrayList<>();
        if (exp instanceof EqualsTo) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).get(ind).equals(value)) {
                    index.add(i);
                }
            }
        } else if (exp instanceof MinorThan) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).get(ind).compareTo(value) < 0) {
                    index.add(i);
                }
            }
        } else if (exp instanceof GreaterThan) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).get(ind).compareTo(value) > 0) {
                    index.add(i);
                }
            }
        } else if (exp instanceof GreaterThanEquals) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).get(ind).compareTo(value) >= 0) {
                    index.add(i);
                }
            }
        } else if (exp instanceof MinorThanEquals) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).get(ind).compareTo(value) <= 0) {
                    index.add(i);
                }
            }
        } else if (exp instanceof NotEqualsTo) {
            for (int i = 0; i < rows.size(); i++) {
                if (!rows.get(i).get(ind).equals(value)) {
                    index.add(i);
                }
            }
        } else {
            try {
                throw new DBError(exp.toString(), 1);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return null;
            }
        }
        return index;
    }

    public TableData getTableDataByDelete(String deleteSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Delete delete = null;
        try {
            delete = (Delete) pm.parse(new StringReader(deleteSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
        }
        TableData tableData = TableData.getDataByName(delete.getTable().getName());
        if (delete.getWhere() == null) {
            DataPersistence.dropData(delete.getTable().getName());
            return null;
        }
        List<Integer> result = doExpression(tableData, delete.getWhere());
        for (int i = 0; i < result.size(); i++) {
            tableData.getRows().remove(Integer.parseInt(result.get(i).toString()));
        }
        DataPersistence.persistenceData(tableData);
        IndexPersistence.updateIndex(tableData.getTable().getName());
        return tableData;
    }

    public TableData getTableDataByUpdate(String updateSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Update update = null;
        try {
            update = (Update) pm.parse(new StringReader(updateSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
        }
        TableData tableData = TableData.getDataByName(update.getTables().get(0).getName());
        List<Integer> result = new ArrayList<>();
        if (update.getWhere() == null) {
            for (int i = 0; i < tableData.getRows().size(); i++) {
                result.add(i);
            }
        } else {
            result = doExpression(tableData, update.getWhere());
        }
        List<String> cols = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < update.getColumns().size(); i++) {
            cols.add(update.getExpressions().get(i).toString());
            indexes.add(getColumnIndex(tableData.getTable(), update.getColumns().get(i).toString()));
        }
        for (int i = 0; i < result.size(); i++) {
            List<String> list = tableData.getRows().get(Integer.parseInt(result.get(i).toString()));
            for (int j = 0; j < cols.size(); j++) {
                list.remove(Integer.parseInt(indexes.get(j).toString()));
                list.add(indexes.get(j), cols.get(j));
            }
            tableData.getRows().remove(Integer.parseInt(result.get(i).toString()));
            try {
                tableData.insertRow(update.getColumns(), new ArrayList<>(list), Integer.parseInt(result.get(i).toString()));
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return null;
            }
        }
        DataPersistence.persistenceData(tableData);
        IndexPersistence.updateIndex(tableData.getTable().getName());
        return tableData;
    }

    public int[] getSelectItemsIndex(List<SelectItem> items, Table table) {
        int[] n = new int[items.size()];
        for (int i = 0; i < items.size(); i++) {
            boolean b = false;
            for (int j = 0; j < table.getColumns().size(); j++) {
                if (table.getColumns().get(j).getName().toUpperCase().equals(items.get(i).toString().toUpperCase())) {
                    n[i] = j;
                    b = true;
                    break;
                }
            }
            if (!b && !items.get(i).toString().equals("*")) try {
                throw new DBError("column", items.get(i).toString(), null);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return null;
            }
        }
        return n;
    }

    public ResultView getResultViewBySelect(String selectSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Select select;
        try {
            select = (Select) pm.parse(new StringReader(selectSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
            return null;
        }
        PlainSelect ps = (PlainSelect) select.getSelectBody();
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tables = tablesNamesFinder.getTableList(select);
        TableData tableData = TableData.getDataByName(tables.get(0));
        List<Integer> result = doExpression(tableData, ps.getWhere());
        List<List<String>> lists = tableData.getRows();
        String[] titles;
        int[] n = null;
        if (ps.getSelectItems().size() == 1 && ps.getSelectItems().get(0).toString().equals("*")) {
            titles = new String[tableData.getTable().getColumns().size()];
            n = new int[tableData.getTable().getColumns().size()];
            for (int i = 0; i < tableData.getTable().getColumns().size(); i++) {
                titles[i] = tableData.getTable().getColumns().get(i).getName();
                n[i] = i;
            }
        } else {
            n = getSelectItemsIndex(ps.getSelectItems(), tableData.getTable());
            if (n == null) return null;
            titles = new String[n.length];
            for (int m = 0; m < n.length; m++) {
                titles[m] = tableData.getTable().getColumns().get(n[m]).getName();
            }
        }
        ResultView resultView = new ResultView(titles);
        if (ps.getWhere() != null) {
            for (int j = 0; j < result.size(); j++) {
                List<String> ls = lists.get(result.get(j));
                String[] temp = new String[titles.length];
                for (int k = 0; k < n.length; k++) {
                    temp[k] = ls.get(n[k]);
                }
                resultView.addRow(temp);
            }
        } else {
            for (int j = 0; j < lists.size(); j++) {
                List<String> ls = lists.get(j);
                String[] temp = new String[titles.length];
                for (int k = 0; k < n.length; k++) {
                    temp[k] = ls.get(n[k]);
                }
                resultView.addRow(temp);
            }
        }
        System.out.println(resultView);
        return resultView;
    }

    public Table getTableByAlter(String alterSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Alter alter;
        try {
            alter = (Alter) pm.parse(new StringReader(alterSql));
        } catch (JSQLParserException e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
            return null;
        }
        AlterExpression ae = alter.getAlterExpressions().get(0);
        Table table = Table.getTableByName(alter.getTable().getName());
        TableData tableData = TableData.getDataByName(alter.getTable().getName());
        List<Column> columns = table.getColumns();
        Column column = new Column();
        if (ae.getOperation().name().toUpperCase().equals("ADD")) {
            String cname = ae.getColDataTypeList().get(0).getColumnName();
            for (int i = 0; i < columns.size(); i++) {
                if (cname.toUpperCase().equals(columns.get(i).getName().toUpperCase())) {
                    try {
                        throw new DBError("Duplicate column name '" + columns.get(i).getName() + "'");
                    } catch (DBError dbError) {
                        dbError.printStackTrace();
                        return null;
                    }
                }
            }
            column.setName(cname);
            List<List<String>> lists = tableData.getRows();
            if (ae.getColDataTypeList().get(0).getColDataType().getArgumentsStringList() == null) {
                column.setLength(0);
                for (int i = 0; i < lists.size(); i++) {
                    lists.get(i).add(String.valueOf(0));
                }
            } else {
                column.setLength(Integer.parseInt(ae.getColDataTypeList().get(0).getColDataType().getArgumentsStringList().get(0)));
                if (ae.getColDataTypeList().get(0).getColDataType().getDataType().toLowerCase().equals("int")) {
                    for (int i = 0; i < lists.size(); i++) {
                        lists.get(i).add(String.valueOf(0));
                    }
                } else {
                    for (int i = 0; i < lists.size(); i++) {
                        lists.get(i).add("");
                    }
                }
            }
            column.setType(ae.getColDataTypeList().get(0).getColDataType().getDataType());
            column.setNotNull(false);
            column.setPrimaryKey(false);
            columns.add(column);
            table.setColumns(columns);
            TablePersistence.alterTable(table);
            DataPersistence.persistenceData(tableData);
        } else if (ae.getOperation().name().toUpperCase().equals("DROP")) {
            int index = getIndexByColumnName(table, ae.getColumnName());
            if (index == -1) {
                return null;
            } else {
                table.getColumns().remove(index);
                for (int i = 0; i < tableData.getRows().size(); i++) {
                    tableData.getRows().get(i).remove(index);
                }
                TablePersistence.alterTable(table);
                DataPersistence.persistenceData(tableData);
            }
        }
        return null;
    }

    private int getIndexByColumnName(Table table, String name) {
        int index = -1;
        boolean b = false;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (table.getColumns().get(i).getName().toUpperCase().equals(name.toUpperCase())) {
                b = true;
                index = i;
                break;
            }
        }
        if (!b) {
            try {
                throw new DBError("column", name, null);
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
        }
        return index;
    }

    public void drop(String dropSql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Drop drop;
        try {
            drop = (Drop) pm.parse(new StringReader(dropSql));
        } catch (JSQLParserException e) {
            String[] divs = dropSql.split(" ");
            String indexName = divs[2];
            String tableName = divs[4];
            String fileName = tableName + "-" + indexName;
            IndexPersistence.dropIndex(fileName);
            return;
        }
        if (drop.getType().equals("table")) {
            TablePersistence.dropTable(drop.getName().getName());
        } else {
            System.out.println(drop.getParameters());
        }
    }

    public void executeCreateIndex(String createIndexSql, Boolean bool) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        CreateIndex createIndex = null;
        try {
            createIndex = (CreateIndex) pm.parse(new StringReader(createIndexSql));
        } catch (Exception e) {
            try {
                throw new DBError("You have an error in your SQL syntax;");
            } catch (DBError dbError1) {
                dbError1.printStackTrace();
            }
        }
        String indexName = createIndex.getIndex().getName();
        List<String> columnList = createIndex.getIndex().getColumnsNames();
        Table table = Table.getTableByName(createIndex.getTable().getName());
        TableData data = TableData.getDataByName(table.getName());
        int position = -1;
        for (int i = 0; i < table.getColumns().size(); i++) {
            if (table.getColumns().get(i).getName().toUpperCase().equals(columnList.get(0).toUpperCase())) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            try {
                throw new DBError("column", columnList.get(0), null);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                return;
            }
        }
        Set<IndexNode> nodeTree = new TreeSet<>();
        if (position > -1) {
            for (int i = 0; i < data.getRows().size(); i++) {
                IndexNode indexNode = new IndexNode();
                indexNode.setData(data.getRows().get(i).get(position));
                indexNode.setIdx(i);
                nodeTree.add(indexNode);
            }
            if (bool) {
                IndexPersistence.createIndex(nodeTree, columnList.get(0) + "-" + table.getName() + "-" + indexName);
                ArrayList<String[]> resRow = new ArrayList<>();
                String[] title = {"Index", "Data"};
                for (IndexNode in : nodeTree) {
                    String[] temp = {String.valueOf(in.getIdx()), in.getData()};
                    resRow.add(temp);
                }
                ResultView resultView = new ResultView(title);
                resRow.forEach(resultView::addRow);
                System.out.println(resultView);
            }else {
                IndexPersistence.persistenceIndex(nodeTree, columnList.get(0) + "-" + table.getName() + "-" + indexName);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the MySQL monitor.");
        Scanner in = new Scanner(System.in);
        String sql;
        SqlParse parse = new SqlParse();
        while (!(sql = in.nextLine()).equals("exit")) {
            parse.setSqlString(sql);
            try {
                parse.parse();
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
        }
        System.out.println("Bye.");
    }
}

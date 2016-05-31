package unitils.dataset.excel;


import org.apache.commons.lang.StringUtils;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.*;
import org.dbunit.dataset.excel.XlsDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.MultiSchemaDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Created by juemingzi on 16/3/15.
 */

public class MultiSchemaXlsDataSetReader {
    private String defaultSchemaName;

    public MultiSchemaXlsDataSetReader(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

    public MultiSchemaDataSet readDataSetXls(File... dataSetFiles) {
        try {
            Map<String, List<ITable>> tableMap = getTables(dataSetFiles);
            MultiSchemaDataSet dataSets = new MultiSchemaDataSet();
            for (Entry<String, List<ITable>> entry : tableMap.entrySet()) {
                List<ITable> tables = entry.getValue();
                try {
                    DefaultDataSet ds = new DefaultDataSet(tables
                            .toArray(new ITable[] {}));
                    dataSets.setDataSetForSchema(entry.getKey(), ds);
                } catch (AmbiguousTableNameException e) {
                    throw new UnitilsException("构造DataSet失败!",  e);
                }
            }
            return dataSets;
        } catch (Exception e) {
            throw new UnitilsException("解析EXCEL文件出错：", e);
        }
    }

    private Map<String, List<ITable>> getTables(File... dataSetFiles) {
        Pattern pattern = Pattern.compile("\\.");
        Map<String, List<ITable>> tableMap = new HashMap<String, List<ITable>>();
        try {
            for (File file : dataSetFiles) {
                IDataSet dataSet = new XlsDataSet(new FileInputStream(file));
                String[] tableNames = dataSet.getTableNames();
                for (String each : tableNames) {
                    String schema = null;
                    String tableName;
                    String[] temp = pattern.split(each);
                    if (temp.length == 2) {
                        schema = temp[0];
                        tableName = temp[1];
                    } else {
                        schema = this.defaultSchemaName;
                        tableName = each;
                    }
                    ITable table = dataSet.getTable(each);
                    if (!tableMap.containsKey(schema)) {
                        tableMap.put(schema, new ArrayList<ITable>());
                    }
                    tableMap.get(schema).add(new XlsTable(tableName, table));
                }
            }
        } catch (Exception e) {
            throw new UnitilsException("error: "
                    + Arrays.toString(dataSetFiles), e);
        }
        return tableMap;
    }
    class XlsTable extends AbstractTable {
        private ITable delegate;
        private String tableName;

        public XlsTable(String tableName, ITable table) {
            this.delegate = table;
            this.tableName = tableName;
        }

        public int getRowCount() {
            return delegate.getRowCount();
        }

        public ITableMetaData getTableMetaData() {
            ITableMetaData meta = delegate.getTableMetaData();
            try {
                return new DefaultTableMetaData(tableName, meta.getColumns(),
                        meta.getPrimaryKeys());
            } catch (DataSetException e) {
                throw new UnitilsException("Don't get the meta info from  "
                        + meta, e);
            }
        }

        public Object getValue(int row, String column) throws DataSetException {
            Object delta = delegate.getValue(row, column);
            if (delta instanceof String) {
                if (StringUtils.isEmpty((String) delta)) {
                    return null;
                }
            }
            return delta;
        }

    }
}
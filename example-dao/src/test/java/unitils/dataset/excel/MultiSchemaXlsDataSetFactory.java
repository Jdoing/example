package unitils.dataset.excel;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by juemingzi on 16/3/15.
 */
public class MultiSchemaXlsDataSetFactory implements DataSetFactory {

    protected String defaultSchemaName;

    public void init(Properties configuration, String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

    public MultiSchemaDataSet createDataSet(File... dataSetFiles) {
        try {
            MultiSchemaXlsDataSetReader xlsDataSetReader = new MultiSchemaXlsDataSetReader(
                    defaultSchemaName);
            return xlsDataSetReader.readDataSetXls(dataSetFiles);
        } catch (Exception e) {
            throw new UnitilsException("创建数据集失败:\n "
                    + Arrays.toString(dataSetFiles), e);
        }
    }

    public String getDataSetFileExtension() {
        return "xls";
    }

}
package artoria.file;

import artoria.beans.BeanUtils;
import artoria.entity.Student;
import artoria.mock.MockUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class ExcelTest {
    private static Map<String, String> titleStuMap = new HashMap<String, String>();
    private static List<Student> students = new ArrayList<Student>();
    private static List<Map<String, Object>> studentsMap = new ArrayList<Map<String, Object>>();

    static {
        titleStuMap.put("Student Name", "name");
        titleStuMap.put("Student Age", "age");
        titleStuMap.put("Student Height", "height");
        titleStuMap.put("Student Id", "studentId");
        titleStuMap.put("School Name", "schoolName");
        for (int i = 0; i < 5; i++) {
            students.add(MockUtils.mock(Student.class));
        }
        studentsMap.addAll(BeanUtils.beanToMapInList(students));
    }

    @Test
    public void test1() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel excel = new Excel();
        excel.setExtension("XLS");
        excel.setRowStartNumber(4);
        excel.setColumnStartNumber(5);
        excel.fromMapList(studentsMap);
        excel.writeToFile(new File("e:\\123.xls"));
    }

    @Test
    public void test2() throws Exception {
        Excel excel = new Excel();
        excel.readFromFile(new File("e:\\123.xls"));
        excel.setRowStartNumber(4);
        excel.setColumnStartNumber(5);
        List<Map<String, Object>> students = excel.toMapList();
        System.out.println(JSON.toJSONString(students, true));
    }

    @Test
    public void test3() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel excel = new Excel();
        excel.setExtension("XLSX");
        excel.setRowStartNumber(3);
        excel.setColumnStartNumber(4);
        excel.addHeaders(titleStuMap);
        excel.fromMapList(studentsMap);
        excel.writeToFile(new File("e:\\456.xlsx"));
    }

    @Test
    public void test4() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        byte[] template = FileUtils.read(new File("e:\\template.xlsx"));
        Excel excel = new Excel();
        excel.setExtension("XLSX");
        excel.setTemplate(template);
        excel.setRowStartNumber(3);
        excel.setColumnStartNumber(4);
        excel.addHeaders(titleStuMap);
        excel.fromMapList(studentsMap);
        excel.writeToFile(new File("e:\\456.xlsx"));
    }

    @Test
    public void test5() throws Exception {
        System.out.println(JSON.toJSONString(students, true));
        Excel excel = new Excel();
        excel.setExtension("XLSX");
        excel.addHeaders(titleStuMap);
        excel.fromMapList(studentsMap);
        excel.writeToFile(new File("e:\\456.xlsx"));
    }

    @Test
    public void test6() throws Exception {
        Excel excel = new Excel();
        excel.readFromFile(new File("e:\\456.xlsx"));
        excel.addHeaders(titleStuMap);
        excel.setRowStartNumber(3);
        excel.setColumnStartNumber(4);
        List<Map<String, Object>> list = excel.toMapList();
        System.out.println(JSON.toJSONString(list, true));
    }

}
